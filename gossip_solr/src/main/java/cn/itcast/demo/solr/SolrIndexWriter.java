package cn.itcast.demo.solr;

import cn.itcast.demo.pojo.Product;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class SolrIndexWriter {

    //添加索引
    @Test
    public void writerIndex() throws IOException, SolrServerException {
        //1.得到服务器对象
        SolrServer solrServer = new HttpSolrServer ("http://localhost:8080/solr/collection1");

        //2.添加数据
        SolrInputDocument doc = new SolrInputDocument ();
        doc.addField ("id", "102");
        doc.addField ("title", "简介");
        doc.addField ("content", "我是一个中国人");

        solrServer.add (doc);

        //3.提交数据
        solrServer.commit ();
    }

    //添加多条数据
    @Test
    public void writerIndexs() throws IOException, SolrServerException {
        //1.得到服务器对象
        SolrServer solrServer = new HttpSolrServer ("http://localhost:8080/solr/collection1");

        //2.添加数据
        ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument> ();
        for (int i = 0; i <= 100; i++) {
            SolrInputDocument doc = new SolrInputDocument ();
            doc.addField ("id", i + "");
            doc.addField ("title", "solr的简介");
            doc.addField ("content", "solr是一个独立的企业级搜索应用服务器, 可以通过http请求访问这个服务器, 获取或者写入对应的内容, 其底层是Lucene" + i);
            docs.add (doc);

        }
        solrServer.add (docs);

        //3.提交数据
        solrServer.commit ();

    }

    //添加javaBean
    @Test
    public void addBean() throws IOException, SolrServerException {
        //1.得到服务器对象
        SolrServer solrServer = new HttpSolrServer ("http://localhost:8080/solr/collection1");

        //2.添加数据
        Product product = new Product ();
        product.setId ("1");
        product.setBrand ("华为");
        product.setName ("华为Mate20");
        product.setPrice (5000);
        product.setTitle ("华为旗舰手机");
        product.setContent ("亮瞎你的双眼，就是这么贵！！！");

        solrServer.addBean (product);

        solrServer.commit ();
    }

    //删除索引
    @Test
    public void deleteIndex() throws IOException, SolrServerException {
        //1.得到服务器对象
        SolrServer solrServer = new HttpSolrServer ("http://localhost:8080/solr/collection1");

        //2.删除数据
        solrServer.deleteById ("2");

        //3.提交
        solrServer.commit ();
    }

}
