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

### 3.2.1、数据分区存放策略

> 为什么要分区？分区的优点何在？

这个分区和我们刚才说的log文件分片半毛钱关系没有，不要混淆了。

首先我们要了解分区，一个topic可以创建无限量的分区，而每个分区有管理着若干log段，而单个log的文件大小是受限制的。且生产者的消息就是发到这些分区里面的，若果不分区，每个topic能存下的消息就会非常受限。这就引出了分区的第一个优点：

- ==*当日志大小超过了单台服务器的限制，允许日志进行扩展。每个单独的分区都必须受限于主机的文件限制，不过一个主题可能有多个分区，因此可以处理无限量的数据。*==
  以上是官方的话，说白了就是虽然分区中文件大小受限，但是由于分区的数量是不受限制的，我们就可以无限扩展，等同于可以处理无限量的数据了！！

我们之前也说了，消费者组在消费数据的时候，**同一个分区同时只能由组中一个消费者消费！**这个限制是为什么暂且不说，但是从集群性能来说，多几个分区，就能多几个消费者同时消费消息，何乐而不为？这就是优点二：

- ==*可以作为并行的单元集*==

  多个分区之间是可以被消费者组中多个消费者并行消费的，==提高了并行度==！



> 生产者消息分区存放的策略

首先我们要记住一个点！：生产者生产的消息在Kafka消息队列中存放是要封装为`ProductorRecord`对象的！！
[Kafka JavaAPI手册——KafkaProducer](https://kafka.apachecn.org/10/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html)
在API手册中就能找到这个类：
![image-20200807103206014](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200807103206.png)

先看文档的第一段话：
![image-20200807103435917](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200807103435.png)

一个kv数据发到Kafka，要包含一个要发送到的topic名，以及分区号（可选），和key（可选，和分区有关）和value消息数据。
刚好下面给出了方法列表：
![image-20200807104546865](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200807104546.png)



图中文档的第二段话，就简述了==消息存放分区的策略==：
![image-20200807104728593](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200807104728.png)

- 如果分区号有效，消息直接放入该分区中
- 若每个没有分区号，而有key，使用key的hash值对分区数取模，得到最终的分区号
- 如果没有分区号也没有key，那就循环放置到各个分区中



### 3.2.2、数据可靠性保证

学习之前看看官方文档的关于replication的介绍=> [4.7、replication](https://kafka.apachecn.org/documentation.html#replication)

**数据的可用性和一致性对于这样一个消息系统是非常重要的，所以基本上所有的消息系统、分布式系统都有副本功能来保证数据的可用性。**

Kafka官方认为这个副本功能并不常用，并且还可能对吞吐性能带来影响，Follower一直处于待命的状态也要消耗资源。所以默认配置中replication的数量是1（即只有一个Leader!!）！

==副本功能设置的初衷：为了在Leader遇到故障掉线的时候，Follower能够临危受命担起Leader的重任。这就要求Follower中的数据要尽可能与Leader的数据保持一致（一致性），并且在正常情况下Follower同步Leader的数据的速度要快（延迟低，可用性）！！==

> 一致性如何保证？

生产者在往Partition中追加消息的时候，并不是一股脑往里面发，也要确保消息确实被topic接收并且同步。这个机制叫做**ACK应答机制**由Productor配置，目前我们只需要知道Productor需要一个回应后才会继续写消息。

Follow和普通的Consumer一样，从Leader中拉取消息进行保存写入log文件。

目前有两种常用的副本备份的策略：

1. **多数投票机制**
   2f+1个节点，最多冗余f次故障。
   **优点**：延迟较低，只要前f个节点完成了同步，topic就能响应生产者开始写下一条数据。
   **缺点**：当故障数量大于f，可能就久久无法响应，并且在Leader故障的时候，也迟迟选不出新的Leader来，并且存在Follower数据不完整。

2. **最少同步数**

   f+1个节点，就能冗余f次故障
   **优点**：数据的一致性得以保证，所有的节点同步完成才会响应。
   **缺点**：延迟高，整体的同步速度取决于最慢的那个Follower的同步速度。
   	 如果它死活完成不了同步，那也会久久无法继续读取消息了。

以上的两种策略都存在比较严重的缺点，**Kafka的选择更偏向后者**，但是引出了一个新的东西`a set of in-sync replicas`简称`ISR`，其实我们已经见过面了，只是没有留意。
**Kafka 动态维护者这个同步状态的备份的集合**，ISR集合中都Follower中挑出来的“精英”，集合中的Follower必须和Leader保持高度一致。并且这个==集合中的Follower信息是要写到Zookeeper中的==！**只要这个集合中的Follower都同步完成就视为同步完成。当集合中Follower没有达到精英要求就会被从集合中剔除…**这就解决了死活无法完成同步Follower带来的问题了。

> 精英要求？入选IRS的要求

0.9版本前：两个要求：

- 信息滞后的数量小于一定值（对应配置：`replica.lag.max.messages`）
- 从Leader抓取数据和同步所用的时间小于一定值（对应配置：`replica.lag.time.max.ms`）

但是0.9版本，删除了条件一：
![image-20200807150255103](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200807150255.png)

什么原因？很简单啊。
生产者批次写入数据到Leader的时候，只要批次的数据量**大于**这个`replica.lag.max.message`，简直就是灾难，Leader刚把这批消息追加到log文件后，此时Follower刚要开始拉取的时候，Leader来了一句ISR中的所有Follower都不符合要求，都滚出去！Zookeeper执行删除操作，Follower拉取的差不多了，Leader又重新把他们加到了ISR中。下一批消息一来，又是同样的过程，反反复复Zookeeper的操作就要消耗大部分资源！！

**这样的做法其实是牺牲了数据的一致性但是提高了可用性。**



> Kafka使用ISR的Leader选取 & Unclean leader 选举

使用IRS后，一旦Leader故障 ，新的Leader一定是从IRS集合中产生！！

【问题】：所有的节点就崩了，怎么选？！

==请注意，Kafka 对于数据不会丢失的保证，是基于至少一个节点在保持同步状态，一旦分区上的所有备份节点都挂了，就无法保证了。==

对于这个问题，官方也给出了两种策略：

- **选取第一个恢复的ISR中的副本作为Leader**
  因为它极有可能拥有全部数据，数据丢失的成本最小！但是如果IRS中的副本迟迟不能恢复就会使服务长期不可用。

- **选取第一个恢复的副本（不一定是ISR中）作为Leader**

  它可能会丢失大部分消息，但是能保证服务的最快恢复。

这是**可用性**和**一致性**之间的简单妥协

kafka默认是使用策略二！！当所有的 ISR 副本都挂掉时，会选择一个可能不同步的备份作为 leader。
可以配置unclean.leader.election.enable禁用策略二，这样就会使用策略一了。



> 看看ISR：

![image-20200807152346111](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200807152346.png)





### 3.2.3、ACK应答机制

ack的策略有三种，对应三个可选配置值：

- `ack = 0`: Productor发送消息后，就不管了，也不管你到底有没有收到，有没有同步，我发我的。
  **这种做法很容易导致数据丢失！！**当Leader故障下线，选取Leader期间Productor并不知情，它仍然高高兴兴发着消息，这期间所有的消息也就丢失了。
- `ack = 1`: Productor只要接到了Leader的应答，就继续发消息。
  当Leader正在追加生产者发来的消息的时候发生故障，Follower还没来得及拉取Leader中数据就发生了数据丢失。
- `ack = all(-1)`: Productor要接收到Leader和所有Follower(ISR集合中的Follower)的响应才继续发送消息
  这样的做法看起来万无一失，可是也存在问题：
  - 当副本数量只有1个(只有Leader)的时候，这个配置毫无意义，同样会产生数据丢失。只有配合副本数量>=2d的情况使用才有效果。
  - ==可能存在消息重复的问题！==当所有的ISR中Follower同步完成，Leader还没来得及响应就掉线了，此时重新选举Leader后，又会重新发一遍数据，就造成了数据重复..



### 3.2.4、面向消费者保持消息一致

之前说的 Leader即使和ISR集合中的Follower高度一致，也难免产生消息同步不完整的情况。可能Leader中存放的消息有20条消息，可能ISR中Follower 一个只同步到13条，一个只同步到16条。

【问题】:当消费者正常消费Leader中的数据的时候，Leader崩了，选了那个只同步了16条的Follower上来，结果消费者嚷嚷着：“我不管我不管，我明明都消费到了19了，我现在要20，你不给我不走！！”

这就是我们要解决的消息副本面向消费者保持一致的问题，==前提说明：这个并不能保证数据的完整性，无可避免消息丢失！==

我们先知道两个东西：

- HW(High Watermark, 高水位线)
- LEO(Log End Offset, 日志结束偏移量)

画个图吧：

![image-20200807181641937](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200807181642.png)



这个高水位线就很像木桶效应里面的最高水位，而每个长度不同的副本呢就像木桶的长短不一的木板。

回到问题，为了防止用户的“超前消费”,规定HW之前的消息，对Consumer可见，这样就不会出现换了Leader，Consumer死皮赖脸了。

【新问题】那么后续生产者追加数据的时候，各个副本怎么做呢？不采取措施的话就可能出现生产者的消息 消费者无法消费的问题。

> 在选取了新的Leader后，首先把所有“木板”高于最高水位的部分截掉，这样Leader和Follower就又回到了同一条线上。**但是就难免会出现数据部分丢失的情况。**

其中所有的副本仅限于ISR!!

这俩东西还可以解决Follower故障：当发现了Follower发生了故障会立即将其踢出ISR，此时HW已经变化，恢复后Follower读取故障前的HW,将高于上一次的HW的log截掉，然后和Leader进行同步，直到追上当前的的HW，就可以重新加入ISR。

---



### 3.2.5、消息交付语义以及幂等性

按照惯例，先看官方文档：[消息交付语义](https://kafka.apachecn.org/documentation.html#semantics)

三种交付语义：

- *At most once*——消息可能会丢失但绝不重传。
- *At least once*——消息可以重传但绝不丢失。
- *Exactly once*——这正是人们想要的, 每一条消息只被传递一次.

At most once，很好理解，就是不管收没收到我就传一次。类似于`ack=0`
At least once，至少传一次，如果没有得到响应则重传，直到得到响应为止。可能产生数据重复。
Exactly once，刚刚好一次，这是最理想的状态，消息只传一次并且不会传丢。

前两者分别存在的问题是：消息丢失和消息重复。
我们的理想状态是，==消息完整的情况下，保证不重复！==

所以我们要从At least once开始改造，解决消息重复的问题。谈到消息重复就不得不提到幂等性

> 什么是幂等性？

所谓幂等性，就是同一个操作的多次执行，得到的结果不变，并且不会产生其他副作用。（例如一条消息被反复提交，最终只能有一条提交被持久化。）

Kafka在0.11版本开始，也开启了幂等性的传递选项：

![image-20200807205448792](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200807205448.png)

那么 at least once + 幂等性 = Exactly once；

> 消息传递幂等性如何实现的？

消息是Productor发出去，它自己可能也不知情，那么我们能操作的地方就只有broker和consumer了。按官方文档的说明，其实是对Productor做了一定的改造，并且对消息的封装也做了优化。
首先Broker会给每个Productor一个ID(又称PID)，并且Productor发送的每一条消息，都要有一个序列号（这个序列号就是区分存放信息的关键），这些消息过来就有了三个部分：==<Productor ID, 分区号, 消息序列号>==,**一旦三者同时重复，就可以认为是重复的消息，Leader持久化以后就会覆盖原先的提交。**

那么就有局限性了：

- Productor故障重启后，重新分配的Productor ID不同了，即使是消息重复也检查不到了
- 分区之间的（partition_1 和 partition_2）中重复消息无法检测

所以，==幂等性仅能保证单个生产者会话中单个分区的消息不重复==

---

官方文档还提到，0.11版本加入了类似事务的语义将Productor消息写入topic。自行阅读官方文档学习。



