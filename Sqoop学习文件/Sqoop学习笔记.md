[toc]

# 一、Sqoop简介

Sqoop是一款开源的数据传输工具！**主要用于Hadoop(Hive)和传统数据库（MySQL、Oracle、PostgreSQL）之间的数据传输。**

最早作为Hadoop的一个第三方模块存在，后面为了开发人员更快地迭代开发，独立出来成为一个Apache项目！

<img src="http://sqoop.apache.org/images/sqoop-logo.png" alt="Sqoop" style="zoom:200%;" />

其实Sqoop可看作是`Sql to Hadoop`的缩写！

官网地址：http://sqoop.apache.org/

> 为什么需要Sqoop

我们在实际的生产过程中，从应用采集的数据大多存放在这些传统的数据库中。而我们需要用大数据框架对数据进行分析的时候，就需要将数据迁移到大数据的数据库中。最终的分析计算结果还要传回传统数据库中，被应用利用展现给用户。

所以此过程中，数据在两端之间的传输和正确性的直接影响了数据的准确性，虽然不起眼但是尤为重要！



# 二、Sqoop安装配置



本次学习我们使用的版本的是1.4.6（当前最新版本为1.4.7）

下载地址：https://archive.apache.org/dist/sqoop/1.4.6/
下载版本：sqoop-1.4.6.bin__hadoop-2.0.4-alpha.tar.gz  



可以看到官网中，有一个Sqoop2，版本号多为1.99.x，官方也说明了这个发行版本不会用于生产部署，而是多用于测试。所以我们不必关心。

![image-20201011152803040](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20201011152803040.png)



## 安装步骤

1. 下载对应的包，拷贝到服务器，并解压。

2. 将`conf/sqoop-env-template.sh`重命名为`sqoop-env.sh`

   ```shell
   cd conf
   mv sqoop-env-template.sh sqoop-env.sh
   ```

3. 修改`sqoop-env.sh`

   配置大数据框架中常用的存储框架（Hadoop、Hive、Zookeeper、HBase）的路径

   这里由于我们目前只学习了这几种大数据的数据存储框架，所以只设置这几个，还有其他的存储框架我不做设置当然也就无法使用！

   ```shell
   export HADOOP_COMMON_HOME=/opt/module/hadoop-2.7.7
   export HADOOP_MAPRED_HOME=/opt/module/hadoop-2.7.7
   
   export HBASE_HOME=/opt/module/hbase-1.6.0
   
   export HIVE_HOME=/opt/module/hive-2.3.7
   
   export ZOOCFGDIR=/opt/module/zookeeper-3.6.1
   export ZOOKEEPER_HOME=/opt/module/zookeeper-3.6.1
   ```

4. 我们最终是要连接到MySQL的，所以还需要**MySQL的连接驱动。**

   将驱动Jar包拷贝到sqoop的lib目录下

    

5. 配置环境变量（可选）

   ```shell
   # sqoop
   export SQOOP_HOME=/opt/module/sqoop-1.4.6
   export PATH=$PATH:$SQOOP_HOME/bin
   ```

6. 验证Sqoop

   ```shell
   sqoop help
   (这是配置了环境变量，没配置的话要到Sqoop的目录下使用bin/sqoop help)
   ```

   ![image-20201011160545836](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20201011160545836.png)

    他这里提示的HCAT_HOME和ACCUMULO_HOME就是对应其他的两种存储框架，我们没有进行设置，所以提示我们如果我们导入导出数据到这两个框架下可能失败。。。

   所以现在可以确定Sqoop配置是正确的！

   

7. **测试连接MySQL!**

   ```shell
   sqoop list-databases --connect jdbc:mysql://hadoop102:3306/ --username root --password 123456
   ```

   ![image-20201011161113132](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20201011161113132.png)

   可以看到正确输出了我们MySQL中的所有数据库。

   来说说这条sqoop命令，结合我们上面的sqoop help。

   `list-databases`是sqoop的功能命令：列出数据库服务器上的所有数据库

   后面的`--connect、--username、 --password`顾名思义就是连接数据库啦！





# 三、案例学习

## 案例一、数据导入

**前置知识提要**：

在Sqoop中所说的**导入**，指的都是**从非大数据数据库（RDBMS）导入数据到大数据数据库（HDFS、HBase、Hive）中！**



### 1.1、从MySQL导入表数据到HDFS

1. 在MySQL中创建模拟数据

   ```mysql
   CREATE database company;
   
   use company;
   
   CREATE table staff (
       `id` int not null auto_increment primary key,
       `name` varchar(255) not null comment '用户名',
       `sex` varchar(255) not null comment '性别'
   );
   
   insert into staff(name,sex) values 
   ('Bob','Male'),('Lily','Female');
   
   SELECT * from staff;
   
   /**
   id|name|sex   |
   --|----|------|
    1|Bob |Male  |
    2|Lily|Female|
    */
   ```

2. 启动Hadoop集群（HDFS、Yarn）

