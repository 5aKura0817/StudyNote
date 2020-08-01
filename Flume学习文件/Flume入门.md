[toc]

# 一、Flume概述

![image-20200731124717587](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200731124717.png)

## 1.1、Flume定义

Flume是Cloudera(Hadoop的三大发型版本之一公司)提供的==一个高可用，高可靠的分布式的海量**日志采集**、聚合和传输系统==。Flume基于流式架构，灵活简单

> 为什么使用Flume

我们**Java后台的日志数据**需要使用大数据工具来进行**实时**分析处理时候，就需要通过Flume来进行流式采集，交给大数据服务器进行分析计算。解决的就是这个==实时问题==！

如果对实时性要求不高的场景也可以选择每天定时上传日志文件到HDFS上，也就用不到Flume。

==Flume的主要作用：**实时读取服务器本地磁盘的数据，并将数据写入到HDFS！**==

---



## 1.2、Flume基础架构

![image-20200731130154510](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200731130154.png)

Flume在服务器运行时，是一个JVM进程（Agent），运行在JVM虚拟机上。==以事件（Event）的形式将数据从源头传输到目的地。==

> 三大组件

- **Source**：对接外部用于采集日志数据

  <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200731132417.png" alt="image-20200731132417516" style="zoom:67%;" />

- **Channel**：作为缓冲区调节Source读取和Sink写出

  <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200731132640.png" alt="image-20200731132640003" style="zoom:67%;" />

- **Sink**：对接外部用于写出数据

  <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200731132533.png" alt="image-20200731132533499" style="zoom:67%;" />

==以上三个组件在特殊的开发环境下支持自定义！！==



> Event

是Flume在传输数据的传输单元，以Event的形式将数据从源头传输到目的地。
由两部分构成：

- **Header**存放该event的一些属性（KV结构）
- **Body**存放传输的数据内容（字节数组）

结构有点类似网络信息传输的包。

---



# 二、Flume快速入门

