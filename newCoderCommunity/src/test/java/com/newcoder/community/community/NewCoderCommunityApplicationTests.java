package com.newcoder.community.community;

import com.newcoder.community.community.NewCoderCommunityApplication;
import com.newcoder.community.community.dao.DemoAlphaDao;
import com.newcoder.community.community.service.DemoAlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = NewCoderCommunityApplication.class)//在测试代码中，也使用正式代码的配置类作为配置类
    /*Ioc 核心是Spring容器，而容器时自动创建的。那么怎么得到容器呢？很方便，哪个类要得到核心容器则实现接口ApplicationContextAware。
      - 如果一个类实现了此接口，以及setApplicationContext方法。Spring容器会检测到这样的bean，调用这个set方法，把自身穿进去。
     *///gitTest
class NewCoderCommunityApplicationTests implements ApplicationContextAware {


    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
    }


    @Override //参数applicationContext，就是Spring 容器。下面时接口继承层次
    // ApplicationContext（子接口，功能更强）-->HierarchicalBeanFactory-->BeanFactory(Spring容器的顶层接口。
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext; //当程序运行的时候，参数自动传入，并做了记录。就可以在其他地方使用
    }

    @Test
    public  void testApplicationContext(){
        //xxx.GenericWebApplicationContext@2b5825fa; 打印出了实现该接口的类名+hashcode。
        System.out.println( applicationContext );
        /*getBean通过类型的方式获取，问题点：接口类，有多个实现怎么办（会报异常）？
            1、@Primary注解。又有问题：又在其他地方想要用Hibernate的实现怎么办？
            2、通过bean的名字，强制获取bean
         */
        DemoAlphaDao alphaDao = applicationContext.getBean(DemoAlphaDao.class);
        //优点：降低了调用方和实现类的耦合度。无直接的联系，可以更换新的。
        System.out.println(alphaDao.select());

        alphaDao = applicationContext.getBean("alpahHibernate",DemoAlphaDao.class);
        System.out.println(alphaDao.select());
    }


    @Test
    void testBeanManagement(){
        DemoAlphaService alphaService = applicationContext.getBean(DemoAlphaService.class);
        System.out.println(alphaService);
        //Bean默认只被实例化一次。
        alphaService = applicationContext.getBean(DemoAlphaService.class);
        System.out.println(alphaService);
    }

    @Test
    void testBeanConfig(){
        SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(simpleDateFormat.format(new Date()));
    }

    //上面几个测试方法，都是自己主动去获取。是从底层的方式去讲解，但是比较笨重。我们下面讲自动注入
    @Autowired //表示 希望Spring把DemoAlphaDao给注入到这个属性
    @Qualifier("alpahHibernate") //多个类产生冲突时，可以指定bean的名字注入
    private  DemoAlphaDao alphaDao;

    @Test
    void testDI(){
        System.out.println(alphaDao.select());
    }

}
