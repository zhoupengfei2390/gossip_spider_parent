package com.itheima.spider.news.version2;

import com.google.gson.Gson;
import com.itheima.spider.news.constant.SpiderConstant;
import com.itheima.spider.news.utils.HttpClientUtils;
import com.itheima.spider.news.utils.JedisUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 获取新闻列表的json数据,判断是否已经爬取过,处理下一页的url
 */
public class News163Master {

    public static void main(String[] args) throws Exception {

        //1.确定爬取的url
        String indexUrl = "http://ent.163.com/special/000380VU/newsdata_index.js";

        //2. 遍历新闻的列表数据，针对每一条新闻，再次发送请求，获取新闻的详情页的html页面
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
     * @param jsonNews
     */

    private static void parseJsonNews(String jsonNews) throws IOException {
        //1.处理json数据
        String newsJson = getJsonString (jsonNews);
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

            //将解析出来的新闻url,存放到redis的list集合中:bigData:spider:urlList(新增的代码重点)
            Jedis jedis = JedisUtils.getJedis ();
            jedis.lpush (SpiderConstant.SPIDER_NEWS_URLLIST, docurl);
            jedis.close ();

        }
    }

    /**
     * 处理json的方法
     *
     * @param newsJosn
     * @return newsJosn
     */
    private static String getJsonString(String newsJosn) {
        int beginIndex = newsJosn.indexOf ("(");
        int endIndex = newsJosn.lastIndexOf (")");
        newsJosn = newsJosn.substring (beginIndex + 1, endIndex);
        return newsJosn;
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
        Boolean sismember = jedis.sismember (SpiderConstant.SPIDER_NEWS_URLSET, docrul);

        //关源
        jedis.close ();

        //返回
        return sismember;
    }
}
