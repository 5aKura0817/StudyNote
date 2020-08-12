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



## 2.2、命令行操作

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