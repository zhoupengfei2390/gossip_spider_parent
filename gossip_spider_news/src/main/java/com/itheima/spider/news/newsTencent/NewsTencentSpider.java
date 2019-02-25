package com.itheima.spider.news.newsTencent;

import com.google.gson.Gson;
import com.itheima.spider.news.constant.SpiderConstant;
import com.itheima.spider.news.dao.NewsDao;
import com.itheima.spider.news.news.News;
import com.itheima.spider.news.utils.HttpClientUtils;
import com.itheima.spider.news.utils.IdWorker;
import com.itheima.spider.news.utils.JedisUtils;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
腾讯娱乐新闻爬虫
 */
public class NewsTencentSpider {
    /*
    生成ID
     */
    private static IdWorker idWorker = new IdWorker (0, 0);

    /*
    dao层对象
     */
    private static NewsDao newsDao = new NewsDao ();

    /*
    转成json
     */
    public static Gson gson = new Gson ();

    public static void main(String[] args) throws IOException {
        //1.确定要爬取的新闻热点 非新闻热点
        String hotUrl = "https://pacaio.match.qq.com/irs/rcd?cid=137&token=d0f13d594edfc180f5bf6b845456f3ea&id=&ext=ent&num=60";

        String nohotUrl = "https://pacaio.match.qq.com/irs/rcd?cid=108&token=349ee24cdf9327a050ddad8c166bd3e3";

        //2.发送请求,获取新闻的json数据
       /* String hotJson = HttpClientUtils.doGet (hotUrl);
        String nohotJson = HttpClientUtils.doGet (nohotUrl);
        System.out.println ("热点新闻:" + hotJson);
        System.out.println ("非热点新闻:" + nohotJson);*/

        //3.解析json数据   List<News>
        /*List<News> hotlist = parseNewsJson (hotJson);
        List<News> nohotlist = parseNewsJson (nohotJson);*/

        //分页查询
        pageTencent (hotUrl, nohotUrl);
    }

    /**
     * 腾讯娱乐新闻的分页查询
     *
     * @param hotUrl
     * @param nohotUrl
     */
    private static void pageTencent(String hotUrl, String nohotUrl) throws IOException {

        //1.先处理热点新闻数据(一页)
        String hotJson = HttpClientUtils.doGet (hotUrl);
        List<News> hotNewsList = parseJsonNews (hotJson);
        saveNewsToDb (hotNewsList);

        //2.非热点新闻数据需要处理分页
        int page = 1;
        while (true) {
            String nohotJson = HttpClientUtils.doGet (nohotUrl);

            List<News> nohotNewsList = parseJsonNews (nohotJson);

            //跳出循环的逻辑:没有json数据的时候
            if (nohotNewsList == null || nohotNewsList.size () == 0) {
                break;
            }

            saveNewsToDb (nohotNewsList);

            //构造下一项的url地址
            nohotUrl="https://pacaio.match.qq.com/irs/rcd?cid=146&token=49cbb2154853ef1a74ff4e53723372ce&ext=ent&page="+page;
            page++;
        }

    }

    /**
     * 保存新闻列表数据到数据库中
     *
     * @param newsList
     */
    private static void saveNewsToDb(List<News> newsList) {
        for (News news : newsList) {
            newsDao.saveNews (news);

            //将爬取过的url保存到redis的set集合中
            Jedis jedis = JedisUtils.getJedis ();

            jedis.sadd (SpiderConstant.SPIDER_NEWS_TENCENT, news.getUrl ());
            jedis.close ();
        }
    }

    /**
     * 解析json数据,返回新闻列表
     *
     * @param jsonNews
     * @return newsList
     */
    private static List<News> parseJsonNews(String jsonNews) {
        ArrayList<News> newsList = new ArrayList<News> ();

        //1.jsonNews   >>>>  Map<String,Object>
        Map<String, Object> map = gson.fromJson (jsonNews, Map.class);

        //2.获取新闻列表数据
        List<Map<String, Object>> data = (List<Map<String, Object>>) map.get ("data");
        for (Map<String, Object> news : data) {

            News newsBean = new News ();
            //获取url
            String docUrl = (String) news.get ("vurl");

            //进行过滤,如果是视频,排除掉
            if (docUrl.contains ("viedo")) {
                continue;
            }

            //判断url是否已经爬取过
            boolean hasParsed = isParseDocUrl (docUrl);
            if (hasParsed) {
                continue;
            }
            System.out.println ("url:" + docUrl);

            //标题
            String title = (String) news.get ("title");
            System.out.println ("标题:" + title);

            //时间
            String time = (String) news.get ("update_time");
            System.out.println ("时间:" + time);

            //来源
            String source = (String) news.get ("source");

            //来源==编辑

            //内容
            String content = (String) news.get ("intro");

            //id
            String id = idWorker.nextId () + "";

            newsBean.setId (id);
            newsBean.setTitle (title);
            newsBean.setSource (source);
            newsBean.setTime (time);
            newsBean.setContent (content);
            newsBean.setEditor (source);
            newsBean.setUrl (docUrl);

            newsList.add (newsBean);

        }
        //返回
        return newsList;
    }

    /**
     * 判断给定的url是否已经爬取过
     *
     * @param docUrl
     * @return sismember
     */
    private static boolean isParseDocUrl(String docUrl) {
        //1.获取jedis连接
        Jedis jedis = JedisUtils.getJedis ();

        //2.判断是否已经在set集合中
        Boolean sismember = jedis.sismember (SpiderConstant.SPIDER_NEWS_TENCENT, docUrl);

        //3.关闭
        jedis.close ();

        //4.返回
        return sismember;
    }

}
