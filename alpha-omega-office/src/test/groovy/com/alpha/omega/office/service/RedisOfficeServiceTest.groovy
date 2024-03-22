package com.alpha.omega.office.service

import com.alpha.omega.office.DefaultRedisOfficeConfiguration
import com.fasterxml.jackson.databind.ObjectMapper
import com.redis.testcontainers.RedisContainer
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import org.junit.jupiter.api.Assertions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.ResourceLoader
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.mock.DetachedMockFactory

@ContextConfiguration(classes = [TestConfig.class])
class RedisOfficeServiceTest extends Specification {
    private static final Logger logger = LoggerFactory.getLogger(RedisOfficeServiceTest.class);

    /*
    ./mvnw clean test -Dtest=RedisOfficeServiceTest
     */

    /*
    static GenericContainer redis = new GenericContainer<>("redis:5.0.3-alpine")
            .withExposedPorts(6379)

     */
    private static RedisContainer container = createRedisContainer()

    static  RedisContainer createRedisContainer(){
        RedisContainer container = new RedisContainer(
                RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG))
//                .withStartupCheckStrategy(
//                        new OneShotStartupCheckStrategy().withTimeout(Duration.ofSeconds(20))
//                );

        return container

    }

    static ResourceLoader resourceLoader = new DefaultResourceLoader();


    @Autowired
    RedisTemplate<?, ?> redisTemplate

    @Autowired
    ReactiveStringRedisTemplate reactiveStringRedisTemplate

    @Autowired
    RedisConnectionFactory redisConnectionFactory

    def setup() {

    }          // run before every feature method
    def cleanup() {}        // run after every feature method
    def setupSpec() {
        container.start()
        System.setProperty("REDIS_HOST", container.getHost())
        System.setProperty("REDIS_PORT", container.getRedisPort().toString())
    }     // run before the first feature method
    def cleanupSpec() {
        container.stop()
    }   // run after


    def test_config(){
        expect:
        redisTemplate
        reactiveStringRedisTemplate
    }

    def test_sanity() {
        given:
        // Retrieve the Redis URI from the container
        String redisURI = container.getRedisURI();
        RedisClient client = RedisClient.create(redisURI);
        logger.info("Using uri => {}",redisURI)
        expect:
        try (StatefulRedisConnection<String, String> connection = client.connect()) {
            RedisCommands<String, String> commands = connection.sync();
            Assertions.assertEquals("PONG", commands.ping());
        }
    }


    @Configuration
    @Import([DefaultRedisOfficeConfiguration.class])
    public static class TestConfig {

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        DetachedMockFactory mockFactory = new DetachedMockFactory()

        /*
        @Bean
        ContextRepository contextRepository() {
            return mockFactory.Mock(ContextRepository.class)

        }

         */



    }

}
