package com.itheima.spider.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

public class JsoupDocument {
    /*
    如何将获取到的html页面转换成document
     */
    @Test
    public void documentParse() {
        //1.调用httpClient,获取网页的html文档
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>jsoup转换网页成document对象的页面演示</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "    <a id=\"aaa\">链接</a>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
        //2.调用jsoup换成document
        Document document = Jsoup.parse (html);
        //3.获取想要的内容
        String title = document.title ();
        Element aaa = document.getElementById ("aaa");
        System.out.println (aaa.text ());
        System.out.println (title);
    }

    /*
    connent发送请求获取document对象
     */
    @Test
    public void textJsoupConnect() throws IOException {
        //1.确定要爬取的url
        String qidian = "https://www.qidian.com/";
        //2.使用jsoup,发送请求,获取document对象
        Document document = Jsoup.connect (qidian).get ();
        //3.获取我们想要的数据
        String title = document.title ();
        System.out.println (title);
    }

    /**
     * 解析html的片段（了解）
     */
    @Test
    public void testJsoupBodyFragment() {
        String htmlFragment = "<a href='http://manmanbuy.com'>慢慢买</a>";
        Document document = Jsoup.parseBodyFragment (htmlFragment);
        Elements a = document.getElementsByTag ("a");
        String text = a.text ();
        String href = a.attr ("href");
        System.out.println (text + "-----" + href);
    }

}
