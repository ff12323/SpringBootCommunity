package com.newcoder.community.community;


import com.newcoder.community.community.NewCoderCommunityApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
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



    //统计20万个重复数据的独立总数
    @Test
    public void testHeyperLogLog(){
        String redisKey = "test:hll:01";
        for (int i = 0; i < 1000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey,i);
            System.out.println(i);
        }

        for (int i = 0; i < 1000; i++) {
            int r = (int) (Math.random()*1000 + 1);
            redisTemplate.opsForHyperLogLog().add(redisKey,r);
            System.out.println("f:" + i);
        }

        //去重之后，数据是1~100000之间
        System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey));
        redisTemplate.opsForHyperLogLog().delete(redisKey);
    }

    //将3组数据合并，再统计合并后的重复数据总数
    @Test
    public void testHyperLogLogUnion(){
        String redisKey2 = "test:hll:02";
        for (int i = 0; i < 100; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2,i);
        }

        String redisKey3 = "test:hll:02";
        for (int i = 50; i < 150; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey3,i);
        }

        String redisKey4 = "test:hll:02";
        for (int i = 100; i < 200; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey4,i);
        }

        String unionKey = "test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionKey,redisKey2,redisKey3,redisKey4);
        System.out.println(redisTemplate.opsForHyperLogLog().size(unionKey));

        redisTemplate.opsForHyperLogLog().delete(redisKey2);
        redisTemplate.opsForHyperLogLog().delete(redisKey3);
        redisTemplate.opsForHyperLogLog().delete(redisKey4);
        redisTemplate.opsForHyperLogLog().delete(unionKey);

    }


    //统计一组数据的布尔值
    @Test
    public void testBitMap(){
        String redisKey = "test:bm:01";

        //记录
        redisTemplate.opsForValue().setBit(redisKey,1,true);
        redisTemplate.opsForValue().setBit(redisKey,4,true);
        redisTemplate.opsForValue().setBit(redisKey,7,true);

        //查询
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,2));

        //统计
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);

        redisTemplate.delete(redisKey);
    }


    //统计3组数据布尔值，并对3组数据做OR运算
    @Test
    public void testBitMapOperation(){
        String redisKey = "test:bm:02";
        redisTemplate.opsForValue().setBit(redisKey,0,true);
        redisTemplate.opsForValue().setBit(redisKey,1,true);
        redisTemplate.opsForValue().setBit(redisKey,2,true);

        redisKey = "test:bm:03";
        redisTemplate.opsForValue().setBit(redisKey,2,true);
        redisTemplate.opsForValue().setBit(redisKey,3,true);
        redisTemplate.opsForValue().setBit(redisKey,4,true);


        redisKey = "test:bm:04";
        redisTemplate.opsForValue().setBit(redisKey,4,true);
        redisTemplate.opsForValue().setBit(redisKey,5,true);
        redisTemplate.opsForValue().setBit(redisKey,6,true);


        String rk = "test:bm:or";
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR,rk.getBytes(),
                        "test:bm:02".getBytes(),"test:bm:03".getBytes(),"test:bm:04".getBytes()
                        );
                return connection.bitCount(rk.getBytes());
            }
        });

        System.out.println(obj);

        System.out.println(redisTemplate.opsForValue().getBit(rk,0));
        System.out.println(redisTemplate.opsForValue().getBit(rk,1));
        System.out.println(redisTemplate.opsForValue().getBit(rk,2));
        System.out.println(redisTemplate.opsForValue().getBit(rk,3));
        System.out.println(redisTemplate.opsForValue().getBit(rk,4));
        System.out.println(redisTemplate.opsForValue().getBit(rk,5));
        System.out.println(redisTemplate.opsForValue().getBit(rk,6));




    }

}
