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



   配置Zookeeper，默认是true,使用内置的Zookeeper。推荐设置为false，设置使用本地的Zookeeper

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

==以上删除笔记不够严谨，请查看3.8 重新认识删除==





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
3. 当WAL log文件的数量达到配置值（`hbase.regionserver.max.logs`）时，保存着最老的WAL Log文件的RegionServer最先刷写。

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



意思是说，这个配置项的值与其所在的RegionServer所占用的堆内存大小有关，**默认按照40%分配**。即RegionServer会将自己占用内存（hbase_heapsize）的40%分配给MemStore，但是绝对不可能让你用满这40%的堆内存，于是就有了这个配置项`hbase.regionserver.global.memstore.size.lower.limit` 替代的旧版的~~`hbase.regionserver.global.memstore.lowerLimit`~~

此项的默认配置为**95%**，对这项的描述是**当分配给MemStore的内存使用占比达到该值，就会启动强制刷新。**例如当前RegionServer堆内存占用是2G，那么RegionServer中所有MemStore的内存占用达到 2×40%×95%=0.76G的时候，就会开始顺序强制刷写MemStore。

为什么不能用到100%呢？因为一旦使用到达100%的时候，此刷写过程就会因为内存不足受到操作限制！！

==注意这里的刷写是顺序刷写！！从最大内存占用的Region的MemStore开始刷写，直到所有的MemStore的内存占用低于了最高限制。==这种刷写是RegionServer级别的刷写。此级别的刷写，所有请求此RegionServer的操作都会阻塞！！



#### 3、WAL Log文件达到上限

首先我们清楚，WAL(HLog)是一个RegionServer维护一个，数据到达HBase都是先进WAL，然后到MemStore，最后才刷写到磁盘。虽然WAL是用来做容灾的，但是如果这个文件太大个数太多，就意味着MemStore中有大量的数据没有被持久化，那么一旦宕机故障，恢复的时候所用时就会很长。所以当WAL文件的数量达到一定阈值，有必要进行刷写来减少WAL文件的数量。

这个数量的由`hbase.regionserver.max.logs`决定。官方文档中并没有对此配置给出说明。博客中给出了参数说明：

如果设置了 hbase.regionserver.maxlogs，那就是这个参数的值；否则是
max(32, hbase_heapsize * hbase.regionserver.global.memstore.size * 2 / logRollSize)。如果某个 RegionServer 的 WAL 数量大于 maxLogs 就会触发 MemStore 的刷写。

**刷写的策略是，找到最老的WAL文件所在的RegionServer，然后刷写其所有Region中的所有MemStore！直到WAL个数低于max.logs**



#### 4、数据长时间未更新自动刷写

当数据既没有达到内存使用上限，也没有达到WAL文件数量上限，那么就不刷写了？当然不是！当你的数据长时间未更新的时候就会自动刷写。这个时间的长度由`hbase.regionserver.optionalcacheflushinterval (默认 3600000ms=3600s=1h，设置为0表示禁用)`配置控制。HBase会专门启动一个线程每隔`hbase.server.thread.wakefrequency（默认 10000ms=10s）`去检查有没有超过自动刷新时间的Region。

即检查线程没10秒检查一次RegionServer中有没有超过1个小时没有更新数据和刷写的Region。

-----



## 3.4、读数据

和写流程一样，只不多在从RegionServer读取数据的过程需要把握。

1. 到Zookeeper请求meta表的位置
2. 扫描meta表，取出要查询表的所在位置
3. 到目标表的RegionServer上请求get数据

> 这里Get数据的位置有三个：

- StoreFile(HFile) 文件
- MemStore 内存
- BlockCache 文件内容缓存

==并不是传统中从内存或者缓存中读到数据就忽略磁盘了！！==因为HBase数据的特殊性，数据再修改后旧版本的数据不会被立即删除，按照时间戳来决定数据版本。如果按照传统的读取方式，就很有可能漏读磁盘中最新数据，从而将内存中旧版的数据作为有效数据。

==正确的读取方式，应该是每次都连带着磁盘（HFile）和内存（MemStore）以及缓存（BlockCache）一起读，通过时间戳的比较对数据进行一次Merge,然后拿到最新数据。==并将磁盘中读出的新数据写到BlockCache，下次读取磁盘的时候，BlockCache中已经缓存的文件不再读取。并且**BlockCache遵从LRU（最久未使用）算法淘汰机制。**
这（每次都要读磁盘）就是为什么HBase的读数据要比写数据慢的原因了。



> 解析HFile

在HDFS中，在你所配置HBase的文件存放目录(我的配置 /hbase)，那么在`/hbase/data/命名空间/表名/RegionID/列族名/`目录下就是所有的HFile。官方提供了工具供我们解析HFile文件。`bin/hbase org.apache.hadoop.hbase.io.hfile.Hfile`

![image-20200814103732023](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200814103732.png)

解析结果：

![image-20200814104149731](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200814104149.png)





## 3.5、StoreFile Compaction

当我们的StoreFile达到一定数量或者每间隔一段时间，就会触发Compaction，即将多个HFile合并为一个HFile。合并方式分为：
`Minor Compaction` 和 `Major Compaction`

首先来看相关的配置：

- `hbase.hstore.compaction.min` 默认值none,代码逻辑中为3，取代了旧版本中的~~`hbase.hstore.compactionThreshold`~~

  这个是触发Minor Compaction的最小HFile数量。（旧版本中配置值为3）

- `hbase.hstore.compaction.max` 默认值10，单次MinorCompaction能够合并的最大HFile数量

- `hbase.hregion.majorcompaction` 默认值604800000ms = 7Day 进行Major Compaction的时间间隔。生产环境一般设置为0，禁用自动MajorCompaction，因为十分占用资源。在空闲时间手动开启

- `hbase.regionserver .compaction.enabled`默认true 是否启用Compaction。

其他信息可以查看官方文档**71.7.7. Compaction**

> MinorCompaction 和 Major Compaction的区别

先看官方文档的这几段话吧：

