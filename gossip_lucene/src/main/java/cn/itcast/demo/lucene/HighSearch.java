package cn.itcast.demo.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

public class HighSearch {
    //高亮查询
    @Test
    public void highLightQuery() throws Exception {
        //索引查询器
        FSDirectory d = FSDirectory.open (new File ("d:/test"));
        IndexReader reader = DirectoryReader.open (d);
        IndexSearcher indexSearcher = new IndexSearcher (reader);

        QueryParser queryParser = new QueryParser ("content", new IKAnalyzer ());
        Query query = queryParser.parse ("杨颖");

        //高亮对象 高亮的设置
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter ("<em sytle='color:red'>", "</em>");
        Scorer scorer = new QueryScorer (query);
        Highlighter highlighter = new Highlighter (formatter, scorer);

        TopDocs topDocs = indexSearcher.search (query, 10);

        System.out.println ("总记录数:" + topDocs.totalHits);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            float score = scoreDoc.score;//匹配度
            int id = scoreDoc.doc;//得到的是lucene的id

            Document doc = indexSearcher.doc (id);
            String title = doc.get ("title");
            String content = doc.get ("content");

            //高亮对象对某个字段进行高亮处理
            String content1 = highlighter.getBestFragment (new IKAnalyzer (), "content", content);

            String sid = doc.get ("id");

            System.out.println ("lucene:" + id + " " + score + " " + title + " " + content1 + " " + sid);

        }
    }

    //分页查询 Lucene本身不支持分页.当前页码 5 每页显示条数 10
    @Test
    public void pageQuery() throws IOException, ParseException {
        int pageSize = 10;//每页显示条数
        int pageNum = 5;//当前页码
        //起始和结束的位置
        int start = (pageNum - 1) * pageSize;
        int end = start + pageSize;

        Directory directory = FSDirectory.open (new File ("d:/test"));
        DirectoryReader reader = DirectoryReader.open (directory);
        IndexSearcher indexSearcher = new IndexSearcher (reader);

        QueryParser queryParser = new QueryParser ("content", new IKAnalyzer ());
        Query query = queryParser.parse ("杨颖");

        //参数1:根据哪个字段排序 参数2:排序字段的类型 参数3:是否反转 true反转 降序
        Sort sort = new Sort (new SortField ("id", SortField.Type.INT, false));
        TopDocs topDocs = indexSearcher.search (query, pageSize, sort);

        System.out.println ("总记录数:" + topDocs.totalHits);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (int i = start; i < end; i++) {
            Document doc = indexSearcher.doc (i);
            String title = doc.get ("title");
            String content = doc.get ("content");
            String sid = doc.get ("id");
            System.out.println ("Lucen的主键:" + i + " " + title + " " + content + " " + sid);
        }

    }
}
