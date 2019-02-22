package com.itheima.spider.jdk;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JdkGet {
       public static void main(String[] args) throws Exception {
        //1.确定爬取的url
        String domain = "http://www.itcast.cn?username=zhangsan&sex=1";
        //2,发送请求.传递参数
        //2.1创建url对象
        URL url = new URL (domain);
        //2.2发送请求,获取连接对象
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection ();
        //2.3设置参数:请求参数和请求方式 请求头
        urlConnection.setRequestMethod ("GET");
        //3.获取响应数(流处理)
        //图片视频z字节流 html文档(字节和字符都可以)
        InputStream in = urlConnection.getInputStream ();

        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = in.read (bytes)) != -1) {
            System.out.println (new String (bytes, 0, len));
        }
        //4.释放资源
        in.close ();

    }

}

