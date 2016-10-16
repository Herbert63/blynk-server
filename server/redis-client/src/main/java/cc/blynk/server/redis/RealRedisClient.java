package cc.blynk.server.redis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 14.10.16.
 */
public class RealRedisClient implements Closeable, RedisClient {

    public static final String REDIS_PROPERTIES = "redis.properties";
    public static final int USER_DB_INDEX = 0;
    public static final int TOKEN_DB_INDEX = 1;

    private static final Logger log = LogManager.getLogger(RealRedisClient.class);

    private final JedisPool tokenPool;
    private final JedisPool userPool;

    public RealRedisClient(Properties props) {
        this(props.getProperty("redis.host"), props.getProperty("redis.pass"));
    }

    protected RealRedisClient(String host, String pass) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        config.setBlockWhenExhausted(true);
        this.userPool = new JedisPool(config, host, Protocol.DEFAULT_PORT, Protocol.DEFAULT_TIMEOUT, pass, USER_DB_INDEX);
        this.tokenPool = new JedisPool(config, host, Protocol.DEFAULT_PORT, Protocol.DEFAULT_TIMEOUT, pass, TOKEN_DB_INDEX);
        checkConnected();
    }

    private void checkConnected() {
        try (Jedis jedis = userPool.getResource()) {
        }
    }

    @Override
    public String getServerByToken(String token) {
        try (Jedis jedis = tokenPool.getResource()) {
            return jedis.get(token);
        } catch (Exception e) {
            log.error("Error getting server by token {}.", token, e);
        }
        return null;
    }

    @Override
    public void assignServerToToken(String token, String server) {
        try (Jedis jedis = tokenPool.getResource()) {
            jedis.set(token, server);
        } catch (Exception e) {
            log.error("Error setting server {} to token {}.", server, token, e);
        }
    }

    @Override
    public void assignServerToUser(String username, String server) {
        try (Jedis jedis = userPool.getResource()) {
            jedis.set(username, server);
        }
    }

    @Override
    public void removeToken(String token) {
        try (Jedis jedis = tokenPool.getResource()) {
            jedis.del(token);
        }
    }

    //only for tests
    @Override
    public Jedis getUserClient() {
        return userPool.getResource();
    }
    @Override
    public Jedis getTokenClient() {
        return tokenPool.getResource();
    }

    @Override
    public void close() throws IOException {
        userPool.destroy();
        tokenPool.destroy();
    }
}
