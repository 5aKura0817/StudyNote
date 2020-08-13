[toc]



# 一、HBase简介

## 1.1、HBase定义

 HBase是一个==分布式==、==可扩展==、==支持海量数据存储==的==NoSQL数据库==



## 1.2、HBase数据模型

 **逻辑上，HBase的数据模型和关系型数据库类型，数据存在一张表上，有行有列，但从HBase的底层物理存储结构（KV结构）来看更像是一个multi-dimensional map**，灵感来源于Google三大件中的==BigTable==。



### 1.2.1、HBase逻辑结构

![image-20200812100631223](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200812100631.png)



- **行键(Rowkey)**：唯一标识一行数据，按照字典序(row_key1 < row_key11 < rowkey2)排列.
- **列Col**：数据记录的一条属性
- **列族**：将多列划分为一类，视为一个列族。例如上图中（math、DS、DB列被归到Scores这一列族）
- **Region**：将行数据进行切片后得到的，记录切片。（数据按行切分后的一部分）
- **Store**：对Region再按照列族切分后，得到的一块一块的零散数据。

==这个逻辑结构只是方便我们理解数据，但是在存储上并不是这样的结构。==



### 1.2.2、物理存储结构

在物理的存储上，是==按照Store==对数据进行存储的和操作的：

![image-20200812104021201](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200812104021.png)

逻辑结构上的每一列的值，都要使用一行去存储。
Row_key用于对应逻辑结构中的一行数据，其中有两个重要列：`TimeStamp`，`Type`；

- Timestamp,时间戳：相当于**数据的一个版本号**，每次操作数据后都会对应一个时间戳。具有最新的时间戳的数据代表最新的数据。
- Type,操作类型：数据的操作类型



### 1.2.3、数据模型

> 命名空间（Name Space）

命名空间(NameSpace)，类似于关系型数据的数据库(DataBases)。每个命名空间下可以有多个表。HBase系统自带两个命名空间：`hbase`和`default`，前者是HBase存放元数据的位置。Default是用户默认使用的命名空间。

----

> Region

类似于关系型数据库的表（Table）,不过table中存放的是具体的列，而Region中**只需要指定列族**，在插入数据的时候，可以动态指定列族中的列。因此可以轻松应对字段（列）变更的场景

----

> Row

逻辑结构中，每一行数据都由一个唯一的RowKey和若干个Col组成，RowKey按照字典序排列，查询数据时RowKey是一个重要的指标！！

----

> Column

每个Column都由ColumnFamily（列族）和ColumnQualifier（列限定符）唯一确定。**建表时，列需要制定列族**，限定符无需预先定义。

---

> Timestamp

用于标识数据的版本，修改数据时，以修改时间作为该列的值，最新的时间戳可以表示最新数据。其也是查询数据的一重要指标！！

-----

> Cell

由`{Row_key, ColumuFamily, ColumnQualifier, Timestamp}`唯一确定的单元。对应物理存储结构中的一个个Value，其数值是没有类型的，(即逻辑结构中列数据不需要指定数据类型) 都是以字节码形式存储的。



## 1.3、HBase基础架构（入门版）

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200812131138.png" alt="image-20200812131138118" style="zoom:80%;" />



以上是一个简易版的架构图：看看各部分的作用：

-  **RegionServer**：
  - 管理着分配给自己的“表”Region，包括对Region的拆分以及合并。
  - 接收客户端的数据 增Put、删Del、查Get
- **master**:
  - 管理着所有的RegionServer,监控各个Server的状态，可以调动Region，分配给其他RegionServer维护
  - 对表（Region）的增Create、删Delete、改Alter
- **Zookeeper**
  - 存放公共的元数据。





# 二、快速入门

## 2.1、HBase安装部署

