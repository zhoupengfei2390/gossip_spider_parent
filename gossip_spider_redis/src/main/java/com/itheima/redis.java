package com.itheima;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

public class redis {
    //jedis的入门代码
    @Test
    public void jedisOne() {
        //1.创建jedis对象
        Jedis jedis = new Jedis ("192.168.72.142", 6379);
        //2.测试是否连通
        String ping = jedis.ping ();
        //返回PANG表示已经连通
        System.out.println (ping);
        //3.关闭资源
        jedis.close ();
    }

    @Test
    public void jedisOfString() throws InterruptedException {
        Jedis jedis = new Jedis ("192.168.72.142", 6379);
        jedis.del ("age");
        jedis.set ("age", "18");
        String age = jedis.get ("age");
        System.out.println (age);
        System.out.println ("--------------------");

        //为age进行+1操作
        Long incr = jedis.incr ("age");
        System.out.println (incr);
        String age1 = jedis.get ("age");
        System.out.println (age1);
        System.out.println ("--------------------");

        //为age进行-1操作
        Long decr = jedis.decr ("age");
        System.out.println (decr);
        String age2 = jedis.get ("age");
        System.out.println (age2);
        System.out.println ("--------------------");

        //为age进行-10操作
        Long age3 = jedis.incrBy ("age", 10);
        System.out.println (age3);
        String age4 = jedis.get ("age");
        System.out.println (age4);
        System.out.println ("--------------------");

        //为age进行+10操作
        Long age5 = jedis.incrBy ("age", 10);
        System.out.println (age5);
        String age6 = jedis.get ("age");
        System.out.println (age6);
        System.out.println ("--------------------");
        //拼接字符串: 如果key存在就是拼接, 如果不存在就会重新创建
        jedis.del ("name");
        String name = jedis.set ("name", "景甜");
        jedis.append ("name", "亦菲");
        String name1 = jedis.get ("name");
        System.out.println (name1);
        System.out.println ("--------------------");

        //为key设置有效时长
        jedis.del ("address");
        jedis.setex ("address", 5, "北京");
        jedis.get ("address");
        while (jedis.exists ("address")) {
            System.out.println (jedis.get ("address"));
            Thread.sleep (1000);
        }

        jedis.del ("date");
        jedis.setex ("date", 10, "2018.9.15");

        while (jedis.exists ("date")) {
            System.out.println (jedis.ttl ("date"));
            Thread.sleep (1000);
        }

        jedis.close ();
    }

    /*
        使用jedis操作redis --> list
        list可以将其看做是java的队列类似
	    list数据类型的应用场景: 任务队列
     */
    @Test
    public void jedisForList() throws InterruptedException {
        //1.创建jedis对象
        Jedis jedis = new Jedis ("192.168.72.142", 6379);
        jedis.del ("age");
        /*jedis.lpush ("age", "1", "2", "3", "4", "5");
        while(jedis.exists ("age")){
            String age = jedis.rpop ("age");
            System.out.println (age);
            Thread.sleep (1000);
        }*/

        /*jedis.rpush ("age", "1", "2", "3", "4", "5");
        while (jedis.exists ("age")) {
            String age = jedis.lpop ("age");
            System.out.println (age);
            Thread.sleep (1000);
        }*/


        /*List<String> list = jedis.lrange ("age", 0, -1);
        for (String s : list) {
            System.out.println (s);
        }*/
        jedis.lpush ("age", "1", "2", "3", "4", "5");
        /*Long age = jedis.llen ("age");
        System.out.println (age);*/
        //在2后面加入101
       /* jedis.linsert ("age", BinaryClient.LIST_POSITION.BEFORE, "2", "12");
        List<String> list = jedis.lrange ("age", 0, -1);
        for (String s : list) {
            System.out.println (s);
        }*/
        jedis.rpoplpush ("age", "age");
        List<String> age = jedis.lrange ("age", 0, -1);
        System.out.println (age);
    }

