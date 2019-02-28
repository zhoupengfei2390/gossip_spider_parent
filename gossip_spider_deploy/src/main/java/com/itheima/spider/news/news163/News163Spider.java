package com.itheima.spider.news.news163;

import com.google.gson.Gson;
import com.itheima.spider.news.constant.SpiderConstant;
import com.itheima.spider.news.dao.NewsDao;
import com.itheima.spider.news.news.News;
import com.itheima.spider.news.utils.HttpClientUtils;
import com.itheima.spider.news.utils.IdWorker;
import com.itheima.spider.news.utils.JedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class News163Spider {
    /*
    分布式id生成器:0~31之间的参数
     */
    private static IdWorker idWorker = new IdWorker (0, 0);

    /*
    新闻dao
     */
    private static NewsDao newsDao = new NewsDao ();

    public static void main(String[] args) throws Exception {
        //1.确定爬取的url
        String indexUrl = "http://ent.163.com/special/000380VU/newsdata_index.js";

        //2.发送请求,获取新闻的列表信息
        //String newsJson = HttpClientUtils.doGet (indexUrl);

        //3.解析新闻列表数据
        //parseJsonNews (newsJson);

        //3. 遍历新闻的列表数据，针对每一条新闻，再次发送请求，获取新闻的详情页的html页面
        page163 (indexUrl);

    }

    /**
     * 分页爬取的方法
     *
     * @param indexUrl
     */
    private static void page163(String indexUrl) throws Exception {
        int page = 2;
        while (true) {
            //1.发送请求,获取新闻的列表数据
            String newsjson = HttpClientUtils.doGet (indexUrl);

            //没有数据跳出循环
            if (StringUtils.isEmpty (newsjson)) {
                break;
            }

            //2.解析新闻的json数据
            parseJsonNews (newsjson);

            //3. 构造下一页的url数据
            String pageString = "";
            if (page < 10) {
                pageString = "0" + page;
            } else {
                pageString = page + "";
            }

            indexUrl = "http://ent.163.com/special/000380VU/newsdata_index_" + pageString + ".js";
            System.out.println (indexUrl);
            page++;
        }
    }

    /**
     * 解析新闻列表的json数据
     *
     * @param newsJson
     */

    private static void parseJsonNews(String newsJson) throws IOException {
        //1.处理json数据
        int beginIndex = newsJson.indexOf ("(");
        int endIndex = newsJson.lastIndexOf (")");
        newsJson = newsJson.substring (beginIndex + 1, endIndex);

        //2.解析json数据
        Gson gson = new Gson ();
        List<Map<String, Object>> jsonList = gson.fromJson (newsJson, List.class);

        for (Map<String, Object> newsMap : jsonList) {
            //获取url
            String docurl = (String) newsMap.get ("docurl");
            //过滤图集新闻
            if (docurl.contains ("photoview")) {
                continue;
            }

            //判断是否已经爬取
            boolean hasParsed = isParseUrl (docurl);
            if (hasParsed) {
                //已经爬取过
                continue;
            }
            System.out.println ("新闻的url:" + docurl);

            //3.根据新闻的docul,获取新闻的详情页数据(html页面)
            News news = parseItemNews (docurl);

            //4. 保存新闻数据到mysql数据库
            newsDao.saveNews (news);

            //5.将爬取过的url存放到redis数据库中:set数据结构
            saveDocUrlToRedis (docurl);
        }
    }

    /**
     * 判断是否爬取过
     *
     * @param docrul
     * @return sismember
     */

    private static boolean isParseUrl(String docrul) {
        //通过工具类创造jedis
        Jedis jedis = JedisUtils.getJedis ();

        //判断是否redis是否有url
        Boolean sismember = jedis.sismember (SpiderConstant.SPIDER_NEWS163, docrul);

        //关源
        jedis.close ();

        //返回
        return sismember;
    }

    /**
     * 将已经爬取到的url保存到redis数据库中
     *
     * @param docul
     */
    private static void saveDocUrlToRedis(String docul) {
        //1.获取redis连接
        Jedis jedis = JedisUtils.getJedis ();

        //2.存放数据到set集合中
        jedis.sadd (SpiderConstant.SPIDER_NEWS163, docul);

        //3.关闭
        jedis.close ();
    }

    /**
     * 解析每一条新闻的url   新闻的详情页数据
     *
     * @param docul
     * @return news
     */
    private static News parseItemNews(String docul) throws IOException {

        News news = new News ();
        //1.根据url,发送请求,获取html页面
        String newsHtml = HttpClientUtils.doGet (docul);

        //2.使用json进行解析html页面---------->封装成一个javabean对象News
        Document document = Jsoup.parse (newsHtml);

        //4.解析document对象 id 标题 来源 时间 编辑 url 内容
        //标题
        String title = document.select ("#epContentLeft h1").text ();
        System.out.println ("标题:" + title);

        //时间和来源
        String timeAndSource = document.select (".post_time_source").text ();
        String time = null;
        String source = null;
        if (timeAndSource != null && timeAndSource.contains ("　来源: ")) {
            String[] timeAndSourceString = timeAndSource.split ("　来源: ");
            time = timeAndSourceString[0];
            source = timeAndSourceString[1];
            System.out.println ("时间:" + time + "来源:" + source);
        }


        //内容
        String content = document.select ("#endText p").text ();

        //Elements Elements = document.select ("#post_text p");
        //String content = null;
        /*for (Element element : Elements) {
            //判断是否有图片
            if (element.getElementsByTag ("src") != null || element.getElementsByTag ("src").size () > 0) {
                continue;
            }
            content = content + "/r/n" + element.text ();
        }*/

        System.out.println ("内容:" + content);

        //编辑
        String text = document.select (".ep-editor").text ();
        String editor = null;
        if (text != null && text.contains ("：")) {
            String[] editors = text.split ("：");
            editor = editors[1];
        }
        System.out.println ("编辑:" + editor);

        //id
        String id = idWorker.nextId () + "";

        //给news加入参数
        news.setId (id);
        news.setUrl (docul);
        news.setEditor (editor);
        news.setContent (content);
        news.setTime (time);
        news.setTitle (title);
        news.setSource (source);

        return news;
    }

}
        /*//1.确定爬取新闻列表的url:http://ent.163.com/special/000380VU/newsdata_index.js
        String url = "http://ent.163.com/special/000380VU/newsdata_index.js";

        //2.调用HttpClientUtils工具,获取新闻列表json数据
        String jsonNews = HttpClientUtils.doGet (url);

        //3.解析新闻数据
        parseJsonNews (jsonNews);
    }
    *//*
    解析新闻数据
     *//*

    private static void parseJsonNews(String jsonNews) throws IOException {
        //1.处理json字符串,转换成格式良好的json数组
        jsonNews = getJsonString (jsonNews);
        //System.out.println (jsonNews);

        //2.遍历json数组:json字符串----->集合对象
        Gson gson = new Gson ();
        List<Map<String, Object>> newsJson = gson.fromJson (jsonNews, List.class);
        for (Map<String, Object> newsString : newsJson) {
            String docurl = (String) newsString.get ("docurl");
            //过滤图集的url
            if (docurl.contains ("photoview")) {
                continue;
            }
            System.out.println ("新闻的url:" + docurl);

            //3.解析一条新闻的url--News
            News news = parseItemNews (docurl);
            System.out.println (news);

            //4.保存新闻数据
            newsDao.saveNews (news);

            //将爬取的新闻url保存到redis的set集合中
            saveDocUrlToRedis (docurl);

        }
    }

    *//*
    将爬取的新闻url保存到redis的set集合中
     *//*
    private static void saveDocUrlToRedis(String docurl) {
        Jedis jedis = JedisUtils.getJedis ();
        //将给定的url保存保redis的set集合中
        jedis.sadd (SpiderConstant.SPIDER_NEWS163, docurl);

        //用完释放
        jedis.close ();
    }

      *//*
      解析新闻列表中的一条新闻的方法
       *//*
    private static News parseItemNews(String docurl) throws IOException {

        News news = new News ();

        //1.获取每一条新闻数据的url

        //2.获取每一条新闻数据的html页面
        String newsHtml = HttpClientUtils.doGet (docurl);

        //3.转换成document对象
        Document document = Jsoup.parse (newsHtml);

        //4.解析document对象 id 标题 来源 时间 编辑 url 内容
        //标题
        String title = document.select ("#epContentLeft h1").text ();
        //System.out.println ("标题" + title);

        //时间和来源
        String timeAndSource = document.select (".post_time_source").text ();
        String time = null;
        String source = null;
        if (timeAndSource != null && timeAndSource.contains ("　来源: ")) {
            String[] timeAndSourceString = timeAndSource.split ("　来源: ");
            time = timeAndSourceString[0];
            source = timeAndSourceString[1];
        }

        //System.out.println ("时间:"+timeAndSourceString[0] + "   来源:" + timeAndSourceString[1]);

        //内容
        Elements Elements = document.select ("#post_text p");
        String content = null;
        for (Element element : Elements) {
            //判断是否有图片
            if (element.getElementsByTag ("src") != null || element.getElementsByTag ("src").size () > 0) {
                continue;
            }
            content = content + "/r/n" + element.text ();
        }

        //编辑
        String text = document.select (".ep-editor").text ();
        String editor = null;
        if (text != null && text.contains ("：")) {
            String[] editors = text.split ("：");
            editor = editors[1];
        }

        //id
        String id = idWorker.nextId () + "";

        //给news加入参数
        news.setId (id);
        news.setUrl (docurl);
        news.setEditor (editor);
        news.setContent (content);
        news.setTime (time);
        news.setTitle (title);
        news.setSource (source);

        return news;
    }
    *//*

    处理json的方法
     *//*
    private static String getJsonString(String jsonNews) {
        int startIndex = jsonNews.indexOf ("(");
        int lastIndexOf = jsonNews.lastIndexOf (")");
        jsonNews = jsonNews.substring (startIndex + 1, lastIndexOf);
        return jsonNews;
    }
}
*/
