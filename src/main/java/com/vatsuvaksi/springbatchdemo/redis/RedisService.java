package com.vatsuvaksi.springbatchdemo.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vatsuvaksi.springbatchdemo.entities.ProductReview;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RedisService {

    private final Jedis jedis;
    private final ObjectMapper objectMapper;

    public RedisService() {
        this.jedis = new Jedis("localhost", 6379);
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Stores the API response and ProductReview object as a list in Redis.
     *
     * @param id            The key for the Redis entry.
     * @param apiResponse   The API response to store.
     * @param productReview The ProductReview object to store.
     * @throws JsonProcessingException if serialization fails.
     */
    public void saveToRedis(String id, String apiResponse, ProductReview productReview) throws JsonProcessingException {
        List<String> values = Arrays.asList(
                apiResponse,
                objectMapper.writeValueAsString(productReview)
        );

        jedis.rpush(id, values.toArray(new String[0]));
    }

    /**
     * Retrieves the stored data for a given key from Redis.
     *
     * @param id The key for the Redis entry.
     * @return A list of stored data.
     */
    public List getFromRedis(String id) {
        List<String> values = jedis.lrange(id, 0, -1);

        return values.stream().map(value -> {
            try {
                return objectMapper.readValue(value, Map.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize Redis value", e);
            }
        }).collect(Collectors.toList());
    }

    /**
     * Deletes a key from Redis.
     *
     * @param id The key to delete.
     */
    public void deleteFromRedis(String id) {
        jedis.del(id);
    }

    /**
     * Closes the Redis client.
     */
    public void close() {
        jedis.close();
    }
}