[Flume官网地址](http://flume.apache.org/)

[Flume全版本下载地址](http://archive.apache.org/dist/flume/)当前最新版本为1.9.0，**本次我们学习使用1.7.0版本！**

[Flume1.7.0用户使用文档](http://flume.apache.org/releases/content/1.7.0/FlumeUserGuide.html)

## 2.1、安装Flume部署

1. 将压缩包放入/opt/software目录下

2. 解压到/opt/module目录中

   `tar -zxvf /opt/software/apache-hive-1.7.0 -C /opt/module/`

3. 将其目录下conf/目录中flume-env.sh.template改名为flume-env.sh，并进行以下配置

   `export JAVA_HOME=/opt/module/jdk1.8.0_251`





## 2.2、入门案例

### 2.2.1、监控端口数据（官方案例）

> 案例需求：

使用Flume监听端口（NetCat Source），将端口中传输过来的数据 输出到控制台(Logger Sink)。

> 步骤实现

1. 安装netcat工具

   `sudo yum install -y nc`

   > netcat的基本使用

   - 使用`nc -lk port`开启对端口xxx的持续监听（-lk：listen,keep open）（服务端）

     ![image-20200731150725559](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200731150725.png)

   - 在另一台主机上使用`nc hostname port`与对应主机上的端口建立连接（客户端）

   - 然后客户端和服务端就可以相互通信了！！

   - 一旦服务端停止，客户端相继断开。

   

2. 判断44444端口是否被占用

   `sudo netstat -tunlp | grep 44444`

3. 创建Flume-Agent配置文件 `flume-netcat-logger.conf`

   在flume的根目录下创建job文件夹，在job文件夹中创建此配置文件。并添加一下内容(官方的配置文件)：

   ```markdown
   # Name the components on this agent
   a1.sources = r1
   a1.sinks = k1
   a1.channels = c1
   
   # Describe/configure the source
a1.sources.r1.type = netcat
   a1.sources.r1.bind = localhost
   a1.sources.r1.port = 44444
   
   # Describe the sink
   a1.sinks.k1.type = logger
   
   # Use a channel which buffers events in memory
   a1.channels.c1.type = memory
   a1.channels.c1.capacity = 1000
   a1.channels.c1.transactionCapacity = 100
   
   # Bind the source and sink to the channel
   a1.sources.r1.channels = c1
   a1.sinks.k1.channel = c1
   ```
   
   我们先来解读一下这个配置文件做了些什么=>
   
   ```markdown
   # 第一部分：为名为a1的agent进行组件命名
   sources:r1, sinks:k1, channels:c1
   > 发现组件后都有一个s,可以看出在一个Agent中是可以同时存在多个source、channel、sink的
   
   # 第二部分：配置source相关信息
   r1.type: 名为r1的source通过netcat采集获取数据（监听端口）
   r1.bind: 监听端口的主机名
   r1.port: 监听的端口号
   
   # 第三部分：配置sink
   k1.type: 名为k1的sink通过logger来输出数据（控制台输出）
   
   # 第四部分：配置channel
   c1.type: 名为c1的channel使用memory作为缓冲区（内存缓冲）
   c1.capacity: channel的事件(event)容量
   c1.trancactionCapacity: 传输事务的最大事件(event)容量
   
   # 第五部分：source、sink和channel对接配置
   a1.sources.r1.channels
   a1.sinks.k1.channel
   > source配置项是channels，而sink配置项是channel。下面有图可以查看
   > 这就说明channel是可以接受来自多个source的信息的，一个source的信息也可以写到多个channel中
   > channel的数据也可以供多个sink获取数据写出，**而每个sink只能从一个channel中获取数据写出**
   
   ```
   
   以下是官方文档中给出的`NetCat Source`的配置清单：可对应第二部分的source配置，其他更多参照官方文档
   
   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200731145225.png" alt="image-20200731145225334" style="zoom:67%;" />
   
   以下是官方文档给出的`logger sink`的配置清单：对应第三部分sink配置
   
   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200731145710.png" alt="image-20200731145710309" style="zoom:67%;" />
   
   channel也不例外，官方文档也有对应的配置清单
   
   ![image-20200731150109146](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200731150109.png)
   
   ----
   
4. 启动一个Flume-Agent

   `bin/flume-ng agent --conf conf --conf-file job/netcat-flume-logger.conf --name a1 -Dflume.root.logger=INFO,console`

   - `--conf/-c`基本配置文件目录
   - `--conf-file/-f`配置文件名
   - `--name/-n`agent的名字
   - `-Dflume.root.logger=INFO,console`将所有INFO日志消息输出到控制台(附加配置)

   或者使用

   `bin/flume-ng agent -c conf/ -f job/netcat-flume-logger.conf -n a1 -Dflume.root.logger=INFO,console`

   ![image-20200731153619244](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200731153619.png)

   

5. 使用netcat进行连接
   **启动agent之后，运行flume进程的主机就相当一个netcat的服务端！**

   我们之前的主机名写的是localhost，所以我们连接也只能在本地上连接
   `nc localhost 44444`，如果要跨主机连接，可以将配置文件中bind主机名改为hadoop102

   

6. 测试发送信息

   ![image-20200731154408566](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200731154408.png)

   可以看到Flume控制台输出的内容确实是以Event为单位，分为headers和body两大部分！！





### 2.2.2、实时监控单个追加文件

> 案例需求：

实时监控hive的日志文件(/opt/module/hive-1.2.2/logs/hive.log)

- exec source
- memory channel
- logger sink



> 实现步骤

1. 配置文件`file-flume-logger.conf`

   除了第二部分的source配置需要查看官方文档的配置清单，其他可以完全照抄

   ```markdown
   # Name the components on this agent
   a1.sources = r1
   a1.sinks = k1
   a1.channels = c1
   
   # Describe/configure the source
   a1.sources.r1.type = exec
   a1.sources.r1.command = tail -F /opt/module/hive-1.2.2/logs/hive.log
   
   # Describe the sink
   a1.sinks.k1.type = logger
   
   # Use a channel which buffers events in memory
   a1.channels.c1.type = memory
   a1.channels.c1.capacity = 1000
   a1.channels.c1.transactionCapacity = 100
   
   # Bind the source and sink to the channel
   a1.sources.r1.channels = c1
   a1.sinks.k1.channel = c1
   ```

   官方给出的exec source配置清单：

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200731160913.png" alt="image-20200731160913017" style="zoom:67%;" />

   官方示例：

   ```
   a1.sources = r1
   a1.channels = c1
   a1.sources.r1.type = exec
   a1.sources.r1.command = tail -F /var/log/secure
   a1.sources.r1.channels = c1
   ```

   `tail`命令官方的推荐使用`-F`而非`-f`两者的区别在于，==使用-F时，当文件发生变化导致监控失败，会自动进行尝试重新监控。==

   ---

2. 启动flume-agent，开始监控hive.log

   `bin/flume-ng agent -c conf/ -f job/file-flume-logger.conf -n a1 -Dflume.root.logger=INFO,console`

3. 启动hive，执行查询，观察flume的控制台输出情况

   首先会读取日志文件的末尾10行，输出到控制台。

   ![image-20200731161929109](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200731161929.png)

   监控成功！！



### 2.2.3、升级版

将控制台的输出内容转移输出到HDFS上！

- HDFS sink

> 准备工作

将以下jar包导入到flume的lib目录下：（这里的Hadoop相关Jar包最好与Hadoop环境版本相同）
![image-20200801095311487](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801095311.png)



> 配置项解读

查看官方给出的配置清单，==HDFS sink在生产环境中使用的频率还是比较高的==！

![](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801102735.png)

除了默认黑体的必须配置以外，以上圈出的都是常用的HDFS sink的配置

来看一个官方给出的配置示例：
![image-20200801102852708](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801102852.png)

HDFS sink的别名大全：
<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801103724.png" alt="image-20200801103724541" style="zoom:67%;" />



**来对这些常用的配置项做一下详细的解读：**

1. 文件前、后缀

   - `filePrefix`
   - `fileSuffix`

   在向HDFS写入文件的时候，自定义文件的前后缀，可以使用别名。例如

   ```markdown
   a1.sinks.k1.hdfs.filePrefix=%t
   a1.sinks.k1.hdfs.fileSuffix=.log
   ```

2. roll文件滚动

   在向HDFS中写入的数据的时候，总不能只向一个文件中写入的吧，当达到一定条件时，需要强制滚动文件（切换到一个新文件），这样===利于数据的分离，避免文件太大影响效率。但是如果配置不当又会导致大量的小文件产生！！==

   - `rollInterval` : 文件滚动之间的时间间隔（默认30s）
   - `rollSize`: 滚动前，文件大小上限（默认1024kb）
   - `rollCount`：滚动前 写入事件的最大次数（默认10）

   以上三个配置项，只要**满足其一**就会进行一次文件滚动！！**三个配置项都可以配置为0，代表不作为判断项。**例如：

   ```markdown
   # 每60s滚动一次
   a1.sinks.k1.hdfs.rollInterval=60
   # 文件大小达到128×1024×1024=134217728 实际配置时尽量比这个小！保证一个文件能存放到一个块中 
   a1.sinks.k1.hdfs.rollSize=134217700
   # rollCount不作为判断项，无论多少的Event都不影响
   a1.sinks.k1.hdfs.rollCount=0
   ```

   ==可以看到当前的环境下其实使用默认的配置中很容易产生小文件，所以根据业务需求适当配置！！==

3. 批处理写入文件

   `batchSize`: 当Event数量达到一定值，flush写入到HDFS中。（默认100）

   其实到达一定时间也会被强制刷新写入到HDFS

4. 文件存储格式和编码解码器

   - `codeC`：编/解码器(可选：gzip, bzip2, lzo, lzop, snappy)==仅当文件格式（fileType）是CompressedStream时配置！！==

   - `fileType`: 文件存储格式（默认SequenceFile，可选：`SequenceFile`or`DataStream`or`CompressedStream`）

5. 文件夹滚动

   除了之前说的文件需要不断滚动，当我们需要配合使用Hive的分区表的时候，我们也希望定时切换文件夹，例如每天一个文件夹，文件夹下每个小时又分一个文件夹。==如果要使用此功能，必要配置中hdfs.path就不能写死而是使用别名！==否则滚来滚去都是这个文件夹没有效果！！

   - `round`： 是否开启文件夹滚动（默认为false）需要设置为true才能生效
   - `roundValue`：滚动的间隔单元(Unit)数（默认1，每过一个Unit滚动一次）
   - `roundUnit`：滚动的间隔单元单位（默认second，可选：`second`, `minute` or `hour`.）

6. 使用本地时间戳

   `useLocalTimeStamp`(默认false，使用header中的时间戳），由于我们现在的header中什么都没有，如果不设置为`true`,那么所有与时间相关的都不会生效。

---



> 实现步骤

1. Flume配置文件`file-flume-hdfs.conf`

   ```markdown
   # Name the components on this agent
   a1.sources = r1
   a1.sinks = k1
   a1.channels = c1
   
   # Describe/configure the source
   a1.sources.r1.type = exec
   a1.sources.r1.command = tail -F /opt/module/hive-1.2.2/logs/hive.log
   
   # Describe the sink
   a1.sinks.k1.type = hdfs
   a1.sinks.k1.hdfs.path=hdfs://hadoop102:9000/flume/%Y%m%d/%H
   # 文件前缀
   a1.sinks.k1.hdfs.filePrefix=log-
   # 文件夹滚动
   a1.sinks.k1.hdfs.round=true
   a1.sinks.k1.hdfs.roundValue=1
   a1.sinks.k1.hdfs.roundUnit=hour
   # 使用本地时间戳
   a1.sinks.k1.hdfs.useLocalTimeStamp=true
   # 批处理最大数量
   a1.sinks.k1.hdfs.batchSize=1000
   # 文件存储类型
   a1.sinks.k1.hdfs.fileType=DataStream
   # 文件滚动
   a1.sinks.k1.hdfs.rollInterval=60
   a1.sinks.k1.hdfs.rollSize=134217700
   a1.sinks.k1.hdfs.rollCount=0
   
   
   # Use a channel which buffers events in memory
   a1.channels.c1.type = memory
   a1.channels.c1.capacity = 1000
   a1.channels.c1.transactionCapacity = 100
   
   # Bind the source and sink to the channel
   a1.sources.r1.channels = c1
   a1.sinks.k1.channel = c1
   ```

2. 启动开始监控

   `bin/flume-ng agent -n a1 -c conf/ -f job/file-flume-hdfs.conf `

   

![image-20200801113810311](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801113810.png)

每当日志变化时，在文件滚动的间隔时间内，所有的事件都会被 传输写入到临时文件(.tmp)中！文件滚动时间到达且有新的Event过来就会重新创建文件，之前的临时文件变为持久性文件！==如果只是到了滚动时间，而没有Event过来是不会创建文件的！==

---



### 2.2.4、监控文件夹变化

> 案例需求

实时监控一个文件夹下的文件添加，并实时同步到HDFS上。

- Spooling Directory Source
- HDFS sink



> Spooling Directory Source配置清单

![image-20200801151944887](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801151944.png)



1. 基本配置`spoolDir`

   写明要监控的文件夹，只能监控文件的个数的增加，但是==并不能监测到文件内容的变化！！==

2. `fileSuffix`：完成同步后添加的文件后缀

   当被监控的目录下的文件被同步到了HDFS上时，会为本地文件系统中的文件加上后缀（默认`.COMPLETED`）

3. `includePattern`,`ignorePattern`文件‘黑白名单’

   使用正则表达式，设置哪些文件同步，哪些不同步。



> 实现步骤

1. 创建一个文件夹用于监控（/opt/module/flume-1.7.0/upload）

2. 配置文件`spooldir-flume-hdfs.conf`

   修改2.2.3、的配置文件中source部分即可：

   ```markdown
   a1.sources.r1.type = spooldir
   a1.sources.r1.spoolDir = /opt/module/flume-1.7.0/upload/
   ```

3. 启动

   `bin/flume-ng agent -n a1 -c conf/ -f job/spooldir-flume-hdfs.conf`

4. 测试

   启动时upload文件夹下什么都没有，所以HDFS上也没有输出文件。

   - 创建文件test.txt 拷贝到 upload/
     HDFS上出现输出文件，大小和test.txt内容一样！ upload/文件夹中的test.txt被加上了COMPLETED后缀！

     <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801154309.png" alt="image-20200801154309343" style="zoom:67%;" />

   - 测试直接在upload/下创建文件

     也可以正常同步到。但是==当同步过后，继续修改文件是同步不到修改的内容的！！！==

   - 尝试重复添加同一文件

     <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801154714.png" alt="image-20200801154714444" style="zoom:67%;" />

      ![image-20200801154740860](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801154740.png)

     文件内容被成功上传！！但是文件名并没有被修改，查看flume日志，出现报错

     <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801154953.png" alt="image-20200801154953225" style="zoom:67%;" />

   - 尝试添加.COMPLETED后缀的文件

     <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801155220.png" alt="image-20200801155220833" style="zoom:67%;" />

     ![image-20200801155317429](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801155317.png)

     HDFS上并没有同步到！！！

5. 增加配置再次测试

   ```
   # 不同步xx.tmp文件
   a1.sources.r1.ignorePattern=([^ ]*\.tmp)
   ```

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801162345.png" alt="image-20200801162345630" style="zoom:67%;" />

    果然test4.tmp就没有被同步到！！

   

> 测试总结

- 上传的文件，同步输出到HDFS上，==只同步文件内容！！==
- 对已上传同步的文件进行修改，==修改的内容无法被同步监测到！！==
- ==不要多次上传同一文件，会导致文件内容同步成功，但是在修改文件名的时候失败！==
- `fileSuffix`==尽量选择未使用的后缀，==否则会导致无法被监控到！





### 2.2.5、断点续传——实时监控目录文件追加修改

> 案例需求

监控一个或者多个的文件修改，并且支持**断点续传**

- Taildir source



> Exec Source监控文件变化存在的问题：

1. 不支持断点续传

   如果生产环境中，Flume挂掉，再次启动的时候只能传输最后10行的数据！！

2. 正常情况下有数据重复

   每次启动都要将文件的最后10行传输，可能存在数据的重复。

3. 使用 -c +0每次从文件头扫描监控

   可以解决生产时挂掉导致大部分数据丢失的问题，但是平时使用数据重复率飙升！

4. 只能监控一个文件

这几个问题就决定了==ExecSource不适合用来做文件内容追加修改监控==

而使用**TailDir，使用了一个positionFile记录上一次传输的位置，即使挂掉，中间的文件修改在重启之后也会被正确传输！**
**而且可以同时监控多个文件！！**



> TaildirSource配置清单

![image-20200801170006523](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801170006.png)



- `filegroups`：文件组，多个组使用**空格**分隔
- `filegroups.<filegroupName>`：为每个文件组设置监控文件的路径！（每个文件组只能有一个监控文件！！多次设置会进行覆盖，仅最后一次有效）
- `positionFile`：用于记录上一次传输位置的json文件的存放位置

官方示例：

![image-20200801171016264](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801171016.png)



> 实现步骤

1. 创建一个文件夹用于监控（/opt/module/files）

2. 配置文件`taildir-flume-logger.conf`

   ```markdown
   # Name the components on this agent
   a1.sources = r1
   a1.sinks = k1
   a1.channels = c1
   
   # Describe/configure the source
   a1.sources.r1.type = TAILDIR
   a1.sources.r1.filegroups=f1 f2
   a1.sources.r1.filegroups.f1=/opt/module/flume-1.7.0/files/file1.txt
   a1.sources.r1.filegroups.f2=/opt/module/flume-1.7.0/files/file2.txt
   a1.sources.r1.positionFile=/opt/module/flume-1.7.0/positionFile/position.json
   
   # Describe the sink
   a1.sinks.k1.type = logger
   
   # Use a channel which buffers events in memory
   a1.channels.c1.type = memory
   a1.channels.c1.capacity = 1000
   a1.channels.c1.transactionCapacity = 100
   
   # Bind the source and sink to the channel
   a1.sources.r1.channels = c1
   a1.sinks.k1.channel = c1
   ```

3. 启动

   `bin/flume-ng agent -n a1 -c conf/ -f job/taildir-flume-logger.conf -Dflume.root.logger=INFO,console`

4. 测试

   第一次启动就输出了两个文件夹中已有的内容：

   ![image-20200801172018012](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801172018.png)

   是因为positionFile里面对两个文件的位置记录都为0。

    

   向文件中追加内容

   ![image-20200801172207271](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801172207.png)

   ​	

   关闭Flume进程模拟掉线，继续追加内容到文件中，然后重启Flume
   ![image-20200801172408967](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801172409.png)

   得益于positionFile的存在，即使Flume进程挂了，再次启动也能继续传输，甚至断线期间的修改也能被正常传输！！ 

   

5. 查看positionFile

   ![image-20200801172531520](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801172531.png)

   使用inode(文件的唯一标识)对应文件，并记录着两个文件的上一次传输位置以及文件的路径！
   即使==文件中途发生了重命名，移动会失去监控，在恢复到原始位置和名字时又会重新被监控。==





# 三、Flume进阶

## 3.1、Flume事务

![img](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200801203913.png)

> 事务作用

Flume作为一个数据传输工具，保证数据的安全性和传输的可靠性就显得尤为重要！！而事务特性的引入就很好保证了这两个问题！！



> Flume中事务的结构

在一个Flume Agent中同时存在两个事务

- 推送Event的**Put事务**
- 拉取Event的**Take事务**



> **Put事务**

完整流程：
Source从外部读取数据后封装为Event对象实例，批次提交到一个事务中。事务中使用`doPut`方法将批数据中的Event加入到临时缓冲区(`putList`)
当缓冲区中数据达到一定数量或者批次数据传输完成，使用`doCommit`推送缓冲区的Event，并检查Channel中内存队列中是否足够存放这些Event，若全部成功推送则清空临时缓冲区，若Channel内存不足则使用`doRollback`将数据回滚。

三个方法，调用顺序：

1. `doPut`
2. `doCommit`
3. `doRollback`

一个区，临时缓冲区`putList`



> **Take事务**

完整流程：
Sink创建一个事务，使用`doTake`将Event从Channel队列批次取出放入缓冲区`takeList`，缓冲区数据到达一定数量使用`doCommit`准备将缓冲区的数据发送给Sink写出，若数据全部成功发送清空缓冲区，否则调用`doRollback`将缓冲区数据归还到Channel内存队列中。

三个方法，调用顺序

1.  `doTake`
2. `doCommit`
3. `doRollback`

一个区，缓冲区`takeList`

---

了解这些方法的调用过程和事务的执行过程，是==便于我们后续编写自定义Source、Sink时调用对应的方法来用代码实现事务功能。==