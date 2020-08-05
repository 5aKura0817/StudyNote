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



### 2.2.3、监控文件 升级版

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





## 3.2、Agent内部原理

参考博客：

[Flume Agent 内部原理概述](https://www.jianshu.com/p/c57e43424813)

[Flume Sink组、Sink处理器](https://www.jianshu.com/p/d50ab471c012)

> 基础架构复习

Flume Agent中包含三大组件：Source、Channel、Sink。

以上三个组件都运行在Agent(一个JVM应用程序)上，且数量没有限制！
**每个Source至少对接一个Channel，每个Sink只能对接一个Channel，但是每个Channel可以对接多个Source以及多个Sink!!**

**数据在Flume中间传输以事件（Event）作为基本单位，Event的数据结构包含头部信息(Header)和数据体(Body)**



> 新内容导入

接下来我们会将Event在Flume中传输的过程进行拆分，了解学习内部的原理，需要引入以下几个新的概念：

- Channel处理器（Channel Processor）
- 拦截器（Interceptor）
- Channel选择器（ChannelSelector）
- Sink组（SinkGroup）
- Sink运行器（Sink Runner）
- Sink处理器（SinkProcessor）

我们先用两张图来看一下他们之间的运作流程：

Source和Channel交互：

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200803124245.png" alt="image-20200803124245387" style="zoom:67%;" />

Channel和Sink交互：

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200803130946.png" alt="image-20200803130946602" style="zoom:67%;" />



简述以下Agent中工作流程：

1. Source从特定的位置读取到数据并封装为Event对象

2. 封装好的批次Event，交给ChannelProcessor（每个Source都有自己的ChannelProcessor）

3. Channel处理器会将这些Event使用拦截器做一遍过滤，筛选出符合要求的Event，返回到Channel处理器

4. Channel处理器访问ChannelSelector，通过选择器来确定这些Event发到哪个Channel中

   ==ChannelSelector官方提供了两种分别对应两种选择策略，同时**支持自定义**！！==

   `Replicating Channel Selector (default)`: 所有与Source绑定的Channel都发送

   `Multiplexing Channel Selector`：自定义Source的数据发往那些Channel

5. Channel接收到Event后，与SinkRunner对接，每个SinkRunner运行一个Sink组，一般Sink组用于RPC Sink，在层之间以负载均衡或者故障转移方式交发送数据

6. 每个Sink组中可以有多个Sink，组中每个Sink单独配置，包括从哪个Channel中拉取数据等。

7. SinkProcessor决定了对应的Sink组中哪个Sink来拉取哪个Channel中的数据
   ==SinkProcessor官方有三种，也支持自定义==

   `Default Sink Processor`:只有一个Sink,不强制要求Sink组

   `Failover Sink Processor`: 故障转移

   `Load Balance Processor`: 负载均衡





## 3.3、拓扑结构

当需要跨机器去读取数据时候，就需要在不同的机器上运行Agent，Agent之间的信息传递就需要使用网络拓扑，官网给出了三种拓扑结构。

### 3.3.1、简单串联

![Two agents communicating over Avro RPC](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200803150917.png)

在节点之间交换数据的时候，通常会用到AVRO Sink和AVRO Source，分别设置在两个Agent中，然后通过AVRO RPC进行数据传输！两个节点就能被串联起来，方便取数据。



### 3.3.2、合并（Consolidation）

![A fan-in flow using Avro RPC to consolidate events in one place](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200803153105.png)

通过图就能理解，当需要从多个位置（节点）读取数据的时候，此种拓扑结构可以很好地解决这个问题，有两种实现方式：

- 可以是多个节点往固定的端口发送数据然后使用一个Agent在端口接收数据进行合并
- 也可以是多个节点各自选择端口，然后Agent使用多个Source来从这些端口中接受数据进行合并



### 3.3.3、多路复用

![A fan-out flow using a (multiplexing) channel selector](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200803153507.png)

当一个位置的数据想要发送到多个目的地。这种拓扑结构就能解决问题，但是明显ChannelSelector就要使用默认的ReplicatingChannelSelector



### 3.3.4、负载均衡和故障转移

其拓扑结构刚好**与合并相反**，使用一台接收事件，然后使用Sink组转发到多个的Agent中去完成写出的工作。多个Agent增大了缓存Event的数量，降低了单个Agent的写出压力，并且提高了可用性。

---



## 3.4、开发案例

### （一）、复制以及多路复用

> 案例描述：

启动三个Flume Agent，其中**Agent1**负责监控文件的变动，使用**AVRO RPC**与Agent2进行数据交换，**Agent2**将采集的数据写入HDFS，同时Agent1与Agent3同样使用AVRO RPC进行数据交换，**Agent3**将采集数据写入本地文件夹

图示：

![image-20200803183029889](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200803183029.png)

看到这个图会不会很好奇，在Agent1中同样是数据为什么需要两个MemoryChannel和Sink,这就涉及到我们所说的**Sink组**了，同一个Sink组中除了默认的SinkProcessor外，就只有负载均衡和故障转移的SinkProcessor了，注定同一个Sink组中的所有Sink的写出位置都是相同的，那么==想要写到不同的位置就需要多个Sink组。==



> 配置清单与配置文件

**FileRoll Sink:**

![image-20200803172019939](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200803172020.png)



**Avro Sink:**

![image-20200803172109147](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200803172109.png)



**Avro Source:**

![image-20200803172212110](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200803172212.png)



可以看出AVRO是专门一套用于节点端口数据传递的 Source和Sink,其配置和NetCat十分相似！
由于是三个Agent，所以就需要三个配置文件：

Agent1:

```markdown
# 组件命名
a1.sources = r1
a1.channels = c1 c2
a1.sinks = k1 k2

# source配置（TailDir Source）
a1.sources.r1.type = TAILDIR
a1.sources.r1.filegroups = file1
a1.sources.r1.filegroups.file1 = /opt/module/data/flume_data/example1.log
a1.sources.r1.positionFile=/opt/module/flume-1.7.0/positionFile/example1_position.json

# sink配置（AVRO Sink）
a1.sinks.k1.type = avro
a1.sinks.k1.hostname = hadoop102
a1.sinks.k1.port = 4545

a1.sinks.k2.type = avro
a1.sinks.k2.hostname = hadoop102
a1.sinks.k2.port = 4646

# Channel配置 （MemoryChannel）
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

a1.channels.c2.type = memory
a1.channels.c2.capacity = 1000
a1.channels.c2.transactionCapacity = 100

# 对接Channel
a1.sources.r1.channels = c1 c2
a1.sinks.k1.channel = c1
a1.sinks.k2.channel = c2
```

Agent2:

```markdown
# 组件命名
a2.sources = r1
a2.channels = c1
a2.sinks = k1

# Source配置 （AVRO Source）
a2.sources.r1.type = avro
a2.sources.r1.bind = hadoop102
a2.sources.r1.port = 4545

# Sink配置
a2.sinks.k1.type = hdfs
a2.sinks.k1.hdfs.path=hdfs://hadoop102:9000/flume/%Y%m%d/%H
    # 文件前缀
a2.sinks.k1.hdfs.filePrefix=log-
    # 文件夹滚动
a2.sinks.k1.hdfs.round=true
a2.sinks.k1.hdfs.roundValue=1
a2.sinks.k1.hdfs.roundUnit=hour
    # 使用本地时间戳
a2.sinks.k1.hdfs.useLocalTimeStamp=true
    # 批处理最大数量
a2.sinks.k1.hdfs.batchSize=1000
    # 文件存储类型
a2.sinks.k1.hdfs.fileType=DataStream
    # 文件滚动
a2.sinks.k1.hdfs.rollInterval=60
a2.sinks.k1.hdfs.rollSize=134217700
a2.sinks.k1.hdfs.rollCount=0

# Channel配置 （MemoryChannel）
a2.channels.c1.type = memory
a2.channels.c1.capacity = 1000
a2.channels.c1.transactionCapacity = 100

# 对接Channel
a2.sources.r1.channels = c1
a2.sinks.k1.channel = c1
```

Agent3:

```markdown
# 组件命名
a3.sources = r1
a3.channels = c1
a3.sinks = k1

# Source配置 （AVRO Source）
a3.sources.r1.type = avro
a3.sources.r1.bind = hadoop102
a3.sources.r1.port = 4646

# Sink配置 （File Roll Sink）
a3.sinks.k1.type = file_roll
a3.sinks.k1.sink.directory = /opt/module/flume-1.7.0/files/

# Channel配置 （MemoryChannel）
a3.channels.c1.type = memory
a3.channels.c1.capacity = 1000
a3.channels.c1.transactionCapacity = 100

# 对接Channel
a3.sources.r1.channels = c1
a3.sinks.k1.channel = c1
```



> 启动测试

==注意启动顺序！！先启动下游！ARVO RPC中 AVRO Source是服务端，AVRO Sink是客户端。==

`bin/flume-ng agent -n a3 -c conf/ -f job/example1/agent3.conf`

`bin/flume-ng agent -n a2 -c conf/ -f job/example1/agent2.conf`

`bin/flume-ng agent -n a1 -c conf/ -f job/example1/agent1.conf`



测试情况：

RollFileSink的输出文件夹下每30秒生成一个文件，文件中只有变化的内容，若没有对监控文件修改也会生成文件但是内容为空！
<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200803193848.png" alt="image-20200803193847988" style="zoom:67%;" />

HDFS上成功生成文件记录着修改内容：
<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200803193941.png" alt="image-20200803193941510" style="zoom:67%;" />

----





### （二）、负载均衡和故障转移

> 故障转移案例

配置三个Flume Agent，Agent1使用NetCat Source接受数据，然后使用**Sink组** 使用两个AVRO Sink与Agent2、Agent3连接，Agent2和Agent3将接受数据输出到控制台（logger Sink）。期间停掉Agent2或Agent3中任意一台，查看变化。

![image-20200804092649035](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200804092649.png)



> FailOver Sink Processor配置清单

![image-20200804091435032](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200804091435.png)



根据文档中所给的提示：**要创建一个Sink Group，并且为其中每个Sink指定一个唯一的priority（优先级），优先级越高优先从Channel中拉取事件。**

`maxpenalty`：故障转移的时间上限。（默认30000ms=30s）意思是：从知道机器挂掉后**最长**30s内都不会去尝试给这个Agent发送数据（即使30s内已经恢复），30s后才会重新尝试建立连接发送数据。



> 三个配置文件

Agent1

```markdown
# 组件命名
a1.sources = r1
a1.sinks = k1 k2
a1.channels = c1

# Source配置 NetCat
a1.sources.r1.type = netcat
a1.sources.r1.bind = hadoop102
a1.sources.r1.port = 44444

# Sink配置 AVRO Sink
a1.sinks.k1.type = avro
a1.sinks.k1.hostname = hadoop102
a1.sinks.k1.port = 4545

a1.sinks.k2.type = avro
a1.sinks.k2.hostname = hadoop102
a1.sinks.k2.port = 4646

# Channel配置 MemoryChanne
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

# 对接Channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1
a1.sinks.k2.channel = c1

# FailOver Processor配置
a1.sinkgroups = g1
a1.sinkgroups.g1.sinks = k1 k2
a1.sinkgroups.g1.processor.type = failover
a1.sinkgroups.g1.processor.priority.k1 = 5
a1.sinkgroups.g1.processor.priority.k2 = 10
a1.sinkgroups.g1.processor.maxpenalty = 10000
```



Agent2:

```markdown
a2.sources = r1
a2.sinks = k1
a2.channels = c1

# Source配置 AVRO Source
a2.sources.r1.type = avro
a2.sources.r1.bind = hadoop102
a2.sources.r1.port = 4545

# Sink配置 Logger Sink
a2.sinks.k1.type = logger

# Channel配置 MemoryChannel
a2.channels.c1.type = memory
a2.channels.c1.capacity = 1000
a2.channels.c1.transactionCapacity = 100

# 对接Channel
a2.sources.r1.channels = c1
a2.sinks.k1.channel = c1
```

Agent3相同，只需修改agent名字和 AVRO Source接收的端口就行了。。此次省略



> 启动测试

![image-20200804095036080](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200804095036.png)

存活Agent中优先级最高的拉取数据。
由于故障挂掉的Agent重启之后，仍然可以接收数据！！

在Flume的日志文件中，就能看到当我们关闭Agent的时候，会提示Agent被加入到了FailOver List:

![image-20200804100119323](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200804100119.png)



---



> 负载均衡（LoadBalance Processor）配置清单：

![image-20200804105311875](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200804105311.png)

简直不要太简单！！！完全可以使用官方给出的配置，默认使用轮询进行负载均衡！

这里的backoff关联到**退避算法**，是对Agent挂掉后机器选择的一种策略，建议开启。

----



### （三）、聚合（Consolidation）

> 案例描述

还是三个Agent，不过这次不同三个Agent分布在三台主机上，Agent1接收NetCat监听端口发送的数据，Agent2使用TailDir监控文件，两者使用AVRO Sink与Agent3建立连接，Agent3做聚合处理，将接受的数据输出到控制台

![image-20200804144820912](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200804144821.png)



> 配置文件

Agent1

```markdown
# 组件命名
a1.sources = r1
a1.channels = c1
a1.sinks = k1

# Source配置 TailDir Source
a1.sources.r1.type = TAILDIR
a1.sources.r1.filegroups = f1
a1.sources.r1.filegroups.f1 = /opt/module/data/flume_data/example3.log
a1.sources.r1.positionFile = /opt/module/flume-1.7.0/positionFile/example3_position.json

# Sink配置 AVRO Sink
a1.sinks.k1.type = avro
a1.sinks.k1.hostname = hadoop104
a1.sinks.k1.port = 4545

# Channel配置 MemoryChannel
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

# 对接Channe
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1
```



Agent2

```markdown
# 组件命名
a2.sources = r1
a2.channels = c1
a2.sinks = k1

# Source配置 NetCat Source
a2.sources.r1.type = netcat
a2.sources.r1.bind = hadoop102
a2.sources.r1.port = 44444

# Sink配置 AVRO Sink
a2.sinks.k1.type = avro
a2.sinks.k1.hostname = hadoop104
a2.sinks.k1.port = 4545

# Channel配置 MemoryChannel
a2.channels.c1.type = memory
a2.channels.c1.capacity = 1000
a2.channels.c1.transactionCapacity = 100

# 对接Channe
a2.sources.r1.channels = c1
a2.sinks.k1.channel = c1
```



Agent3

```markdown
# 组件命名
a3.sources = r1
a3.channels = c1
a3.sinks = k1

# Source配置 AVRO Source
a3.sources.r1.type = avro
a3.sources.r1.bind = hadoop104
a3.sources.r1.port = 4545

# Sink配置 Logger Sink
a3.sinks.k1.type = logger

# Channel配置 MemoryChannel
a3.channels.c1.type = memory
a3.channels.c1.capacity = 1000
a3.channels.c1.transactionCapacity = 100

# 对接Channe
a3.sources.r1.channels = c1
a3.sinks.k1.channel = c1
```



> 启动测试

![image-20200804153212711](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200804153212.png)

这只是实现方式的中的一种：

- ==需要合并的Agent往合并Agent所在的主机的同一个端口发送数据，用于合并的Agent从那个端口中取数据进行集中输出。==

还有一种就是：

- ==需要合并的Agent自定义选择合并Agent所在主机的端口，然后用于Agent从这些个端口中取出数据然后进行集中输出。==



---



## 3.5、自定义Interceptor(拦截器)

> 拦截器

首先拦截器是什么不用多说了吧，字面意义上很容易理解，就是把xx拦住，然后做一些不可描述的事情【坏笑】，然后放行。或者说直接原路打回，不处理。

==在Flume中拦截器作用于Event，不可描述的操作就是在Event的头部(Header)添加一些东西，或者也可以修改Body部分。==

使用拦截器在Header中添加一些KV键值对后，就可以方便后续配合使用`Multiplexing Channel Selector`，利于对Event的分类。



> 为什么Flume需要拦截器？

小小Flume,一个数据传输的中间件，为什么要用拦截器大做文章？

**首先，生产环境中我们使用Flume采集的多方数据，我们希望这些数据能够被区分开来放到不同的位置！！**
杠精：那你就用独立的Agent串联啊，哪个地方的到哪去一条条线写清楚不就能控制了！

**其次，这些数据中存在脏数据我们需要剔除**
杠精：这。。。

**最后，我们希望即使是发到同一个目的地（例如HDFS），也能够按照数据的内容进行分类然后放到不同的位置（例如不同目录）**
杠精：拦截器，算你nb



==拦截器配置Multiplexing Channel Selector实现多路复用，就可以完美解决上面这些需求！！==
拦截器在头部添加标志，多路选择器根据头部的标志信息，决定Event去向的Channel。



> Interceptor接口

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200804195143.png" alt="image-20200804195143082" style="zoom:67%;" />

四个方法：

- `initalize()`初始化方法，每次使用拦截器只调用一次
- `intercept(Event event)`对单个事件的拦截处理
- `intercept(List<Event> eventList)`对批事件的处理
- `close()`拦截器使用结束，用于关闭释放资源

内部还有一个接口，在我们实现的时候，也要对应创建一个内部类实现此接口（Builder）并重写两个方法

- `build()`用于实例化拦截器
- `configure()`用于配置拦截器的相关一些额外配置





已有的实现类：

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200804200422.png" alt="image-20200804200422383" style="zoom: 80%;" />

---



> 代码自定义实现拦截器

**案例需求描述**：

自定义一个拦截器，并结合使用Multiplexing Channel Selector，判断Event中的数据内容是否包含"hello"单词，如果包含则输出到控制台，若果不包含就放到其他位置。

其中涉及到**Multiplexing Channel Selector的配置以及自定义拦截器的配置，**我们稍后再说。



对了，我们先来看看Event类的操作和使用：

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200804200848.png" alt="image-20200804200848835" style="zoom:80%;" />

四个方法简洁明了。傻子都能看得懂了吧！！！



**自定义拦截器类**

```java
public class HelloInterceptor implements Interceptor {
    
    /**
     * 用于批事件处理方法的返回列表
     */
    private List<Event> handledEvents;

    @Override
    public void initialize() {
        handledEvents = new ArrayList<>();
    }

    /**
     * 处理单个事件
     * @param event
     * @return
     */
    @Override
    public Event intercept(Event event) {
        // 获取事件的头部
        Map<String, String> headers = event.getHeaders();
        // 获取时间的数据本体
        String body = new String(event.getBody());

        // 判断数据内容
        if (body.contains("hello")) {
            headers.put("hasHello", "Y");
        } else {
            headers.put("hasHello", "N");
        }
        return event;
    }

    /**
     * 批事件处理
     * @param list
     * @return
     */
    @Override
    public List<Event> intercept(List<Event> list) {
        // 1.清空列表
        handledEvents.clear();
        // 2.循环调用单事件处理的方法
        list.forEach(x -> handledEvents.add(intercept(x)));

        return handledEvents;
    }

    @Override
    public void close() {

    }

    public static class Builder implements Interceptor.Builder {

        /**
         * @return
         * 实例化拦截器
         */
        @Override
        public Interceptor build() {
            return new HelloInterceptor();
        }

        @Override
        public void configure(Context context) {
        }
    }
}
```

这就是一个简单的Interceptor实现啦。下一步就是Agent的配置了！



先上图：

![image-20200804203417479](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200804203417.png)



> Multiplexing Channel Selector配置以及拦截器的配置

先看多路选择器的配置清单：

![image-20200804203556536](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200804203556.png)

官方案例：

```properties
a1.sources = r1
a1.channels = c1 c2 c3 c4
a1.sources.r1.selector.type = multiplexing
a1.sources.r1.selector.header = state
a1.sources.r1.selector.mapping.CZ = c1
a1.sources.r1.selector.mapping.US = c2 c3
a1.sources.r1.selector.default = c4
```

重点关注一下最后四行，倒数二三行像是给mapping中 CZ和US指定了Channel！！这正是我们想要的！！倒数第四行还有一个header中取出的state，莫非?? 莫非!? 莫非?! 莫非!!
对！！==其实CZ和US只是header中名为state的key所对应的两个value==!!所以说结合多路选择器就可以按照头部的标记决定Event的去向！！

那么按照我们的拦截器代码就能推出我们要配置的多路选择器配置内容：

```properties
a1.sources = r1
a1.channels = c1 c2
a1.sources.r1.selector.type = multiplexing
a1.sources.r1.selector.header = hasHello
a1.sources.r1.selector.mapping.Y = c1
a1.sources.r1.selector.mapping.N = c2
a1.sources.r1.selector.default = c1
```



自定义拦截器如何配置？

官方文档案例：

==小知识：文档中反复提起的FQCN，全拼是fully qualified class name（全限定类名）==

```properties
a1.sources = r1
a1.sinks = k1
a1.channels = c1
# 拦截器命名
a1.sources.r1.interceptors = i1 i2
# 拦截器的类型（使用已有的拦截器类型 或者 自定义类的全限定类名$Builder）
a1.sources.r1.interceptors.i1.type = org.apache.flume.interceptor.HostInterceptor$Builder
# 以下省略...
a1.sources.r1.interceptors.i1.preserveExisting = false
a1.sources.r1.interceptors.i1.hostHeader = hostname
a1.sources.r1.interceptors.i2.type = org.apache.flume.interceptor.TimestampInterceptor$Builder
a1.sinks.k1.filePrefix = FlumeData.%{CollectorHost}.%Y-%m-%d
a1.sinks.k1.channel = c1
```



> 配置启动

1. 把写好的拦截器程序打包放到flume的lib目录下

2. 三个配置文件：

   Agent1:

   ```markdown
   # 组件命名
   a1.sources = r1
   a1.channels = c1 c2
   a1.sinks = k1 k2
   
   # Source配置 NetCat Source
   a1.sources.r1.type = netcat
   a1.sources.r1.bind = hadoop102
   a1.sources.r1.port = 44444
   
   # Sink配置 AVRO Sink
   a1.sinks.k1.type = avro
   a1.sinks.k1.hostname = hadoop103
   a1.sinks.k1.port = 4545
   
   a1.sinks.k2.type = avro
   a1.sinks.k2.hostname = hadoop104
   a1.sinks.k2.port = 4545
   
   # Channel配置 MemoryChannel
   a1.channels.c1.type = memory
   a1.channels.c1.capacity = 1000
   a1.channels.c1.transactionCapacity = 100
   
   a1.channels.c2.type = memory
   a1.channels.c2.capacity = 1000
   a1.channels.c2.transactionCapacity = 100
   
   # ChannelSelector配置 MultiplexingChannelSelector
   a1.sources.r1.selector.type = multiplexing
   a1.sources.r1.selector.header = hasHello
   a1.sources.r1.selector.mapping.Y = c1
   a1.sources.r1.selector.mapping.N = c2
   
   # Interceptor配置 HelloInterceptor
   a1.sources.r1.interceptors = i1
   a1.sources.r1.interceptors.i1.type = com.sakura.interceptor.HelloInterceptor$Builder
   
   # Channel对接
   a1.sources.r1.channels = c1 c2
   a1.sinks.k1.channel = c1
   a1.sinks.k2.channel = c2
   ```

   Agent2:

   ```markdown
   # 组件命名
   a2.sources = r1
   a2.channels = c1
   a2.sinks = k1
   
   # Source配置 AVRO Source
   a2.sources.r1.type = avro
   a2.sources.r1.bind = hadoop103
   a2.sources.r1.port = 4545
   
   # Sink配置 LoggerSink
   a2.sinks.k1.type = logger
   
   # Channel配置 MemoryChannel
   a2.channels.c1.type = memory
   a2.channels.c1.capacity = 1000
   a2.channels.c1.transactionCapacity = 100
   
   # 对接Channel
   a2.sources.r1.channels = c1
   a2.sinks.k1.channel = c1
   ```

   Agent3配置文件与Agent2大致相同，修改avro source的bind和agent的名字就可以了，这里省略。。

3. 启动

   先2,3后1



> 测试结果：

![image-20200805094958495](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200805094958.png)

----





## 3.6、自定义Source

> 为什么要自定义Source?

虽然官方提供了很多可选的Source，但是依然不足以满足我们的业务需求，所以官方也给出了自定义Source的方案，我们可以通过固定的逻辑框架来实现独属于我们的Source。

> 官方的自定义Source模板

```java
public class MySource extends AbstractSource implements Configurable, PollableSource {
  private String myProp;

  @Override
  public void configure(Context context) {
    String myProp = context.getString("myProp", "defaultValue");

    // Process the myProp value (e.g. validation, convert to another type, ...)

    // Store myProp for later retrieval by process() method
    this.myProp = myProp;
  }

  @Override
  public void start() {
    // Initialize the connection to the external client
  }

  @Override
  public void stop () {
    // Disconnect from external client and do any additional cleanup
    // (e.g. releasing resources or nulling-out field values) ..
  }

  @Override
  public Status process() throws EventDeliveryException {
    Status status = null;

    try {
      // This try clause includes whatever Channel/Event operations you want to do

      // Receive new data
      Event e = getSomeData();

      // Store the Event into this Source's associated Channel(s)
      getChannelProcessor().processEvent(e);

      status = Status.READY;
    } catch (Throwable t) {
      // Log exception, handle individual exceptions as needed

      status = Status.BACKOFF;

      // re-throw all Errors
      if (t instanceof Error) {
        throw (Error)t;
      }
    } finally {
      txn.close();
    }
    return status;
  }
}
```

整个代码的重点在于`process()`方法，透过这个方法我们能看到之前我们在Flume事务和Agent原理中讲到的那些东西。
所有的Source配置项都以全局属性的形式存在，使用`context.getString()`到配置文件中取，或者直接使用默认值。
等等… 这些细节我们边写边说吧



> 自定义Source代码实现

1. `extends AbstractSource implements Configurable, PollableSource`继承实现这些类

2. 官方模板中的`start()`和`stop()`用于和外部数据源客户端建立/断开连接（例如MySQL）这两个方法是可选实现，这里我们做的是简单的实现就不使用这俩方法了。

3. 接口中必须实现的四个方法:

   - `void configure(Context context)`
   - `Status process()`
   - `long getBackOffSleepIncrement()`
   - `long getMaxBackOffSleepInterval()`

   关键在于前两个方法，后俩和BackOff的时间设置有关。

4. `process()`方法的任务

   - 将数据封装为Event对象
   - 将封装好的Event交给ChannelProcessor处理（查看源码！！）
   - 控制Event的处理，为其设置状态(State: READY or BACKOFF)

5. `configure(Context context)`方法

   - 读取配置文件中的配置并为Source中的属性设值

----

包装Event时，需要用到Event接口的一个实现类：`SimpleEvent` 另外一个实现类（JSONEvent）;

使用`getChannelProcessor().processEvent(event);`完成Event的处理，包括拦截器，事务，Channel选择
来看看源码：

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200805142155.png" alt="image-20200805142155016" style="zoom: 67%;" />

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200805142419.png" alt="image-20200805142406917" style="zoom: 80%;" />

之前事务中讲到的doPut、doCommit、doRollBack现在都见到活物了！！！拦截器链和Selector的工作顺序也得以验证。



完整代码：

```java
public class MySource extends AbstractSource implements Configurable, PollableSource {

    /**
     * 数据前缀
     */
    private String prefix;

    /**
     * 数据后缀
     */
    private String suffix;

    @Override
    public void configure(Context context) {
        // 读取配置文件或使用默认值(data)
        prefix = context.getString("prefix", "data");
        suffix = context.getString("suffix", "data");
    }

    /**
     * 1. 接收数据包装为Event
     * 2. 将Event传给Channel
     *
     * @return
     * @throws EventDeliveryException
     */
    @Override
    public Status process() throws EventDeliveryException {
        Status status = null;

        try {
            // 模拟接收数据
            for (int i = 0; i < 5; i++) {
                SimpleEvent event = new SimpleEvent();
                // 为 Event设置数据本体
                event.setBody((prefix + "--" + i + "--" + suffix).getBytes());
    
                getChannelProcessor().processEvent(event);
            }
            status = Status.READY;
        } catch (Exception e) {
            e.printStackTrace();
            // 发送失败 status置为 BACKOFF
            status = Status.BACKOFF;
        }
        
        // 延迟两秒
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return status;
    }

    @Override
    public long getBackOffSleepIncrement() {
        return 0;
    }

    @Override
    public long getMaxBackOffSleepInterval() {
        return 0;
    }
}
```

这个Source有两个配置项：

- prefix
- suffix

默认值都是data。
数据由Source自己伪造就是那个for循环:laughing:

> 配置文件和启动

1. 打包放入flume的lib目录下

2. 配置文件(默认配置):

   ```properties
   # Name the components on this agent
   a1.sources = r1
   a1.sinks = k1
   a1.channels = c1
   
   # Describe/configure the source
   a1.sources.r1.type = com.sakura.source.MySource
   
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

3. 测试运行

   ![image-20200805145459348](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200805145459.png)

   默认配置通过，每10秒一批数据。

4. 添加配置

   ```properties
   a1.sources.r1.prefix = wuhu
   a1.sources.r1.suffix = qifei
   ```

5. 再次启动

   ![image-20200805145734618](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200805145734.png)



> 总结：

以上的Source自定义数据，是我们通过程序伪造的，在应对不同的需求的时候，只要使用对应的办法将数据读到程序中就可以实现啦！只要在==程序中拿到数据就一切都好说==！！（比如MySQL, 和MySQL建立连接后查询就能拿到数据。。）

----



## 3.7、自定义Sink

Source都能自定义了，那Sink没有理由不行！！

同样官方也给出到了自定义Sink的模板：

```java
public class MySink extends AbstractSink implements Configurable {
  private String myProp;

  @Override
  public void configure(Context context) {
    String myProp = context.getString("myProp", "defaultValue");

    // Process the myProp value (e.g. validation)

    // Store myProp for later retrieval by process() method
    this.myProp = myProp;
  }

  @Override
  public void start() {
    // Initialize the connection to the external repository (e.g. HDFS) that
    // this Sink will forward Events to ..
  }

  @Override
  public void stop () {
    // Disconnect from the external respository and do any
    // additional cleanup (e.g. releasing resources or nulling-out
    // field values) ..
  }

  @Override
  public Status process() throws EventDeliveryException {
    Status status = null;

    // Start transaction
    Channel ch = getChannel();
    Transaction txn = ch.getTransaction();
    txn.begin();
    try {
      // This try clause includes whatever Channel operations you want to do

      Event event = ch.take();

      // Send the Event to the external repository.
      // storeSomeData(e);

      txn.commit();
      status = Status.READY;
    } catch (Throwable t) {
      txn.rollback();

      // Log exception, handle individual exceptions as needed

      status = Status.BACKOFF;

      // re-throw all Errors
      if (t instanceof Error) {
        throw (Error)t;
      }
    }
    return status;
  }
}
```

最大也是唯一的区别在`process()`方法中，==在自定义Source的时候，所有事务相关的操作不用我们来做，框架已经帮我们完成。而自定义Sink时，所有的事务操作需要我们亲力亲为。==我们可以借鉴官方Source的事务代码。

注意官方给出的模板中并没有使用`close()`关闭事务！！一定一定要使用close()关闭事务！！

具体步骤就不说了和Source的大差不差。
完整代码：

```java
public class MySink extends AbstractSink implements Configurable {

    private String suffix;
    // 获取一个Logger对象 用于输出到控制台
    private Logger logger = Logger.getLogger(MySink.class);

    @Override
    public void configure(Context context) {
        suffix = context.getString("suffix", "--data");
    }

    @Override
    public Status process() throws EventDeliveryException {

        Status status = null;

        Channel ch = getChannel();
        Transaction txn = ch.getTransaction();
        txn.begin();
        try {
            
            Event event = ch.take();
            logger.info(new String(event.getBody()) + suffix);

            txn.commit();
            status = Status.READY;
        } catch (Throwable t) {
            txn.rollback();
            status = Status.BACKOFF;
        } finally {
            // 关闭事务
            if (txn != null) {
                txn.close();
            }
        }
        return status;
    }
}
```



> 配置文件及启动

```properties
# Name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1

# Describe/configure the source
a1.sources.r1.type = netcat
a1.sources.r1.bind = hadoop102
a1.sources.r1.port = 44444

# Describe the sink
a1.sinks.k1.type = com.sakura.sink.MySink

# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1
```



启动测试

![image-20200805170055104](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200805170055.png)

增加配置，再测试

```properties
a1.sinks.k1.suffix = --from sakura
```

![image-20200805170336707](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200805170336.png)



> 总结

和Source一样，我们的数据输出位置使用的是最简易的控制台输出，在面对不同的场景下，你可以将这些数据以特定的方式输出到你想要的位置。我们的目的只是了解整个自定义的过程，具体的业务实现要根据场景来具体实现。（比如MySQL，就有很多很多种方式来利用这些数据插入到表中。）



# 四、常见面试题

1. Source、Sink、Channel的作用
2. 常用的Source Sink Channel
3. 关于ChannelSelector，两种Selector以及Interceptor
4. Flume调优（MemoryChannel参数调整等）
5. Flume事务机制，Flume采集的数据会丢失吗？(有事务机制可以保证数据不会丢失，但是有可能会出现数据重复的情况)

