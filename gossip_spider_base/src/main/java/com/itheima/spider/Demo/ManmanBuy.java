package com.itheima.spider.Demo;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManmanBuy {
    public static void main(String[] args) throws IOException {

        //1.确定模拟器登录的url
        String loginUrl = "http://home.manmanbuy.com/login.aspx?tourl=http%3a%2f%2fhome.manmanbuy.com%2fusercenter.aspx";
        //2.创建httpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault ();
        //3.创建发HttpPost请求
        HttpPost httpPost = new HttpPost (loginUrl);
        //4.设置参数:请求头 请求体 :登录表单的数据
        httpPost.setHeader ("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
        httpPost.setHeader ("Referer", "http://home.manmanbuy.com");

        List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair> ();
        list.add (new BasicNameValuePair ("__VIEWSTATE", "/wEPDwULLTIwNjQ3Mzk2NDEPZBYCAgUPZBYCAgQPFgIeCWlubmVyaHRtbAVwPGJyIC8+PGltZyBzcmM9Ii8vbWlzYy5tYW5tYW5idXkuY29tL2ltYWdlcy91c2VyL0xvZ2luRXJyb3IucG5nIiBhbGlnbj0iYWJzbWlkZGxlIiA+IOeUqOaIt+WQjeaIluWvhueggemUmeivr++8gWQYAQUeX19Db250cm9sc1JlcXVpcmVQb3N0QmFja0tleV9fFgEFCWF1dG9Mb2dpbgBh3BDzC3Bm3zXM9Wiz5r3vnk8x"));
        list.add (new BasicNameValuePair ("__EVENTVALIDATION", "/wEWBQLWtMXXDwLB2tiHDgLKw6LdBQKWuuO2AgKC3IeGDLdh5P2IcOaCPXBowF3xw8rfEhx+"));
        list.add (new BasicNameValuePair ("txtUser", "itcast"));
        list.add (new BasicNameValuePair ("txtPass", "www.itcast.cn"));
        list.add (new BasicNameValuePair ("btnLogin", "登陆"));
        HttpEntity entity = new UrlEncodedFormEntity (list, "utf-8");
        httpPost.setEntity (entity);
        //5.发送请求,获取响应
        CloseableHttpResponse response = httpClient.execute (httpPost);
        String s = EntityUtils.toString (response.getEntity (), "utf-8");
        System.out.println (s);
        StatusLine statusLine = response.getStatusLine ();
        int statusCode = statusLine.getStatusCode ();
        if (statusCode == 302) {
            //获取重定向的地址及登录的cookie信息
            Header[] locations = response.getHeaders ("Location");
            String location = locations[0].getValue ();
            Header[] cookies = response.getHeaders ("Set-Cookie");
            //发送新的请求xie'dai
            HttpGet httpGet = new HttpGet (location);
            httpGet.setHeader ("Referer", "http://home.manmanbuy.com/login.aspx?tourl=http%3a%2f%2fhome.manmanbuy.com%2fusercenter.aspx");
            httpGet.setHeader ("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
            //httpGet.setHeader (cookies);
            CloseableHttpResponse redirectResponse = httpClient.execute (httpGet);
            HttpEntity redEntity = redirectResponse.getEntity ();
            //获取html页面
            String html = EntityUtils.toString (redEntity, "utf-8");
            System.out.println (html);
            //6.解析页面,获取积分
            Document document = Jsoup.parse (html);
            Elements font = document.select ("#aspnetForm > div.udivright > div:nth-child(2) > table > tbody > tr > td:nth-child(1) > table:nth-child(2) > tbody > tr > td:nth-child(2) > div:nth-child(1) > font");
            System.out.println ("我的积分:" + font.text ());
        }
        //7.释放资源
        httpClient.close ();

    }

}
