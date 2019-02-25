package com.itheima.spider.news.version2;

import com.google.gson.Gson;
import com.itheima.spider.news.constant.SpiderConstant;
import com.itheima.spider.news.news.News;
import com.itheima.spider.news.utils.HttpClientUtils;
import com.itheima.spider.news.utils.JedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class News163Master {
    public static void main(String[] args) throws IOException {
        //1.确定url
        String url = "http://ent.163.com/special/000380VU/newsdata_index.js";

        //2.分页爬取新闻列表json数据
        page163 (url);
    }

    /**
     * 分页爬取的方法
     *
     * @param indexUrl
     */
    private static void page163(String indexUrl) throws IOException {
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
            if (page < 0) {
                pageString = "0" + page;
            } else {
                pageString = page + "";
            }

            indexUrl = "http://ent.163.com/special/000380VU/newsdata_index_" + pageString + ".js";

            page++;
        }
    }

    //解析json数据
    private static void parseJsonNews(String jsonNews) {

        //1.处理json字符串 转换成格式良好的json数组
        jsonNews = getJsonString (jsonNews);

        //2.遍历json数组
        Gson gson = new Gson ();
        List<Map<String, Object>> newslist = gson.fromJson (jsonNews, List.class);

        for (Map<String, Object> newsString : newslist) {
            String docurl = (String) newsString.get ("docurl");

            //过滤图集的url
            if (docurl.contains ("photociew")) {
                //已经爬取过
                continue;
            }
            System.out.println ("新闻的url:" + docurl);

            //过滤已经爬取过的url(redis中的set集合进行判断)
            boolean hasParsed = hasParsedUrl (docurl);
            if (hasParsed) {
                //已经爬取过了
                continue;
            }

            //3.根据新闻的docul,获取新闻的详情页数据(html页面)


            //将解析出来的新闻url,存放到redis的list集合中:bigData:spider:urlList（新增的代码重点）
            Jedis jedis = JedisUtils.getJedis ();
            jedis.lpush (SpiderConstant.SPIDER_NEWS_URLLIST, docurl);
            jedis.close ();
        }

    }

    /**
     * 解析每一条新闻的url   新闻的详情页数据
     *
     * @param docurl
     */

    private static void parseItemNews(String docurl) throws IOException {
        News news = new News ();

        //1.根据url,发送请求,获取html页面
        String newsHtml = HttpClientUtils.doGet (docurl);

        //2.使用json进行解析html页面     封装成一个javaBean对象News
        Document document = Jsoup.parse (newsHtml);

        //标题
        String title = document.select ("#epContentLeft h1").text ();

    }

    /*
    处理json数据的方法
     */
    private static String getJsonString(String jsonNews) {

        int startIndex = jsonNews.indexOf ("(");
        int lastIndexOf = jsonNews.lastIndexOf (")");
        jsonNews = jsonNews.substring (startIndex + 1, lastIndexOf);
        return jsonNews;
    }

    /*
    判断给定的url是否已经爬取过
     */
    private static boolean hasParsedUrl(String docurl) {
        //获取redis连接
        Jedis jedis = JedisUtils.getJedis ();

        //判断给定的url是否已经存在在redis的set集合中
        Boolean sismember = jedis.sismember (SpiderConstant.SPIDER_NEWS_URLSET, docurl);
        jedis.close ();
        return sismember;
    }

}
