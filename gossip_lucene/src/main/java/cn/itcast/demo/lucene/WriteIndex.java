package cn.itcast.demo.lucene;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WriteIndex {
    public static void main(String[] args) throws IOException {
        //1.构建索引写入器
        //1.1 创建索引库目录
        FSDirectory d = FSDirectory.open (new File ("d:/test"));

        //1.2索引写入其配置对象 参数1 版本  参数2 分词器
        IndexWriterConfig config = new IndexWriterConfig (Version.LATEST, new IKAnalyzer ());
        IndexWriter indexWriter = new IndexWriter (d, config);

        //迭代器
        List<Document> docs = new ArrayList<Document> ();
        for (int i = 1; i <= 100; i++) {
            //2.构建文文档,创建一条数据
            Document doc = new Document ();
            doc.add (new IntField ("id", i, Field.Store.YES));
            doc.add (new StringField ("title", "向太陈岚给向佐和郭碧婷的照片加爱心，准婆婆发声：情多在珍惜", Field.Store.YES));
            doc.add (new TextField ("content", "《流浪地球》的票房已经41亿了，已经逼近吴京的《战狼2》，谁能想到当初一个连宣传都少得可怜的电影，最后却爆发了这么大的能量。而吴京也再次红火了起来，成为国内最新的票房红人。除了电影之外，吴京本人的一……", Field.Store.YES));
            docs.add (doc);
        }
        for (int i = 1; i <= 100; i++) {
            Document doc = new Document ();
            doc.add (new IntField ("id", i, Field.Store.YES));
            doc.add (new StringField ("title", "杨颖后援会称《跑男》为某综艺，还列举五大罪状，网友：开撕了？", Field.Store.YES));
            doc.add (new TextField ("content", "相信在很多观众眼中，杨颖都是《跑男》的团宠吧，不仅每一次游戏大家都会照顾她，在第五季的时候，为了保持她团宠的地位，甚至不惜得罪了当时流量惊人的小花迪丽热巴，将热巴排挤出了跑男团，坐实了她团宠的位置。……", Field.Store.YES));
            docs.add (doc);
        }
        indexWriter.addDocuments (docs);

        //3.提交
        indexWriter.commit ();

        //4.释放资源
        indexWriter.commit ();
        d.close ();
    }

    //修改
    @Test
    public void updateIndex() throws IOException {
        //1. 构建索引写入器
        //1.1 创建索引库目录
        Directory d = FSDirectory.open (new File ("d:/test"));

        //1.2 索引写入器配置对象 参数1 版本  参数2 分词器
        IndexWriterConfig config = new IndexWriterConfig (Version.LATEST, new IKAnalyzer ());

        IndexWriter indexWriter = new IndexWriter (d, config);

        //修改
        Document doc = new Document ();
        doc.add (new IntField ("id", 1, Field.Store.YES));
        doc.add (new StringField ("title", "陈百强遗物被粉丝夺走，称给了一百元红包，陈母哽咽对不起儿子", Field.Store.YES));
        doc.add (new TextField ("content", "2月27日，据台媒报道，香港乐坛巨星陈百强生前的遗物，在家人为他建造的纪念馆里不翼而飞，秀服、奖杯以及私人物品都被人拿走。一位自称干女儿和陈妈妈的对话短片曝光，表示有粉丝抢夺偶像物品，展览馆很多珍藏……", Field.Store.YES));

        //修改
        indexWriter.updateDocument (new Term ("content", "杨颖"), doc);

        //3.提交
        indexWriter.commit ();

        //4.释放资源
        indexWriter.commit ();
        d.close ();
    }

    @Test
    public void deleteIndex() throws IOException {
        //1.构建索引写入器
        //1.1 创建索引库目录
        FSDirectory d = FSDirectory.open (new File ("d;/test"));
        //1.2 索引写入器配置对象 参数1 版本 参数2 分词器
        IndexWriterConfig config = new IndexWriterConfig (Version.LATEST, new IKAnalyzer ());

        IndexWriter indexWriter = new IndexWriter (d, config);

        //删除 满足条件(最好根据条件先查询
        indexWriter.deleteDocuments (new Term ("content", "吴京"));

        //3.提交
        indexWriter.commit ();

        //4.释放资源
        indexWriter.commit ();
        d.close ();
    }
}
