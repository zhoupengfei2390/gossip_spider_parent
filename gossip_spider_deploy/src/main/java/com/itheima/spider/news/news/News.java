package com.itheima.spider.news.news;

public class News {

    //标识id： String    分布式id
    private String id;

    //标题
    private String title;

    //来源
    private String source;

    //时间
    private String time;

    //链接
    private String url;

    //编辑
    private String editor;

    //内容
    private String content;

    public News() {
    }

    public News(String id, String title, String source, String time, String url, String editor, String content) {
        this.id = id;
        this.title = title;
        this.source = source;
        this.time = time;
        this.url = url;
        this.editor = editor;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "News{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", source='" + source + '\'' +
                ", time='" + time + '\'' +
                ", url='" + url + '\'' +
                ", editor='" + editor + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
