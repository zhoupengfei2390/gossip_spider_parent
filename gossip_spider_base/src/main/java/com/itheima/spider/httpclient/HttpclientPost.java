package com.itheima.spider.httpclient;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpclientPost {
    public static void main(String[] args) throws IOException {
        //1.确定爬取的url
        String domain = "http://www.itcast.cn";
        //2.创建httpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault ();
        //3.创建一个HttpPost对象
        HttpPost httpPost = new HttpPost (domain);
        //3.1.设置参数:请求头 请求参数(请求体)
        httpPost.setHeader ("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36");
        httpPost.setHeader ("Host", "www.itcast.cn");
        httpPost.setHeader ("Cookie", "UM_distinctid=16910205ff66e0-074117d4bf5f-5b402d18-100200-16910205ff780c; bad_id22bdcd10-6250-11e8-917f-9fb8db4dc43c=0748a441-35d8-11e9-9a95-e726cc7fecda; bad_idb2f10070-624e-11e8-917f-9fb8db4dc43c=fa128e41-36a3-11e9-b431-a171bc4f94fc; CNZZDATA4617777=cnzz_eid%3D1365224440-1550752364-%26ntime%3D1550883621; qimo_seosource_22bdcd10-6250-11e8-917f-9fb8db4dc43c=%E7%AB%99%E5%86%85; qimo_seokeywords_22bdcd10-6250-11e8-917f-9fb8db4dc43c=; href=http%3A%2F%2Fwww.itcast.cn%2F; nice_id22bdcd10-6250-11e8-917f-9fb8db4dc43c=306e0881-3711-11e9-a0c5-9766216bb6aa; Hm_lvt_0cb375a2e834821b74efffa6c71ee607=1550841976,1550842144,1550888257,1550888398; qimo_seosource_b2f10070-624e-11e8-917f-9fb8db4dc43c=%E7%99%BE%E5%BA%A6%E6%90%9C%E7%B4%A2; qimo_seokeywords_b2f10070-624e-11e8-917f-9fb8db4dc43c=%25E4%25BC%25A0%25E6%2599%25BA; nice_idb2f10070-624e-11e8-917f-9fb8db4dc43c=845c3d41-3711-11e9-9a95-e726cc7fecda; openChatb2f10070-624e-11e8-917f-9fb8db4dc43c=true; parent_qimo_sid_b2f10070-624e-11e8-917f-9fb8db4dc43c=881c7990-3711-11e9-bc2a-21726d547278; Hm_lpvt_0cb375a2e834821b74efffa6c71ee607=1550888414; accessId=22bdcd10-6250-11e8-917f-9fb8db4dc43c; pageViewNum=10");
        //3.2 设置请求参数
        List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair> ();
        //封装参数
        list.add (new BasicNameValuePair ("username", "张三"));
        list.add (new BasicNameValuePair ("age", "33"));
        list.add (new BasicNameValuePair ("address", "西三旗"));
        list.add (new BasicNameValuePair ("sex", "2"));
        list.add (new BasicNameValuePair ("password", "123456"));

        HttpEntity entity = new UrlEncodedFormEntity (list, "utf-8");
        httpPost.setEntity (entity);
        //4.发送请求,获取响应对象
        CloseableHttpResponse response = httpClient.execute (httpPost);
        //5.获取响应的数据
        StatusLine statusLine = response.getStatusLine ();
        int statusCode = statusLine.getStatusCode ();
        if (statusCode == 200) {
            //获取响应头
            Header[] allHeaders = response.getAllHeaders ();
            for (Header header : allHeaders) {
                System.out.println ("响应头:" + header.getName () + "===" + header.getValue ());
            }
            //获取响应体
            HttpEntity getResponseEntity = response.getEntity ();
            String html = EntityUtils.toString (getResponseEntity, "utf-8");
            System.out.println ("--------------------------");
            System.out.println (html);
        }
        //6.关闭资源
        httpClient.close ();
    }
}
