package com.newcoder.community.community.service;


import com.newcoder.community.community.dao.elasticsearch.DiscussPostRepository;
import com.newcoder.community.community.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchService {

//    @Autowired
//    private DiscussPostRepository discussPostRepository;
//
//    @Autowired
//    private ElasticsearchRestTemplate elasticsearchRestTemplate;
//
//    public void saveDiscussPost(DiscussPost discussPost){
//        discussPostRepository.save(discussPost);
//    }
//
//    public void deleteDiscussPost(int id){
//        discussPostRepository.deleteById(id);
//    }
//
//
//    /**
//     *
//     * @param keywords 搜索关键字
//     * @param current 分页：当前页面
//     * @param limit 分页：每页多少条
//     * @return
//     */
//    public List<DiscussPost> searchDiscussPost(String keywords,int current, int limit){
//        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
//                //注意#01： 多条件搜索
//                .withQuery(QueryBuilders.multiMatchQuery(keywords,"title","content"))
//                //注意#02： 搜索排序，按照类型（是否置顶、分数，最后时间）
//                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
//                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
//                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
//                //注意#03：设置分页，避免过多。
//                .withPageable(PageRequest.of(current,limit))
//                //注意#04：对搜索的内容，前后添加html标签。设置样式，而高亮显示。
//                .withHighlightFields(
//                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
//                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
//                ).build();
//
//        List<DiscussPost> resList = new ArrayList<>();
//        SearchHits<DiscussPost> list2 = elasticsearchRestTemplate.search(searchQuery,DiscussPost.class);
//        for(SearchHit<DiscussPost> ds:list2){
//
//            List<String> l1 = ds.getHighlightField("title");
//            List<String> l2 = ds.getHighlightField("content");
//            if(l2.size() >0)
//                ds.getContent().setContent(l2.get(0));
//            if(l1.size() >0)
//                ds.getContent().setTitle(l1.get(0));
//
//            resList.add(ds.getContent());
//        }
//
//        return resList;
//    }

}
