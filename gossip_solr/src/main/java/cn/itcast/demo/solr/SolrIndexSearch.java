package cn.itcast.demo.solr;

import cn.itcast.demo.pojo.Product;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SolrIndexSearch {
    @Test
    public void baseQueryToSolr() throws Exception {
        //1.创建solr服务器
        SolrServer solrServer = new HttpSolrServer ("http://localhost:8080/solr/collection1");

        //2.查询
        SolrQuery query = new SolrQuery ("*:*");//查询条件
        QueryResponse queryResponse = solrServer.query (query);
        SolrDocumentList results = queryResponse.getResults ();

        for (SolrDocument solrDocument : results) {
            String id = (String) solrDocument.get ("id");
            String title = (String) solrDocument.get ("title");
            String content = (String) solrDocument.get ("content");
            System.out.println (id + " " + title + " " + content);
        }
        solrServer.commit ();

    }

    //查询javaBean类型
    @Test
    public void getBeans() throws Exception {
        //1.创建sorl服务器
        SolrServer solrServer = new HttpSolrServer ("http://localhost:8080/solr/collection1");

        //2.查询
        SolrQuery query = new SolrQuery ("*:*");
        QueryResponse queryResponse = solrServer.query (query);
        List<Product> productList = queryResponse.getBeans (Product.class);
        for (Product product : productList) {
            System.out.println (product.getContent ());
        }

        solrServer.commit ();
    }

    //查询方法
    public void baseQuery(SolrQuery query) throws SolrServerException, IOException {
        //1.创建solr服务器
        SolrServer solrServer = new HttpSolrServer ("http://localhost:8080/solr/collection1");

        //2.查询
        QueryResponse queryResponse = solrServer.query (query);
        SolrDocumentList results = queryResponse.getResults ();
        for (SolrDocument solrDocument : results) {
            String id = (String) solrDocument.get ("id");
            String title = (String) solrDocument.get ("title");
            String content = (String) solrDocument.get ("content");
            System.out.println (id + " " + title + " " + content);
        }
        solrServer.commit ();
    }

    //通配符查询
    @Test
    public void wildCartQuery() throws Exception {
        SolrQuery solrQuery = new SolrQuery ("title:简?");
        baseQuery (solrQuery);
    }

    //布尔查询
    @Test
    public void BooleanQuery() throws Exception {
        SolrQuery solrQuery = new SolrQuery ("title:简介 OR content:中国");
        baseQuery (solrQuery);

    }

    @Test
    public void expressionQueryToSolr() throws IOException, SolrServerException {
        SolrQuery solrQuery = new SolrQuery ("(title:简介 OR content:中国)OR(title:华为旗舰手机 OR content:亮瞎)");
        baseQuery (solrQuery);
    }

    //相识度查询
    @Test
    public void fuzzyQuery() throws Exception {
        SolrQuery solrQuery = new SolrQuery ("title:简介~2");
        baseQuery (solrQuery);
    }

    //范围查询
    @Test
    public void rangQuery() throws Exception {
        SolrQuery solrQuery = new SolrQuery ("id:[10 TO 20]");
        baseQuery (solrQuery);
    }

    @Test
    public void pageQuery() throws Exception {
        int pageSize = 5;//每页显示条数
        int pageNum = 5;//当前页码
        SolrQuery solrQuery = new SolrQuery ("*:*");
        solrQuery.setStart ((pageNum - 1) * pageSize);//设置起始位置
        solrQuery.setRows (pageSize);//设置每页显示条数
        baseQuery (solrQuery);
    }

    //高亮查询
    @Test
    public void highLighterQuery() throws Exception {
        //1.创建solr服务器
        SolrServer solrServer = new HttpSolrServer ("http://localhost:8080/solr/collection1");

        SolrQuery solrQuery = new SolrQuery ("content:服务器");//查询条件

        QueryResponse response = solrServer.query (solrQuery);

        SolrDocumentList results = response.getResults ();
        /**
         * 高亮查询和普通查询在一起循环
         * 普通的id传给高亮,这样就可以查询出高亮的内容
         */
        for (SolrDocument result : results) {
            String id = (String) result.get ("id");
            String title = (String) result.get ("title");

            //高亮设置
            solrQuery.addHighlightField ("content");//添加高亮字段
            solrQuery.addHighlightField ("title");//添加高亮字段
            solrQuery.setHighlight (true);//开启高亮
            solrQuery.setHighlightSimplePre ("<font color='red'>");
            solrQuery.setHighlightSimplePost ("</font>");
            solrQuery.setHighlightSnippets (1);//如果不是多汁的默认分片解释1

            QueryResponse queryResponse = solrServer.query (solrQuery);
            Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting ();
            //有几条数据，就有多少个key-value
            Map<String, List<String>> stringListMap = highlighting.get (id);
            List<String> content = stringListMap.get ("content");
            String str = content.get (0);
            System.out.println (id + " " + title + " " + str);
        }
    }

}
