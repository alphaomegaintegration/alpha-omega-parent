package com.alpha.omega.cache.config;

import com.alpha.omega.cache.expiration.ExpiringCacheEntry;
import com.alpha.omega.cache.redis.RedisTemplateExpiringCacheEntry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@EnableConfigurationProperties(CacheConfigProperties.class)
@ConditionalOnProperty(prefix = "cache.provider", name = "name", havingValue = "redis")
public class DefaultRedisCacheConfiguration extends BaseCacheConfiguration{

	private static final Logger logger = LoggerFactory.getLogger(DefaultRedisCacheConfiguration.class);

	@Autowired
	CacheConfigProperties cacheConfigProperties;

	public void setCacheConfigProperties(CacheConfigProperties cacheConfigProperties) {
		this.cacheConfigProperties = cacheConfigProperties;
	}

	@Bean
	@ConditionalOnProperty(prefix = "cache.provider", name = "type", havingValue = "standalone", matchIfMissing = true)
	RedisStandaloneConfiguration RedisStandaloneConfiguration(CacheConfigProperties cacheConfigProperties){

		RedisPassword redisPassword = RedisPassword.of(cacheConfigProperties.getPassword());
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(cacheConfigProperties.getHost());
		redisStandaloneConfiguration.setPort(cacheConfigProperties.getPort());
		redisStandaloneConfiguration.setPassword(redisPassword);
		return redisStandaloneConfiguration;
	}


	/*@Bean
	@ConditionalOnProperty(prefix = "cache.provider", name = "type", havingValue = "sentinel")
	RedisStandaloneConfiguration RedisStandaloneConfiguration(CacheConfigProperties cacheConfigProperties){

		RedisPassword redisPassword = RedisPassword.of(cacheConfigProperties.getPassword());
		RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
		redisSentinelConfiguration.setPassword(redisPassword);
		redisStandaloneConfiguration.setHostName(cacheConfigProperties.getHost());
		redisStandaloneConfiguration.setPort(cacheConfigProperties.getPort());
		redisStandaloneConfiguration.setPassword(redisPassword);
		return redisStandaloneConfiguration;
	}*/

	// RedisConfiguration

	@Bean("redisConnectionFactory")
	@ConditionalOnMissingBean(name = {"redisConnectionFactory"})
	public LettuceConnectionFactory redisConnectionFactory(RedisConfiguration redisStandaloneConfiguration, CacheConfigProperties cacheConfigProperties){

		LettuceClientConfiguration clientConfiguration = null;
		if (cacheConfigProperties.isUseSSl()){
			clientConfiguration = LettuceClientConfiguration.builder()
					.commandTimeout(Duration.of(cacheConfigProperties.getTimeout(), ChronoUnit.MILLIS))
					.useSsl()
					.disablePeerVerification()
					.build();
		} else {
			clientConfiguration = LettuceClientConfiguration.builder()
					.commandTimeout(Duration.of(cacheConfigProperties.getTimeout(), ChronoUnit.MILLIS))
					.build();
		}
		return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfiguration);
	}

	/*
	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
		redisTemplate.setConnectionFactory(cf);
		return redisTemplate;
	}

	 */

	@Bean
	public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

		RedisTemplate<byte[], byte[]> template = new RedisTemplate<byte[], byte[]>();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

	/*
	@Bean("reactiveRedisTemplate")
	ReactiveRedisTemplate<?, ?> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
		return new ReactiveRedisTemplate<>(factory, RedisSerializationContext.string());
	}

	 */

	@Bean
	@ConditionalOnMissingBean(
			name = {"reactiveRedisTemplate"}
	)
	@ConditionalOnBean({ReactiveRedisConnectionFactory.class})
	public ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory, ResourceLoader resourceLoader) {
		JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(resourceLoader.getClassLoader());
		RedisSerializationContext<Object, Object> serializationContext = RedisSerializationContext.newSerializationContext().key(jdkSerializer).value(jdkSerializer).hashKey(jdkSerializer).hashValue(jdkSerializer).build();
		return new ReactiveRedisTemplate(reactiveRedisConnectionFactory, serializationContext);
	}

	@Bean
	@ConditionalOnMissingBean(
			name = {"reactiveStringRedisTemplate"}
	)
	@ConditionalOnBean({ReactiveRedisConnectionFactory.class})
	public ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
		return new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory);
	}

	@Bean("aoExpiringCacheEntry")
	ExpiringCacheEntry expiringCacheEntry(RedisTemplate<String, String> redisTemplate){
		return new RedisTemplateExpiringCacheEntry(redisTemplate);
	}


	@Bean("aoCacheManager")
	@ConditionalOnProperty(prefix = "cache.provider", name = "name", havingValue = "redis")
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory,CacheConfigProperties cacheConfigProperties) {
		return RedisCacheManager.create(redisConnectionFactory);
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

	@PostConstruct
	public void postInit(){
		logger.info("Configured => {} with properties  => {}", this.getClass().getName(), cacheConfigProperties);
	}

}
