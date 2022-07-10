package com.newcoder.community.community.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;


/**
 * 使用ElasticsearchRepository的方法，所有的内容是自动生成的。但是我们需要告诉它，
 * 帖子的表，和ES的索引是什么对应关系。表存到ES变成索引的时候，每个字段对应什么样类型，以什么方式搜索。需要注解。
 *
 * 1、我们要把数据里存的帖子，映射到ES的服务器里。 2、然后我们去ES服务器搜索帖子
 *
 * Spring整合ES其所提供的技术，在访问数据库服务器的时候，其底层会自动地将我们的实体数据，和es服务器里的数据进行映射。
 */
@Document(indexName ="discusspost")
public class DiscussPost {

    //es索引的ID与该注解对应。
    @Id
    private int id;

    @Field(type = FieldType.Integer)
    private int userId;

    //我们搜索帖子，搜索的就是标题和内容。
    //还有存储的时候的解析器，搜索时候的解析器
    /**
     * 1、例如标题为: 互联网校招，则es保存完之后，需要给这句话建立索引。就是
     * 把这句话提炼出关键词，用关键词关联这句话。将来，我们用关键词去搜索的时候，就能用关键词搜索到这一块。
     * 所以，保存的时候，其应该尽可能地拆分出更多的关键词，更多的词条。这样这句话就能增加搜索的范围。
     * 用大的分词器，包含中文
     *
     * 2、好了，现在你这句话保存起来了。将来去搜。则你所搜索的词“xxxxx”需要像保存一样拆分出那么多词吗？
     * 拆分得粗一点，可以看出你意图的。
     */
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer ="ik_smart" )
    private String title;

    //我们搜索帖子，搜索的就是标题和内容。
    //还有存储的时候的解析器，搜索时候的解析器
    /**
     * 1、例如标题为: 互联网校招，则es保存完之后，需要给这句话建立索引。就是
     * 把这句话提炼出关键词，用关键词关联这句话。将来，我们用关键词去搜索的时候，就能用关键词搜索到这一块。
     * 所以，保存的时候，其应该尽可能地拆分出更多的关键词，更多的词条。这样这句话就能增加搜索的范围。
     * 用大的分词器，包含中文
     *
     * 2、好了，现在你这句话保存起来了。将来去搜。则你所搜索的词“xxxxx”需要像保存一样拆分出那么多词吗？
     * 拆分得粗一点，可以看出你意图的。
     */
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer ="ik_smart" )
    private String content;

    @Field(type = FieldType.Integer)
    private int type;

    @Field(type = FieldType.Integer)
    private int status;

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Integer)
    private int commentCount;

    @Field(type = FieldType.Double)
    private double score;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "DiscussPost{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", createTime=" + createTime +
                ", commentCount=" + commentCount +
                ", score=" + score +
                '}';
    }
}