![image-20200814112726359](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200814112726.png)

**Minor Compaction是会选择相邻的，少量的小文件进行合并，并且是不会对数据进行操作的，仅仅只是合并而已。**

**而MajorCompaction，是将每个Store中的所有StoreFile合并为一个！并且对数据进行合并，删除过期的数据，保留有效数据。如果VERSIONS的设置大于1，则会将超额的版本删除。**

> 合并 和 数据删除的关系

数据删除操作，事实上并没有将数据移除。而是为这个数据做了一个“墓碑”作为标记。以确保查询请求的结果不会输出带有此标记的数据。然而在进行MajorCompaction的时候，就会真正将数据删除，并移除标记。如果文件中有过期数据，不会创建标记，而是在合并重写StoreFile时，直接过滤不写入新的StoreFile中！！



> 命令行操作

当前我们已经强制刷写(flush)了三个StoreFile:

![image-20200814123129281](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200814123129.png)

其内容分别为：

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200814123354.png" alt="image-20200814123354132" style="zoom:67%;" />

现在我们可以使用`compact`进行minor compact，或者`major_compact`进行major compact

先尝试minor compact：

执行后会，生成一个新的StoreFile，是由原先的数据重写得到的。短暂延迟后会删除之前的StoreFile。。。

![image-20200814123904081](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200814123904.png)

发现最后其合并后，只保留了三个版本的数据。相当于就是执行了MajorCompact。。。（是因为那些文件合并的大小太小了，而且文件数量太少。。）

![image-20200814123848683](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200814123848.png)



当前我们有了12个StoreFile: 依次存放的是 “xiaohua1”~ "xiaohua11"
![image-20200814124707499](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200814124707.png)

我们再使用`compact`，合并后短暂延迟，剩下了几个文件，说明执行的是minor compact：
![image-20200814125209541](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200814125209.png)



然后我们使用`major_compact`，就能让这些所有的StoreFile合并为一！！
![image-20200814125407246](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200814125407.png)

最终文件中只留下了，最新的三个版本：
<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200814125449.png" alt="image-20200814125449170" style="zoom:67%;" />





## 3.6、读写扩展

根据对读写流程的了解，发现全程都没有Master的出现，客户端始终只是与RegionServer和Zookeeper进行通信，因为Zookeeper上就存放了RegionServer的一些信息，以及集群的元数据。Client就不需要和Master进行交互，而是直接通过Zookeeper进行信息交换。

==所以即使Master进程挂掉，也不影响Client的数据读写！！==
但是当涉及到命名空间、Region、ColumnFamily的创建的的时候，没有Master是无法进行的！！
并且当数据增长到一定的量时，需要拆分Region调度到不同的RegionServer上，没有Master也是不行的哦！！



## 3.7、Region Split

当某个Region的StoreFile的总和达到了阈值，需要将一个Region**一分为二**！！

![image-20200814133421397](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200814133421.png)

这是0.95版本前的策略。0.95版本后的策略是，每当StoreFile的大小达到了：

`min(RegionNum^2*hbase.hregion.memstore.flush.size,hbase,hregion.max.filesize)`的时候，就会进行一次拆分。

- RegionNum表示Region的数量
- hbase.hregion.memstore.flush.size = 128MB
- hbase,hregion.max.filesize = 10G

那么初始创建的Region，当StoreFile达到 1^2 * 128 = 128M是切分为两个64MB的Region，
然后两个64MB的Region要 2^2 * 128 = 512MB 的时候切分为两个256MB的数据，…这样的切分方式在数据分布不均匀的时候容易造成数据倾斜！！

> 由于Region切分的缘故，所以列族也要进行切分，所以官方不建议使用多个列族，原因就是在Flush的时候由于是针对Region，可能会成倍生成小文件。但是据说现在引入了以Store为单位的刷写，那么就可以解决这个问题了。



# 四、HBase API

## 4.1、项目创建 依赖导入

Maven依赖：

```xml
<dependency>
    <groupId>org.apache.hbase</groupId>
    <artifactId>hbase-client</artifactId>
    <version>1.3.6</version>
</dependency>

<dependency>
    <groupId>org.apache.hbase</groupId>
    <artifactId>hbase-server</artifactId>
    <version>1.3.6</version>
</dependency>
```



## 4.2、DDL API

**官方文档的Chapter100,有API案例！！**尽量使用新的API！！！

### 4.2.1、获取HBase的Client(Admin对象)

```java
private static Admin admin;
private static Connection connection;
private static Configuration conf;

static {
    try {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop102,hadoop103,hadoop104");

        connection = ConnectionFactory.createConnection(conf);
        admin = connection.getAdmin();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```



### 4.2.2、常用操作

> 表是否存在

```java
public static boolean isTableExist(String tableName) {
    try {
        boolean exists = admin.tableExists(TableName.valueOf(tableName));
        return exists;
    } catch (IOException e) {
        e.printStackTrace();
    }
    return false;
}
```



> 创建表

```java
public static void createTable(String tableName, String... columnFamilies) {

    // 检查表是否存在
    if (isTableExist(tableName)) {
        System.out.println(tableName + "已经存在");
        return;
    }
    // 检查列族数量是否合乎要求
    if (columnFamilies.length <= 0) {
        System.out.println("请给出有效的列族");
        return;
    }

    // 创建表描述器
    HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));

    // 为表添加列族
    for (String columnFamily : columnFamilies) {
        // 创建列族描述器
        HColumnDescriptor columnDescriptor = new HColumnDescriptor(columnFamily);
        // 添加列族
        tableDescriptor.addFamily(columnDescriptor);
    }

    // 创建表
    try {
        admin.createTable(tableDescriptor);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```



> 删除表

```java
public static void deleteTable(String tableName) {
    try {
        // 下线表
        admin.disableTable(TableName.valueOf(tableName));
        // 删除表
        admin.deleteTable(TableName.valueOf(tableName));
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```





> 创建命名空间

