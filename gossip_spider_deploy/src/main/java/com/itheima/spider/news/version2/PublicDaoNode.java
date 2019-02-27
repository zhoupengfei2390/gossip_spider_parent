package com.itheima.spider.news.version2;

import com.google.gson.Gson;
import com.itheima.spider.news.constant.SpiderConstant;
import com.itheima.spider.news.dao.NewsDao;
import com.itheima.spider.news.news.News;
import com.itheima.spider.news.utils.JedisUtils;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * 读取redis中的News对象，判断这个url是否已经爬取过，
 * 将这个News对象保存到mysql数据库中，还需要将这个url保存到set集合中
 */

public class PublicDaoNode {
    /**
     * json转换成gson
     */
    private static Gson gson = new Gson ();

    /**
     * dao
     */
    private static NewsDao newsDao = new NewsDao ();

    public static void main(String[] args) {
        while (true) {
            //1.读取redis中的News对象
            Jedis jedis = JedisUtils.getJedis ();
            List<String> jsonList = jedis.brpop (20, SpiderConstant.SPIDER_NEWS_NEWJSONLIST);
            jedis.close ();

            //跳出循环
            if (jsonList == null || jsonList.size () == 0) {
                break;
            }

            //2.判断是否已经爬取过
            String newsJson = jsonList.get (1);
            News news = gson.fromJson (newsJson, News.class);
            boolean hasParsed = isParsedUrl (news.getUrl ());
            if (hasParsed) {
                continue;
            }
            //3.将News对象保存在mysql数据库中
            newsDao.saveNews (news);

            //4.将这个新闻的url保存到redis的集合中
            jedis = JedisUtils.getJedis ();
            jedis.sadd (SpiderConstant.SPIDER_NEWS_URLSET, news.getUrl ());
            jedis.close ();
        }
    }

    /**
     * 判断这个url是否已经爬取过： 判断这个url是否在redis的set集合中
     *
     * @param docurl
     * @return sismember
     */
    private static boolean isParsedUrl(String docurl) {
        //1. 获取连接
        Jedis jedis = JedisUtils.getJedis ();

        //2. 判断
        Boolean sismember = jedis.sismember (SpiderConstant.SPIDER_NEWS_URLSET, docurl);

        //3. 关闭
        jedis.close ();

        //4. 返回
        return sismember;
    }
}
