[toc]

#Kafka学习笔记

终于开始学习大名鼎鼎的消息队列Kafka了，久闻大名了。并且Kafka的官网是我目前看到的所有Apache项目中除了Dubbo之外最好看的官网了！！！而且Kafka的标志是真的酷炫！！激起了我学习的欲望！！

[Kafka官网](http://kafka.apache.org/)
![image-20200805191218203](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200805191218.png)



# 一、Kafka概述

## 1.1、定义

Kafka是一个**分布式**的==基于**发布/订阅模式**的消息队列==（Message Queue，MQ）主要应用于**大数据实时处理领域**。



## 1.2、消息队列（MQ）

见名知意，使用队列来有序存放数据的中间件。

### 1.2.1、传统应用场景——异步处理

常见的场景中，如果当前用户访问服务处于繁忙状态，如果使用传统的同步处理，用户就必须等待服务器响应，这样的情况使用体验极差！

为了提高用户的使用体验，可以将用户的请求消息放入到消息队列中，然后先加载出页面，让用户感觉服务器响应很快，与此同时服务器从消息队列中取出消息进行处理，进行结果呈现。这样的做法就是异步处理。

==异步处理也是常用于调高用户体验的方式之一。==、



### 1.2.2、使用消息队列的优点

> 解耦

在上述案例的场景中，就可以充分体现，使用同步处理方式，用户与服务器之间的收发消息是直连的。使用消息队列后，两者之间的耦合性大大降低，相互可以独立更改，各做各的事情而并不影响！

> 可恢复性

得益于使用消息队列的解耦优点，服务系统中的各个组件之间没有强烈的依赖关系，即使其中一个组件挂掉，消息队列中的数据并不受影响，重新恢复后又可以正常收发处理数据。

> 缓冲

当产生数据的速度大于处理数据的速度时，若不加以控制就会导致系统阻塞，严重甚至导致系统崩溃。而使用消息队列就起到了缓冲作用，协调产生和处理数据的速度，有效控制系统平稳运行。

> 灵活性&峰值处理能力（削峰）

在服务访问量剧增的情况下，会带给服务器巨大的压力可能导致服务崩溃，但是当这种峰值访问量只是偶尔发生，而大多数正常情况下都低于这个峰值(可能导致服务瘫痪的值)时，如果在设计时就按照峰值的处理标准来投入资源搭建损失巨大！！因为大部分时间都无法充分利用这些资源。
而只是在峰值期使用消息队列，发挥其缓冲的功能就能抗住峰值访问带来的压力。（又名**削峰**）这样的做法投入小，灵活性高，并且很好地解决了峰值处理的问题。

> 异步通信

当系统繁忙时，使用消息队列存放用户的消息，服务器按顺序进行处理，完成异步响应，提高用户的使用体验，同时缓解服务器的压力！



### 1.2.3、消息队列的两种模式

[参考博客：消息队列的两种模式](https://www.cnblogs.com/jackytang/p/9011051.html)

> 点对点模式（一对一）

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200805212634.jpeg" alt="img" style="zoom:67%;" />

一个Queue支持多个消费者(Consumer)，但是对于一条消息只能被一个消费者消费，当消息被==主动消费==后后Queue中清除。注意这里的主动消费，消费者是主动到Queue中Pull消息过来消费。





> 发布/订阅模式（一对多）

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200805212651.jpeg" alt="img" style="zoom:67%;" />

- 当消息生产者推送（发布）一个消息到Topic中后，同时有多个消费者消费（订阅）消息，并且消费消息是通过Topic==推送==给Consumer消费。
- 也存在各个Consumer主动从Topic中Pull数据，那么就要求被消费的数据不能被清除，需要保留。kafka就是采用这种（随之产生了==重复消费的问题==！）



## 1.3、Kafka架构

[官方中文文档](https://kafka.apachecn.org/)



<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806101525.webp" alt="img" style="zoom: 67%;" />

这张图可能看起来有点花，仔细看完我们来简单分析图中的各个部分。

首先我们可以把整张图划分三个区域：

- Productor（消息生产者）：负责产出消息
- Kafka Cluster（集群）：每个Kafka集群中由若干个Broker（Kafka服务器）组成
- Consumer Group（消费者组）：每个消费者组由若干个Consumer（消费者）组成。

简化就是`Productor`、`Kafka Broker`、`Consumer`

> Broker

生产者不用多说 ，直接看Broker的内容。
三个Broker像是复制来的。Broker中存在多个`topic`，而每个topic又有多个`partition`分区。每个Topic的所有分区又在其他Broker上存在并且存在主副的关系。

- `topic`:数据按照类别存放，便于消费者 区分消息。每个topic代表一个消息分类。

- `partiton`: 每个topic维护多个partition,partition的存在是为了提高消费消息的速度，==结构上是一个个队列。==
  来看官方的解释：

  ![img](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806103353.png)

  这个图就表示生产者在往topic写入消息的时候是写到topic的多个分区的。

  ***每个分区都是有序且顺序不可变的记录集，并且不断地追加到结构化的commit log文件。分区中的每一个记录都会分配一个id号来表示顺序，我们称之为offset，offset用来唯一的标识分区中每一条记录。***

  这个offset在后面我们谈消费者的时候再详说。

- ==不同Broker之间partition的主从关系==

  架构图中很清楚红字标识的partition就是Leader!!每一个partition leader在其他Broker上还有副本，我们称为Follower。

  主从区分有什么作用呢？

  Follower永远都是Leader的备胎，==消费者在消费消息的时候，只从Leader中去取消息。Follower随时准备接替Leader。==



> Consumer

在学习消息队列的发布订阅模式的时候，参考博客中有说道Kafka的消费模式。

每个消费者组（ConsumerGroup）相当于一个庞大的消费者，并且消费过程是消费者**主动到Topic中拉取数据**！

通过架构图，我们需要知道：

- ==对于一个topic的某个partition，同一个消费组中，只能有一个消费者进行消费==!!(即一个partition对应一个ConsumerGroup，组中只要有一个Consumer在用，其他的就不能动！！)
- 一个消费者可以同时消费 同一个topic的不同partition
- 一个消费者也可以同时消费多个topic!!

所有的消息都是从partition中的Leader中拉取的！！

![img](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806105300.png)



==关于消费顺序，以及消费者重连恢复问题==

我们刚才看到的一个关键字叫`offset`，偏移量。
==kafka为每条在分区的消息保存一个偏移量offset，这也是消费者在分区的位置==。比如一个偏移量是5的消费者，表示已经消费了从0-4偏移量的消息，下一个要消费的消息的偏移量是5

这个就和Flume中TailDir使用的partitionFile.json一样，记录着当前的位置。只要这个`offset`得以保存，消费者重连恢复后仍然可以接着上一次消费的位置继续消费。

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806110058.png" alt="img" style="zoom: 25%;" />

之前的版本中所有的集群相关的要存放的数据都由ZK（Zookeeper）进行存储管理， 现在这些信息都放在了本地。

----

以上内容建议学习官方文档：https://kafka.apachecn.org/intro.html





# 二、快速入门

Kafka版本：Scala 2.11  - [kafka_2.11-0.11.0.0.tgz](https://archive.apache.org/dist/kafka/0.11.0.0/kafka_2.11-0.11.0.0.tgz)

## 2.1、安装配置与部署

1. tar包放到集群上解压

2. 看一下bin目录里面有什么：

   ![image-20200806144349671](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806144349.png)

3. 查看配置目录conf（配置文件挺多的，我们目前只看`server.properties`,后续学习的时候在配置其他的），以下配置需要修改

   - Broker.id和 topic删除开启（默认关闭）

     ```properties
     # The id of the broker. This must be set to a unique integer for each broker.
     broker.id=0
     
     # Switch to enable topic deletion or not, default value is false
     delete.topic.enable=true
     ```

     Broker的ID在集群中应该是唯一的，所以等会分发以后，要把这个值设好！！

   - log文件存放位置：

     ```properties
     log.dirs=/tmp/kafka-logs
     # 改为
     log.dirs=/opt/module/kafka-0.11/logs
     ```

     其实在kafka中log文件中是存放的消息数据！！也叫做日志，当然kafka也有他自己的运行日志！

   - zookeeper连接配置

     ```properties
     zookeeper.connect=localhost:2181
     # 改为
     zookeeper.connect=hadoop102:2181,hadoop103:2181,hadoop104:2181
     ```

4. 分发kafka(记得改一下Broker.id)

5. 群起Zookeeper,使用群起脚本（参考zookeeper大数据学习笔记）

6. kafka单点启动

   `bin/kafka-server-start.sh config/server.properties`这种启动是阻塞式进程启动，可以加上`-daemon`以守护进程启动。

   ![image-20200806144039648](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806144039.png)

7. 模仿zookeeper群起脚本来写一个kafka的群起脚本

   ```shell
   #!/bin/bash
   case $1 in
   "start"){
       for i in hadoop102 hadoop103 hadoop104
       do
           echo "******kafka-$i-start********"
           ssh $i "/opt/module/kafka-0.11/bin/kafka-server-start.sh -daemon /opt/module/kafka-0.11/config/server.properties"
       done
   };;
   
   "stop"){
       for i in hadoop102 hadoop103 hadoop104
       do
           echo "******kafka-$i-stop********"
           ssh $i "/opt/module/kafka-0.11/bin/kafka-server-stop.sh"
       done
   };;
   esac
   ```

   使用脚本时报错：

   ![image-20200806150147593](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806150147.png)

   修改`bin/kafka-run-class.sh`：
   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806150415.png" alt="image-20200806150415822" style="zoom:67%;" />



补充：kafka环境变量配置

```shell
# kafka enviroment
export KAFKA_HOME=/opt/module/kafka-0.11
export PATH=$PATH:$KAFKA_HOME/bin
```

然后使用source /etc/profile重新加载配置文件即可，群起脚本就可以进行简化了。



## 2.2、Kafka命令行操作

> Topic操作

使用`kafka-topics.sh`。

1. 创建topic

   `bin/kafka-topics.sh --create –zookeeper hadoop102:2181 --topic topic名字 --partitions 分区数 --replication-factor 副本数`

   ```shell
   [sakura@hadoop102 logs]$ kafka-topics.sh --create --zookeeper hadoop102:2181 --topic testA --partitions 3 --replication-factor 3
   Created topic "testA".
   ```

   在我们创建的logs目录中就存在这个topic:

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806154418.png" alt="image-20200806154418355" style="zoom:67%;" />

   为了和这些日志文件分开来，最好还是另外创建一个目录来存放topics。

   由于是3个分区，三个副本数也就意味着三台机器上都有三个分区副本，但是还不清楚每个分区的Leader在哪个Broker上。

   

2. 列出所有topic

   `bin/kafka-topics.sh --list –zookeeper hadoop102:2181`

   ```shell
   [sakura@hadoop102 logs]$ kafka-topics.sh --list --zookeeper hadoop102:2181
   testA
   ```

   

3. 输出topic的详细信息

   `bin/kafka-topics.sh --describe –zookeeper hadoop102:2181 --topic topic名字`

   ```shell
   [sakura@hadoop102 logs]$ kafka-topics.sh --describe --topic testA --zookeeper hadoop102:2181
   Topic:testA	PartitionCount:3	ReplicationFactor:3	Configs:
   	Topic: testA	Partition: 0	Leader: 1	Replicas: 1,2,0	Isr: 1,2,0
   	Topic: testA	Partition: 1	Leader: 2	Replicas: 2,0,1	Isr: 2,0,1
   	Topic: testA	Partition: 2	Leader: 0	Replicas: 0,1,2	Isr: 0,1,2
   # 清除列举了testA的 0,1,2三个分区 其Leader依次是 1,2,0 对应我们配置的broke.id
   # testA-0的Leader副本在hadoop103主机上
   # testA-1的Leader副本在hadoop104主机上
   # testA-2的Leader副本在hadoop102主机上
   # 其他字段后面再说
   ```

   

4. 删除topic

   `bin/kafka-topics.sh --delete –zookeeper hadoop102:2181 --topic topic名字`

   ```shell
   [sakura@hadoop102 logs]$ kafka-topics.sh --delete --topic testA --zookeeper hadoop102:2181
   Topic testA is marked for deletion.
   Note: This will have no impact if delete.topic.enable is not set to true.
   ```

   提示说，已经被标记为删除，但是如果没有将`delete.topic.enable`设置为true,这个操作没有任何作用。（我们在server.properties已经将其打开，详情见2.1步骤三）



==注意点！！！replication-factor的参数不能操作当前的broker数量（即Kafka集群节点数量），否则会报错。==

```shell
[sakura@hadoop102 logs]$ kafka-topics.sh --create --zookeeper hadoop102:2181 --topic testA --partitions 3 --replication-factor 5
Error while executing topic command : replication factor: 5 larger than available brokers: 3
[2020-08-06 15:55:18,865] ERROR org.apache.kafka.common.errors.InvalidReplicationFactorException: replication factor: 5 larger than available brokers: 3
 (kafka.admin.TopicCommand$)
```

因为不同于Hadoop中设置副本数，那个是最大副本数，随着节点的添加自动创建的。
而==Kafka是创建topic时就立即创建副本，如果副本数大于了节点数量，效果就是在同一台节点上两个testA-0?! 这当然是不允许的！！==

与之相反，是==允许分区数量大于节点数量的==，testA-0~testA-9在一个节点上不会冲突！



> 日志与数据分离

logs目录是Kafka默认存放日志的位置。所以我们要将消息数据与日志文件分离开。

1. 首先关闭kafka集群和Zookeeper集群

2. 删除kafka中logs目录

3. 因为我们之前操作topic连接了Zookeeper了，所以zookeeper上还残留很多kafka的相关文件：

   ![image-20200806162656215](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806162656.png)

   除了zookeeper都要删除。
   暴力的办法就是删除**zkData(修改后的zookeeper数据目录，参考zookeeper学习笔记)中的version-2**，即删除zookeeper的之前所有的使用记录。（慎用！！）

4. 修改server.properties，将`log.dirs`修改为:(/opt/module/kafka-0.11/data)

5. 重启生效

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806165658.png" alt="image-20200806165658819" style="zoom: 67%;" />



> Consumer & Productor

1. 确保当前已有一个topic，若没有则创建。

   ```shell
   kafka-topics.sh --create --zookeeper hadoop102:2181 --topic testA --partitions 2 --replication-factor 2
   ```

2. 启动消息生产者，连接到broker0（hadoop102:9092）==9092是Kafka的端口!!牢记!!==并准备向testA这个topic中存放消息

   `kafka-console-producer.sh --broker-list hadoop102:9092 --topic testA`

   启动后，进入生产者命令行，随时可以发送消息到topic中。

3. 启动消费者，从topic中消费数据

   连接zookeeper启动：
   `kafka-console-consumer.sh --zookeeper hadoop102:2181 --topic testA`

   提示：Using the ConsoleConsumer with old consumer is deprecated and will be removed in a future major release. Consider using the new consumer by passing [bootstrap-server] instead of [zookeeper].==(告诉我们连接到zookeeper这种方式马上要过时啦！！尝试使用连接bootstrap-server吧）这就是我们之前说的新版本中kafka的数据存放到本地而不是zookeeper上了==

   不过还是可以正常接收消息的：
   ![image-20200806170551852](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806170551.png)

   ----

    

   测试连接bootstrap-server启动消费者：
   `kafka-console-consumer.sh --bootstrap-server hadoop102:9092 --topic testA`

   ![image-20200806180119760](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806180119.png)

   同样可以接受到消息。

   加上`--from-beginning`启动消费者，就会从头开始接受消息，但是**消息是无序**的！！

    

   一个奇怪的东西：
   ![image-20200806180845892](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806180846.png)

   这个`__consumer_offsets`topic 一共50个分区，并且每个分区仅有一个副本（即只有leader）

   这个就是我们使用--bootstrap-server产生的 之前放在zookeeper中的数据文件！！

    

4. 完成消息的生产与消费，我们来看看topic的文件夹里面到底有什么：
   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806181514.png" alt="image-20200806181514652" style="zoom:67%;" />

   这还仅仅是一个分区中的内容，另外一个分区中也存放着一些消息的序列化内容。







# 三、Kafka高级—架构深入

## 3.1、Kafka运作流程及消息文件存储

用图来表示吧：

![image-20200806193741752](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806193741.png)

整个流程中，生产者生产的数据放入哪个分区，消费者组中各个消费者怎么划分消费的分区都有大有讲究的！！

并且在kafka的架构中，==topic只是概念理论上的东西，实际上并不存在，而partition确是在物理上实实在在存在的。==



> 数据文件的存储

首先官方文档中这几段话细品：

![image-20200806194825827](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806194825.png)

![image-20200806194911560](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806194911.png)

![image-20200806194948320](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806194948.png)



首先第一段话中所说的**结构化的commit log文件**正是我们在topic分区目录中看到的像**0000000000.log**的文件，所有的消息数据都存在了这个里面。**同一个分区中每一条消息都有一个唯一offset记录这条消息的"位置"。**

再看第二段话，消息存在log文件中是有期限的！！默认配置是七天，在server.properties中查看=>
以下就是配置文件中log文件的保留策略。

![image-20200806200723366](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806200723.png)

其中提到了`segment file`也就是log文件也是会有大小上限的，达到1G后，就会新建一个log文件。
==其实与log segment file一起创建的还有对应的一个.index文件！！==

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806201158.png" alt="image-20200806201158786" style="zoom:67%;" />

画图表示就是这样的：

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806202005.png" alt="image-20200806202005648" style="zoom: 67%;" />

index文件和log文件 就是以其Segment File的第一条消息的offset命名的！



> index文件和log文件的关系

前面我们说到offset用于标识消息的位置，而log文件和index文件又是以offset命名的，那么通过一个全局的offset值，就可以快速定位到消息在哪个segment中，但是在log中定位到这条消息的位置就遇到了麻烦了。你可以算出消息在log文件中是第x条消息，但是并不知道消息的起始位置和结束位置。

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806204340.png" alt="image-20200806204340053" style="zoom:67%;" />

这时候就要讲到这个index文件了，kafka之所以速度不受信息量影响，就是因为其采用了==分片和索引==每一个.log文件都有一个index文件作为索引，记录着消息在log文件中的**文件偏移量！！（注意是文件偏移量不是那个offset）**

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200806205325.png" alt="image-20200806205325072" style="zoom:67%;" />

那么我要的78条数据，在index里面对应的offset就是78-29=48，然后取到这个log文件中offset为49的文件偏移量，就能快速定位到消息在log文件中的位置了！！

---



## 3.2、Kafka生产者



