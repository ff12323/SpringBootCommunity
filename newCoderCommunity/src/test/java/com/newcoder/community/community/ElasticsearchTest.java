package com.newcoder.community.community;


import com.newcoder.community.community.NewCoderCommunityApplication;
import com.newcoder.community.community.dao.DiscussPostMapper;
import com.newcoder.community.community.dao.elasticsearch.DiscussPostRepository;
import com.newcoder.community.community.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NewCoderCommunityApplication.class)
public class ElasticsearchTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

//    @Autowired
//    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void testInsert(){
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111,0,100));
    }

    @Test
    public void testUpdate(){
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(280);
        discussPost.setContent("事务（关系型数据库）");
        discussPostRepository.save(discussPost);
    }


    @Test
    public void testDelete(){
        discussPostRepository.deleteById(280);
    }




    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    public void testSearchByRepository(){
        //search(NativeSearchQuery
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                //注意#01： 多条件搜索
                .withQuery(QueryBuilders.multiMatchQuery("彩笔","title","content"))
                //注意#02： 搜索排序，按照类型（是否置顶、分数，最后时间）
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                //注意#03：设置分页，避免过多。
                .withPageable(PageRequest.of(0,10))
                //注意#04：对搜索的内容，前后添加html标签。设置样式，而高亮显示。
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        List<DiscussPost> list = discussPostRepository.findByTitle("事务");
        for(DiscussPost ds:list){
            System.out.println(ds.getContent());
        }

        System.out.println("-------------------");

        SearchHits<DiscussPost> list2 = elasticsearchRestTemplate.search(searchQuery,DiscussPost.class);
        for(SearchHit<DiscussPost> ds:list2){

            List<String> l1 = ds.getHighlightField("title");
            List<String> l2 = ds.getHighlightField("content");
            if(l2.size() >0)
                ds.getContent().setContent(l2.get(0));
            if(l1.size() >0)
                ds.getContent().setTitle(l1.get(0));

            System.out.println(ds.getContent());
        }
    }

}