```java
public static void createNamespace(String namespace) {
    // new NamespaceDescriptor(); 构造器私有，不能直接new,要用静态内部类Builder创建
    NamespaceDescriptor.Builder builder = NamespaceDescriptor.create(namespace);
    // Build对象调用build方法，内部调用私有的构造器进行创建
    NamespaceDescriptor namespaceDescriptor = builder.build();

    try {
        admin.createNamespace(namespaceDescriptor);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

在对应的命名空间里面建表，操作和命令行一样，只要在表名中标出命名空间即可。。



## 4.3、DML API

### 4.3.1、向表中插入数据(PUT)

从这里开始，我们不再使用admin对象来操作集群。而是先通过connection对象获取我们要操作的表，然后用表的具体方法来完成数据的操作。中间还需要将操作实例化为对应的对象，例如Put对象、Get对象

```java
public static void putData(String tableName, String row_key, String colFamily, String colQualifier, String value) throws IOException {
    // 获取要插入数据的表
    Table table = connection.getTable(TableName.valueOf(tableName));
    // 创建Put 对象，并设置row_key
    Put put = new Put(row_key.getBytes());

    // 为Put对象，配置数据信息
    put.addColumn(colFamily.getBytes(), colQualifier.getBytes(), value.getBytes());

    // 表执行插入
    table.put(put);
    
    // 关闭表
    table.close();
}
```

以上是单条数据的插入。也可以批量数据插入：

- 单行多列数据插入：

  put对象，每次addColumn表示对当前行的一列数据进行插入，多次addColumn即可实现单行多列数据批量插入

- 多行单列：

  每个Put对象，对应一行的数据插入操作，要操作多行就需要多个Put对象，Table的put方法支持接收List\<Put>,即可完成多行单列的数据插入

- 多行多列

  前两者的融合。。



### 4.3.2、获取数据(GET)

```java
public static void getData(String tableName, String row_key, String colFamily, String colQualifier) throws IOException {
    Table table = connection.getTable(TableName.valueOf(tableName));
    // 获取Get对象
    Get get = new Get(row_key.getBytes());

    // 设置列族(和列) 不设置默认全列族全列
    // get.addFamily(colFamily.getBytes()); //只设置列族
    get.addColumn(colFamily.getBytes(), colQualifier.getBytes());

    // 设置获取数据的版本数量 不得超过VERSIONS配置值,超过无效
    get.setMaxVersions(); // 不设置默认值与列族的VERSIONS配置相同

    Result result = table.get(get);

    // 获取结果Cell 并遍历输出内容
    List<Cell> cells = result.listCells();
    for (Cell cell : cells) {
        //Cell的 getValue等方法均已过时，推荐使用CellUtil的cloneValue()等方法
        String value = new String(CellUtil.cloneValue(cell));
        String col = new String(CellUtil.cloneQualifier(cell));
        String family = new String(CellUtil.cloneFamily(cell));
        System.out.println("ColFamily: " + family + ", "
                           + "ColQualifier: " + col + ", "
                           + "Value: " + value);
    }
    table.close();
}
```

同样也是可以批量查询的，多列多行都可以，操作过程和Put数据一致。

==注意对Cell的操作！==基本的get方法都已经过时，应换用`CellUtil`的cloneValue()等方法



### 4.3.3、扫描表(scan)

```java
public static void scanTable(String tableName, String startRow, String stopRow) throws IOException {
    Table table = connection.getTable(TableName.valueOf(tableName));
    // 创建Scan对象 并设置扫描的起始位置，默认全表
    Scan scan = new Scan(startRow.getBytes(), stopRow.getBytes());

    //scan.addColumn("".getBytes(), "".getBytes()); 同样也可以指定列

    // 获取扫描结果 ResultScanner对象(结果迭代器)
    ResultScanner results = table.getScanner(scan);

    for (Result result : results) {
        List<Cell> cells = result.listCells();
        for (Cell cell : cells) {
            System.out.println("row_key:" + new String(CellUtil.cloneRow(cell)) + ", "
                               + "col:" + new String(CellUtil.cloneQualifier(cell)) + ", "
                               + "value:" + new String(CellUtil.cloneValue(cell)));
        }
    }
    results.close();
    table.close();
}
```

不同于Get方式的获取数据，使用Scan扫描表，是跨行的。所以对于的扫描结果也就对应多个Result对象。但是并不是用Result数组来返回的，因为那样做当数据量很大的时候，结果全放在一个数组里面 极容易造成OOM。其使用的是一个类似于迭代器的东西，当输出数据的时候，直接到服务端拉取即可。



### 4.3.4、删除数据

在学习这个之前，我觉得我们有必要先来<a href="#review delete">重新认识一下HBase的数据删除！！</a>

<a name="delete">欢迎回来！</a>

首先Delete对象在设置列族和列的时候，和其他对象有点不同：

![image-20200815165231043](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815165231.png)

`addColumn`和`addColumns`，两个方法又分别有带时间戳和不带时间戳两种。

> addColumn

- 不带时间戳：删除指定列数据的最新版本数据

  为了避免干扰，我们每次都清空一下表。。重新插入数据。

  ```java
  public static void deleteData(String tableName, String row_key, String colFamily, String colQualifier) throws IOException {
      Table table = connection.getTable(TableName.valueOf(tableName));
  
      // 创建Delete对象 并指定操作的行
      Delete delete = new Delete(row_key.getBytes());
  
      delete.addColumn(colFamily.getBytes(), colQualifier.getBytes());
  
      table.delete(delete);
      table.close();
  }
  ```

  执行结果
  ![image-20200815165941429](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815165941.png)

   

- 加上时间戳

  ```java
  public static void deleteData(String tableName, String row_key, String colFamily, String colQualifier) throws IOException {
      Table table = connection.getTable(TableName.valueOf(tableName));
  
      // 创建Delete对象 并指定操作的行
      Delete delete = new Delete(row_key.getBytes());
  
      delete.addColumn(colFamily.getBytes(), colQualifier.getBytes(),1597482087810L);
  
      table.delete(delete);
      table.close();
  }
  ```

   执行结果：拉黑了一个时间戳，现有数据不受影响

  ![image-20200815170324244](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815170324.png)

所以此方法调用，==打的标记都是Delete!!==



> addColumns

- 不带时间戳

  ```java
  public static void deleteData(String tableName, String row_key, String colFamily, String colQualifier) throws IOException {
      Table table = connection.getTable(TableName.valueOf(tableName));
  
      // 创建Delete对象 并指定操作的行
      Delete delete = new Delete(row_key.getBytes());
  
      delete.addColumns(colFamily.getBytes(), colQualifier.getBytes());
  
      table.delete(delete);
      table.close();
  }
  ```

  ![image-20200815170640022](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815170640.png)

  和注释所描述一样，删除所有版本的数据！！使用的标记是DeleteColumn！

- 带时间戳

  ```java
  public static void deleteData(String tableName, String row_key, String colFamily, String colQualifier) throws IOException {
      Table table = connection.getTable(TableName.valueOf(tableName));
  
      // 创建Delete对象 并指定操作的行
      Delete delete = new Delete(row_key.getBytes());
  
      delete.addColumns(colFamily.getBytes(), colQualifier.getBytes(),1597482496670L);
  
      table.delete(delete);
      table.close();
  }
  ```

  ![image-20200815171004424](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815171004.png)

  还是使用的DeleteColumn, 效果是什么样不用我多说了吧。。

  所以使用此法删除==打的标记都是DeleteColumn！！==



> 删除列族

使用`addFamily()`方法，同样也可以时间戳，效果与addColumns一样。

```java
public static void deleteData(String tableName, String row_key, String colFamily, String colQualifier) throws IOException {
    Table table = connection.getTable(TableName.valueOf(tableName));

    // 创建Delete对象 并指定操作的行
    Delete delete = new Delete(row_key.getBytes());

    delete.addFamily(colFamily.getBytes());

    table.delete(delete);
    table.close();
}
```

![image-20200815182414930](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815182415.png)

使用的是`DeleteFamily`标记！！



> 删除整行

如果什么都不指定的话，直接创建Delete对象，然后table调用delete方法，则直接删除对应行。
其过程就是**对所有ColumnFamily 加上DeleteFamily！！**

```java
public static void deleteData(String tableName, String row_key, String colFamily, String colQualifier) throws IOException {
    Table table = connection.getTable(TableName.valueOf(tableName));

    // 创建Delete对象 并指定操作的行
    Delete delete = new Delete(row_key.getBytes());

    table.delete(delete);
    table.close();
}
```

![image-20200815195213760](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815195213.png)





## <a name="review delete">3.8、重新认识删除</a>

### 3.8.1、什么时候数据会被真正删除？

- ==Major Compact的时候会删除数据==
- ==Flush的时候删除数据！==

**那么他们分别是删除什么数据？**

> Major Compact删除数据

MinorCompact胆小,不会对数据内容参数影响。MajorCompact则不一样，将**一个Store**的所有HFile合并成一个，删除所有过期的数据，和被标记了删除的数据。删除方式就是，将所有HFile的内容读到内存中，挑选出需要保留的数据（多版本数据等），其他被标记或者过期的数据（时间戳太老，Version超额的）直接抛弃不写入到新的HFile中。

所以这种操作是很耗费资源的，所以我们都会关闭其7天自动Compact的配置，改为手动执行。

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815141015.png" alt="image-20200815141014890" style="zoom:67%;" />

 

> Flush删除数据

Flush，是过程对象是MemStore到HFile，而且每次刷写都是一个全新的HFile。**删除的是内存中无用的数据**。

那么哪些是无用的数据？由于MemStore在刷写数据的时候，不知情其他HFile中的数据，所以刷写是MemStore中没有持久化的数据，并剔除过期数据（只保留VERSIONS个数据版本。）和已经标记删除的数据，这里和Compaction是一样的处理方式，只不过它所能判断的**范围是当前MemStore中没有持久化的数据。**（即便另一个HFile中也有VERSIONS个版本的数据，但是MemStore不知道。）对于多个HFile中的版本舍取是MajorCompact的工作！！

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815142605.png" alt="image-20200815142605562" style="zoom:67%;" />

==并且删除的标记，在Flush的时候是，不会删除的！因为删除标记可能对于已经持久到其他的HFile的数据起作用。==**是否有作用只有当MajorCompact的时候，所有的记录都见面了才知道。**



==使用`scan 'xx',{RAW=>TRUE,VERSIONS=10}`其实看到的数据是MemStore、HFile(和BlockCache)的综合数据，千万不要误以为全是内存的数据，不信你使用`major compact`后，短暂延迟后所有的HFile归并，多余的数据就会消失，删除标记基本也会消失==

----

### 3.8.2、删除标记

现在我们来谈谈关于的==删除的标记==，简简单单的删除操作，却有多种删除标记:

- **Delete**
- **DeleteFamily**
- **DeleteColumn**

![image-20200815153135677](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815153135.png)

> 测试Delete

我们现在清空这个表，分别对（1001，info:name）三次写入数据：aaa,bbb,ccc，最新的数据是ccc。（没有持久化到HFile）但是我们执行
`delete 'stu','1001','info:name'`后，bbb出来了：
<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815153803.png" alt="image-20200815153802925" style="zoom:67%;" />

此时标记是Delete!! ==说明Delete标记只对单个时间戳有效。==假如我们现在向这个时间戳插入数据的话应该是无效的：

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815154147.png" alt="image-20200815154147431" style="zoom:67%;" />

果不其然，相当于被打了Delete标记的时间戳被“拉黑了”。。

当我们刷写并Compact后，删除标记和别标记删除的ddd就没了！！！也就进一步证实了Delete标记只能作用于单个时间戳！！



> DeleteColumn

再次清空表！还是插入三条数据，这次我们执行`deleteall 'stu','1001','info:name'`

![image-20200815155328646](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815155328.png)

此时所有的数据都没有了，道理和我们初次学习删除的时候一样。==DeleteColumn标记作用于一个列，时间戳小于此标记的数据都会视为无效数据==

我们直接刷写数据，无效数据删除了，删除标记还在！
![image-20200815155701859](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815155701.png)

我们执行major compact，标记移除：

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815155816.png" alt="image-20200815155816134" style="zoom:67%;" />

 

> DeleteFamily

这个在命令行中使用`deleteall '表名','row_key'`的时候会出现，命令行删除列族不可用。。但是API中的删除列族可以，也会出现这个标记。
<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815160503.png" alt="image-20200815160503398" style="zoom:67%;" />

和DeleteColumn差不多，==它是作用于列族！！在此标记时间戳前所有向此列的此列族所有的插入数据都无效！！==

![image-20200815160900110](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815160900.png)

不出所料的话，现在刷写，所有的数据都会消失，只会留下删除标记：
![image-20200815161130785](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815161130.png)

执行major_compact后，这些标记也就一并消失了。这里就不截图了。

 

> 总结一下：

- Delete标记：作用于一个时间戳（单条数据），即当前最新数据所在的时间戳！！不影响其他时间戳
- DeleteColumn：作用于某行的一个列，所有小于此标记时间戳的列数据都视为过期无效数据！
- DeleteFamily：作用于某行的一个列族，所有小于此标记时间轴的所有列族数据视为无效数据！
- 

在重新认识了HBase的后，我们再返回学习<a href="#delete">DML API中的Delete=></a>





## 4.4、HBase API和MR交互

既然是一个存储框架那么必然有他作用的位置，这些数据最终是要被拿去处理分析然后再存储的。现在我们让HBase和MR进行交互，这也是官方给出的案例！

### 4.4.1、MR读取HBase的数据

1. 使用`bin/hbase mapredcp`查看MR操作HBase所用的jar包

2. 环境变量设置

   /etc/profile：

   ```shell
   export HBASE_HOME=/opt/module/hbase-1.3.6
   export HADOOP_HOME=/opt/module/hadoop-2.7.7
   ```

   修改Hadoop配置目录（/opt/module/hadoop-x.x.x/etc/hadoop）下的hadoop-env.sh：

   ```shell
   export HADOOP_CLASSPATH=$HADOOP_CLASSPATH:/opt/module/hbase-1.3.6/lib/*
   ```

   ![image-20200817110743596](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200817110743.png)

   （图中有误，末尾缺了个**/***）

   就是将刚才我们看到的jar包让Hadoop能够扫描到！

   配置完成后，重新加载环境变量，并分发修改的配置(hadoop-env.sh)！！

