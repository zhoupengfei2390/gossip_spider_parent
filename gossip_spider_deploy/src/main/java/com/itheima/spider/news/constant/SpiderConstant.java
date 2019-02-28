package com.itheima.spider.news.constant;

/**
 * @author itheima
 * @Title: SpiderConstant
 * @ProjectName gossip_spider_parent
 * @Description: 爬虫项目的常量类
 * @date 2019/2/2411:33
 */
public class SpiderConstant {

    /**
     * 163新闻爬虫：存放已经爬取的url的set集合的key
     */
    public static final String SPIDER_NEWS163 = "bigData:spider:163news:docurl";

    /**
     * 腾讯娱乐新闻爬虫：存放已经爬取的url的set集合的大key
     */
    public static final String SPIDER_NEWS_TENCENT = "bigData:spider:tencentnews:docurl";

    /**
     * 163和腾讯爬虫共用的set集合：判断url是否重复爬取的set集合的key
     */
    public static final String SPIDER_NEWS_URLSET = "bigData:spider:urlSet";

    /**
     * 163爬虫爬取的url存放在redis的list数据结构中的大key
     */
    public static final String SPIDER_NEWS_URLLIST = "bigData:spider:urlList";

    /**
     * 163和腾讯爬虫共用的list集合: 存放解析后的News对象的大key
     */
    public static final String SPIDER_NEWS_NEWJSONLIST = "bigData:spider:newsJsonList";

}
