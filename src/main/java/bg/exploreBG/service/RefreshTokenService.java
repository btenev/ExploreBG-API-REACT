package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RefreshTokenService {
    // Deletion related messages
    private static final String TOKEN_DELETION_SUCCESS = "Successfully deleted token and user ID from Redis for userId {}";
    private static final String TOKEN_DELETION_ERROR = "Error while deleting token and user ID from Redis for userId {}: {}";
    private static final String TOKEN_NOT_FOUND = "No existing token found for userId {}. Skipping deletion.";
    private static final String TOKEN_FOUND = "Token found for userId {}: {}. Deleting the token and user ID from Redis.";

    // Storage related messages
    private static final String STORAGE_ATTEMPT = "Attempting to store token-user pair - token: {}, userId: {}, TTL: {}s";
    private static final String STORAGE_SCRIPT_EXEC = "Executing Lua script with keys: {} and args: {}";
    private static final String STORAGE_SUCCESS = "Successfully stored token-user pair - token: {}, userId: {}";
    private static final String STORAGE_SCRIPT_FAILURE = "Failed to store token-user pair - token: {}, userId: {}. Script returned: {}";
    private static final String STORAGE_EXCEPTION = "Exception while storing token-user pair - token: {}, userId: {}. Error: {}";

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

    private static final String STORE_SCRIPT =
            "redis.call('SET', KEYS[1], ARGV[1], 'EX', ARGV[3], 'NX') " +
                    "redis.call('SET', KEYS[2], ARGV[2], 'EX', ARGV[3], 'NX') " +
                    "return 1";

    private static final String REVOKE_SCRIPT =
            "local userIdKey = KEYS[1]\n" +
                    "local token = redis.call('GET', userIdKey)\n" +
                    "if token then\n" +
                    "    redis.call('DEL', 'refresh_token:'..token, userIdKey)\n" +
                    "    return 1\n" +
                    "end\n" +
                    "return 0";

    private static final String REFRESH_TOKEN = "refresh_token:";
    private static final String USER_ID = "USER_ID:";

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Optional<Long> getUserIdByToken(String refreshToken) {
        String value = redisTemplate.opsForValue().get(REFRESH_TOKEN + refreshToken);
        if (value == null) return Optional.empty();
        return Optional.of(Long.parseLong(value));
    }

    public void deleteToken(String refreshToken) {
        this.redisTemplate.delete(REFRESH_TOKEN + refreshToken);
    }

    public Optional<Long> validateToken(String refreshToken) {
        return getUserIdByToken(refreshToken);
    }

    public void storeTokenUserIdPair(String refreshToken, Long userId, Duration ttl) {
        String keyToken = REFRESH_TOKEN + refreshToken;
        String keyUserId = USER_ID + userId;
        String ttlSeconds = String.valueOf(ttl.getSeconds());

        logger.info(STORAGE_ATTEMPT, refreshToken, userId, ttlSeconds);

        try {
            List<String> keys = Arrays.asList(keyToken, keyUserId);
            List<String> args = Arrays.asList(userId.toString(), refreshToken, ttlSeconds);

            logger.debug(STORAGE_SCRIPT_EXEC, keys, args);

            Long result = redisTemplate.execute(
                    new DefaultRedisScript<>(STORE_SCRIPT, Long.class),
                    keys,
                    args.toArray()
            );

            if (result == 1) {
                logger.info(STORAGE_SUCCESS, refreshToken, userId);
            } else {
                logger.error(STORAGE_SCRIPT_FAILURE, refreshToken, userId, result);
                throw new AppException("Failed to store token-user pair", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            logger.error(STORAGE_EXCEPTION, refreshToken, userId, e.getMessage(), e);
            throw new AppException("Redis operation failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void revokeExistingRefreshTokenAtomic(Long userId) {
        String userIdKey = USER_ID + userId;
        try {
            Long result = redisTemplate.execute(
                    new DefaultRedisScript<>(REVOKE_SCRIPT, Long.class),
                    Collections.singletonList(userIdKey)
            );

            if (result == 1) {
                logger.info(TOKEN_DELETION_SUCCESS, userId);
            } else {
                logger.debug(TOKEN_NOT_FOUND, userId);
            }
        } catch (Exception e) {
            logger.error(TOKEN_DELETION_ERROR, userId, e.getMessage(), e);
        }
    }
}