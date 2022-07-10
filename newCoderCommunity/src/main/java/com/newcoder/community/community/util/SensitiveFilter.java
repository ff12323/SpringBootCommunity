package com.newcoder.community.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct //这个注解表示这是一个初始化方法，当容器实例化这个bean之后，这个初始化（容器启动时）的方法会被调用。
    public void init(){
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                )
        {
            String keyword;
            while( (keyword = reader.readLine()) != null){
                //添加到前缀树里
                this.addKeyWord(keyword);
            }

        } catch (Exception e) {
            logger.error("加载敏感词文件失败！！" + e.getMessage());
        }

    }

    //将一个敏感词添加到前缀树里去
    private void addKeyWord(String keyword){
        TrieNode temp = rootNode;
        for(int i=0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subNode = temp.getSubNode(c);
            if(subNode == null){
                //初始化子节点
                subNode = new TrieNode();
                temp.addSubNode(c,subNode);
            }
            //指针指向子节点，进入下一层循环
            temp = subNode;

            //设置结束的标识
            if( i == keyword.length() -1){
                temp.setKeywordEnd(true);
            }

        }
    }


    /**
     * 过滤敏感词
     * @param text 带过滤的文本
     * @return 过滤之后的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }

        //指针1 指向的是树
        TrieNode tempNode = rootNode;

        //指针2 指向
        int begin = 0;

        //指针3
        int position = 0;

        //结果
        StringBuilder sb = new StringBuilder();

        while (begin < text.length()){
            if( position < text.length()){

                char c = text.charAt(position);
                // 跳过符号 比如：开票  输入：♥开♥票♥
                if(isSymbol(c)){
                    // 若指针1处于根节点，将此符号记录结果。让指针2向下走一步
                    if(tempNode == rootNode){
                        sb.append(c);
                        begin++;
                    }
                    //无论符号是在开头还是中间，指针3都向下走一步
                    position++;
                    continue;
                }

                //检查下级节点
                tempNode = tempNode.getSubNode(c);
                if(tempNode == null){
                    //以begin为开头的字符串不是敏感词
                    sb.append(text.charAt(begin));
                    //进入下一个位置
                    begin++;
                    position = begin;
                    //重新指向根节点
                    tempNode = rootNode;
                }else if(tempNode.isKeywordEnd()){
                    //发现敏感词，将begin~position这段字符串替换掉
                    sb.append(REPLACEMENT);
                    //进入下一个位置
                    position++;
                    begin =position;
                    tempNode = rootNode;
                }else {
                    //检查下一个字符
                    position++;
                }
            }else{
                sb.append(text.charAt(begin));
                begin++;
                position =begin;
                tempNode = rootNode;
            }
        }


        return sb.toString();
    }


    //判断是否为符号
    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF); //0x2E80~0x9FFF这个数字范围是东亚的文字范围
    }

    //前缀树的结构
    private class TrieNode{
        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //关键词结束的标识
        private boolean isKeywordEnd = false;

        //当前节点的子节点(key是下级节点的字符，value是下级节点）
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        //添加子节点
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c,node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }





}
