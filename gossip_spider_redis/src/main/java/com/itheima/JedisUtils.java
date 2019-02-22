package com.itheima;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisUtils {
    private static JedisPool jedisPool;

    static {
        JedisPoolConfig poolConfig = new JedisPoolConfig ();
    }
}