3. 使用Sqoop的`import`命令，将数据从MySQL导入到HDFS（**重点**）

   （可以使用`sqoop import --help`查看对应的参数设置）

   ```shell
   sqoop import \
   --connect jdbc:mysql://hadoop102:3306/company \
   --username root \
   --password 123456 \
   --table staff \
   --target-dir /user/company/ \
   --delete-target-dir \
   --num-mappers 1 \
   --fields-terminated-by "\t"
   ```

   （使用`\`是为了命令分行，回车不执行）

   前三个参数不用多说，从第四个参数开始：

   `--table`：指定你要导入到HDFS的数据表

   `--target-dir`：数据在HDFS中的存放位置

   `--delete-target-dir`：以删除模式（若target-dir已经存在，先删除原有的，然后重新创建）导入数据。（实际生产中要慎用！）

   `--num-mappers`：导入数据过程中使用的map task的个数。（根据数据大小自行决定，合理设置可以提高导入数据的效率）

   `--fields-terminated-by`：数据格式化写入到文件的时候，数据字段分隔符。

    

4. 查看执行过程中的日志输出

   可以看出这个数据导入过程就是一个单独的Map Job的执行过程，并没有进行Reduce阶段。。

   ![image-20201013170206031](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20201013170206031.png)

    

   也就是说此时ReduceTask的个数被设置为了0，那么输出文件的个数就会与MapTask的数量保持一致！（忘记了的，去看Hadoop-MapReduce中ReduceTask的工作机制！）

   那么此时HDFS的`/user/company/`目录下就必有一个`part-m-xxxx`的文件，里面的内容就是我们从MySQL导入的数据！

    

5. 查看HDFS中对应目录下的输出文件

   ![image-20201013171418829](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20201013171418829.png)

   到这里，从MySQL全表数据导入到HDFS就算成功了！！



### 1.2、查询导入

即将要学到的查询导入，顾名思义是将SQL Query的结果导入到HDFS中。

首先我们往MySQL中添加一些数据。

```mysql
insert into staff(name,sex) values
('Louis','Male'),('Kristin','Female'),('Mary','Female');

/**
1	Bob	Male
2	Lily	Female
3	Louis	Male
4	Kristin	Female
5	Mary	Female
*/
```

假如现在我想把所有sex=Female的数据导入到HDFS中，用上面的全表数据导入肯定是不行的。所以要用到这里的查询导入。来看看使用方法吧：

```shell
sqoop import \
--connect jdbc:mysql://hadoop102:3306/company \
--username root \
--password 123456 \
--target-dir /user/company \
--delete-target-dir \
--num-mappers 1 \
--fields-terminated-by "\t" \
--query "select name,sex from staff where sex='Female' and \$CONDITIONS"
```

来看看有什么变化吧，中间包括了很多小点！！

- 由于不再是全表数据的导入，不再使用`--table` 取而代之的是`--query`

- `--query`后面的SQL语句怎么写，为什么这么写，哪些不推荐写，官方文档都做出了解释！

  ![image-20201013205615705](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20201013205615705.png)

  1.4.6官方文档：<http://sqoop.apache.org/docs/1.4.6/SqoopUserGuide.html>

   

  `$CONDITIONS`??这是个什么东西？我的表里面没有这玩意儿啊？！

  > 上图中，第一个红框中的文字就能解释。
  >
  > *当你想要将一条查询的结果作为数据导入时，每个MapTask都要执行此查询的副本，然后通过Sqoop的==边界条件==对结果进行分区，而`$CONDITIONS`就是用于分区的条件，在每个Sqoop进程中它会被一个唯一的表达式替代，你还必须指定一个切分列，使用`--split-by`*

  很明确，每个MapTask都会执行此次查询，相互之间是副本关系，但是我们要做的数据导入，那么我们**必须保证导入的数据和原始数据保持一致，包括顺序且不能重复**，那么我们就要对这些MapTask的数据加以规划（你做哪些，我做哪些，他做哪些，相互之间不重复），官方称之为结果分区。既然要分区就必须要有一个分区依据，要用`--split-by`指定一个列，作为分区的指标。
  **当然，这些的前提都是MapTask的数量大于1**

  

  我们案例中使用的SQL语句，只设置了一个MapTask，故不存在结果分区的问题。但是`$CONDITIONS`还是得带着！不然就报错给你看！
  ![image-20201013213211817](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20201013213211817.png)

  虽然没有用，在日志文件中看到替换后的SQL语句居然是这样的：

  ![image-20201013213415686](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20201013213415686.png)

  没用的`$CONDITIONS`替换成了`1=0`？还能有结果？有点迷惑，，，

   

  当我们使用`--num-mappers`或者`-m`设置MapTask数量>1的时候，就必须加上`--split-by`来指定分区的字段了！否则无法执行。。

  ![image-20201013213934707](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20201013213934707.png)

   

  正确姿势：

  ```shell
  sqoop import \
  --connect jdbc:mysql://hadoop102:3306/company \
  --username root \
  --password 123456 \
  --target-dir /user/company \
  --delete-target-dir \
  --num-mappers 2 \
  --fields-terminated-by "\t" \
  --query "select name,sex from staff where sex='Female' and \$CONDITIONS" \
  --split-by name
  ```

  **split的字段肯定是从查询结果中的字段挑选！！**

  最后的输出结果，令人满意，顺序一致、没有重复:

  ![image-20201013215156156](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20201013215156156.png)

   

- 为什么有时候`$CONDITIONS`前面有个`\`？

  > 注意看下面第一个Note：
  >
  > 当你的SQL语句是用双引号`“`包裹的时候（SQL中使用了单引号，就必须使用双引号包裹），为了防止`$CONDITIONS`被识别为shell变量，使用`\`做一下转译！

  案例中的条件子句用了`sex = 'Female'`，SQL就要使用双引号包裹，所以要对`$CONDITIONS`转译。 

   

- 哪些SQL不推荐写？

  > 官方文档原话：
  >
  > *在Sqoop的当前版本中，使用自由形式查询的便利仅限于在where子句中没有模糊投影和`OR`条件的简单查询。使用复杂查询(如具有子查询或连接的查询)导致不明确的投影，可能会导致意外的结果。*