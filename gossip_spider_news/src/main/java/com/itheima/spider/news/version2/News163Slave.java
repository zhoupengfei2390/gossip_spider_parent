package com.itheima.spider.news.version2;

import com.google.gson.Gson;
import com.itheima.spider.news.constant.SpiderConstant;
import com.itheima.spider.news.news.News;
import com.itheima.spider.news.utils.HttpClientUtils;
import com.itheima.spider.news.utils.IdWorker;
import com.itheima.spider.news.utils.JedisUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.List;

/*
获取要爬取的url(redis),发送请求，获取html页面，解析html页面成News对象，保存News对象(json字符串)到redis的list集合中
 */
public class News163Slave {
    /*
    分布式id生成器:0~31之间的参数
     */
    private static IdWorker idWorker = new IdWorker (0, 0);
    /*
    对象和json转换
     */
    private static Gson gson = new Gson ();

    public static void main(String[] args) throws IOException {
        while (true) {
            //1.获取要爬取的url(redis)
            Jedis jedis = JedisUtils.getJedis ();
            List<String> urlList = jedis.brpop (20, SpiderConstant.SPIDER_NEWS_URLLIST);
            jedis.close ();

            //跳出循环的条件
            if (urlList == null || urlList.size () <= 0) {
                break;
            }
            String url = urlList.get (1);

            //2.发送请求,获取html页面,解析html页面成News对象
            News news = parseItemNews (url);

            //3.保存News对象(json字符串)到redis的list集合中
            String newsJson = gson.toJson (news);
            jedis = JedisUtils.getJedis ();
            jedis.lpush (SpiderConstant.SPIDER_NEWS_NEWJSONLIST, newsJson);
            jedis.close ();
        }
    }

    /*
      解析新闻列表中的一条新闻的方法
       */
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

}
