package bg.exploreBG.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "refresh_token:";

    public RefreshTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeToken( String refreshToken, Long userId, Duration ttl) {
        String key = PREFIX + refreshToken;
        redisTemplate.opsForValue().set(key, userId.toString(), ttl);
    }

    public Optional<Long> getUserIdByToken(String refreshToken) {
        String value = redisTemplate.opsForValue().get(PREFIX + refreshToken);
        if (value == null) return Optional.empty();
        return Optional.of(Long.parseLong(value));
    }

    public void deleteToken(String refreshToken) {
        this.redisTemplate.delete(PREFIX + refreshToken);
    }

    public Optional<Long> validateToken(String refreshToken) {
        return getUserIdByToken(refreshToken);
    }
}
