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