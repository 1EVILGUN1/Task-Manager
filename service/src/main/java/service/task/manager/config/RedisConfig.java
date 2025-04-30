package service.task.manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import service.task.manager.model.HistoryEntry;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, HistoryEntry> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, HistoryEntry> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(new Jackson2JsonRedisSerializer<>(HistoryEntry.class));
        return template;
    }
}