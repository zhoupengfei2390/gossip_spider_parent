package com.itheima.spider.news.dao;

import com.itheima.spider.news.news.News;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.beans.PropertyVetoException;

public class NewsDao extends JdbcTemplate {
    //数据源
    private static ComboPooledDataSource dataSource;

    static {
        dataSource = new ComboPooledDataSource ();
        try {
            dataSource.setDriverClass ("com.mysql.jdbc.Driver");
            dataSource.setUser ("root");
            dataSource.setPassword ("root");
            dataSource.setJdbcUrl ("jdbc:mysql://192.168.72.141:3306/gossip?characterEncoding=UTF-8");
        } catch (PropertyVetoException e) {
            e.printStackTrace ();
        }
    }

    //构造方法
    public NewsDao() {
        //将初始化的数据源复制给父类的DataSource
        super (dataSource);
    }

    /*
    保存新闻的方法
     */
    public  void saveNews(News news){
        String sql = "insert into news(id,title,url,content,source,time,editor) values(?,?,?,?,?,?,?)";
        update(sql,news.getId(),news.getTitle(),news.getUrl(),news.getContent(),news.getSource(),news.getTime(),news.getEditor());
    }
}