    /**
     * 使用jedis操作redis --> hash
     * <p>
     * redis中hash和java中的hashMap类似
     * <p>
     * hash的应用场景:
     * 做缓存(目前使用较少,大部分的业务场景可以被String替代掉)
     */
    @Test
    public void jedisOfHash() {
        Jedis jedis = new Jedis ("192.168.72.142", 6379);
        jedis.hset ("person", "name", "lisi");
        jedis.hset ("person", "age", "22");
        jedis.hset ("person", "address", "beijing");
        String age = jedis.hget ("person", "age");
        //System.out.println (age);
       /* Map<String, String> person = jedis.hgetAll ("person");
        System.out.println (person);*/
        /*List<String> list = jedis.hmget ("person", "name", "age");
        System.out.println (list);*/
       /* Set<String> person = jedis.hkeys ("person");
        for (String s : person) {
            System.out.println (s);
        }*/
        List<String> person = jedis.hvals ("person");
        for (String s : person) {
            System.out.println (s);
        }
        jedis.del ("person");
        jedis.close ();
    }

    /*
     * 使用jedis 操作 redis --> set
	 *
	 * set特点: 无序 不重复
	 *
	 * set的应用场景:  去重操作
	 */
    @Test
    public void jedisOfSet() {
        Jedis jedis = new Jedis ("192.168.72.142", 6379);
        jedis.sadd ("age1", "1", "3", "2", "4", "5");
        jedis.sadd ("age2", "4", "5", "7", "6", "8");
      /*  Set<String> age1 = jedis.smembers ("age1");
        System.out.println (age1);*/
       /* Long age1count = jedis.scard ("age1");
        System.out.println (age1count);*/
       /* jedis.srem ("age1", "1");
        Set<String> age1 = jedis.smembers ("age1");
        System.out.println (age1);*/
      /*  Boolean age2 = jedis.sismember ("age2", "4");
        System.out.println (age2);*/
        //jedis.del ("age3");
        //Set<String> age3 = jedis.sinter ("age1", "age2");
        //Set<String> age3 = jedis.sdiff ("age1", "age2");
        //Set<String> age3 = jedis.sunion ("age1", "age2");
        //System.out.println (age3);
      /*  Long scard = jedis.scard ("age2");
        System.out.println (scard);*/
        jedis.del ("age3");
        //jedis.sdiffstore ("age3", "age2", "age1");
        //jedis.sunionstore ("age3", "age1", "age2");
        jedis.sinterstore ("age3", "age1", "age2");
        System.out.println (jedis.smembers ("age3"));
    }

    /*
 * 使用jedis来操作redis --> sortedSet集合
 *
 * soredSet 特点: 有序 去重复
 *
 * sortedSet的的应用场景:
 * 						排行榜
 *
 */
    @Test
    public void jedisOfsortedSet() {

        Jedis jedis = new Jedis ("192.168.72.142", 6379);
        jedis.del ("name");
        jedis.zadd ("name", 11, "A");
        jedis.zadd ("name", 22, "B");
        jedis.zadd ("name", 33, "C");
        jedis.zadd ("name", 44, "D");
        jedis.zadd ("name", 55, "E");
        jedis.zadd ("name", 66, "F");
        //Double all = jedis.zscore ("name", "A");
        //Long all = jedis.zcard ("name");
        //System.out.println (all);
        //jedis.zrem ("name", "A");
        //System.out.println (jedis.zrange ("name", 0, -1));
        //System.out.println (jedis.zrevrange ("name", 0, -1));
        jedis.zincrby ("name", 10, "A");
        jedis.zrem ("name", "C");
        jedis.close ();
    }

    /*
     * jedis连接池的基本使用
	 */
    @Test
    public void jedisPoolOfTest() {
        JedisPoolConfig poolConfig = new JedisPoolConfig ();
        JedisPool jedisPool = new JedisPool (poolConfig, "192.168.72.142", 6379);
        Jedis jedis = jedisPool.getResource ();
        String ping = jedis.ping ();
        System.out.println (ping);
        jedis.close ();
    }
}