[HBase-1.3.6下载地址](https://www.apache.org/dyn/closer.lua/hbase/hbase-1.3.6/hbase-1.3.6-bin.tar.gz)

> 配置修改 

1. 解压后，进入conf目录，修改配置文件

2. hbase.env

   配置JAVA_HOME：要求JDK1.7+

   ```shell
   # The java implementation to use.  Java 1.7+ required.
   export JAVA_HOME=/opt/module/jdk1.8.0_251
   ```

    

   可选配置：这两行配置可以直接注释掉

   ```shell
   # Configure PermSize. Only needed in JDK7. You can safely remove it for JDK8+
   export HBASE_MASTER_OPTS="$HBASE_MASTER_OPTS -XX:PermSize=128m -XX:MaxPermSize=128m -XX:ReservedCodeCacheSize=256m"
   export HBASE_REGIONSERVER_OPTS="$HBASE_REGIONSERVER_OPTS -XX:PermSize=128m -XX:MaxPermSize=128m -XX:ReservedCodeCacheSize=256m"
   ```

    

   配置Zookeeper，默认是true,使用内置的Zookeeper。推荐设置为true，设置使用本地的Zookeeper

   ```shell
   # Tell HBase whether it should manage it's own instance of Zookeeper or not.
   export HBASE_MANAGES_ZK=false
   ```

3. hbase-site.xml **官方文档8.1.1节给出了标准配置模板**，按照自己集群的情况进行配置，**官方文档7.2节有默认配置！！**

   配置示例：

   ````xml
   <?xml version="1.0"?>
   <?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
   
   <configuration>
     <!-- Zookeeper的集群主机，默认端口不用写 -->
     <property>
         <name>hbase.zookeeper.quorum</name>
         <value>hadoop102,hadoop103,hadoop104</value>
         <description>The directory shared by RegionServers.
         </description>
     </property>
   
     <!-- Zookeeper的dataDir，在zoo.cfg中配置的  -->
     <property>
         <name>hbase.zookeeper.property.dataDir</name>
         <value>/opt/module/zookeeper-3.6.1/zkData</value>
         <description>Property from ZooKeeper config zoo.cfg.
             The directory where the snapshot is stored.
         </description>
     </property>
   
     <!-- hbase在hdfs上的工作目录 -->
     <property>
         <name>hbase.rootdir</name>
         <value>hdfs://hadoop102:9000/hbase</value>
         <description>The directory shared by RegionServers.
         </description>
     </property>
   
     <!-- 是否开启集群模式，true:集群模式 false：单机模式 -->
     <property>
         <name>hbase.cluster.distributed</name>
         <value>true</value>
         <description>The mode the cluster will be in. Possible values are
             false: standalone and pseudo-distributed setups with managed ZooKeeper
             true: fully-distributed with unmanaged ZooKeeper Quorum (see hbase-env.sh)
         </description>
     </property>
       
     <!-- master端口，默认16000 -->
     <property>
         <name>hbase.master.port</name>
         <value>16000</value>
     </property>
   </configuration>
   ````

    

4. 群起HBase的文件：regionservers

   默认只有localhost，启动时仅启动本机，修改为:

   ```
   hadoop102
   hadoop103
   hadoop104
   ```

    

5. 创建Hadoop的core-site.xml、hdfs-site.xml软连接到此配置目录下

   ```shell
   ln -s /opt/module/hadoop-2.7.7/etc/hadoop/core-site.xml /opt/module/hbase-1.3.6/conf/core-site.xml
   
   ln -s /opt/module/hadoop-2.7.7/etc/hadoop/hdfs-site.xml /opt/module/hbase-1.3.6/conf/hdfs-site.xml
   ```

   ![image-20200812143237981](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200812143238.png)

6. 集群中分发



> 启动测试

1. 进入bin目录

   ![image-20200812144507022](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200812144507.png)

2. 单节点启动：
   ![image-20200812145040450](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200812145040.png)

   `hbase-daemon.sh start master`启动master，jps查看出现**HMaster进程**！

   访问hadoop102:16010，进入master的web页面
   ![image-20200812145150217](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200812145150.png)

   

   `hbase-daemon.sh start regionserver`启动regionserver。jps查看出现**HRegionServer进程**

   ![image-20200812145413947](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200812145414.png)

   ==注意！！一定要先启动HDFS和Zookeeper！！==
   
3. 关闭单节点的HRegionServer和HMaster进程，群起RegionServer

   `bin/start-hbase.sh`群起HMaster、HRegionServer

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200812150944.png" alt="image-20200812150944775" style="zoom: 67%;" />

   ![image-20200812153837857](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200812153837.png)

   ==注意！！群起时一定要保证集群直接的时间是同步的，最大时间差不要超过30s，否则HRegionServer会由于ClockOutOfSyncException异常自动下线。==

4. 群关

   `bin/stop-hbase.sh`一键关闭HMaster、HRegionServer

-----



## 2.2、命令行操作 DDL

### 2.2.1、基本操作

1. `bin/hbase shell`进入命令行

2. 注意，命令行使用删除可能需要注意，ctrl+backspace可以正常执行删除操作

3. `help`查看帮助

   ![image-20200812155645602](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200812155645.png)

   后面还给出了各种示范。

4.  操作注意：

   不要使用符号作为命令结尾，否则会认为是多行命令。当`>`变为`*`时，使用`''`（联系两个单引号）退出。当`>`变为`'`时，使用`'`退出





### 2.2.2、表操作

1. 创建表(使用help "create"查看帮助)

   ```shell
   create 'tableName','columnfamily1','columnfamily2',...
   ```

   表名放在第一个，后面接列族名，**至少要指定1个列族！！**

   示范：

   `create 't1','infoA','infoB'` 创建t1表，含有infoA、infoB两个列族

    

2. 查看表

   可以通过web页面查看：
   ![image-20200812161005720](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200812161005.png)

    

   也可以通过命令行查看：

   - `list`：列出table

     ```
     hbase(main):038:0> list
     TABLE                                                                                                                                                              
     t1                                                                                                                                                                  
     1 row(s) in 0.0110 seconds
     
     => ["t1"]
     ```

   - `describe 'tableName'`：查看表详细信息

     ```
     describe 't1'
     Table t1 is ENABLED                                                                                                                                                 
     t1                                                                                                                                                                  
     COLUMN FAMILIES DESCRIPTION                                                                                                                                         
     {NAME => 'infoA', BLOOMFILTER => 'ROW', VERSIONS => '1', IN_MEMORY => 'false', KEEP_DELETED_CELLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', TTL => 'FOREVER', COMPR
     ESSION => 'NONE', MIN_VERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'}                                                        
     {NAME => 'infoB', BLOOMFILTER => 'ROW', VERSIONS => '1', IN_MEMORY => 'false', KEEP_DELETED_CELLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', TTL => 'FOREVER', COMPR
     ESSION => 'NONE', MIN_VERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'}
     
     2 row(s) in 0.0480 seconds
     ```

      

3. 修改表（修改列族）

   `alter 't1',{NAME=>'infoA',VERSIONS=>'3'}`

   将VERSIONS从默认值1，改为3。

   这里的VERSIONS表示为，列族中列能保存的最大版本数。每一次修改都会产生一个新的版本。
   ==注意由于一个表中有多个列族，所以修改的时候要使用{NAME=>'xx'}来唯一指定列族，后接修改的内容== 

   

4. 删除表

   不能直接`drop tablename`
   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200812162028.png" alt="image-20200812162028617" style="zoom:67%;" />

   提醒我们表正在使用，回到我们查看表的位置，有一个OnlineRegions和OfflineRegions，前者表示Region处于启用状态即enable状态，需要使用`disable ’tableName‘`先停用，变为OfflineRegion后，才能使用drop删除表

5. 清空表`truncate`

   ![image-20200813105526430](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813105526.png)



### 2.2.3、NameSpace操作

1. 创建命名空间

   `create_namespace xxx`

2. 列出所有的命名空间

   `list_namespace`

   ```
   hbase(main):056:0> list_namespace
   NAMESPACE                                                                                                                                                           
   bigdata                                                                                                                                                             
   default                                                                                                                                                             
   hbase                                                                                                                                                               
   3 row(s) in 0.0200 seconds
   ```

3. 创建表到指定命名空间

   默认创建表到default命名空间，如果想要指定命名空间 ：

   `create table 'xxx:t1','info'`，建表时使用`命名空间:表名`作为表名

   ![image-20200812163951995](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200812163952.png)

    

4. 删除命名空间

   `drop_namespace xxx`，当命名空间中有内容时，会报异常。==必须先清空命名空间再删除！！==





## 2.3、命令行操作 DML

### 2.3.1、数据增&查

建表：

```shell
create 'stu','info','scores'
```



> 增加数据

`put '表名','row_key','列族:列名','value'` （更多方法，使用put或者help "put"查看帮助）

 ```shell
hbase(main):017:0> put 'stu','1002','info:name','zhangsan'

hbase(main):018:0> put 'stu','1002','info:sex','male'

hbase(main):019:0> put 'stu','1002','scores:math','90'

hbase(main):020:0> put 'stu','1002','scores:ds','88'

...
 ```

 

> 查询数据

`scan` 全表扫描

```shell
scan 'stu'
或者：
stu = get_table 'stu'
stu.scan
```

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813090453.png" alt="image-20200813090453013" style="zoom:67%;" />

`scan` 范围扫描

`scan 'tableName',{STARTROW=>'row_key',STOPROW=>'row_key2'}` [row_key, row_key2)范围（左闭右开）

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813091448.png" alt="image-20200813091447989" style="zoom:67%;" />

STARTROW 和 STOPROW可以省略一个，表示从 STARTROW扫描到尾 或者 从头扫描到STOPROW。





`get` 指定列取值

```shell
get 'stu','1001'
COLUMN                               CELL                           
 info:age                            timestamp=1597280140839, value=20                             
 info:name                           timestamp=1597280104048, value=sakura                             
 scores:db                           timestamp=1597280186785, value=70                           
 scores:ds                           timestamp=1597280198870, value=61                             
 scores:math                         timestamp=1597280168731, value=66

get 'stu','1001','info'
COLUMN                               CELL                            
 info:age                            timestamp=1597280140839, value=20                            
 info:name                           timestamp=1597280104048, value=sakura                                       

get 'stu','1001','info:name','scores:math'
COLUMN                               CELL                             
 info:name                           timestamp=1597280104048, value=sakura                        
 scores:math                         timestamp=1597280168731, value=66
```





### 2.3.2、数据改&删

> 修改数据

在HBase中，**直接使用Put修改值**，系统会判断时间戳的位置，来决定哪条数据是最新数据。

![image-20200813100204212](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813100204.png)

看似原值sakura，是被"删除"了，但是可以通过这样的方式查看历史数据：

`scan 'stu',{RAW=TRUE,VERSIONS=10}`，即查看所有记录的最近10条版本记录。

![image-20200813100546961](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813100547.png)

对比来看，其实最终展示的数据都是时间戳最新的数据！！！那么也就是如果我们现在put一条数据，如果时间戳小于最新数据的时间戳，其实无济于事。

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813100950.png" alt="image-20200813100950897" style="zoom:80%;" />

果然不错，所以==时间戳在HBase中，作为数据的版本依据，非常重要！！==

![image-20200813101728980](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813101729.png)

 

> 删除数据

`delete '表名','row_key','列名'`

```shell
delete 'stu','1002','info:sex'
```

![image-20200813102242343](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813102242.png)

这个删除操作，非常贴心地将删除时间戳与创建值的时间戳进行重叠。为了就是不让后续的插入操作受删除操作的时间戳影响，导致插入失败。



我们上面修改操作是按照时间戳取最新的值，作为有效值，那么我们执行一次delete，你猜结果如何？

你以为laoli(name时间轴上第二个值)会冒出来？delete操作还是贴心地把时间戳和当前有效数据的创建时间戳做了重叠，那么之前的put都是无效的了。

![image-20200813102937228](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813102937.png)

当来获取的数据的时候，一看是type=delete，就知道这个值被删除了。

与之对应，如果我们删除的是当前有效值时间戳之前的值（例如laoli）,那么是不会影响有效值的哦！！
但是如果**超前删除**，效果是什么样，应该就可以脑补出来了，就是那个时间点前，所有的put都是无用功！==但是如果你集群的时间还没有监测到你的超前删除时，还是可以取到有效值的。==图示：

![image-20200813104242928](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813104242.png)

你超前的修改与删除操作，能被集群的时间能够监测到时，都会一一生效。

==总之一句话：时间戳是王道，谁大听谁的！！==



> 删除整行

使用`deleteall '表名','row_key'`

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813105351.png" alt="image-20200813105351013" style="zoom:67%;" />

暂时没有找到删除列族的命令。。。。

----



### 2.3.3、数据多版本（VERSIONS）

之前在修改表中，我们修改了表的列族的**VERSIONS属性，此属性表示该列族保留多少个数据版本。**

虽然我们可以使用{RAW=>TRUE,VERSIONS=>10}看到所有数据的10条版本，但是想要具体查看具体某条数据的某列的版本数量时，就受到了这个参数的限制。

例如：

![image-20200813111023815](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813111023.png)

  

当我们使用`get '表名','row_key,{COLUME='列族:列名',VERSIONS=n}`查看某个数据的最近n个版本的时候，结果却是：

![image-20200813111319961](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813111320.png)

只有一条，就是因为列族那个VERSIONS属性的设置是1，只保存了一个版本的数据。我们现在将其修改为3：

`alter 'stu',{NAME=>'info',VERSIONS=>3}`，再次尝试：

![image-20200813111738312](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813111738.png) 

继续修改，版本继续增加，但是只保留最新的3个。





# 三、HBase进阶

## 3.1、架构原理（进阶版）

![img](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813130656.png)

从上往下聊：

- Client，连接HRegionServer，操作表数据。
- Zookeeper：存放RegionServer的地址，以及HMaster的一些信息。
- HMaster，监控所有HRegionServer，调度HRegion分配给HRegionServer维护，协调工作
- HRegionServer：维护着一个HLog以及HMaster分配的Region
- HLog(WAL log)：WAL意为write ahead log, 作用类似于HDFS中Edits文件，记录所有的数据操作记录。**用于做容灾备份，一旦RegionServer宕机，可以从HLog中恢复。**
- HRegion: 存储某张表的一段数据的文件目录
- Store：每个Region按照列族拆分的单元数据。
- MemStore: 加载到内存的列族中的数据，当达到一定阈值后，会被`flush`（刷写）到文件中（磁盘）。
- StoreFile：MemStore刷写后形成的文件，后续不断刷写追加。
- HFile：StoreFile的底层**存储格式**
- DFS Client: 接收HBase的数据写入请求，连接HDFS操作文件系统
- HDFS DataNode：处理DFS Client的命令，对数据进行存储。

[参考博客：详解HBase架构原理](https://www.cnblogs.com/steven-note/p/7209398.html)



## 3.2、写流程

> 流程

先用一张图来说明一下：

![image-20200813140816774](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813140816.png)

 简述流程：

1. Client首先请求Zookeeper获取到meta表（命名空间hbase中的一个region，还有一个叫namespace，均属于内部表）的位置

   ![image-20200813141042495](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813141042.png)

   

2. 获取到meta表的位置后，到对应的RegionServer上扫描meta表的数据。找到要写数据的目标表的位置信息

3. 获取目标表的位置后，到相应的RegionServer上执行写数据操作。



> 关键点演示，检查对应位置

- Zookeeper上meta表位置的存放节点：

  ![image-20200813141626589](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813141626.png)

  通过web页面来检验一下：

  <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813141711.png" alt="image-20200813141710949" style="zoom: 50%;" />

- scan 扫描一下meta表：

  ![image-20200813142132283](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813142132.png)

  果然是可以拿到表的信息以及其所在的RegionServer的。

  ==这里只有一个RegionServer，但是当表变得足够大后，切分为了多个Region就会分散到多个RegionServer上，就会获取到多个RegionServer!!==

  通过web页面的信息来验证一下：
  <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813142501.png" alt="image-20200813142501477" style="zoom:67%;" />



> 小知识点：在老版本中，因为考虑到meta表也可能被切分，所以就用了`_ROOT_`表来存储所以meta表的位置信息，以防只读取了一个meta表而导致获取表的位置信息不准确。但是实际情况中，meta表基本不可能被切分。 后续版本已经移除。



通过源码的标注，来看看数据是怎么写入的

> 写数据流程，源码

找到HRegion类的源码，代码中注释了STEP1~STEP9：

1. 获取大量的锁，保证至少获取到一个锁（JUC）

2. 更新时间戳，确保服务端的时间戳是最新的，所有的操作时间戳默认使用服务端的时间戳

3. 创建对WAL的编辑，但是不写入WAL文件

4. 将最后的编辑内容追加到WAL文件中，但是不同步！！

5. 写回到MemStore

   这里你可能会疑问WAL不同步，就写到MemStore中，发生意外宕机不是完了？源码中给了解释：

   *It is ok to write to memstore  first without syncing the WAL because we do not roll forward the memstore MVCC. The MVCC will be moved up when the complete operation is done. These changes are not yet visible to scanners till we update the MVCC. The MVCC is moved only when the sync is complete.*

   用到了`MVCC`(MultiVersionConsistencyControl,多版本控制协议)。==在MemStore写入成功，更新MVCC之前，所有的写操作对外界是不可见的。如果写入MemStore发生了意外，就将清除所有的写入内容。（回滚）==

   ![image-20200813154545439](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813154545.png)

    

6. 释放锁，等其他一些资源释放操作

7. 同步WAL

8. 更新推动MVCC,使得写操作的内容修改可以被扫描到

   以下是回滚代码逻辑：

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813155110.png" alt="image-20200813155110640" style="zoom:67%;" />

==数据操作先写到WAL(Hlog)，然后写回MemStore，然后同步WAL，写回和同步过程中任意一个出现异常都会回滚，撤销写入。==



## 3.3、MemStore刷写

[推荐阅读：HBase 入门之数据刷写(Memstore Flush)详细说明](https://blog.csdn.net/b6ecl1k7BS8O/article/details/86486186)

前面我们讲到，数据到达HBase的时候，首先是进入到WAL（即HLog文件）中，然后写回到MemStore中，而MemStore终归还是内存数据，还是要写入到磁盘(文件系统)中去的。那么何时触发这个flush（刷写）操作呢？

> 什么情况会触发刷写？

我们先看官方文档：
![image-20200813191547820](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813191547.png)

 ==文档中说明：最小的刷写单元是Region而不是MemStore！！==

1. 单个MemStore内存占用达到配置值（`hbase.hregion.memstore.flush.size`）时
2. 全局所有MemStore内存占用的达到配置值（`hbase.regionserver.global.memstore.upperLimit（旧版本）`，`hbase.regionserver.global.memstore.size (新版本)`）时
3. 当WAL log文件的数量达到配置值（`hbase.regionserver.max.logs`）时，保存着最老的WAL Log文件的MemStore最先刷写。

除此以外，还有三种：

4. 定期自动刷写
5. 数据更新超过一定阈值
6. 手动触发刷写



#### 1、单个MemStore达到内存占用上限

当单个MemStore的内存占用超过了（`hbase.hregion.memstore.flush.size`，默认值134217728B=**128MB**）,是其所在的Region中所有的MemStore都要进行刷写。（注意这样的机制容易产生小文件，这个与刷写策略有关。）

当MemStore的刷写速度比MemStore的写入速度慢的时候，MemStore会不断增长，增加刷写的负担并且随时可能OOME(内存溢出)，所以当MemStore内存占用达到（`hbase.hregion.memstore.flush.size * hbase.hregion.memstore.block.multiplier` ）`hbase.hregion.memstore.block.multiplier`默认值4，即 128MB*4=**512MB**时，会强制叫停对该Store的数据写入！！此时继续请求写入会报`RegionTooBusyException`异常。

 

#### 2、RegionServer的MemStore内存总和达到上限

官方文档给出的内存上限的配置项~~`hbase.regionserver.global.memstore.upperLimit`~~已经被新版本的`hbase.regionserver.global.memstore.size`替代了。官方文档对于这个配置项的描述：

![image-20200813194635406](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813194635.png)

 

意思是说，这个配置项的值与其所在的RegionServer所占用的堆内存大小有关，**默认按照40%分配**。即RegionServer会将自己占用内存的40%分配给MemStore，但是决定不可能让你用满这40%的堆内存，于是就有了这个配置项`hbase.regionserver.global.memstore.size.lower.limit` 替代的旧版的~~`hbase.regionserver.global.memstore.lowerLimit`~~

此项的默认配置为**95%**，对这项的描述是**当分配给MemStore的内存使用占比达到该值，就会启动强制刷新。**例如当前RegionServer堆内存占用是2G，那么RegionServer中所有MemStore的内存占用达到 2×40%×95%=0.76G的时候，就会开始顺序强制刷写MemStore。

为什么不能用到100%呢？因为一旦使用到达100%的时候，此刷写过程就会因为内存不足受到操作限制！！

==注意这里的刷写是顺序刷写！！从最大内存占用的Region的MemStore开始刷写，直到所有的MemStore的内存占用低于了最高限制。==这种刷写是RegionServer级别的刷写。此级别的刷写，所有请求此RegionServer的操作都会阻塞！！



#### 3、WAL Log文件达到上限

