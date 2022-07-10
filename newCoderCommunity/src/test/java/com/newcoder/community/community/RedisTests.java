package com.newcoder.community.community;


import com.newcoder.community.community.NewCoderCommunityApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NewCoderCommunityApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        String redisKey = "test:count";


        redisTemplate.opsForValue().set(redisKey,1);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        redisTemplate.opsForValue().increment(redisKey);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        redisTemplate.opsForValue().decrement(redisKey);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
    }

    @Test
    public void testHashes(){
        String redisKey = "test:user";

        redisTemplate.opsForHash().put(redisKey,"id",1);
        redisTemplate.opsForHash().put(redisKey,"username","apple");

        System.out.println("id: "+ redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println("username:" + redisTemplate.opsForHash().get(redisKey,"username"));
    }

    @Test
    public void testLists(){
        String redisKey = "test:ids";

        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        redisTemplate.opsForList().leftPush(redisKey,103);

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey,0));
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2));

        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    }

    @Test
    public void testSets(){
        String redisKey = "test:teachers";

        redisTemplate.opsForSet().add(redisKey,"aaa","bbb","ccc","ddd");
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testZSets(){
        String redisKey = "test:students";
        redisTemplate.opsForZSet().add(redisKey,"aaa",10);
        redisTemplate.opsForZSet().add(redisKey,"bbb",20);
        redisTemplate.opsForZSet().add(redisKey,"ccc",30);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"aaa"));
        System.out.println(redisTemplate.opsForZSet().rank(redisKey,"bbb"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"bbb"));
        System.out.println(redisTemplate.opsForZSet().range(redisKey,0,1));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey,0,1));
    }

    @Test
    public void testKeys(){
        redisTemplate.delete("test:user");
        System.out.println(redisTemplate.hasKey("test:user"));

        redisTemplate.expire("test:students",10, TimeUnit.SECONDS);

    }

    //多次访问同一个key,绑定key
    @Test
    public void testBindOperations(){
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);

        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();

        System.out.println(operations.get());
    }

    //事务 不是关系型不满足ACID4个特性。不要在事务中间做出查询，不会立即返回
    //编程方式事务
    @Test
    public void testTransactional(){
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";
                operations.multi();//启用事务
                operations.opsForSet().add(redisKey,"aaa");
                operations.opsForSet().add(redisKey,"bbb");
                operations.opsForSet().add(redisKey,"ccc");

                System.out.println(operations.opsForSet().members(redisKey)); //返回时空的

                return operations.exec(); //提交事务
            }
        });

        System.out.println(obj);//[1, 1, 1, [bbb, aaa, ccc]] 添加数据返回 1 1 1以及一个集合。

    }

}