3. 使用官方的案例并运行

   启动HBase、Hadoop（HDFS、YARN）、ZK.
   到HBase的根目录下，执行`/opt/module/hadoop-2.7.7/bin/yarn jar lib/hbase-server-1.3.6.jar rowcounter stu`
   如果配置了Hadoop的环境变量可以简写。。。`yarn jar ...`。

   这是一个统计表行数的案例。`rowcounter`是对应jar包中类的名字，stu则是HBase中我们要统计的表的名字！

   ![image-20200817112705525](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200817112705.png)

   结果查看：
   ![image-20200817112747385](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200817112747.png)

   结果验证：

   ![image-20200817112839915](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200817112839.png)

 

这是MR读取HBase的数据，那么肯定就可以MR计算结果写到HBase中去！！



### 4.4.2、MR写入数据到HBase

1. 首先准备好一个tsv文件fruit.tsv（即数据之间使用tab间隔的文件）并上传到HDFS上，便于后续使用

   ```
   1001	apple	red
   1002	Orange	orange
   1003	banana	yello
   1004	grape	purple
   ```

2. 创建一个表，并带上列族

   `create 'fruit','info'`

3. 命令行执行：

   `yarn jar lib/hbase-server-1.3.6.jar importtsv -Dimporttsv.columns=HBASE_ROW_KEY,info:name,info:color fruit hdfs://hadoop102:9000/fruit.tsv`

   其中importtsv同样是案例测试的类名，后面的参数表示 将hdfs上的指定文件或目录中的数据按tsv格式解析为ROW_KEY、info:name、info:color三个列的数据，并导入到Hbase的fruit表中！！

