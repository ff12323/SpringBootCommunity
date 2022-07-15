
Maven多模块项目：
- 1、核心是牛客社区
- 2、附加Demo熟悉项目：如Spring Security的简单Demo
- 3、Spring Cloud项目（包含3个小项目）：Demo熟悉

## 自我记录与索引
 
难带的索引：
- ThreadLocal的使用：
```java
                //在本次请求中持有的用户 这个多线程的，每个线程都要存一份，互不干扰
                //使用HostHoder工具取持有用户，为什么其能持有？我们把数据存到当前线程对应的map里。只要这个请求没有处理完，则这个线程一直还在。
                //当请求处理完，服务器向浏览器做出响应后，这个线程被销毁。所以处理过程中，数据一直都在。
                hostHolder.setUser(user);
```


bug记录：
- 1、教程中在LoginToken拦截器里，每次请求之前若有token存在则将认证信息保存到SecurityContext中。每次请求之后清除SecurityContext。（bug现象：我们已经设置一些http授权了，首先登录，则第一次访问/user/setting自然是成功，但是结束请求后SecurityContext请求被清理；接着马上第二次访问/user/setting，由于过滤器（Filter）是先于拦截器的，而认证信息被清理，直接拒绝访问！！！）


Spring的知识：
- 1、@PostConstruct:依赖注入完成之后，而进行方法的初始化。