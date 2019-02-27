package com.itheima.spider.news.version2;

import com.google.gson.Gson;
import com.itheima.spider.news.constant.SpiderConstant;
import com.itheima.spider.news.news.News;
import com.itheima.spider.news.utils.HttpClientUtils;
import com.itheima.spider.news.utils.IdWorker;
import com.itheima.spider.news.utils.JedisUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.List;

/**
 * 获取要爬取的url(redis),发送请求,回去html页面,解析html页面成News对象,保存News对象
 */
public class News163Slave {
    /**
     * id生成器
     */
    private static IdWorker idWorker = new IdWorker (1, 0);

    /**
     * 对象和json的转化
     */
    private static Gson gson = new Gson ();

    public static void main(String[] args) throws IOException {
        while (true) {
            //1.获取要爬取的url(redis)
            Jedis jedis = JedisUtils.getJedis ();

            List<String> urlList = jedis.brpop (20, SpiderConstant.SPIDER_NEWS_URLLIST);
            jedis.close ();

            //跳出循环
            if (urlList == null || urlList.size () <= 0) {
                break;
            }
            //获取1   0是key
            String docUrl = urlList.get (1);
            News news = parseItemNews (docUrl);

            //3.将解析出来的News对象保存到redis的list集合中
            jedis = JedisUtils.getJedis ();
            jedis.lpush (SpiderConstant.SPIDER_NEWS_NEWJSONLIST, gson.toJson (news));
            jedis.close ();
        }
    }

    /**
     * 解析每一条新闻的url  -----    新闻的详情页数据
     *
     * @param docul
     * @return news
     * @throws IOException
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
