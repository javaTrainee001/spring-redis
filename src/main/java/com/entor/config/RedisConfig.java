package com.entor.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;
    private int database = 0;
    @Value("${spring.redis.timeout}")
    private int timeout;


    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        //JedisConnectionFactory是RedisConnectionFactory的父类
        JedisConnectionFactory jcf = new JedisConnectionFactory();
        jcf.setHostName(host);
        jcf.setPort(port);
        jcf.setTimeout(timeout);
        jcf.setPassword(password);
        RedisConnectionFactory factory = jcf;
        return factory;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory());
        //表示用Jackson对redis的值进行序列化与反序列化
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        //指定序列化的范围，public ,private修饰的field，get，set等
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        serializer.setObjectMapper(mapper);

        template.setValueSerializer(serializer);
        //懒加载
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(@Qualifier("redisTemplate") RedisTemplate template) {
        RedisCacheManager manager = RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(template.getConnectionFactory()).cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(10))).transactionAware().build();
        return manager;
    }
}
