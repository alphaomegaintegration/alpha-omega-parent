package com.alpha.omega.office;

import com.alpha.omega.office.model.Office;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.core.convert.MappingConfiguration;
import org.springframework.data.redis.core.index.IndexConfiguration;
import org.springframework.data.redis.core.index.IndexDefinition;
import org.springframework.data.redis.core.index.SimpleIndexDefinition;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

import static com.alpha.omega.core.Constants.COLON;


@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@EnableRedisRepositories(basePackages = {"com.alpha.omega.office.repository"})
public class DefaultRedisOfficeConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRedisOfficeConfiguration.class);

    @Autowired
    RedisProperties redisProperties;

    @Autowired
    Environment env;

    @Configuration
    @ConditionalOnProperty(prefix = "cache.server", name = "mode", havingValue = "standalone", matchIfMissing = true)
    public static class StandaloneConfig{
        @Autowired
        Environment env;

        @Bean
        @Primary
        public ReactiveRedisConnectionFactory connectionFactory() {
            String host = env.getProperty("REDIS_HOST", "localhost");
            Integer port = env.getProperty("REDIS_PORT", Integer.class,6379);
            logger.info("Using redis host => {} and redis port => {}",host, port);
            return new LettuceConnectionFactory(host, port);
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "cache.server", name = "mode", havingValue = "cluster", matchIfMissing = false)
    public static class ClusterConfig{
        @Autowired
        Environment env;

        /*
         * spring.redis.cluster.nodes[0] = 127.0.0.1:7379
         * spring.redis.cluster.nodes[1] = 127.0.0.1:7380
         * ...
        List<String> nodes;
         */

        @Bean
        @Primary
        public ReactiveRedisConnectionFactory connectionFactory() {
            String host = env.getProperty("REDIS_HOST", "localhost");
            Integer port = env.getProperty("REDIS_PORT", Integer.class,6379);
            List<String> nodes = Collections.singletonList(host+COLON+port);
            return new LettuceConnectionFactory(new RedisClusterConfiguration(nodes));
        }


    }

    private RedisNode populateNode(String host, Integer port) {
        return new RedisNode(host, port);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    RedisStandaloneConfiguration RedisStandaloneConfiguration(RedisProperties redisProperties) {

        RedisPassword redisPassword = RedisPassword.of(redisProperties.getPassword());
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        redisStandaloneConfiguration.setPassword(redisPassword);
        return redisStandaloneConfiguration;
    }



    @Bean
    public ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory, ResourceLoader resourceLoader) {
        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(resourceLoader.getClassLoader());
        RedisSerializationContext<Object, Object> serializationContext = RedisSerializationContext.newSerializationContext().key(jdkSerializer).value(jdkSerializer).hashKey(jdkSerializer).hashValue(jdkSerializer).build();
        return new ReactiveRedisTemplate(reactiveRedisConnectionFactory, serializationContext);
    }

    @Bean("reactiveStringRedisTemplate")
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory);
    }

	/*
	@Bean("redisson")
	RedissonClient redissonClient(RedisStandaloneConfiguration redisStandaloneConfiguration) {
		Config config = new Config();
		String host = redisStandaloneConfiguration.getHostName();
		Integer port = redisStandaloneConfiguration.getPort();
		String address = String.format("redis://%s:%d", host,port);
		config.useSingleServer().setAddress(address);
		RedissonClient redisson = Redisson.create(config);
		return redisson;
	}

	 */

    @Bean
    public RedisMappingContext keyValueMappingContext() {
        //return new RedisMappingContext(new MappingConfiguration(new IndexConfiguration(), new AppKeyspaceConfiguration()));
        return new RedisMappingContext(new MappingConfiguration(new AppIndexConfiguration(), new AppKeyspaceConfiguration()));
    }

    public static class AppKeyspaceConfiguration extends KeyspaceConfiguration {

        @Override
        protected Iterable<KeyspaceSettings> initialConfiguration() {
            return Collections.singleton(new KeyspaceSettings(Office.class, "office"));
        }
    }

    public static class AppIndexConfiguration extends IndexConfiguration {

        @Override
        protected Iterable<IndexDefinition> initialConfiguration() {
            return Collections.singleton(new SimpleIndexDefinition("office", "email"));
        }
    }

    @PostConstruct
    public void postInit() {
        logger.info("Configured => {} with properties  => {}", this.getClass().getName(), "");
    }



}
