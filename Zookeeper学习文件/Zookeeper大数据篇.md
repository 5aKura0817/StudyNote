# 一、Zookeeper入门

## 1.1、概述

> Zookeeper是什么？

Zookeeper是一个开源的、分布式的、为分布式应用==提供协调服务==的Apache项目。

我们初次见到Zookeeper是在Spring Boot中使用Zookeeper+Dubbo来做分布式服务管理。Zookeeper在这套框架中充当服务注册中心，便于消费者调用。

> Zookeeper工作机制简述

从设计模式的角度来看：Zookeeper是基于==观察者模式的分布式服务管理框架==

它==负责存储和管理“大家所关心的”数据，并且接受观察者的注册==，一旦被管理的数据产生了变化就通知已经注册的观察者进行相应的操作。

从功能来看，Zookeeper=文件系统+通知机制



## 1.2、特点

![img](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/u=556249625,3116473969&fm=26&gp=0.jpg)

通过上图看出，Zookeeper的集群也是采用的主从模式，**一个Leader多个Follower**。每个节点（server）都可以接收来自客户端的请求。

-   **可靠性高**：集群中只要有半数以上的节点存活，Zookeeper集群就可以正常提供服务
-   **全局数据一致**：所有的节点保存一份相同的数据副本，所以每个server都可以提供服务，因为数据都是一致的！
-   **请求顺序执行**：来着同一个Client的更新请求是顺序执行
-   **数据更新原子性**：一次数据更新，要么成功要么失败，数据更新过程中不允许有其他操作
-   **实时性**：一定时间范围内，更新的数据会很快同步到其他Server上，Client能读取到最新的数据。（数据同步时间间隔短，因为存放的数据量小）



## 1.3、数据结构

![img](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/timg)

Zookeeper的数据结构类似于UNIX的文件系统，树形结构，每一个节点被称为一个ZNode，每个ZNode默认能存储`1MB`的数据，每个ZNode都可以通过路径名唯一标识。



## 1.4、应用场景

