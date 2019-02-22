package com.itheima.spider.jdk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JdkPost {
    public static void main(String[] args) throws Exception {
        //1.确定爬取的url(不能丢失http://)
        String domain = "http://www.itcast.cn";
        //2.创建url对象,开启连接
        URL url = new URL (domain);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection ();
        //2.1设置请求方式:POST
        urlConnection.setRequestMethod ("POST");
        //2.2开启输出模式
        urlConnection.setDoOutput (true);
        //3.传递参数
        OutputStream out = urlConnection.getOutputStream ();
        String params = "username=zhangsan&age=12&address=xxx";
        out.write (params.getBytes ());
        //刷新缓存
        out.flush ();
        out.close ();
        //4.获取响应数据(尝试使用字符流读取)
        InputStream in = urlConnection.getInputStream ();
        //4.1使用字符流的方式读取
        InputStreamReader inputStreamReader = new InputStreamReader (in);
        BufferedReader bufferedReader = new BufferedReader (inputStreamReader);
        //通过字符流打印网页数据
        String line = null;
        while ((line = bufferedReader.readLine ()) != null) {
            System.out.println (line);
        }
        //5.释放资源
        bufferedReader.close ();
        inputStreamReader.close ();
        in.close ();
    }
}