4. 执行结果：

   ![image-20200817132905849](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200817132905.png)

   HBase表数据：

   ![image-20200817132958293](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200817132958.png)

   OK!!

----

这样一来HBase就能够将数据提供给分析计算框架来处理了，并且也能接受到它们的数据写入！！



### 4.4.3、自定义HBase-MapReduce（一）

上面还只是官方的，我们自己从零开始编码实现一下

Mapper：充当一个了传输数据的摆设。。

```java
public class MyMapper extends Mapper<LongWritable, Text, LongWritable, Text> {

    /**
     * 由于 Mapper中没有太多业务，我们直接略过直接写到Reducer即可
     * @param key
     * @param value
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        context.write(key, value);
    }
}
```

 

Reducer:处理数据并写出

```java
public class MyReducer extends TableReducer<LongWritable, Text, NullWritable> {
    @Override
    protected void reduce(LongWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // 遍历Reducer的组数据
        for (Text value : values) {
            // 开始解析每一行数据
            String data = value.toString();
            String[] fields = data.split("\t");

            // 插入表，需要构建Put对象 并从读取的数据中取到row_key并设置
            Put put = new Put(fields[0].getBytes());

            // 为Put对象添加信息 列族信息和列信息是可以通过调用时传入的
            put.addColumn("info".getBytes(), "name".getBytes(), fields[1].getBytes());
            put.addColumn("info".getBytes(), "color".getBytes(), fields[2].getBytes());

            context.write(NullWritable.get(), put);
        }

    }
}
```