[推荐阅读博客](https://www.jianshu.com/p/418d95f0092c)

主要的应用场景有：统一服务命名、统一配置管理、统一集群管理、服务器节点动态上下线、软负载均衡
> **统一服务命名**

在分布式系统中，客户端大多是通过域名去调用服务，而少有直接通过服务器IP去调用服务了，所以为了便于管理服务和方便客户端访问，Zookeeper可以对服务进行统一命名，Zookeeper用ZNode保存服务器的地址进行转发即可。

> **统一配置管理**

分布式系统中，保证集群中各个节点的配置信息一致十分重要，在Hadoop中我们使用集群分发脚本在每次修改配置信息后进行分发。而使用Zookeeper就方便了很多，用一个ZNode保存集群的统一配置信息，所有的服务节点监控这个文件，一旦配置文件发生了变化，就立马进行同步，就可以保证集群的配置信息一致了

> **统一集群管理**

集群中数据的一致和配置一致一样重要，一定要确保某个服务器的数据修改其他服务器也能快速得知并进行同步，才不至于同一个请求在不同的服务器上返回的结果是不一致的。Zookeeper使用ZNode存储每一个服务器的服务信息，每当有一个服务器注册就增加一个ZNode，一旦其中一个ZNode的数据变更就立马更新其他几个ZNode。

> **服务器节点动态上下线**

在集群中难免出现某个节点突然挂掉的情况，挂掉的服务器是无法继续提供服务访问的，而客户端并不清楚服务器的状况，而Zookeeper则可以监控和管理服务器的状态，当服务器崩掉后通知客户端此服务器不可用，或者接收客户端的请求转发到其他服务器上，并等待服务器重新上线同步数据和配置。

> **软负载均衡**

在SpringCloud Netflix中使用ribbon集成在消费者方进行负载均衡，或者使用NGINX做反向代理服务器也可以进行负载均衡。而使用Zookeeper可以实现软负载均衡，使用ZNode记录每一个服务器的访问次数和服务器信息，每次让访问次数最小的服务器去处理客户端的请求，平衡集群中的服务器的压力。



# 二、安装Zookeeper（Linux本地模式环境搭建）

## 2.1、安装

[Zookeeper下载地址](https://zookeeper.apache.org/releases.html#download)

1. 解压文件

2. 进入zookeeper目录中conf目录，将`zoo_sample.cfg`修改为`zoo.cfg`

3. 并在zoo.cfg中修改以下内容

   `dataDir=/opt/module/apache-zookeeper-3.6.1-bin/zkData`

4. 在Zookeeper的根目录下创建zkData目录



## 2.2、操作Zookeeper

1. 启动Zookeeper

   `bin/zkServer.sh start`

   使用jps查看进程是否启动：QuorumPeerMain

   ![image-20200706164111262](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200706164111262.png)

2. 查看状态

   `bin/zkServer.sh status`

   ![image-20200706164149507](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200706164149507.png)

3. 客户端启动

   `bin/zkCli.sh`

   ![image-20200706164407658](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200706164407658.png)

4. 退出客户端

   quit

   ![image-20200706164453324](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200706164453324.png)

5. 停止Zookeeper

   `bin/zkServer.sh stop`

   ![image-20200706164601116](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200706164601116.png)



## 2.3、配置参数解读

![image-20200706165314891](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200706165314891.png)

- `tickTime`：每次滴答心跳间隔时间2000毫秒（2秒）
- `initLimit`：集群中服务器连接到Leader和初始化所限制的最大时间：10个心跳滴答（20秒）
- `syncLimit`：集群中服务器数据同步的最大限制时间：5个心跳滴答（10秒）
- `dataDir`：zookeeper的数据文件存放位置
- `clientPort`：zookeeper服务器提供给客户端访问的端口：2181



# 三、Zookeeper内部原理

## 3.1、选举机制

![img](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/u=556249625,3116473969&fm=26&gp=0.jpg)

==半数机制：只要集群中存在半数以上的服务器可用，集群就可以正常服务。==所以Zookeeper的集群服务器数量推荐为奇数。

Zookeeper的Leader和Follower并不是通过配置文件配置的，而是通过内部选举选出来的。

> 每个服务器在投票时，首先将票投给自己，当发现投给自己无法选出Leader时，将票投给当前所有存活服务器中id最大的服务器，一旦某个服务器的**总票数超过了现存节点的半数**，即被选为Leader，当Leader产生以后，后续加入的服务器不再进行投票，直接成为Follower。



## 3.2、节点类型

==持久（Persistent）==：客户端和服务器端断开连接后，创建的节点不删除

==短暂（Ephemeral）==：客户端和服务器端断开连接后，创建的节点自动删除



> 持久节点

- 持久化目录节点：普通的持久目录节点，客户端断开后不会删除
- 持久化顺序编号目录节点：在持久化目录节点的基础上，为节点的名字增加一个顺序编号。

**说明：创建ZNode设置顺序编号时，编号直接加在ZNode名称后，顺序号是一个单调递增的计数器！**

在分布式系统中，顺序号可以被用于为所有的时间进行全局排序，**通过顺序号来推断事件发生的先后关系。**

> 临时节点

- 临时目录节点：客户端断开即删除节点，==适合用于做服务器动态上下线==
- 临时顺序编号目录节点：同理。



## 3.3、Stat结构体

（推荐先看完4.1、4.2）

```properties
[zk: localhost:2181(CONNECTED) 43] stat /students/zhangsan
cZxid = 0x30000002c
ctime = Tue Jul 07 14:24:14 CST 2020
mZxid = 0x300000033
mtime = Tue Jul 07 14:28:11 CST 2020
pZxid = 0x300000039
cversion = 5
dataVersion = 4
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 16
numChildren = 1
```

- cZxid:创建节点的事务ID
- ctime:创建的时间
- mZxid:最后一次修改节点的事务ID
- mtime:最后一次修改时间
- pZxid:最后一次修改子节点的事务ID
- cversion:子节点的修改次数（版本号）
- dataVersion:节点数据的修改次数（版本号）
- aclVersion:访问控制列表的变化号
- ephemeralOwner:当节点是临时节点时此属性的值为节点的拥有者的Session id，若为持久节点，此属性值为0；
- **dataLength**:节点存放数据的长度
- **numChildren**:节点拥有的子节点数量



## 3.4、监听器原理

启动的客户端创建两个线程：Listener和Connect，前者通过一个端口用于接收来自服务端的监听信息接收，Connect负责网络通信。

- 使用connect发送一个监听事件的注册请求给服务端
- 服务端接收以后加入到注册监听器列表中
- 当列表中有节点发生了变化，就会立马发生数据变化的消息给客户端
- Listener线程从指定接口接收到数据
- 通过接收到的消息，客户端执行process()进行相应的操作



## 3.5、写数据流程

> 在集群上写入数据为了保证数据的一致性Zookeeper采用的方式是将请求广播。

当一个Client连接到的是一台Follower服务器，Follower服务器是无法决定数据写入的，Follower需要将接受的数据写入请求转发给集群的Leader，然后Leader将这条写数据的请求广播到集群到所有的Follower去执行。当所有的Follower接受到广播执行，执行完成向Leader响应。当Leader收到大多数（半数以上）的完成响应，就认为数据写入成功，然后告诉最开始转发给Leader请求的Follower数据写好了，然后Follower给客户端反馈——数据写入成功。

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707161329354.png" alt="image-20200707161329354" style="zoom:67%;" />





# 四、Zookeeper实战

## 4.1、分布式安装部署

1. 使用xsync将本地模式的Zookeeper分发到103、104上

2. 在各自的zkData目录下创建一个myid文件，文件中只写服务器的编号，后面有用。

   ![image-20200706194051874](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200706194051874.png)
   
3. 修改conf/zoo.cfg文件加入以下内容：

   ```markdown
   #####################Cluster############################
   server.2=hadoop102:2888:3888
   server.3=hadoop103:2888:3888
   server.4=hadoop104:2888:3888
   ```

   配置解读:

   `server.N=A:B:C`

   - N 对应于节点中myid文件中的数值
   - A 对应集群中每个服务器IP
   - B（2888）：集群中用于**数据**通信的端口
   - C（3888）：集群中用于**投票选举**通信的端口

   配置完成后，进行分发

4. 依次启动服务器

   > 当只启动了一个服务器时，由于不超过集群服务器数量的半数，所有是不可用状态：

   ![image-20200706235809245](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200706235809245.png)

   > 当时当启动两台服务器之后，Leader成功选出，集群也变为了可用状态，第一台服务器角色是Follower

   ![image-20200707000022780](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707000022780.png)

   ![image-20200706235952831](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200706235952831.png)

   Leader选出以后，加入的服务器都是Follower

   ![image-20200707000132935](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707000132935.png)

    

   但是一旦我关闭了当前的Leader，就会重新选举，hadoop104由于id大的优势变为新的Leader

   ![image-20200707000309500](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707000309500.png)



## 4.2、Shell命令操作

所有的shell命令都是在操作zookeeper的目录树，要通过客户端完成。

1. `bin/zkCli.sh`启动客户端连接ZookeeperServer

2. 使用`help`查看所有可用的shell命令

   ![image-20200707104523458](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707104523458.png)

3. 基本命令

   - `ls` 列出指定路径下的节点【option：-s:列出节点的详细信息，-R:递归列出路径下所有节点】

     <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707104744915.png" alt="image-20200707104744915" style="zoom:50%;" /><img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707104916483.png" alt="image-20200707104916483" style="zoom:67%;" />

     

   - `create`创建节点【option：-s:创建带顺序编号的节点，-e:创建临时节点(默认是创建持久节点)】

     ![image-20200707105149827](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707105149827.png)

     这样创建的节点中是没有数据的。使用get取值返回的是null

     ==Zookeeper的Shell操作中有这样一个规则就是，路径不能以/结尾！但是在Linux文件系统和HDFS中是可以的，要注意区分！==

     ![image-20200707105518533](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707105518533.png)

     现在创建一个带有数据的节点：

     ![image-20200707105941313](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707105941313.png)

     关于持久节点和临时节点和顺序号节点等会再说

     

   - `get`获取节点的值【option：-s:获取节点的详细信息】

     ![image-20200707110157672](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707110157672.png)

     对于相同的路径 get -s 和 ls -s有可能是有不同的结果：

     <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707110447489.png" alt="image-20200707110447489" style="zoom:67%;" /><img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707110507462.png" alt="image-20200707110507462" style="zoom:67%;" />

     其实很容易理解：==路径名是唯一标识一个Node==，ls只不过是列出这个Node的子节点，get是获取的是节点的值，至于-s就是列出这个路径对应Node的详细信息。

      

   - 临时节点和持久节点以及带顺序编号的节点创建

     ==使用create创建的节点默认是持久节点且不带顺序编号==。

     创建临时节点：`create -e nodepath`

     ![image-20200707123302222](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707123302222.png)

     ![image-20200707123453516](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707123453516.png)

     现在断开这个客户端，在其他客户端查看节点是否还存在：

     ![image-20200707123404805](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707123404805.png)

     被正常删除，之前创建的持久节点并没有删除。

     创建带顺序编号的节点：`create -s nodepath`

     > 关于这个顺序编号：==编号由父节点进行维护的一个计数器决定==，这个计数器记录着节点的子节点创建次数，即时之前创建的子节点删除了也算数。==也就是说每当节点创建了一个子节点计数器就加一，甭管是什么类型的节点。==当创建带顺序编号的节点的时候，直接将当前计数器的值加到节点名后面。

     由于是父节点维护，所以相同的节点下，不同客户端创建节点都是使用同一个计数器。但是不同的路径下计数器是互不干扰的。

     ![image-20200707124907385](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707124907385.png)

     现在计数器的值就是0000000008，即时这个子节点删除了也不会影响计数器的值。

      

   - `set`设置节点的值

     ![image-20200707125219700](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707125219700.png)

     

   - `delete`、`deleteall`删除节点

     ![image-20200707140935558](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707140935558.png)

     当节点下还有子节点的时候，想要一次性删除类似于Linux文件系统中的rm -r,必须使用`deleteall`命令

     ![image-20200707141109806](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707141109806.png)

      

   - 节点监视

     有两种监视类型：监视节点内容、监视节点路径的变化（即子节点的删除与增加）

     在稍微旧一点的版本中使用ls path watch或者get path watch来分别添加路径监视和内容监视。但是现在的版本中已被弃用。改用` ls -w path`和`get -w path`,或者使用`addWatch`

     - 节点路径监视：ls -w path

       执行后，客户端会持续监视这个路径下的节点变化，当新增子节点或删除子节点都会输出监视信息。

       ![image-20200707142216892](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707142216892.png)

       但是仅一次有效，也就是说加入监视以后，发生一次变化后，输出变化消息后监视失效，需要重新加入。

     - 节点数据监视：get -s path

       执行后，客户端持续监视节点的数据内容，一旦节点被删除或者数据被set，就会输出监视信息：

       ![image-20200707142606063](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707142606063.png)

       同样也是仅一次有效

     - `addWatch`：万能的监视

       使用addWatch，相比于上面两种简直是太香了！！！可以==一键监视节点路径下的所有变化（包括路径变化和数据变化）！！并且是持续监视==

       ![image-20200707143628529](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707143628529.png)

       addWatch命令有两种可选mode，PERSISTENT(持续的)、还有一种是
       PERSISTENT RECURSIVE(持续递归的、默认)。使用 【-m mode】进行指定。

       ![image-20200707143924166](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707143924166.png)

       因为是递归持续监视功能才这么强大，也可以只监视指定目录。

     - `removewathes`移除指定路径节点的监视

       ![image-20200707144156893](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707144156893.png)

      

     

   - 节点状态查看`stat path`【option：-w 开启监视】

     之前使用get -s和ls -s顺带输出节点的信息，现在直接使用stat查看就o了

     <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707144521303.png" alt="image-20200707144521303" style="zoom:67%;" />

   - 输出节点的所有子孙节点数量`getAllChildrenNumber`

     <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707144815308.png" alt="image-20200707144815308" style="zoom:67%;" />

   其他的命令再慢慢摸索把，这些是常用的，必须牢记！！



## 4.3、API应用

### 4.3.1、环境搭建

1. Maven工程创建

2. 导入junit、log4j、zookeeper的pom依赖

   ```xml
   <dependencies>
           <dependency>
               <groupId>log4j</groupId>
               <artifactId>log4j</artifactId>
               <version>1.2.12</version>
           </dependency>
           <dependency>
               <groupId>org.apache.zookeeper</groupId>
               <artifactId>zookeeper</artifactId>
               <version>3.6.0</version>
           </dependency>
           <dependency>
               <groupId>junit</groupId>
               <artifactId>junit</artifactId>
               <version>4.13</version>
           </dependency>
       </dependencies>
   ```

3. 导入log4j.properties文件

   ```properties
   log4j.rootLogger = INFO ,stdout
   
   ### 输出到控制台 ###
   log4j.appender.stdout = org.apache.log4j.ConsoleAppender
   log4j.appender.stdout.Target = System.out
   log4j.appender.stdout.layout = org.apache.log4j.PatternLayout
   log4j.appender.stdout.layout.ConversionPattern = %d    %p    [%c]- %m%n
   
   ### 输出到日志文件 ###
   log4j.appender.file = org.apache.log4j.FileAppender
   log4j.appender.file.File = target/spring.log
   log4j.appender.file.layout = org.apache.log4j.PatternLayout
   log4j.appender.file.layout.ConversionPattern = %d    %p    [%c]- %m%n
   ```

4. 代码：

   要操作Zookeeper需要一个zookeeper对象：

   ```java
   public class GetClient {
   
       private ZooKeeper zkClient;
       /**
        * 集群服务器的连接IP+端口
        */
       private String connectString = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
       
       /**
        * 会话连接超时毫秒数
        */
       private int sessionTimeout = 2000;
   
       /**
        * 一个观察者
        */
       private Watcher watcher = new Watcher() {
           @Override
           public void process(WatchedEvent watchedEvent) {
   
           }
       };
   
       @Test
       public void init() throws IOException {
           // 获取一个zookeeper对象
           zkClient = new ZooKeeper(connectString, sessionTimeout, watcher);
   
       }
   }
   ```



### 4.3.2、API使用

1. 创建节点`create`：

   需要使用Zookeeper对象，所以上面init()的@Test注解要改为@Before，不然zkClient是空对象。

   ```java
   @Test
   public void createNode() throws KeeperException, InterruptedException {
       zkClient.create(
           "/students/sakura", 
           "My name is Sakura".getBytes(), 
           ZooDefs.Ids.OPEN_ACL_UNSAFE, 
           CreateMode.PERSISTENT);
   }
   ```

   **path（String）**：创建的节点的路径

   **data（byte[]）**：节点数据的字节数组

   **acl(List\<ACL>)**：节点的控制访问（OPEN_ACL_UNSAFE:开放访问），可选项：

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707170727032.png" alt="image-20200707170727032" style="zoom:67%;" />

   **createMode(CreateMode)**：节点的创建类型(PERSISTENE，持久节点；_SEQUENTIAL带顺序编号的)

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707171115597.png" alt="image-20200707171115597" style="zoom:67%;" />

   运行完成，立马可以在集群服务器上看到节点：

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707171357335.png" alt="image-20200707171357335" style="zoom:67%;" />

   

2. 获取子节点（`getChildren()`获取子节点名，`getData()`获取节点数据）

   ```java
   public void getAndWatchData() throws KeeperException, InterruptedException {
       // 获取数据但是不监控
       List<String> children = zkClient.getChildren("/students", false);
       for (String child : children) {
           System.out.println(child);
       }
   }
   ```

   要监视数据需要一个监视器，在获取zookeeper对象时候就创建了一个watcher，并且get方法的第二个参数设置为true。

   既然Shell命令有多种监听办法，那API也不例外：

   方式一：

   ```java
   // 监听器，在创建zookeeper对象时作为参数传入
   private Watcher watcher = new Watcher() {
       @Override
       public void process(WatchedEvent watchedEvent) {
           try {
               zkClient.getChildren("/students", true);
           } catch (KeeperException e) {
               e.printStackTrace();
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           System.out.println(watchedEvent);
       }
   };
   
   zkClient.getChildren("/students", true);
   Thread.sleep(Long.MAX_VALUE);
   ```

   当设置开启监听时，当**发生子节点新增或者删除的时候**，就会调用监听器的process()方法，由于这种方式监听设置一次有效，所以需要每次执行process()的时候，都要重新注册监听，才能实现持续监听。这种太复杂啦！！！

   

   方式二：addWatch

   ```java
   private Watcher watcher = new Watcher() {
       @Override
       public void process(WatchedEvent watchedEvent) {
           System.out.println(watchedEvent);
       }
   };
   
   zkClient.addWatch("/students", watcher, AddWatchMode.PERSISTENT_RECURSIVE);
   Thread.sleep(Long.MAX_VALUE);
   ```

   使用addWatch()也可以指定使用哪个监视器，监视模式(AddWatchMode)还是那两种。这种就不用反复地注册监听，并且目录下所有的变化都见监听到。

   

   > 注意：无论使用那种方法，最后的Thread.sleep()是少不了的，不然程序直接结束是毫无意义的。

   

3. 判断ZNode是否存在

   ```java
   public void nodeExist() throws KeeperException, InterruptedException {
       Stat stat = zkClient.exists("/students", false);
       System.out.println(stat == null ? "not exist" : "exist");
       System.out.println(stat);
   }
   ```

   输出结果：

   ```markdown
   exist
   12884901928,12884901993,1594102798657,1594126106205,5,18,0,0,15,4,12884901995
   ```

   下面这段数字的含义对照Stat类的toString方法去对照把：

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707210315102.png" alt="image-20200707210315102" style="zoom:67%;" />



以上都是小部分基本API的使用操作，其他的在开发中继续摸索吧。



## 4.4、监听服务器节点动态上下线案例

> 需求描述

现在有多台服务器，以及多台客户端，客户端需要从服务器上获取服务，需要实时知道服务器的上下线状态，当某个服务器掉线时保证每台客户端都能知道。



> 实现步骤明确

对于Zookeeper的集群来说，这些服务器、客户端都是Zookeeper的客户端，只不过一个是往zookeeper中存数据一个是通过zookeeper监听数据而已。

![image-20200707221123214](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707221123214.png)

为了方便实现，服务器上线创建的节点使用==临时带顺序序号的节点==



> 代码实现

- 服务器端

  ```java
  public class Server {
  
      private String connectString = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
      private int sessionTimeout = 2000;
      private ZooKeeper zkClient;
  
  
      public static void main(String[] args) {
          Server server = new Server();
          // 连接到zookeeper集群啊
          server.getConnection();
          // 注册服务器到zookeeper集群（创建一个临时带顺序编号的节点）
          server.register(args[0]);
          // 业务逻辑实现
          server.service();
      }
  
      private void service() {
          try {
              // 保持存活
              TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
  
      private void register(String hostname) {
          try {
              zkClient.create(
                      "/servers/server",
                      hostname.getBytes(),
                      ZooDefs.Ids.OPEN_ACL_UNSAFE,
                      CreateMode.EPHEMERAL_SEQUENTIAL);
              System.out.println(hostname + " is Online");
          } catch (KeeperException e) {
              e.printStackTrace();
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
  
      private void getConnection() {
          try {
              zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
                  @Override
                  public void process(WatchedEvent event) {
  
                  }
              });
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }
  ```

  

- 客户端

  ```java
  public class Client {
  
      private String connectString = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
      private int sessionTimeout = 2000;
      private ZooKeeper zkClient;
      private static Client client;
  
      public static void main(String[] args) {
          client = new Client();
          // 连接到Zookeeper集群
          client.getConnection();
          // 获取并输出当前存活的服务器列表
          client.showServerList();
          // 添加监听
          client.watch();
          // 业务逻辑
          client.service();
      }
  
      private void service() {
          try {
              TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
  
      private void watch() {
          try {
              zkClient.addWatch("/servers", AddWatchMode.PERSISTENT);
          } catch (KeeperException e) {
              e.printStackTrace();
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
  
      private  void showServerList() {
          try {
              ArrayList<String> onlineServerHost = new ArrayList<>();
              Stat stat = new Stat();
              List<String> servers = zkClient.getChildren("/servers", false);
              for (String server : servers) {
                  byte[] hostBytes = zkClient.getData("/servers/" + server, false, stat);
                  onlineServerHost.add(new String(hostBytes));
              }
              System.out.println("当前在线的节点：");
              System.out.println(onlineServerHost);
          } catch (KeeperException e) {
              e.printStackTrace();
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
  
      private void getConnection() {
          try {
              zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
                  @Override
                  public void process(WatchedEvent event) {
                      // 每次监听发生变化输出一下在线的服务器
                      client.showServerList();
                  }
              });
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }
  ```



> 测试

首先我们再集群上使用命令创建临时节点进行测试，==在启动客户端前先创建好/servers节点不然会报错。==

![image-20200707224839784](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707224839784.png)



现在我们启动我们写的服务器端代码，配置启动参数

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707225334634.png" alt="image-20200707225334634" style="zoom:80%;" />

![image-20200707225449871](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200707225449871.png)

O**K，测试没有问题。