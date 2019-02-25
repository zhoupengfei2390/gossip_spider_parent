package com.itheima.spider.news.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/*
   jedis连接池
    */
public class JedisUtils {

    private static JedisPool jedisPool = null;
    private static final String host = "192.168.72.142";
    private static final int port = 6379;

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig ();
        jedisPoolConfig.setMaxTotal (100);
        jedisPoolConfig.setMaxIdle (20);
        jedisPool = new JedisPool (jedisPoolConfig, host, port);
    }

    /*
    获取jedis连接对象,用完要关闭close
     */
    public static Jedis getJedis() {
        return jedisPool.getResource ();
    }
}