> **为什么使用继承`TableReducer`?**
>
> ==和传统MR不同，之前我们所写的MR数据都是导出到文件系统中的某个文件中。而这次我们需要将数据导入到HBase的表中==，这个抽象类就是HBase提供给我们使用MR与HBase交互的。
>
> **TableReducer和传统的Reducer有何不同？**
>
> ==TableReducer的ValueOut是固定的，即需要返回要求之内符合规范的值！==ValueOut就是操作表的东西，此时KeyOut已经不那么重要了。指定的ValueOut的是Mutation类（抽象类）。
>
> ![image-20200818095109401](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200818095109.png)
>
> **Mutation有哪些实现类？**
>
> <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200818095330.png" alt="image-20200818095330823" style="zoom:67%;" />
>
> 其中Put、Delete对象都是我们接触过的，那么也就是说我们==在Reducer端就已经将数据封装为了对表操作的对象！==
>
> **那么Driver中肯定就有一项设置表和一项设置读取文件的配置！！**



Driver：

```java
public class MyDriver implements Tool {

    private Configuration conf;

    @Override
    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(conf);

        // 设置DriverClass
        job.setJarByClass(MyDriver.class);
        // Mapper Class设置
        job.setMapperClass(MyMapper.class);

        // Mapper端的输入与输出
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(Text.class);

        // Reducer设置 表名,ReducerClass,job
        TableMapReduceUtil.initTableReducerJob(args[0], MyReducer.class, job);

        // 设置输入路径参数
        FileInputFormat.addInputPath(job, new Path(args[1]));

        // 任务提交
        job.waitForCompletion(true);
        return 0;
    }

    @Override
    public void setConf(Configuration configuration) {
        conf = configuration;
    }

    @Override
    public Configuration getConf() {
        return conf;
    }

    public static void main(String[] args) {
        try {
            // 运行任务
            ToolRunner.run(new MyDriver(), args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

注意代码中设置Reducer的方法不是和以前一样了，`TableMapReduceUtil.initTableReducerJob()`这里就需要设置表名，并设置Reducer（继承了TableReducer的）的类名，以及MR的一个Job实例。完美将HBase和MR打通！



> 测试

打包后上传到集群上，这里我们上传到HBase的lib目录下，准备开始运行：
我们先将之前的使用的fruit的表清空！

执行：`yarn jar lib/hbasestudy-1.0-SNAPSHOT.jar com.sakura.mr.MyDriver fruit /fruit.tsv`

yarn jar不用说固定的，后面跟我们上传的jar包，然后就是运行的主类即MyDriver类，然后按照程序中缩写的顺序填写参数！==注意这里的文件位置还是HDFS上的文件位置！！==

任务完成后，我们再次查看HBase中表的数据：完美复现：
![image-20200818104418115](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200818104418.png)



以上还只是最简单的实现，工作中还是要按照业务需求来完成!



### 4.4.4、自定义HBase-MapReduce(二)

上面都还只是MR和HBase单向交互，即从HBase取或者向HBase中写。现在实现终极目标：==从HBase中读取数据处理后写回到HBase中！！==

mapper

```java
public class MyMapper extends TableMapper<ImmutableBytesWritable, Put> {

