package com.itheima.spider.Demo;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Qidain {

    public static void main(String[] args) throws Exception {
        //开始界面的url地址
        String start = "https://www.hongxiu.com/rank";

        //调用document方法
        Document document1 = getDocument (start);
        Elements elements1 = document1.select (".book-rank-list ul:eq(0)");
        int count1 = 0;
        while (true) {
            //System.out.println (elements.size ());
            count1++;
            Element element = elements1.get (count1);

            Elements a = element.select ("a");
            start = "http:" + a.attr ("href");

            //在开始界面获取第一本的

            //1.确定爬取的url
            String hongxiu = start;
            Document document2 = getDocument (hongxiu);

            Elements select = document2.select ("[class=border-btn J-getJumpUrl ]");
            System.out.println (select.html ());
            String href = select.attr ("href");
            String hongxiu2 = "http:" + href;
            int count = 0;
            count++;
            System.out.println (count);

            while (true) {
                //创建httpClient对象,发送请求
                Document document = getDocument (hongxiu2);
                // System.out.println (document);
                //获取章节
                Elements zj = document.select (".j_chapterName");
                //获取下一章
                Elements nextUrl = document.select ("#j_chapterNext");
                if (nextUrl.size () == 0) {
                    return;
                }
                System.out.println ("章节名称:" + zj.text ());
                //获得内容,并根据p分行
                Elements elements = document.select ("[class=read-content j_readContent] p");
                //根据p遍历打印
                //System.out.println (elements.size ());

               /* for (Element element : elements) {
                    //最后一行不打印
                    System.out.println (element.text ());
                }*/
                String href1 = nextUrl.attr ("href");
                hongxiu2 = "http:" + href1;
            }

        }

    }

    /*
    根据url获取document对象
     */
    public static Document getDocument(String url) throws IOException {
        //创建httpClient对象,发送请求
        CloseableHttpClient httpClient = HttpClients.createDefault ();
        HttpGet httpGet = new HttpGet (url);
        //httpGet.setHeader ("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httpGet.setHeader ("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36");

        //httpGet.setHeader ("Cookie", "ZHID=1550914629713ZH0MKU01FOVDT6OP001; zh_visitTime=1550914629715; zhffr=www.hao123.com; Hm_lvt_c202865d524849216eea846069349eb9=1550914631; UM_distinctid=16919b73e8b76b-0e7225c01795dd-3d644701-100200-16919b73e8c468; platform=H5; PassportCaptchaId=ff03d273145e595ea799f21756f3829a; AST=1550921903223a2506ce27e; ver=2018; JSESSIONID=abcgX4WSjSnkM6o8eUzKw; rSet=1_3_1_14; CNZZDATA30037065=cnzz_eid%3D1075747876-1550909589-%26ntime%3D1550909589; v_user=%7Chttp%3A%2F%2Fbook.zongheng.com%2Fchapter%2F189169%2F3431754.html%7C59491710; sajssdk_2015_cross_new_user=1; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%2216919b84f9c35c-0e0826c54cd766-3d644701-1049088-16919b84f9ddc%22%2C%22%24device_id%22%3A%2216919b84f9c35c-0e0826c54cd766-3d644701-1049088-16919b84f9ddc%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24latest_referrer_host%22%3A%22%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%7D%7D; zh_rba=true; Hm_lpvt_c202865d524849216eea846069349eb9=1550914743");
        //httpGet.setHeader ("Accept-Encoding", "gzip, deflate");
        //获取响应,将响应体中的html字符串
        CloseableHttpResponse response = httpClient.execute (httpGet);
        //jsoup将html页面转换成document对象
        String html = EntityUtils.toString (response.getEntity (), "utf-8");
        Document document = Jsoup.parse (html);
        return document;
    }
}
