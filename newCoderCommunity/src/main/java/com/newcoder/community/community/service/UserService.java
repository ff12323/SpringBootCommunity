package com.newcoder.community.community.service;

import com.newcoder.community.community.dao.UserMapper;
import com.newcoder.community.community.util.CommunityConstant;
import com.newcoder.community.community.util.CommunityUtil;
import com.newcoder.community.community.util.MailClient;
import com.newcoder.community.community.util.RedisKeyUtil;
import com.newcoder.community.community.entity.LoginTicket;
import com.newcoder.community.community.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//DiscussPostService返回的是用户id，但是需要的主要还是用户名
@Service
public class UserService implements CommunityConstant {

    //警告参考：https://zhuanlan.zhihu.com/p/92395282
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private RedisTemplate redisTemplate;

/*    @Autowired
    private LoginTicketMapper loginTicketMapper;*/

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    //Redis重构
    public User findUserById(int id){
        /*return userMapper.selectById(id);*/
        User user = getCache(id);
        if(user == null){
            user = initCache(id);
        }
        return user;
    }

    /**
     *
     * @param user 用户注册的所有信息
     * @return 返回的内容包含 账号不能为空、密码不能为空、邮箱不能为空、账号已存在等等。所以用map比较好
     */
    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();

        //对空值进行判断处理
        if(user == null){
            throw new IllegalArgumentException("参数不能为空！！！");
        }

        //提示用户
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空！");
            return map;
        }

        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }

        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空！");
            return map;
        }


        // 验证账号
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg","该账号已经存在！");
            return map;
        }
        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg","该邮箱已被注册！");
            return map;
        }

        // 注册用户，就是把用户的信息存到库里。
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0); //默认普通用户
        user.setStatus(0);//默认未激活
        user.setActivationCode(CommunityUtil.generateUUID());
        // http://images.nowcoder.com/head/1t.png 牛客网的头像，0~1000
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());

        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        // http://localhost:8080/community/activation/101/code   101:用户id code：激活码
        String url = domain + contextPath + "/activation" + "/"+user.getId() +"/"+ user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }



    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            clearCache(userId); //修改用户状态，清理缓存
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAIL;
        }
    }


    /**
     *
     * @param username
     * @param password
     * @param expiredSecondes 多少秒之后过期
     * @return
     */
    public Map<String, Object> login(String username, String password, int expiredSecondes){
        Map<String, Object> map = new HashMap<>();

        //做一些空值的处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号是不能是空的呀");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码是不能空的fff");
            return map;
        }

        //验证账号
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg","这个账号是不存在的！！！");
            return map;
        }

        if(user.getStatus() == 0){
            map.put("usernameMsg","这个账号没有激活yyyya");
            return map;
        }

        //验证密码
        password = CommunityUtil.md5(password+user.getSalt());
        if(! user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确？？？？");
            return map;
        }

        //登录成功，生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        //[UUID如何保证唯一性](https://zhuanlan.zhihu.com/p/70375430)
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSecondes * 1000));
        /*loginTicketMapper.insertLoginTicket(loginTicket);*/

        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        //Redis会把对象序列化为一个json字符串然后保存
        redisTemplate.opsForValue().set(redisKey,loginTicket);


        //如果登录凭证以及生成成功了。我们需要把凭证放到map里返回给客户端，可以放整个对象，也可以放ticket。
        //浏览器只需要记住一个key，下次再访问服务器的时候，把key给它。它去库里找，登录数据，验证状态、时间，用户id。知道登录成功
        map.put("ticket",loginTicket.getTicket());

        return map;
    }


    public void logout(String ticket){
        /*loginTicketMapper.updateStatus(ticket,1); //将凭证改为无效*/
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }


    public Map<String,Object> forget_getVerifyCode(String email){
        Map<String,Object> map = new HashMap<>();
        User user = userMapper.selectByEmail(email);
        //判断邮箱是否注册
        if(user == null){
            map.put("emailMsg","该邮箱尚未注册。。");
            return map;
        }

        //生成随机的6位验证码
        String verifyCode = CommunityUtil.generateVerifyCode();

        Context context = new Context();
        context.setVariable("email",email);
        context.setVariable("verifyCode",verifyCode);
        String content = templateEngine.process("/mail/forget",context);
        mailClient.sendMail(email,"找回密码之验证码",content);
        map.put("forget_getVerifyCode",verifyCode);

        return map;
    }


    public Map<String,Object> forget(String email, String password){
        Map<String,Object> map = new HashMap<>();
        User user = userMapper.selectByEmail(email);
        //判断邮箱是否注册
        if(user == null){
            map.put("emailMsg","该邮箱尚未注册。。");
            return map;
        }

        //密码不能为空
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码是不能空的fff");
            return map;
        }

        return map;
    }

    /**
     * 通过邮箱，更改账号密码。
     * @param email
     * @param password
     */
    public void changePasswordByEmail(String email, String password){
        User user = userMapper.selectByEmail(email);
        userMapper.updatePassword(user.getId(),CommunityUtil.md5(password + user.getSalt()));
    }

    /**
     * 通过用户ID，更改密码
     * @param userId
     * @param newPassword
     */
    public void updatePassword(int userId, String newPassword){
        User user = userMapper.selectById(userId);
        userMapper.updatePassword(userId,CommunityUtil.md5(newPassword + user.getSalt()));
        clearCache(userId);
    }


    /**
     * 通过用户ID，检查传入的密码是否为原来的密码。
     * @param userId
     * @param checkPassword
     * @return true 是原密码 ； false 不是原密码
     */
    public boolean checkPassword(int userId,String checkPassword){
        User user = userMapper.selectById(userId);
        if(user.getPassword().equals( CommunityUtil.md5(checkPassword + user.getSalt()) ) ){
            return true;
        }else {
            return false;
        }
    }


    public LoginTicket findLoginTicket(String ticket){
        /*return loginTicketMapper.selectByTicket(ticket);*/
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }


    public int updateHeader(int userId,String headerUrl){
        int rows = userMapper.updateHeader(userId,headerUrl);
        clearCache(userId);
        return rows;
    }

    public User findByUsername(String username){
        return userMapper.selectByName(username);
    }

    // 1.当查询时，优先从缓存中取。
    private User getCache(int userId){
        String key = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(key);
    }

    // 2.取不到时初始化缓存数据
    private User initCache(int userId){
        String key = RedisKeyUtil.getUserKey(userId);
        User user = userMapper.selectById(userId);
        redisTemplate.opsForValue().set(key,user,3600, TimeUnit.SECONDS);
        return user;
    }

    // 3.数据变更时清楚缓存数据
    private void clearCache(int userId){
        String key = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(key);
    }
}
