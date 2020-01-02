redis
redis.qfjava.cn 8400 redis001
#面试题回顾
##JVM组成部分,每个部分的作用
>类加载子系统,运行时数据区,执行引擎,本地接口
1. 类加载子系统,主要的目的是找类的,然后将他加载到内存中并解析
2. 运行时数据区,这个是我们主要关注的地方, 堆,栈(线程),程序计数器,方法区,本地方法栈
每个线程调用方法的时候会将方法压入到栈中,成为一个栈帧,栈帧中最主要的是局部变量表
3. 程序计数器,这个里面放的是每个线程需要执行的代码,程序计数器和线程有关
4. 方法区,虚拟机规范规定的一个地方,里面放的是类相关的信息, 需要注意 永久代和方法区有关系吗? 永久代是方法区的实现
JDK开始常量池被移动到了堆中,JDK8的时候永久代移除,方法区的实现转移到的了元空间(堆外内存,直接内存)
5. 执行引擎 计算机无法识别任何高级语言
6. 垃收集器GC ,对象回收
##类加载器
>四种类加载器,启动类,扩展类,应用类,自定义
1. 启动类,加载java包 rt包
2. 扩展类 加载java ext扩展目录中的类
3. 应用,加载运行环境的,比如你的容器,你手动添加的依赖
4. 自定义,加载用户自己定义的一些特殊场景
>双亲委派,自己很懒,找父做这个事情,能做到就做,做不到就返回回来自己做
目的: 安全,防止自己编写的类影响到系统的稳定性,比如我们自己写一个java.lang.String,如果不走双亲委派的话,就执行我们自己的了,可能会导致程序异常
#知识点回顾
##UML图
1. 用例图
2. 时序图
3. 活动图
4. 类图
5. 组件图
##项目
划分了几个模块
1. 注册中心
2. 配置中心
3. 缓存模块,我们为什么把缓存给拆分为服务,而不是jar包,如果代码实现做了修改,只需要重新部署缓存服务即可,而不需要重新打包发布jar包,然后让所有依赖这个jar包的项目重新打包
4. webmaster来测试缓存
#网络
##公网,局域网
1. 局域网 ,比如我们的教室网络就是独立的局域网,局域网可以脱离于公网存在
2. 公网: 能在互联网上被全世界联网的计算机发现的网络
##网关
按照我们的业务要求,我们的网关中必须有公共参数校验,参数合法性校验,认证,限制访问次数等等






#重要!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
服务器和服务器之间只有一种方式来通信 就是网络请求
网络请求分为主动请求和被动接收
主动请求都是客户端(客户端和服务端是相对,发起请求的是客户端,接受请求的是服务的)发起,服务端给响应
被动接收 是服务端主动向客户端推送数据
哪怕是MQ 也是生产者主动和MQ服务通信,消费者被动接收MQ的消息
数据数据数据: 数据必须要存储才有用,存储分为内存和磁盘两种方式, 内存就是变量,集合,或者是redis缓存等等,磁盘就是文件啊数据库等等



```sql
create table if not exists `openapi-admin`.public_params
(
	id int auto_increment,
	paramname varchar(100) not null comment '参数的名字',
	status smallint(6) default 1 not null comment '状态,1是正常,0是停用删除',
	constraint public_params_id_uindex
		unique (id)
);

alter table `openapi-admin`.public_params
	add primary key (id);


```



#幂等问题
1. 什么是幂等
>相同的参数总是得到相同的结果,比如我们根据id去查询一个数据

2. 非幂等
>相同的参数总是得到不同的结果,注意结果不是成功与否,比如在不处理的情况下,我们对数据库的update操作总是刷新一次成功一次,结果都是不一样的

##注意!!
>大部分非幂等性的操作应该要保证幂等性,比如我们向数据库中插入数据,刷新表单请求应该不会再插入新的数据,还是之前的那个数据

##动态路由
>网关中是通过服务的名字外加真正的请求地址来转发到对应的服务上面的
requestcontext有两个方法可以去设置服务的名字和地址


#认证
之前用shiro,什么是shiro,判断当前用户是否有权限访问正在访问的资源
//当前用户是谁? 认证
//正在访问的资源是谁 filter
//这个资源需要什么权限 注解,xml,shirofactorybean
//这个用户有什么权限 授权


1. 获输入的用户名和密码,需要一个请求地址
2. 获取数据库中的用户和密码
3. 比较完成后 可以获取用户的角色权限等等
