package cn.itcast.demo.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

public class SearchIndex {
    public static void main(String[] args) throws IOException, ParseException {
        //创建索引查看器
        FSDirectory d = FSDirectory.open (new File ("d:/test"));
        DirectoryReader reader = DirectoryReader.open (d);
        IndexSearcher indexSearcher = new IndexSearcher (reader);

        //查询 query 查询条件对象 参数2 查询的个数
        //查询解析器对象 参数1 字段  参数2 分词器
        QueryParser queryParser = new QueryParser ("content", new IKAnalyzer ());
        Query query = queryParser.parse ("吴京");
        TopDocs topDocs = indexSearcher.search (query, 10);
        //System.out.println (topDocs.toString ());
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        //System.out.println (scoreDocs.toString ());
        for (ScoreDoc scoreDoc : scoreDocs) {
            int id = scoreDoc.doc;//返回结果的是lucene的主键
            float score = scoreDoc.score;//匹配度

            Document doc = indexSearcher.doc (id);
            String title = doc.get ("title");
            String content = doc.get ("content");
            String sid = doc.get ("id");
            System.out.println ("lucene的主键:" + id + "  " + score + " " + title + " " + content + sid);

        }

    }


    public void queryIndex(Query query) throws IOException {
        //创建索引查询器
        FSDirectory d = FSDirectory.open (new File ("d:/test"));
        DirectoryReader reader = DirectoryReader.open (d);
        IndexSearcher indexSearcher = new IndexSearcher (reader);

        TopDocs topDocs = indexSearcher.search (query, 10);
        System.out.println (topDocs.toString ());
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int id = scoreDoc.doc;//返回结果是lucene的主键
            float score = scoreDoc.score;//匹配度

            Document doc = indexSearcher.doc (id);
            String title = doc.get ("title");
            String content = doc.get ("content");
            String sid = doc.get ("id");

            System.out.println ("lucene的主键:" + id + " " + score + " " + title + " " + content + " " + sid);
        }
    }

    //词条查询
    @Test
    public void termQuery() throws IOException {
        TermQuery query = new TermQuery (new Term ("title", "杨颖"));

        queryIndex (query);

    }

    //通配符查询
    //? 代表一位        * 代表0到多位
    @Test
    public void WildcardQuery() throws Exception {
        WildcardQuery query = new WildcardQuery (new Term ("content", "杨?"));

        queryIndex (query);
    }

    //模糊查询
    @Test
    public void fuzzyQuery() throws IOException {
        FuzzyQuery query = new FuzzyQuery (new Term ("content", "观众"));
        queryIndex (query);
    }

    //数值范围查询
    @Test
    public void numericRangeQuery() throws IOException {

        NumericRangeQuery<Integer> query = NumericRangeQuery.newIntRange ("id", 4, 8, false, true);

        queryIndex (query);
    }

    //布尔查询
    @Test
    public void booleanQuery() throws IOException {
        NumericRangeQuery<Integer> query1 = NumericRangeQuery.newIntRange ("id", 5, 15, false, true);
        NumericRangeQuery<Integer> query2 = NumericRangeQuery.newIntRange ("id", 9, 18, true, false);

        //组合查询
        BooleanQuery query = new BooleanQuery ();
        query.add (query1, BooleanClause.Occur.MUST_NOT);
        query.add (query2,BooleanClause.Occur.SHOULD);
        queryIndex (query);
    }

}
