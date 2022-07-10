
自我记录与索引

springSecurity令我惊讶的记录：
- 1、我一直以为login和logout的处理是要自己在controller里实现的，跟着教程做着做着，才意识到SecurityConfig里的授权已经实现了，然后在http.formLogin里声明登录页面、声明登录验证地址就已经完成了功能了。logout同理！！！