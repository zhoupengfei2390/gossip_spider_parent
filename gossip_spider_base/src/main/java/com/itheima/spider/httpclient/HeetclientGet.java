package com.itheima.spider.httpclient;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class HeetclientGet {
    public static void main(String[] args) throws IOException {
        //1.确定url
        String domain = "http://www.itcast.cn?username=zhangsan&age=30";
        //2.发送请求,获取数据
        //2.1创建一个httpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault ();
        //2.2设置请求方式:请求对象
        HttpGet httpGet = new HttpGet (domain);
        //2.3设置请求头信息
        httpGet.setHeader ("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36");
        httpGet.setHeader ("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        //2.4设置请求参数
        //2.5发送请求,获取响应对象
        CloseableHttpResponse response = httpClient.execute (httpGet);
        //3.获取响应:响应行 响应头响应体


    }
}