    /**
     * 对于逻辑表中每一行数据调用一次 map
     * @param key 
     * @param value
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        Put put = new Put(key.get());
        for (Cell cell : value.listCells()) {
            // 取出name字段 并封装为Put对象
            if ("name".equals(new String(CellUtil.cloneQualifier(cell)))) {
                put.add(cell);
            }
        }
        // 写出到Reducer
        context.write(key, put);
    }
}
```

> 这里来一个`TableMapper`整好呼应我们之前的TableReducer，同样这个Mapper是HBase提供给我们从HBase中读取数据的！和普通Mapper不同，他固定了KeyIn和ValueIn，分别是`ImmutableBytesWritable`类, `Result`类。
>
> 前者是不可变字节数组，封装的是RowKey的字节数组，Result这是对应逻辑表中的一行数据，包括了所有列族及列。通过这两个参数就可以逐行读取表中的任何一个单元的数据了。
>
> 当然，表的消息要在Driver中进行设置！
>
> 除此以外，我们在Mapper阶段就对数据进行了操作对象的封装，到Reducer阶段可以直接写出！



Reducer：

```java
public class MyReducer extends TableReducer<ImmutableBytesWritable, Put, NullWritable> {
    @Override
    protected void reduce(ImmutableBytesWritable key, Iterable<Put> values, Context context) throws IOException, InterruptedException {
        for (Put value : values) {
            context.write(NullWritable.get(), value);
        }
    }
}
```

 

Driver：

```java
public class MyDriver implements Tool {

    private Configuration conf;

    @Override
    public int run(String[] strings) throws Exception {
        Job job = Job.getInstance(conf);

        job.setJarByClass(MyDriver.class);

        // job设置Mapper
        TableMapReduceUtil.initTableMapperJob("fruit",
                new Scan(),
                MyMapper.class,
                ImmutableBytesWritable.class,
                Put.class, job);
        // 设置Reducer
        TableMapReduceUtil.initTableReducerJob("fruit2", MyReducer.class, job);

        // 提交
        boolean res = job.waitForCompletion(true);
        return res ? 0 : 1;
    }

    @Override
    public void setConf(Configuration configuration) {
        conf = configuration;
    }

    @Override
    public Configuration getConf() {
        return conf;
    }

    public static void main(String[] args) {
        try {
            ToolRunner.run(new MyDriver(), args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

**注意Job的Mapper的设置，将原先Mapper的相关设置集成到了一个方法中。**和Reducer差不多，都是使用TableMapReduceUtil下的方法！

> 如何在本地连接到集群启动？

1. 将HBase的hbase-site.xml复制一份放到项目的resources目录下（千万不要修改文件名！！）

2. Driver代码改动：

   ```java
   public static void main(String[] args) {
       try {
           // 添加一份HBase的配置，并在启动时使用！！
           Configuration configuration = HBaseConfiguration.create();
           ToolRunner.run(configuration ,new MyDriver(), args);
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
   ```

   通过看create()方法 你就知道为什么不让修改hbase-site.xml的文件名了！！<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200818135035.png" alt="image-20200818135035471" style="zoom:67%;" />

3. 本地启动的时候，如果报了关于jackson的类未定义或者未找到的错，需要导入jackson相关的包：

   ```xml
   <dependency>
       <groupId>org.codehaus.jackson</groupId>
       <artifactId>jackson-mapper-asl</artifactId>
       <version>1.8.8</version>
   </dependency>
   ```

4. 并且本地启动是没有日志输出的，只能死等。或者导入log4j依赖，然后去吧hadoop里面log4j.properties的配置文件复制一份

5. 运行完成，看结果：
   ![image-20200818143937115](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200818143937.png)

   所有name列的数据，被放到了fruit2表中！符合预期。

----



## 4.5、与Hive集成

### 4.5.1、HBase与Hive的对比

> Hive

1. 数据分析框架
2. 数据仓库
3. 用于数据分析和清洗，延迟高
4. 基于HDFS、MapReduce（数据存放在DataNode，编写的HQL最终转化为MR任务执行）



> HBase

1. 数据库：==面向列存储的==非关系行数据库
2. 用于存储结构化和非结构化数据（不适合做关联查询）
3. 存储基于HDFS,数据文件以HFile格式存储
4. 延迟低，可以接入在线业务使用





### 4.5.2、对接环境准备

1. 环境变量设置(已经配置可以忽略)

   ```shell
   # HBase
   export HBASE_HOME=/opt/module/hbase-1.3.6
   
   # Hive
   export HIVE_HOME=/opt/module/hive-1.2.2
   ```

    

2. 将HBase的jar包，创建软链接到Hive的lib目录下

   `ln -s $HBASE_HOME/lib/* $HIVE_HOME/lib/`

    

3. 修改hive-site.xml 让其能够找到zookeeper的存在和位置

   ```xml
   <!-- zookeeper -->
   <property>
       <name>hive.zookeeper.quorum</name>
       <value>hadoop102,hadoop103,hadoop104</value>
   </property>
   <!-- zookeeper端口(因为没有默认值) -->
   <property>
       <name>hive.zookeeper.client.port</name>
       <value>2181</value>
   </property>
   ```

    

4. 创建一个Hive和Hbase的关联表

   ```sql
   create table hive_hbase_emp_table(
       empno int,
       ename string,
       job string,
       mgr int,
       hiredate string,
       sal double,
       comm double,
       deptno int
   )
   stored by 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' -- 存储引擎
   with serdeproperties("hbase.columns.mapping"=":key,info:ename,info:job,info:mgr,info:hiredate,info:sal,info:comm,info:deptno") -- 列的映射
   tblproperties("hbase.table.name"="hbase_emp_table"); -- hbase的table
   ```

   必须保证两个框架中都没有这俩表！！

   可能会出现版本兼容问题：

   ![image-20200818161709815](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200818161709.png)

   需要在hbase的环境下重新编译hive，将对应的jar包拷贝回集群。操作较为复杂。。
   **这种版本兼容问题，后续我们会使用cdh来解决！！**



### 4.5.3、常用的Hive对接场景

我们一般都是在Hbase中有了一定数据后，然后通过Hive建立一个表和HBase的已有表进行关联，因为Hive中查看数据更加方便和顺眼。但是！！==在HBase中表已存在的时候，hive只能创建外部表(外部表的概念参考hive学习笔记)。==

![image-20200818162311842](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200818162311.png)

==创建好后，所有HBase的数据，都可以使用Hive进行查询。并且HBase可以持续修改。。==

![image-20200818162550355](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200818162550.png)

这才是生产中常用的使用方式！！



# 五、HBase优化

## 5.1、高可用

在入门框架学习的时候，我们就了解到了Masters是自带高可用特性的，即集群中的Master故障掉线后，通过==争夺资源==的方式，选出新的Master，保证集群的高可用！

> 演示：

首先我们通过单节点启动HBase集群中每台机器的HMaster，就会发现首先启动被选为master，后续上线的均作为备份，随听命等待上任！

![1](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200828125622.png)

通过web页面就可以很清晰看出来！

![2](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200828130108.png)



但是使用这种单节点启动的方式过于繁琐，所以**我们可以直接创建一个配置文件，标明我们想要作为BackupMaster的主机即可！**

`backup-masters`（conf目录下）

```
hadoop103
hadoop104
```

这样就表明，当集群启动的时候，会为这两个主机也启动HMaster进程作为Backup！

一键启动集群测试：

![3](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200828131132.png)

![4](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200828131317.png)

所以HBase是自带高可用功能的哦！



## 5.2、预分区

在HBase进阶学习过程中，我们就接触了RegionSplit的问题，单个Region在StoreFile达到一定量级后，会自动切分为两个Region进行存储，但是在集群中这种方式无法避免数据倾斜的问题。而现在要学习的预分区，就是解决数据在集群中发生倾斜的问题的一种手段。==提前预估数据的数量，同时结合集群的规模来合理进行预分区！==

默认情况下，初始创建的表都只有一个分区（即一个Region）:

![6](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200828135129.png)

> 预分区表创建

在正式创建预分区表的时候，我们先要知道分区是通过什么来决定数据所在分区的！！rowkey和一个全新的键值，这里我们暂且称其为**分区键**，进行比较，每个分区Region都有预定的**start_key和end_key**，web页面中表详细信息中，分区信息（TableRegions）就能看到这俩字段。对应的rowkey的数据就可以定位到某一个分区了！

所以我们创建表的时候，主要就是对这两个值进行设置！！

```shell
create 'staff','info',SPLITS=>['1000','2000','3000','4000']
```

`SPLITS=>['1000','2000',..]`这是我们指定的分区切点，四刀一共五个分区！

![7](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200828141626.png)

==注意这里的分区键在和Rowkey比较的时候是按**字典序！！**因为rowkey本身在传输的时候就是字节数组！==

所以你插入的rowkey=533的数据按字典序就在最后一个分区中！！



当已有数据量特别大的时候，我们人为分析无法很精准，而且切点巨多写起来也很麻烦，所以HBase也可以自己分析文件数据进行分区，==分区数量=文件rowkey数量+1==

splits.txt文件内容：

```
aaa
ccc
ddd
bbb
```

HBase会如何处理呢？

```shell
create 'splits_table','data',SPLITS_FILE=>'/opt/data/splits.txt'
```

使用`SPLITS_FILE`指定我们要进行分区的数据文件，看看结果吧：（你以为他会直接按你写的数据顺序创建分区？）
![8](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200828143038.png)

HBase还是聪明的，按照数据字典序排序后，再来划分分区。

---

但是实际开发中，更多的还是使用API来实现分区的！

之前我们创建表的时候，就只丢一个表描述器就不管了。但是他还有俩重载方法：

```java
public void createTable(HTableDescriptor desc, byte[] startKey, byte[] endKey, int numRegions)

public void createTable(HTableDescriptor desc, byte[][] splitKeys)
```

- 给出所以数据StartKey和EndKey，然后指定分区数量，HBase平均划分

- 给出一个分区键的字节数组，按照给定的分区切点进行分区，类似于第一种命令行分区方式。

  （这里为什么是二维数组？你看看上面那个StartKey和EndKey不就是字节数组吗？！一个存放字节数组的数组～～）



## 5.3、RowKey设计

上面说了数据具体放在那个区，还是和RowKey息息相关，所以设计好RowKey至关重要。要从以下三个方面考虑：

- 散列性
- 唯一性
- 长度原则（一般70～100位）

常见的设置方案：Hash、加密算法、序列化、时间戳倒置、拼接字符串…

==但是又有一个问题，为了方便我们读取数据方便，我们希望按照数据的特征集中存放，保证一定的数据集中性==，这样就和散列性产生了冲突！所以我们还需要将数据的某些特征字段和RowKey进行关联。

> 背景：xx公司的通话记录数据要求分区存放，要满足用户查询指定时间段的通话记录。

看到前半段，你可能会想到按电话号码分区，假如预定分区数量是300个（299个分区键）。那么为了保证同一个电话号码分到一个区里面。最粗暴的办法就是用手机号对300取模。将结果拼接到手机号的前面：

```
tel:15612341234
region:tel%300 = 15612341234%300 = 134

rowkey => 134_15612341234
```

通过这种方式确定的RowKey，就可以确保相同的电话号码在同一个分区中，并且是连续的！

----

但是继续看后半段，用户要查询时间段的通话记录（假设以日为单位）。因为每个人的通话频率不同，所以仅仅是按照手机号分区未免太草率，也极容易造成数据倾斜！**所以我们决定进一步优化Rowkey，让其关联起通话的日期（具体到日），这样通过手机号和具体的月份就可以确定到一个分区，然后读取数据通过手机号和日期比对取出连续的数据即可！！**

假如还是300个分区，我将手机号和日期都数字化，然后进行一番操作（此处以加法+作为示范）然后对300取模即可确定分区：

```
tel:15612341234
date:2020-08-28 => 20200828

region: (tel+date)%300 = 162

rowkey => 162_15612341234_20200830

查询
startkey => 162_15612341234_202008
endkey => 162_15612341234_202009
```

这样即完成了数据的散列，又将数据内容和RowKey关联保证了一定的集中性！！



## 5.4、内存优化和基础优化

> 内存优化

**在HBase中内存并不是越大越好**，应为内存大间接就会导致MemStore分到的内存也就会随之增大，那么当MemStore达到了内存峰值时候，只能阻塞慢慢刷写，直到刷写到正常范围才能恢复正常工作，而大内存就会导致这个阻塞的时间增长！

并且在面临RegionServer级别的刷写到来时，大内存导致的就是刷写的时间会增加！



> 常见的优化参数

- **允许在HDFS的文件中追加内容**

  `dfs.support.append`(hdfs-site.xml, hbase-site.xml)

  开启HDFS追加同步，可以优秀的配合HBase的数据同步和持久化。默认值为true

   

- **优化DataNode允许的最大文件打开数**

  `dfs.datanode.max.transfer.threads`(hdfs-site.xml)

  HBase在写入和读取数据的时候通常都是对多个文件进行操作，可以根据集群的规模来进行调整。默认为4096

   

- **优化延迟高的数据操作的等待时间**

  `dfs.image.transfer.timeout`(hdfs-site.xml)

  有可能某些数据操作的正常情况下延时偏高，可能会被误判为超时操作。当存在这种情况的时候建议将此值设置调高，减少误判。默认值为60000ms=60s

   

- **优化数据的写入效率**

  `mapreduce.map.output.compress`

  `mapreduce.map.output.compress.codec`(mapred-site.xml)

  将写入的数据在MapReduce阶段进行压缩，提高数据写入到HBase的速度。

   

- **设置RPC监听数量**

  `hbase.regionserver.handler.count`(hbase-site.xml)

  保证读写请求的高并发度，当读写请求特别密集的时候，调高此配置值。默认为30

   

- **优化HStore(HFile)文件大小**

  `hbase.hregion.max.filesize`(hbase-site.xml)

  在对接MR执行任务的时候，单个Region就对应一个Job，HFile太大输入数据就非常耗时。当Region的HFile大小总和达到了这个值，就会强行将HFile一分为二，并同时生成一个HRegion！

   

- **优化HBase客户端**

  `hbase.client.write.buffer`(hbase-site.xml)

  客户端的缓存大小。增大该值，客户端可以缓存更多的查询结果，同批次可以发送更多的请求，以减少RPC调用的次数，减小集群的压力，但是是以消耗更多的内存作为代价的！

   

- **指定scan.next扫描HBase所获取的行数**

  `hbase.client.scanner.caching`（hbase-site.xml）

  记得我们在使用API查询数据的时候，我使用了一个ResultScanner，这个东西就是负责从HBase的查询结果中扫描数据传递给程序的。因为一次性读出的话，数据太大占用的内存会很大拖慢程序运行。

   

- **flush、Compact、Splits**机制的策略优化

  参考前面所讲的相关内容。。