package com.itheima.spider.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

public class JsoupSelector {
    /*
    获取起点中文网:签约作家新书榜
     */
    @Test
    public void testQidian() throws IOException {
        //1.确定爬取的url
        String url = "http://www.biquyun.com/";
        //2.获取html文档(jsoup.conect())
        Document document = Jsoup.connect (url).get ();
        //3.解析document,获取想要的数据(原生,选择器)
        //参数:选择器
        Elements li = document.select ("[class=rank-list mr0] li");
        for (Element element : li) {
            System.out.println (element.toString ());
            System.out.println ("-----------------------");
        }
    }

    /*
    使用原生方案获取
     */
    @Test
    public void testItcast() throws IOException {

        String url = "http://www.itcast.cn/";
        Document document = Jsoup.connect (url).get ();
        //不需要加点
        Elements nav_txt = document.getElementsByClass ("nav_txt");
        Element div = nav_txt.get (0);
        Elements lis = div.getElementsByTag ("li");
        for (Element li : lis) {
            String a = li.getElementsByTag ("a").text ();
            System.out.println (a);
        }
    }

    @Test
    public void testSelectorItcast() throws IOException {
        String url = "http://www.itcast.cn";
        Document document = Jsoup.connect (url).get ();
        Elements select = document.select (".nav_txt li a");
        for (Element element : select) {
            System.out.println (element.text () + "------" + element.attr ("href"));

        }
    }
}
