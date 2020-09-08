[toc]

# 第一部分、MySQL基础、

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/timg.jpg" alt="img" style="zoom:20%;" />

---

## 1、数据库分类

### 1. 关系型数据库（SQL）

- MySQL，Oracle，SQL Server，DB2，SQLlite
- 通过表和表之间的，行与列之间的关系进行数据的存储。（例如：学生信息表）

### 2. 非关系型数据库（NoSQL）Not Only SQL

- Redis，MongoDB
- 对象存储，通过对象的自身的属性来决定

---

## 2、MySQL安装与配置

1. 下载压缩文件，并解压缩到目标文件夹下

2. 配置环境变量，例如安装路径为：`F:\Environment\mysql-8.0.17-winx64` ,系统环境变量下path中新建填写 `F:\Environment\mysql-8.0.17-winx64\bin`.![环境变量](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/环境变量.png)

3. 安装目录下新建并配置my.ini文件

    ```ini
    [mysql]
    default-character-set=utf8
    [mysqld]
    #注意将这两个路径替换成实际的安装目录
    basedir=F:\Environment\mysql-8.0.17-winx64
    datadir=F:\Environment\mysql-8.0.17-winx64\data
    port=3306
    max_connections=200
    character-set-server=utf8
    default-storage-engine=INNODB
    sql_mode = STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION
    ```

4. 以**管理员**身份启动命令提示符,运行`mysqld -install`命令安装MySQL，显示成功后，再运行`mysqld --initialize`,初始化数据库，此时安装目录下生成了data文件夹

5. 以`net start mysql`命令启动MySQL，启动成功后，执行`mysql -u root -p`登录MySQL，此时需要密码，打开**data**文件夹下的一个`.err`文件，找到随机密码输入登录.

6.修改密码，执行以下命令:`ALTER USER 'root'@'localhost' IDENTIFIED BY  'newpassword';`就可以重设密码，然后重启MySQL即可正常使用。

---

## 3、MySQL简单的命令行操作

```sql
net start mysql --启动数据库服务
mysql -u root -p --连接数据库

--所有的语句都以 分号 结尾
show database; --查看所有数据库

use + 'database name' --切换数据库

show tables; --查看数据库中所有的表
describe student; --显示数据库中所有的表的信息

...
```

---

## 4、数据库语言

- DDL(数据库定义语言)
- DML(数据库操作管理语言)
- DQL(数据库查询语言)
- DCL(数据库控制语言)

### 4.1、数据库的数据类型

> 数值

| 数据类型      | 数据大小    | 数据说明                             |
| ------------- | ----------- | ------------------------------------ |
| tinyint       | 1个字节     | 十分小的数据                         |
| smallint      | 2个字节     | 较小的数据                           |
| mediumint     | 3个字节     | 中等大小的数据                       |
| **int(常用)** | **4个字节** | **标准整数**                         |
| bigint        | 8个字节     | 较大的数据                           |
| float         | 4个字节     | 单精度浮点数                         |
| double        | 8个字节     | 双精度浮点数                         |
| decimal       |             | 字符串形式的浮点数(**金融计算使用**) |

> 字符串

| 数据类型          | 数据大小     | 数据说明                         |
| ----------------- | ------------ | -------------------------------- |
| char              | 0~255        | 字符串固定大小(容易造成空间浪费) |
| **varchar(常用)** | **0~65535**  | **可变字符串**                   |
| tinytext          | 2^8 - 1      | 微型文本                         |
| **text(常用)**    | **2^16 - 1** | **文本串**                       |

> 时间日期

|   类型名称    |            格式            |        描述        |
| :-----------: | :------------------------: | :----------------: |
|     date      |         YYYY-MM-DD         |      日期格式      |
|     time      |          HH:mm:ss          |      时间格式      |
| **datetime**  |  **YYYY-MM-DD HH:mm:ss**   | **常用的时间格式** |
| **timestamp** | **1970.1.1到现在的毫秒数** |     **时间戳**     |

### 4.2、拓展

> 每一个表，都必须存在以下五个字段！未来的项目要求，表示一个记录的存在意义

```sql
id 主键
`version` 乐观锁
is_delete 伪删除
gmt_create 创建时间
gmt_update 修改时间
```

### 4.3、navicat中用sql语句创建表

> 要求创建一个学生表包含以下字段
> `学号（int）`，`姓名`，`密码`，`性别`，`生日`，`家庭住址`，`邮箱`。

```sql
--注意点:
--1、表名和字段名都尽量用 ``(反单引号) 括起来
--2、所有语句结尾要加逗号

create table
if
not exists `student` (
    `id` int ( 4 ) not null auto_increment comment '学号',
    `name` varchar ( 30 ) not null default '匿名' COMMENT '姓名',
    `pwd` varchar ( 20 ) not null default '123456' comment '密码',
    `sex` varchar ( 2 ) not null default '女' comment '性别',
    `birthday` datetime default null comment '出生日期',
    `address` varchar ( 100 ) default null COMMENT '家庭住址',
    `email` varchar ( 50 ) DEFAULT null COMMENT '邮箱',
PRIMARY KEY ( `id` )
) ENGINE = INNODB DEFAULT CHARSET = utf8
```

> 格式

```sql
CREAT TABLE [IF NOT EXISTS] `表名`(
    `字段名` 列类型 [属性] [索引] [注释],
    `字段名` 列类型 [属性] [索引] [注释],
    ...
    `字段名` 列类型 [属性] [索引] [注释]
)[表类型][字符集设置][注释]
```

### 4.4、删除和修改数据表

> 修改

```sql
-- 修改表名 ALTER TABLE 旧表名 RENAME AS 新表名
ALTER TABLE `student` RENAME AS `stu`
-- 增加字段 ALTER TABLE 表名 ADD `字段名` 列属性
ALTER TABLE `stu` ADD `age` INT(3) AFTER `sex`

-- 修改表的字段
-- ALTER TABLE 表名 MODIFY 字段名 列属性
ALTER TABLE stu MODIFY age VARCHAR(3) -- 修改约束
-- ALTER TABLE 表名 CHANGE 表名 新表名 列属性
ALTER TABLE stu CHANGE age sage int(3) -- 字段重命名

-- 删除表的字段
-- ALTER TABLE 表名 DROP 字段名
ALTER TABLE stu DROP sage

-- 删除表(先判断是否存在再删除)
DROP TABLE IF EXISTS `tch`
```

- **建议所有的创建删除操作尽量加上判断，一面报错**

> 常用命令

```sql
show CREATE DATABASE school --查看创建数据库的语句
SHOW CREATE TABLE student --查看创建student数据表的定义语句
DESC student --查看student表的结构
```

---

## 5、数据表类型

### 5.1关于数据库引擎

- INNODB 默认使用
- MYISAM 早期使用

|            | MYISAM           | INNODB                   |
| ---------- | ---------------- | ------------------------ |
| 事务支持   | 不支持           | 支持                     |
| 数据行锁定 | 不支持、表锁     | 支持                     |
| 外键约束   | 不支持           | 支持                     |
| 全文索引   | 支持             | 不支持                   |
| 表空间大小 | 较小             | 较大(约为前者2倍)        |
| 优点       | 节约空间，速度快 | 安全性高，多表多用户操作 |

### 5.2引擎在物理文件上的区别

- INNODB 在数据库表中只有一个`.frm`文件(MySQL8.0后没有)，以及目录下对应的`ibdata1(.ibd)`文件。

- MYISAM 对应的文件
  - `.frm`文件 --> 表结构定义文件
  - `.MYD`文件 --> 数据文件(data)
  - `.MYI`文件 --> 索引文件(index)

> 设置数据库表的字符编码

```sql
charset=utf8
```

- **不设置的话，MySQL的默认的字符集编码会导致中文乱码**

---

## 6、MySQL数据管理

### 6.1外键

#### 6.1.1、外键的添加

- 1、创建时添加

```mysql
-- 将学生表中的grade字段设置为外键,引用gradelist中的gradeid字段
-- 方式一 自定约束名
KEY `约束名` (`设为外键的字段号`),
constraint `约束名` foreign key (`设为外键的字段号`) references `被引用的表`(`被引用的字段`)

-- 示例
KEY `FK_gradeid` (`grade`),
constraint `FK_gradeid` foreign key (`grade`) references `gradelist`(`gradeid`)

-- 方式二
foreign key (`设为外键的字段号`) references `被引用的表`(`被引用的字段`)
```

- 2、创建后修改表添加外键

```mysql
alter table `表名`
add constraint `约束名` foreign key (`设为外键的字段号`) references `被引用的表`(`被引用的字段`);

-- 示例
ALTER TABLE `stu`
ADD constraint `FK_gradeid` FOREIGN KEY (`grade`) REFERENCES `gradelist` (`gradeID`);

```

> **以上的操作都是物理外键，数据库级别的外键，不建议使用!**
>
> > **阿里Java规范:[强制]不得使用外键与级联，一切外键概念必须在应用层（代码层）解决**</br>
> > 每次做delete和update都必须考虑外键的约束，会导致开发的时候很痛苦，测试数据极为不方便。

#### 6.1.2、最佳实践

- 数据库中之存放数据，只有行（数据）和列（字段）。
- 当需要使用多张表的时候，可以用程序去实现外键。

### 6.2、DML语言(牢记)

- DML语言：数据操作语言

  - Insert

    ```sql
    -- 插入数据
    -- 语法：INSERT INTO `表名`（[`字段1`,`字段2`,...]）VALUES('值1','值2',...)
    -- 值与字段一一对应
    INSERT INTO `gradelist`(`gradeName`)VALUES ('大一');

    -- 一次插入多条数据
    INSERT INTO `gradelist`(`gradeName`)VALUES ('大二'),('大三'),('大四');
    ```

  - update

    ```sql
    -- 修改数据
    -- 语法:UPDATE 表名 set `字段名` = '新值' [where (条件)]
    UPDATE `stu` SET `name` = '路人甲' WHERE `name` = '张三';

    -- 若不指定条件 默认修改所有
    UPDATE `stu` SET `name` = '路人'

    -- 修改多个数据
    UPDATE `stu`
    SET `grade` = 1,birthday = '2001-7-9' -- 修改的数据用逗号隔开
    WHERE `name` = '路人甲';
    ```

  - delete

    ```sql
    -- 语法 delete from 表名 [where 条件]
    -- 全部删除（危险操作）
    DELETE FROM `stu`

    -- 删除指定数据
    DELETE FROM `stu` where id = 3;
    ```

    - TRANCATE命令
    > 作用：完全清空一个数据库表，表的结构和索引约束不会改变

    - delete 与 trancate命令

    > 相同点：</br>
    > 都能够删除数据，都不会删除表结构</br>
    > 不同点:</br>
    > TRANCETE 重新设置自增列，计数器归零</br>TRANCATE不会影响事务
    >
    > delete：可以撤销，因为删除的数据都写入了日志。

---

## 7、DQL查询数据(重点！)

- （Data Query LANGUAGE）数据查询语言

### 7.1、简单的select查询

> ....

### 7.2、联表查询 Join ON

![联表查询](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/连接查询.png)

| 连接方式   | 描述                                     |
| ---------- | ---------------------------------------- |
| Inner Join | 如果表中有一个匹配项，就返回行           |
| Left Join  | 从左表中返回所有值，即使在右表中没有匹配 |
| Right Join | 从右表中返回所有值，即使在左表中没有匹配 |

### 7.3、分页（limit）和排序 (order by)

> limit(起始下标，页面大小)

  ```sql
  -- 表示显示从第几条起的多少条数据
  select..
  from..
  where..
  ...
  limit 0,5 -- 显示查询的从第一条开始的五条数据
  ```

### 7.4、聚合函数及分组过滤

#### 7.4.1、常用的聚合函数

| 函数名       | 描述         |
| ------------ | ------------ |
| Count()      | 计数         |
| SUM()        | 求和         |
| AVG()        | 求平均值     |
| MAX()、MIN() | 最大、最小值 |
| ...          | ...          |

> **count(列名)，count(1)，count(*) 的区别**

| 使用方法    | 区别                                                                      |
| ----------- | ------------------------------------------------------------------------- |
| count(列名) | 只包括列名那一列，在统计结果的时候，**会忽略列值为空**                    |
| count(1)    | 包括了忽略所有列，用1代表代码行，在统计结果的时候，**不会忽略列值为NULL** |
| count(*)    | 包括了所有的列，相当于行数，在统计结果的时候，**不会忽略列值为NULL**      |

> 后两者本质上区别不大，都不会忽略值为NULL的数据行，在特定的使用场景下，执行效率有所不同

#### 7.4.2、group by （分组）及 having（过滤分组）

> group by 是让排序结果按规则进行分组排列</br>
> having 与 where用途相似 用来过滤分组中不符合要求的分组

```sql
-- 代码示例：
    -- 要求筛选平均分>80的学科
    select SubjectName as '科目',
           AVG(StudentResult) as '平均分',
           MAX(StudentResult) as '最高分',
           MIN(StudentResult) as '最低分'
    FROM result
    LEFT JOIN `subject`
    ON result.SubjectNo = `subject`.SubjectNo
    GROUP BY result.SubjectNo
    HAVING 平均分>80
```

### 7.5、（拓展）数据库级别的MD5加密

- 主要增强算法复杂度和不可逆性

  - MD5不可逆：具体的值MD5加密后的密文是一样的

```sql
-- ============测试MD5============
  create table `testmd5` (
    `id` int ( 4 ) not null,
    `name` varchar ( 20 ) not null,
    `pwd` varchar ( 50 ) not null,
    primary key ( `id` )
  ) engine = innodb default charset = utf8

  insert into testmd5 values (1,'张三','123456'),(2,'李四','123456'),(3,'王五','123456')

  -- 加密
  UPDATE testmd5
  SET pwd=MD5(pwd)
  WHERE id = 1

  -- 插入时加密
  insert into testmd5 values (4,'小明',MD5('123456'))

  -- 查询校验
  SELECT *
  FROM testmd5
  where `name` = '小明' and pwd = MD5('123456')
```

### 7.6、select总结

```sql
-- 语法顺序
select [distinct去重] `要查询的字段` [as '别名'] -- 联表查询时避免模棱两可
from `表名`
(Inner/left/right) join `连接的表` on 等值条件
where (具体的值，或者子查询语句)
group by (分组参照)
having (过滤分组的条件) -- 用法与where一样
order by `排序的参考字段` [ASC/DECS] -- 默认递增
limit index,count -- 列出从第index条开始的count条数据
```

---

## 8、事务（Transaction）

### 8.1、什么是事务

> 作为单个逻辑工作单元执行的一系列操作，**要么都成功，要么都失败**
---
> 事务的特性（4个) 又叫**ACID特性**

- **原子性（Atomicity）**
- **一致性（Consistency）**
- **隔离性（Isolation）**
- **持久性（Durability）**

参考博客：<https://blog.csdn.net/dengjili/article/details/82468576>

**原子性（Atomicity）：**
要么都成功，要么都失败

**一致性（Consistency）：**
事务前后的数据完整性要保证一致

**持久性（Durability）：**
事务一旦提交不可逆，被持久化到数据库中

**隔离性（Isolation）：**
多个用户并发访问数据库时，数据库为每个用户开启事务，不被其他事务的操作所干扰，并发事务之间相互隔离

> ***隔离所导致的一些问题：***
脏读，不可重复读，幻读。

### 8.2、具体实现

```sql
-- mysql 默认开启事务自定提交
SET autocommit = 0 -- 关闭
SET autocommit = 1 -- 开启（默认）

-- 简单流程

-- 1、手动处理事务
SET autocommit = 0 -- 关闭自动提交

-- 2、事务开启
START TRANSACTION -- 标记一个事务的开启

/* 之后的sql操作都在这同一个事务内进行 */



-- 3、提交：持久化（成功!）
COMMIT
-- 3、回滚：回到起始状态（失败!）
ROLLBACK

-- 4、事务结束
SET autocommit = 1 -- 开启自动提交


-- 拓展了解
SAVEPOINT `保存点名` --设置一个事务的保存点
ROLLBACK TO SAVEPOINT `保存点名` --若事务失败 回滚到上一个保存点
RELEASE SAVEPOINT `保存点名`  -- 撤销保存点

```

#### 8.2.1、模拟场景

```sql
-- =======模拟转账场景=========
CREATE DATABASE shop CHARACTER SET utf8 COLLATE utf8_general_ci

USE shop

  CREATE TABLE `account`(
    `id` INT(3) NOT NULL auto_increment,
    `name` VARCHAR(10) not NULL,
    `money` DECIMAL(9,2) NOT NULL,
    PRIMARY KEY(`id`)
)ENGINE = INNODB CHARSET = utf8

INSERT INTO `account`(`name`,`money`) VALUES ('A','2000'),('B','10000')

SET autocommit = 0; -- 关闭自动提交
START TRANSACTION -- 开启事务

UPDATE account set money = money - 500 WHERE `name` = 'A' -- A转出500
UPDATE account SET money = money + 500 WHERE `name` = 'B'  -- B收款500

COMMIT; -- 提交事务
ROLLBACK; -- 回滚

set autocommit = 1;
```

---

## 9、索引

> 索引（index）是帮助MySQL高效获取数据的数据结构。

### 9.1、索引的分类

> **在一个表中，主键索引只能有一个，而唯一索引可以有多个**

- 主键索引（primary key）

  > 唯一的标识，主键不可重复，只能有一个列作为主键

- 唯一索引（unique key）

  > 避免重复的列出现，唯一索引可以重复，多个列都可以标识为唯一索引

- 常规索引（index/key）

  > 默认，用index/key关键字设置

- 全文索引（FullText）
  
  > 快速定位数据位置

### 9.2、基础语法

```sql
-- =======索引的使用=====
-- 显示所有索引信息
show index from student

-- 增加一个索引 add fulltext index `索引名`(`列名`)
alter table student add fulltext index `studentname`(`studentname`)

-- 删除索引：
DROP INDEX 索引名 ON 表名字;
-- 删除主键索引:
ALTER TABLE 表名 DROP PRIMARY KEY;

-- explain 分析sql执行状况
explain select * from student

explain select * from student where match(studentname) against ('刘')

```

### 9.3、测试索引

```sql
  SELECT *
  FROM app_user
  where  `name` = '用户99999'  -- 创建索引前 > 时间: 1.924s
                              -- 创建索引后 > 时间: 0.083s

  -- 索引名 命名规范 id_表名_字段名
  -- CREATE INDEX 索引名 ON 表(字段)
  CREATE INDEX id_app_user_name ON app_user(`name`);
```

![explain创建索引前](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/创建索引前.png)
![explain创建索引后](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/创建索引后.png)

> 从测试结果可以发现索引显著提高了大量数据的查询性能

### 9.4、索引原则

- 索引并不是越多越好
- 不要对经常变动的数据加索引
- 小数据的表不需要添加索引
- 索引一般加在常用来查询的字段上

> 索引的数据结构

Hash类型的索引
B+tree:InnoDB的默认数据结构

> **推荐阅读** [MySQL索引背后的数据结构及算法原理](https://blog.codinglabs.org/articles/theory-of-mysql-index.html)

---

## 10、权限管理与数据库备份

### 10.1、用户管理

> 可视化窗口管理

![用户管理](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/用户管理.png)

> SQL命令

系统数据库中有一用户表即:mysql.user,
本质上还是对表数据的增删改查。

```sql
-- 创建用户  CREATE USER 用户名 IDENTIFIED BY '密码'
CREATE USER sakura0817 IDENTIFIED BY '170312'

-- 修改密码（修改当前用户密码）
SET password = '170312';

-- 修改密码（修改指定用户密码）
set password FOR sakura0817 = '170312'

-- 用户重命名
RENAME USER sakura0817 TO 5akura

-- 用户授权 all privileges全部的权限 （Grant Option）无法授予
grant all privileges on *.* to 5akura

-- 查看权限
show grants for 5akura -- 查看指定用户
show grants for root@localhost -- 查看管理员

-- 撤销权限
REVOKE ALL PRIVILEGES ON *.* FROM 5akura

-- 删除用户
DROP USER 5akura
```

### 10.2、MySQL备份

> 备份方式

- 直接拷贝data文件夹
- 在可视化工具中手动导出
- 使用命令行

```cmd
#模板：
mysqldump -h"host" -u"username" -p"password" "数据库名" "表名" >"导出路径"
#示例：
mysqldump -hlocalhost -uroot -p170312 shop >F:/文档文件/shop.sql

# 导入
# 登陆后
source "备份文件"
```

---

## 11、数据库的规约，三大范式

### 11.1、为什么要需要设计

***当数据较复杂时，我们需要设计数据库辅助管理数据***

- **糟糕的数据库设计**

> 数据冗余，浪费空间
>
> 数据库插入删除操作麻烦、产生异常[屏蔽使用物理外键]
>
> 程序的性能差

- **良好的数据库设计**

> 节省内存空间
>  
> 保证数据库的完整性
>
> 方便我们开发系统

- ***关于数据库的设计***

  - 分析需求：分析业务和需要处理的数据库要求
  - 概要设计：设计关系的ER图

- 数据库设计具体步骤（个人博客为例）

  - 收集信息，分析需求

    - 用户表（用户登录注销，用户个人信息，写博客，创建分类）
    - 分类表（文章分类，创建者）
    - 文章类（文章的信息）
    - 评论表
    - 友联表（友链信息）
    - 自定义表（系统信息，某个关键字，或者一些主字段）

  - 标识实体（将每个需求落地到每个关键字）
  - 标识实体之间的关系

### 11.2、三大范式（数据库设计规范）

- **为什么需要数据规范化**

  - 信息重复
  - 更新异常
  - 插入异常
  - 删除异常

#### 11.2.1、三大范式具体内容

> 第一范式（1NF）

- 要求数据库表中属性都是不可再分的**原子数据项**。

> 第二范式（2NF）

- 在1NF的基础上，所有的非码属性必须**完全依赖**于候选码（**消除部份依赖**）。

> 第三范式（3NF）

- 在2NF的基础上，任何非主属性不依赖与其他任何非主属性（**消除传递依赖**）。

#### 11.2.2、规范性与性能问题

***关联查询的表不得超过三张表***

- 考虑商业需求和目标（成本，用户体验），数据库的性能更加重要.
- 在规范性能的问题时，要适当的考虑以下规范性。
- 故意给某些表增加一些冗余字段。（多表查询-->单表查询）
- 故意增加计算列（从大数据量降低为小数据量的查询:索引）

---

## 12、JDBC(重点！！)

### 12.1、简介

#### 12.1.2、数据库驱动

> `程序` 连接访问 `数据库` 需要通过`数据库驱动`。

#### 12.1.2、JDBC是什么

![JDBC是什么](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/JDBC是什么.png)

> 由于不同数据库的驱动都不相同，操作也各不相同
---
> SUN公司为了简化开发人员的（对数据库的统一）操作，提供了一个（Java操作数据库）规范，俗称JDBC，规范的实现由各个厂商去做，开发人员只需要掌握JDBC接口的使用即可。

### 12.2、第一个JDBC程序

#### 12.2.1、创建测试数据库

```sql
  create database jdbcStudy CHARACTER set utf8 COLLATE utf8_general_ci;

  use jdbcStudy;

  CREATE TABLE users(
    id INT PRIMARY KEY,
    NAME VARCHAR(40),
    PASSWORD VARCHAR(40),
    email VARCHAR(60),
    birthday DATE
  );

  INSERT INTO users values (1,'张三','123456','zs@sina.com','1999-12-03'),
  (2,'李四','123456','ls@sina.com','1997-11-13'),
  (3,'王五','123456','ww@sina.com','2000-2-08');
```

#### 12.2.2、编写Java程序

1. 创建一个空项目
2. 导入数据库驱动jar包
3. 编写Java程序

```java
package com.sakura.jdbcstudy;

import java.sql.*;

/**
 * 第一个JDBC程序
 *
 * @author 桜
 * @Date 2020/2/11
 */
public class JDBC_demo01 {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        //1、加载驱动
        /**
         * com.mysql.jdbc
         * 使用新版驱动 com.mysql.cj.jdbc
         */
        Class.forName("com.mysql.cj.jdbc.Driver"); //固定写法

        //2、用户信息和url
        /**
         * ？:访问链接
         * 三个参数:
         * useUnicode=true 支持中文编码
         * characterEncoding=utf8 字符集utf-8
         * useSSL=true 使用安全链接
         */
        String url = "jdbc:mysql://localhost:3306/jdbcstudy?useUnicode=true&characterEncoding=utf8&useSSL=true";
        String userName = "root";
        String passWord = "170312";

        //3、连接成功,获取数据库对象（getConnection(url,username,password)方法）  返回对象为数据库
        Connection connection = DriverManager.getConnection(url, userName, passWord);

        //4、执行SQL对象(createStatement()方法)
        Statement statement = connection.createStatement();

        //5、SQL的对象去执行SQL(若有执行结果，需要查看返回结果)
        String sql = "select * from users";

        ResultSet resultSet = statement.executeQuery(sql);//执行sql查询语句,并返回封装好的结果集;

        //解读结果集 getObject(列名)
        while (resultSet.next()) {
            System.out.println("id = "+resultSet.getObject("id"));
            System.out.println("name = "+resultSet.getObject("NAME"));
            System.out.println("password = "+resultSet.getObject("PASSWORD"));
            System.out.println("email = "+resultSet.getObject("email"));
            System.out.println("birth = "+resultSet.getObject("birthday"));
            System.out.println("-------------------");
        }

        //6、关闭连接
        resultSet.close();
        statement.close();
        connection.close();	
    }
}
```

---

#### 12.2.3、代码步骤总结

1. 加载驱动（**使用新版驱动com.mysql.cj.jdbc.Driver**）
2. 与数据库建立连接，获取Connection对象（**DriverManager.getConnection()**）
3. 获取执行sql语句的Statement对象（**connection.creatStatement()**）
4. 执行sql语句获得结果集
5. 释放连接

#### 12.2.4、代码详细

> ##### 创建驱动

```java
    //DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
    Class.forName("com.mysql.cj.jdbc.Driver"); //固定写法
```

```java
package com.mysql.cj.jdbc;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Driver extends NonRegisteringDriver implements java.sql.Driver {
    public Driver() throws SQLException {
    }

    static {
        try {
            //注册驱动
            DriverManager.registerDriver(new Driver());
        } catch (SQLException var1) {
            throw new RuntimeException("Can't register driver!");
        }
    }
}
```

> 可以看到在com.mysql.cj.jdbc.Driver()类中有一个静态代码块会在加载时自动执行注册驱动，所以只需用forname()方法即可,**因为对类进行反射调用,一定会引发类初始化从而执行驱动的注册**。

---

> ##### URL

```java
  String url = "jdbc:mysql://localhost:3306/jdbcstudy?useUnicode=true&characterEncoding=utf8&useSSL=true";

  // 协议/主机地址：端口号/数据库名？参数1&参数2&参数3

  //mysql-->3306,Oracle-->1521
  //jdbc:oracle:thin@localhost:1521:sid
```

---

> ##### Connection （代表数据库）

```java
//能执行大多数数据库中的操作
connection.rollback();//事务回滚
connection.commit();//事务提交
connection.setAutoCommit();//设置自动提交
```

---

> ##### 执行SQL的对象

- statement（不安全）

```java
//可以执行多种SQL语句
statement.executeQuery();//查询  返回查询结果集
statement.executeUpdate();// 更新，插入，删除 返回受影响行数
statement.execute();//执行所有SQL语句
statement.executeBatch();//批次处理
```

- connection.prepareStatement()（防止SQL注入）

---

> ##### ResultSet（只有查询有结果集返回）

- 获取数据

```java
resultSet.getObject()//在不清楚列数据类型时使用
//如果知道列类型就使用指定方法get数据
getString(),getFloat(),getDate(),....
```

- 遍历（指针）

```java
resultSet.next();//下一条
resultSet.previous();//上一条
resultSet.beforeFirst();//第一条
resultSet.afterLast();//最后一条
resultSet.absolute();//指定行
```

---

> ##### 资源释放

```java
resultSet.close();
statement.close();
connection.close();//耗费资源,用完即关
```

### 12.3、SQL注入的问题

> sql存在漏洞，会被攻击导致数据泄露

- 数据输入过滤不严格，通过SQL拼接字符串 `or`，进入数据库盗取数据。

```java
public class SQL注入 {

    public static void main(String[] args) {
        login(" 'or '1=1","123456");
    }

    public static void login(String userName, String password) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = JdbcUtils.getConnection();
            statement = connection.createStatement();

            //SQL语言
            String sql = "select * from users where `NAME`= '"+userName+"' and `passWord` = '" +password+ "'";


            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                System.out.println("NAME = "+resultSet.getString("NAME")+"--> email = " + resultSet.getString("email"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.release(connection, statement, resultSet);
        }
    }
}
```

### 12.4、PreparedStatement对象

- 可以防止SQL注入，效率更高。

```java
public class TestInsert {
    public static void main(String[] args) {
        Connection connection =null;
        PreparedStatement preparedStatement =null;
        ResultSet resultSet = null;
        try {
            connection = JdbcUtils.getConnection();

            //区别
            //1、使用 '?'占位符代替数据作为参数
            String sql = "insert into users(id, `NAME`, `passWord`, email, birthday) values(?,?,?,?,?)";

            //2、预编译SQL,先写SQL,但是不执行
            preparedStatement = connection.prepareStatement(sql);

            //3、手动参数赋值
            // preparedStatement.setObject(); 不清楚列类型时使用
            /**
             * 参数为（列标，具体数据）
             * 1代表第一个字段,2代表第二个字段....
             */
            preparedStatement.setInt(1,5);//设置id
            preparedStatement.setString(2,"官宇辰");//设置name
            preparedStatement.setString(3,"170312");//设置passWord
            preparedStatement.setString(4,"G843452233@outlook.com");//设置email
            // 注意点 sql.Date 与 util.Date 不同
            preparedStatement.setDate(5,new Date(new SimpleDateFormat("yyyy-MM-dd").parse("2000-08-17").getTime()));//设置birthday

            //4、执行 直接执行无需传参
            int i = preparedStatement.executeUpdate();
            if (i>0) {
                System.out.println("操作成功");
            }

        } catch (SQLException | ParseException e) {
              e.printStackTrace();
        } finally {
             //...释放资源
            JdbcUtils.release(connection, preparedStatement, resultSet);

        }
      }
}
```

- **PreparedStatement 如何避免SQL注入**

> **本质：把传入的参数当作字符，把用户非法输入的单引号用\反斜杠做了转义，就避免了参数也作为条件的一部分。**

### 12.5、IDEA连接Mysql 处理事务

1. 开启事务`connection.setAutoCommit(false);`
2. 一组业务执行完毕，提交事务。
3. 可以在catch语句中显式的定义回滚语句,但默认失败了就会回滚。

> 代码实现

```java
public class TestTransacation02 {
    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = JdbcUtils.getConnection();
            //关闭数据自动提交，会自动开启事务
            connection.setAutoCommit(false);

            String sql1 = "update account set money = money - 100 where name = 'A'"; //A转出100
            preparedStatement = connection.prepareStatement(sql1);
            preparedStatement.executeUpdate();



            String sql2 = "update account set money = money + 100 where name = 'B'"; //B入账100
            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.executeUpdate();

            //业务完毕，提交事务
            connection.commit();
            System.out.println("转账成功");


        } catch (SQLException e) {
            try {
                connection.rollback(); //失败回滚
                System.out.println("转账失败");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            JdbcUtils.release(connection, preparedStatement, resultSet);
        }
    }
}
```

### 12.6、数据库连接池

> 产生原因：因为数据库的连接与释放 十分浪费系统资源
---
> **池化技术：预先准备一些资源，过来直接连接**

- 常用连接数
- 最小连接数
- 最大连接数（业务最高承载上限）
  - 等待超时

> 编写连接池，实现一个接口 **DataSource**

![dbcp](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/dbcp实现接口.png)
![c3p0](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/c3p0继承接口.png)

---
> 优秀的开源数据源实现（使用后，项目开发中不需要编写连接数据库的代码了）

- DBCP(commons-dbcp-1.4.jar，commons-pool-1.6.jar)
- C3P0
- Druid(阿里巴巴)

> **结论**

无论使用什么数据源，本质都是一样，都要实现DataSource接口，方法不变，只是不同的实现方式。





# 第二部分、MySQL高级

## 13、Linux安装MySQL

1. 检查是否安装了MySQL

   ```shell
   rpm -qa|grep mysql
   ```

2. 若有 使用`rpm -e mysqlxxx`或者`yum remove mysql`一一卸载

3. rpm按顺序安装四个文件

   ![image-20200505201648800](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200505201648800.png)

   安装client时可能遇到这个报错

   ![image-20200708195937641](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200708195937641.png)

4. 查看是否安装成功

   ![image-20200505202119143](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200505202119143.png)

5. 启动Mysql服务

   ```shell
   systemctl start mysql # 启动服务
   systemctl stop mysql # 停止服务
   ```

6. 然后获取临时密码(存放位置：/var/logs/mysqld.log中)

   ```shell
   cat /var/log/mysqld.log|grep password
   ```

   ![image-20200505210012333](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200505210012333.png)

7. 使用临时密码登录后，进入首先需要修改密码

   ```mysql
   SET PASSWORD = PASSWORD('newpassword'); 
   # 或者使用
   ALTER USER USER() IDENTIFIED BY 'newpassword';
   ```

8. 默认的密码策略要求严格（在默认密码的长度最小值为 4 ，由 大/小写字母各一个 + 阿拉伯数字一个 + 特殊字符一个），可以进行修改降低

   ```mysql
   set global validate_password_policy=LOW; # 将密码验证强度将为低
   set global validate_password_length=6; # 将密码长度设置为6
   ```

9. 初次进行远程连接会被拒绝报错1130，是因为只允许localhost连接

   ```shell
   mysql>use mysql;
   
   mysql>select host from user where user='root';
   
   mysql>update user set host = '%' where user ='root';
   
   mysql>flush privileges;
   
   mysql>select 'host'   from user where user='root'
   ```

10. 开启服务开机自启

    ```shell
    systemctl enable mysqld # 设置自启
    
    ntsysv # 查看服务自启状态
    ```

    <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200505212212381.png" alt="image-20200505212212381" style="zoom:50%;" />

## 14、配置文件

配置文件在Windows下是my.ini，==在Linux中是 my.cnf位置：/etc/my.cnf==

以下是默认配置

![image-20200505212525080](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200505212525080.png)

| 路径             | 解释                             |
| ---------------- | -------------------------------- |
| /var/lib/mysql   | mysql数据文件的存放路径，datadir |
| /usr/share/mysql | 配置文件目录                     |
| /usr/bin/mysql*  | mysql命令存放目录                |



1. 修改字符集编码

   ```mysql
   mysql> show variables like '%char%'; # 查看字符集编码
   ```

   ![image-20200505215622870](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200505215622870.png)

   修改配置文件：

   ```mysql
   # 在mysqld下添加
   character-set-server = utf8mb4
   collation-server = utf8mb4_general_ci
   # 在mysql下添加
   default-character-set = utf8mb4
   ```

   重新启动Mysql服务，然后重新建库，建表就可以使用中文了。



2. 其他主要配置项、配置文件

   - 二进制日志：log-bin(主要用于==主从复制==)

   - 错误日志：log-error

     > 默认是关闭的，记录严重警告和错误记录，以及每次启动和关闭的详细信息。

   - 查询日志

     > 默认关闭，记录查询的SQL语句，开启会降低Mysql的整体性能，有利于我们排查慢SQL;

   - 数据文件

     > Windows系统：安装目录下的data文件夹中存放数据库的数据文件
     >
     > Linux：**/var/lib/mysql** 目录下，使用`ls -lF|grep ^d`目录即可查看所有的库

     MyIASM引擎：

     - .frm:表结构

     - .myd:表数据

     - .myi:表索引

       ![image-20200509222823432](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200509222823432.png)

     InnoDB引擎：由于InnoDB的索引和其数据文件是一体的，所以只有两个文件

     - .frm(在Mysql8.0+后移除)

     - .ibd(数据/索引文件)

       ![image-20200509222744059](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200509222744059.png)



## 15、MySQL Server分层

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200509224414960.png" alt="image-20200509224414960" style="zoom:70%;" />



- 连接层
- 服务处
- 引擎层
- 存储层



> Mysql是可拔插的，可高度定制化的。在应对不同场景时发挥稳定。
>
> **插件式的存储引擎架构将查询处理举额其它的系统任务以及数据的存储提取相分离。**
>
> 可以根据业务的需求和实际情况来选择合适的存储引擎。



## 16、MySQL引擎

mysql中`show engines`可以查看所有的引擎，以及当前默认使用的engine

![image-20200509225419795](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200509225419795.png)

`show variables like ‘%storage_engine%’`可以获取当前使用存储引擎的信息

![image-20200509225650262](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200509225650262.png)

### MyISAM和InnoDB对比

| 对比项       |                           MyISAM                           |                            InnoDB                            |
| ------------ | :--------------------------------------------------------: | :----------------------------------------------------------: |
| **外键**     |                           不支持                           |                             支持                             |
| **事务**     |                           不支持                           |                             支持                             |
| **行/表锁**  | 表锁，即使操作一条记录，就会锁住整张表。不适合高并发操作。 |   行锁，操作时只锁定某一行，对其他行不影响，适合高并发操作   |
| **缓存**     |                只缓存索引，不缓存真实数据。                | 不仅缓存索引，还缓存真实数据，对内存要求较高，内存大小对性能有决定性影响。 |
| **表空间**   |                             小                             |                大（由于同时存放了索引和数据）                |
| **侧重点**   |                     性能，适合用于查找                     |                             事务                             |
| **默认安装** |                            YES                             |                             YES                              |





### Alibaba使用的MySQL

- Percona为MySQL数据库服务器进行了改进，在功能和性能上较MySQL有着很显著的提升。该版本提升了在高负载情况下的InnoDB的性能，衍生出来一种新的存储引擎：==XtraDB==.
- XtraDB完全可以代替InnoDB,并且它在高并发和性能方面表现更加出色。
- 阿里巴巴大部分MySQL数据库其实就是使用的Percona的原型(XtraDB)并加以修改。
- AliSQL+AliRedis 已经进行了开源 



## 17、索引优化分析

> 慢SQL形成的原因

- SQL写的烂

- 索引失效（建了索引，但是由于SQL语句不合理索引没用上）

  创建索引：

  ```mysql
  # 单值索引
  create idx_user_name on `user`(name); 
  
  # 联合索引
  create (idx_user_name, idx_user_age) on `user`(name,age);
  ```

  删除索引

  ```mysql
  drop index [index_name] on `table_name`
  ```

  查看索引

  `infomation_schema`是MySQL的元数据库，所有的元数据都存放在这个库中，其中`statistics`表存放着所有索引的相关信息。

  ```mysql
  show index from `table_name`
  ```

- 关联查询(join)太多

- 服务器调优及各种参数的设置(缓冲，线程数等)

  

机器执行SQL的顺序：

```mysql
from `table_name`
on 'join_condition'
join_type join `join_table`
where 'select_condition'
group by 'group_by_list'
having 'having_condition'
select
distinct `select_list`
order by 'order_by_condition'
limit 'offset,rows'
```

![img](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/u=1284298525,1063291515&fm=26&gp=0.jpg)



### 索引选择

1. **复合索引**的选择**优先于单值索引**

2. 每张表的索引数量不超过**5个**

3. 索引并不是越多越好，索引也会占用空间！

   - 推荐创建索引的情况

     1. 主键自动建立索引(InnoDB自动创建)
     2. 频繁作为查询条件的字段建立索引
     3. 查询中与其他表关联的字段（外键关系）建立索引
     4. 查询中排序的字段(Order BY),建立索引会大大调高排序的速度
     5. 查询中统计或者分组的字段(Group BY)

   - 不推荐建立索引的情况

     1. 频繁修改的字段不推荐建立索引

     2. 表记录较少(<2000)

     3. 频繁增删改的字段

     4. 选择性不高的字段

        > 选择性：列数据的基数/列数据的总记录数  (0,1]



### 性能分析



MySQL Query Optimizer(查询优化分析器)

> MySQL服务内置，通过计算分析系统中收集到的统计信息，自动优化SQL语句达到MySQL认为最优的执行计划。(但是不一定是DBA认为的最优)

`Explain`关键字

> 可以模拟优化器执行SQL查询语句，从而知道MySQL是如何处理我们的SQL语句的。方便我们对SQL语句性能进行分析。

![image-20200510222455343](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200510222455343.png)

使用这个关键字能获取那些信息？

- 表的读取顺序
- 数据读取操作的操作类型
- 那些索引可以被使用
- 用到了那些索引
- 表之间的引用
- 每张表多少行被优化器查询



### 性能分析表字段解析

- id：

  > select查询的序列号，包含一组数字，表示查询中执行select子句或操作表的顺序。
  >
  > - id相同：表示执行顺序从上到下依次执行。
  > - id不同：id递增，一般常见在嵌套子查询，之间有父子关系，id越大执行优先级越高，越先被执行。
  > - id相同、不同同时存在：结合前两条的规则，先按照优先级执行，然后相同id再顺序执行。

- select_type

  > 查询的类型
  >
  > 1. `SIMPLE`: 查询语句中不包含子查询和union
  > 2. `PRIMARY`: 当查询中包含若干子查询，最外层的查询则为PRIMARY
  > 3. `SUBQUERY`: 在Select和Where列表中包含了子查询，或子查询本身
  > 4. `DERIVED`: 在from列表中包含的子查询,被标记未DERIVED(衍生)，MySQL会递归执行这些子查询，将结果放在临时表中。
  > 5. `UNION`: 若第二个Select出现在union之后，会被标记为UNION, 若union出现在from列表的子查询中，外层select会被标记会DERIVED.
  > 6. `UNION_RESULT`: 从union表获取结果的select会被标记为UNION_RESUL

- table

  > 执行过程中查询了哪张表。

- type

  > 查询访问表的访问类型
  >
  > 从优到劣：
  >
  > system > const > eq_ref > ref > range > index > ALL
  >
  >  优化至少达到range
  >
  > - system: 表只有一行记录(相当于系统表)，是const的特例。
  > - const: 表示通过==一次==索引就定位到数据,用于比较primary key或者unique索引。因为只匹配一行数据，速度很快。如将主键置于where条件中，MySQL就可以将查询转化为一个常量。
  > - eq_ref: 使用索引，且索引的每个key,有且仅有唯一一条记录与之匹配，常见于primary key和unique索引。
  > - ref: 非唯一性索引扫描，使用索引，但是满足条件的可能是多条记录，属于扫描和查找的结合体。
  > - range: 只检索给定范围的行，使用一个索引来选择行。常见于between、in、<、>查询。
  > - index: 全索引扫描。虽然和ALL一样是读全表，但是index只扫描索引树，速度要比ALL快。
  > - ALL: 遍历全表。(记录较大时，ALL需要进行优化)

- possible_key

  > 列出所有可能用到的索引。

- key

  > 实际使用的索引。NULL：未使用索引
  >
  > 若查询中使用了==覆盖索引==，则该索引仅出现在key列表中。
  >
  > - 通常开发人员会根据查询的where条件来创建合适的索引，但是优秀的索引设计应该考虑到整个查询。其实mysql可以使用索引来直接获取列的数据。**如果索引的叶子节点包含了要查询的数据，那么就==不用回表查询了，也就是说这种索引包含（亦称覆盖）所有需要查询的字段的值==，我们称这种索引为覆盖索引。**即当我们的select列表中的字段包含在了索引的字段中，就会用到覆盖索引。
  >
  >   **注意**：如果要使用覆盖索引，一定要保证查询选择字段是能够被索引的字段覆盖的而避免使用select *；但是如果将所有字段一起创建索引则会导致索引文件变大，同时降低了查询性能。

- ken_len

  > 表示使用中使用的字节数，可以通过该列计算查询中使用的索引长度。==在不损失精确性的情况下，长度越小也好。==
  >
  > key_len显示的是值为索引字段的最大可能长度，==并非实际使用的长度，是通过表定义计算得出的，而不表检索得出的。==

- ref

  > 索引的哪一列被使用，显示哪些列或者常量被使用查找索引上的key
  >
  > const：表示是常量,一般出现在条件中等值匹配一个定值时，例如 where stu.name=‘zs’;

- row

  > 根据表统计信息及索引的选用情况，大致估算出定位数据需要读取的记录行数。==越小越好==

- **Extra**

  > 一些额外信息：
  >
  > - Using filesort（危险）：表示mysql对数据使用了一个外部的索引排序，而不是按照表内的索引顺序进行读取。==MySQL中无法利用索引完成的排序称作**文件内排序**==一般出现在使用了order BY和groupBY的查询中。
  > - Using temporary（十分危险）：表示建立了临时表保存中间结果。常见于order BY和group BY.
  >
  > ==所以在使用group by时，尽量保证groupBY的字段与索引的字段保持相同顺序和数量。==
  >
  > - USING index：表示查询语句中用到了覆盖索引，减少了回表操作，速度和效率提高。
  >   - 如果同时出现了using where 表示索引被用于执行索引键值的查找。
  >   - 如果没有出现using where 表示索引用于读取数据而非执行查找动作。常见于没有where条件的
  > - Using where: 使用了where条件语句
  > - Impossible where: where条件存在逻辑错误。



## 18、索引性能测试

==如何快速进行批量数据的创建？==

- Java多线程插入
- 批量数据脚本（SQL编程）

自建函数、存储过程

```mysql
CREATE TABLE `dept`
(
    `id`       INT(11) not NULL auto_increment,
    `deptName` VARCHAR(30) DEFAULT NULL,
    `address`  VARCHAR(40) DEFAULT NULL,
    ceo        int     NULL,
    PRIMARY KEY (`id`)
) ENGINE = INNODB
  auto_increment = 1
  DEFAULT charset = utf8;

CREATE TABLE `emp`
(
    `id`    INT(11) not NULL auto_increment,
    `empno` INT     not NULL,
    `name`  varchar(20) default null,
    `age`   int(3)      default null,
    deptId  int(11)     default null,
    primary key (`id`)
#     constraint `fk_dept_id` foreign key (deptId) references `dept`(id);
) engine = INNODB
  auto_increment = 1
  default charset = utf8;

# mysql的二进制日志 默认关闭
show variables like 'log_bin_trust_function_creators';
# 全局开启
set global log_bin_trust_function_creators = 1;

# 随机生成字符串
delimiter $$
create function rand_string(n int) returns varchar(255)
begin
    declare char_str varchar(100) default
        'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    declare return_str varchar(255) default '';
    declare i int default 0;
    while i < n
        do
            # 每次取出一个字母拼接到返回字符串中
            set return_str = concat(return_str, substr(char_str, 1 + floor(RAND() * 52), 1));
            set i = i + 1;
        end while;
    return return_str;
end $$

# 随机生成编号
delimiter $$
create function rand_num(from_num int, to_num int) returns int(11)
begin
    declare return_num int(11) default 0;
    set return_num = from_num + floor(RAND() * (to_num - from_num + 1));
    return return_num;
end $$

# 设置用户变量进行测试
set @test_str = rand_string(13);
select @test_str;
# 删除用户变量
set @test_str = null;

# 删除函数：drop function rand_string;

# 创建存储过程 创建员工数据
delimiter $$
create procedure insert_emp(start int, max_num int)
begin
    # 计数器
    declare count int default 0;
    # 关闭自动提交
    set AUTOCOMMIT = false;
    repeat
        set count = count + 1;
        # 插入数据
        insert into emp(empno, name, age, deptId)
        VALUES (start + count,
                rand_string(6),
                rand_num(20, 50),
                rand_num(1, 10000));
    until count = max_num end repeat;
    # 提交
    commit;
end $$

# 创建存储过程 生成部门信息
delimiter $$
create procedure insert_dept(max_num int)
begin
    declare count int default 0;
    set autocommit = false;
    repeat
        set count = count + 1;
        insert into dept(deptName, address, ceo)
        VALUES (rand_string(8),
                rand_string(10),
                rand_num(1,500000));
    until count=max_num end repeat;
    commit;
end $$

delimiter ;
call insert_dept(10000);
call insert_emp(10000,500000);

# 清空表的数据，并重置自增列
truncate table emp;

# 查看自增计算器
select AUTO_INCREMENT
from information_schema.TABLES
where TABLE_NAME = 'dept';
```

游标使用，删除表的除主索引以外的其他索引

```mysql
# 创建存储过程，删除表的全部索引
delimiter $$
create procedure proc_drop_index(dbname varchar(200), tablename varchar(200))
begin
    declare done int default 0;
    declare ct int default 0;
    declare _index varchar(200) default '';
    # 创建游标
    declare _cur cursor for (
        select index_name
        from information_schema.STATISTICS
        where TABLE_SCHEMA = dbname
          and TABLE_NAME = tablename
          and SEQ_IN_INDEX = 1
          and INDEX_NAME <> 'PRIMARY'
    );
    declare continue handler for not found set done = 2;
    open _cur;
    # 获取当前游标指向的索引名
    fetch _cur into _index;
    while _index <> '' do
        # 拼接sql
        set @sql = concat('"drop index ',_index,' on ',tablename);
        # sql预编译
        prepare real_sql from @`sql`;
        execute real_sql;
        deallocate prepare real_sql;
        set _index = '';
        fetch _cur into _index;
    end while;
    close _cur;
end $$;
```



### 索引正确使用(最左前缀原则)

#### 场景一：全列匹配

> 对索引的字段全匹配，且都使用等值匹配(‘=’或 in)   ==Y==

```mysql
# 创建 age+deptId 的复合索引
create index idx_age_deptId on emp(age,deptId);
/*
 使用索引前：execution: 103 ms, fetching: 21 ms
 使用索引后：execution: 5 ms, fetching: 24 ms
 */
explain select sql_no_cache * from emp where age = 30 and deptId = 9988;
```

![image-20200513164633152](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200513164633152.png)

```mysql
# 使用 in 也是可以使用到索引的
explain select sql_no_cache * from emp where age in (20,21,22,23,24) and deptId = 9988;
```

![image-20200513164658878](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200513164658878.png)

> 当where条件中字段有能够完全匹配索引的字段的时候，可以使用索引。
>
> ==这些条件的顺序可以随意调整，mysql的优化器会为我们自动优化顺序然后选择最佳的索引。==



#### 场景二、最左前缀匹配

> where条件中有一个或几个字段使用了索引中最左边开始一个或连续多个字段时会使用==部分索引==
>
> 注意：**索引创建的顺序是可以自定义选择的。**

```mysql
# 场景二： 最左前缀匹配
create index idx_empno_deptId_age on emp(empno,deptId,age);

# empno deptId 匹配 索引的最左前缀  可以使用部分索引 
explain select * from emp where empno = 123213 and deptId = 4235;
```

![image-20200513170140098](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200513170140098.png)

> 这里可以结合场景一，其实全匹配只是最左匹配的一种特殊情况。
>
> 遗留问题：如果查询条件中跳过索引中的字段还能不能用索引呢？



#### 场景三、查询条件使用了索引的最左前缀的部分字段（中间条件缺失）

> where条件子句中，使用到了索引的部分字段，但是中间有字段缺失，只有==部分使用索引==

```mysql
create index idx_empno_deptId_age on emp(empno,deptId,age);

explain select sql_no_cache * from emp where empno = 123123 and age in (23,24,25,26);
explain select sql_no_cache * from emp where empno = 123123 ;
```

![image-20200513171905808](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200513171905808.png)

> 这两行的性能分析结果相同，索引也就只被用于empno字段。
>
> 分析：
>
> 单一个empno字段是可以满足最左前缀的条件的，但是empno+age(中间掉了deptId无法满足最左前缀)，所以where条件中最长的最左前缀就是empno，所以只有empno用到了索引。

**解决办法：**

- 建empno+age的辅助索引

- 将中间缺失的条件填坑

  > 填坑方式一般采用in，当缺失条件的字段==可选值很少的时候==，使用in对字段做指定范围匹配，查询的type会变为`range`
  >
  > ```mysql
  > explain select sql_no_cache * from emp where empno = 100085 and deptId in (2342,4324,4324) and age in (23,24,25,26);
  > ```
  >
  > 这样就既能保证全表索引，还能兼顾索引效率
  >
  > 当可选值很多的时候，就会使得in中的需要填写的值变多，此时最好还是使用建辅助索引吧

  

#### 场景四、查询条件没有使用索引字段的最左列(第一列)

> 当查询的条件中，直接略过索引的最左字段，这样是不满足最左前缀原则的，所以==不会使用索引==

```mysql
# 场景四：不使用最左前缀
explain select sql_no_cache * from emp where deptId = 4455 and age in (23,24,25,26);
```

![image-20200513174019803](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200513174019803.png)



#### 场景五、匹配某列的字符串前缀

> 当再使用模糊查询时候，使用字符串通配符时，通配符不出现在字符串的开头（即匹配字符串的前缀），==可以使用索引==

```mysql
create index idx_name on emp(name);
# 场景五 字符串前缀匹配
explain select sql_no_cache * from emp where name like 'aa%';
explain select sql_no_cache * from emp where name like 'aa_';
```

![image-20200513231330855](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200513231330855.png)

例如这样的字符串前缀匹配，都是可以使用索引的。

```mysql
# 反例
explain select sql_no_cache * from emp where name like '_aa%';
explain select sql_no_cache * from emp where name like '%aa%';
```

![image-20200513231532499](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200513231532499.png)

这种开头就使用通配符的模糊查询是无法使用索引的噢



#### 场景六、使用范围查询

> 当查询条件中用到了范围查询（<、>等），索引中==第一个使用范围查询字段之后的所有字段都不能使用索引。==

```mysql
create index idx_empno_deptId_age on emp(empno,deptId,age);
# 场景六 使用范围查询
explain select sql_no_cache * from emp where empno = 123123 and deptId < 5566 and age in (23,24,25,26,27);
explain select sql_no_cache * from emp where empno = 123123 and deptId < 5566
```

这两条sql的性能分析结果使用的索引长度的一致的，也就证实了 age字段并没有使用索引，两次的查询type都是range也可以进一步证明。

![image-20200513232250757](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200513232250757.png)

> 注意点：
>
> **虽说是范围查询但是between..and.. 和 in不在范畴内噢**
>
> ```mysql
> # 全字段使用索引
> explain select sql_no_cache * from emp where empno = 123123 and deptId between 2233 and 4455 and age in (23,24,25,26,27);
> ```

**解决方案：**

1. 在创建索引之前分析，将有可能使用范围查询的字段放在索引的末端。



#### 场景七、查询条件中含有表达式或者函数

> 一旦查询条件中出现了表达式、函数会使得索引失效，慎重！

```mysql
# 查询条件使用表达式 索引失效
explain
select sql_no_cache *
from emp
where empno + 1 = 288200;

# 查询使用函数 索引失效
explain
select sql_no_cache *
from emp
where left(name, 3) like 'gyc';
```

![image-20200513233628542](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200513233628542.png)

> 提醒：不等于（`<>、!=`）也是表达式的一种，使用后也会是索引失效，同样 `xx is not null`也会使得索引不可用，而is null却可以

#### 场景八、查询条件发生数据类型转换

> 当查询条件中出现数据类型的转换，可能导致索引失效。

```mysql
# 场景八、数据类型转换 
# int转varchar 索引失效
explain
select sql_no_cache *
from emp
where name = 123424;

# varchar 转 int 索引可用
explain
select sql_no_cache *
from emp
where empno = '123424';
```

int转varchar：索引失效![image-20200513234310948](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200513234310948.png)

varchar转int :索引可用

![image-20200513234418508](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200513234418508.png)



#### 场景九、使用了groupby

> **知识补充：**
>
> 当我们使用select * 时使用groupby 会发生报错，关于ONLY_FULL_GROUP_BY。
>
> **MySQL 5.7.5及以上功能依赖检测功能。如果启用了ONLY_FULL_GROUP_BY SQL模式（默认情况下），MySQL将拒绝select列表，HAVING条件或ORDER BY列表的查询引用在GROUP BY子句中既未命名的非集合列，也不在功能上依赖于它们。**
>
> 
>
> ONLY_FULL_GROUP_BY的意思是：对于GROUP BY聚合操作，如果在SELECT中的列，没有在GROUP BY中出现，那么这个SQL是不合法的，因为列不在GROUP BY从句中，也就是说查出来的列必须在group by后面出现否则就会报错，或者这个字段出现在聚合函数里面。



```mysql
explain
select deptId, name
from emp
where name like 'fa%'
group by deptId, name
```

> MySQL要求如果使用groupBY，那么select中的列，必须全部出现在groupBy中，虽然可以使用索引，但是==会触发Using Tempoary和Using Filesort== 而在前面的性能分析结果表的字段分析中，也提到这样个额外信息的出现表示我们的sql是需要进行优化的。



## 19、使用索引建议

1. 在创建索引时，我们尽量选择那些`选择性较高`的组合或者列。

   > 关于选择性（selectivity）
   >
   > 计算选择性：使用列/组合列 不重复的值的数量 ÷ 列/组合列 的总记录数
   >
   > 一般类似ID这种全局唯一的字段，选择性为1，
   >
   > 而例如sex这个字段 不重复的值只有两个，随着总记录数的增加，选择性也会降低。age也是如此，不重复的值顶多120个，而随着记录数的增加也会随之降低。
   >
   > 但是如果将(sex,age)进行组合，选择性又会有些许提高。
   >
   > 有一种与索引选择性相关的索引优化策略：==前缀索引==，使用列中的值的前缀作为索引的key，选择性接近全值列索引，且减少了空间的开销，同时兼顾了速度。

2. 在使用组合索引时，将选择性高的字段放在前面，便于提高过滤效率，提高查询速度。
3. 选择组合索引时，在保证索引尽可能短的同时，尽量多包含where中可能用到的字段。
4. 选择组合索引时，考虑使用范围查询的字段，放在索引的后面位置，避免使其他字段索引失效。
5. 注意SQL编写，结合以上九个场景高效使用索引。



## 20、多表联合查询优化

当我们使用多表关联查询时，有连接发起表，和连接表。在执行过程中它们担任着驱动表和被驱动表的角色（取决于连接表的方式left join、right join、inner join）；

> 当我们两张表都不创建索引的时候
>
> ```mysql
> explain select * from class right join book on class.card = book.card;
> ```

![image-20200516133821469](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516133821469.png)

两张表都是使用全表扫描，相当于做笛卡尔积然后筛选取出符合条件的记录。效率十分低，此时执行过程可以表示为：

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516133122636.png" alt="image-20200516133122636" style="zoom:67%;" />

首先是驱动表中确定一个记录，然后对被驱动表全表扫描，直到对驱动表完成全表扫描。==可以看出对驱动表的全表扫描是无法避免的==，但是我们可以想办法避免对被驱动表进行全表扫描。



此时我们要分清我们写的联表查询SQL中哪个表是驱动表，哪个是被驱动表。

```mysql
# left join 
# 连接发起表是驱动表即 A
# 连接表是被驱动表即 B
A left join B on xxx

# right join 与left join刚好相反
# 连接表是驱动表即 B
# 连接发起表是被驱动表即 A
A right join B on xxx

# inner join 稍后再议
```



> 我们以左连接为例子，创建索引
>
> 注意：==创建索引的字段应该是两表的关联字段==

按照我们刚才的理想优化方式，给book(也就是我们的被驱动表)的card字段建上索引

```mysql
create index idx_book_card on book(card);

explain select * from class right join book on class.card = book.card;
```

执行结果：

![image-20200516133728637](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516133728637.png)

明显看出当被驱动表使用索引后，减少了扫描的行数，执行速度也会相应提升，此时执行过程可以表示为：

![image-20200516134126547](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516134126547.png)

此时虽然对于驱动表的全表扫描无法避免，但是在对被驱动表进行查询时候使用索引，大大加快了数据定位的速度。那如果我们对驱动表也加上索引呢？

> 在被驱动加上索引的基础上为驱动表也加上索引

```mysql
create index idx_class_card on class(card);

explain select sql_no_cache * from class left join book on class.card = book.card;
```

![image-20200516134443437](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516134443437.png)

从结果中可以看到，即使是使用了索引，扫描的行数依旧没有变，相对于之前没有实质性的变化，也就进一步证明==对驱动表的全表扫描是无法避免的==



> 再来看看inner join

![image-20200516135948369](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516135948369.png)

起初我们两张表都有索引，所以默认以inner join的发起表（class）作为驱动表。book 作为被驱动表。

**当我们把book表上关联字段的索引去掉以后，神奇的事情发送了——此时我们的class变为了被驱动表。**

这在我们使用left/right join时是不可能发生的，驱动表是被驱动表是人为写好的， 所以当我们去掉被驱动表的索引后，就是这种情况：

![image-20200516140324862](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516140324862.png)



> 这就涉及到MySQL自动优化的机制：
>
> 当使用inner join时候，驱动表与被驱动表并不是指定不变，==MySQL会选取关联字段有索引的表作为被驱动表，而没有索引的表也就自动变为驱动表，以此来提高效率。==
>
> 当都有关联字段索引的时候，默认前表是驱动表。
>
> 当两张表关联字段都没有索引，选择表记录少的表作为驱动表。



> 联表查询使用子查询

那么到这里我们要注意一个问题：当我们的联表查询中出现了子查询的时候，就要==考虑一下子查询的位置==，使用子查询会出现多趟查询，(MySQL5.7对部分子查询做了优化，有时即使使用子查询也显示一趟完成，即Id列全相同)。

由于我们使用子查询得到的结果是存放在一张虚表里面，==虚表是无法建立索引的！所以尽量将子查询放在驱动表的位置，将被驱动表的位置留给可以创建索引的表。==



此外==如果能使用直接关联多表完成的查询尽量使用直接关联，不用子查询！==



## 21、子查询优化

==尽量不要使用not in 或者 not exists==可以使用`left join on xxx`代替

几个SQL练习（使用之前创建的dept和emp表的缩小版t_dept和t_emp）

1. 列出自己CEO年龄比自己小的人员

	```mysql
	# 优化前
	explain
	select *
	from t_emp e1
	where e1.age > (
	    select e2.age
	    from t_emp e2,
	         t_dept d
	    where e2.empno = d.ceo
	      and e1.deptId = d.id
	);
	
	# 优化后
	explain
	select e1.name '员工姓名', e1.age '员工年龄', e2.name 'CEO姓名', e2.age 'CEO年龄'
	from t_emp e1
	         left join t_dept d on e1.deptId = d.id
	         left join t_emp e2 on d.ceo = e2.empno
	where e1.age > e2.age;
	```

2. 列出所有年龄低于自己部门平均年龄的人员

   ```mysql
   explain
   select e1.name '员工姓名', e1.age '员工年龄', d.avg_age '部门平均年龄'
   from t_emp e1
            inner join
        (
            select deptId, avg(age) avg_age
            from t_emp e2
            where e2.empno is not null
            group by e2.deptId
        ) d
        on e1.deptId = d.deptId
   where e1.age < d.avg_age;
   
   # 进行优化
   # 1. 给 groupby的字段增加索引 
   # 2. 由于使用了 联表查询，且其中有一个虚拟表，应该将其防止在驱动表的位置，要对e1表中 deptId、age创建索引，根据索引建立规则 索引顺序是：deptId+age
   create index idx_deptid on t_emp (deptId);
   create index idx_deptid_age on t_emp (deptId, age);
   ```

3. 列出至少有2个年龄大于40岁成员的部门

   ```mysql
   #优化前
   explain
   select *
   from t_dept d
   where (
             select count(*)
             from t_emp e
             where e.deptId = d.id
               and e.age > 40
         ) > 2;
   
   # 优化后
   explain
   select d.id 部门ID, d.deptName 部门名, count(*) '年龄大于40的员工数'
   from t_emp e1
            inner join t_dept d
                       on e1.deptId = d.id
   where e1.age > 40
   group by d.id, e1.age
   having count(*) > 2;
   
   # 继续优化
   # 由于inner join中dept 受主键索引的影响被选为被驱动表，但是实际上两张表都没有索引的情况下，应该选择记录数较少的 dept表作为驱动表。
   
   # 使用 straight_join 指定固定的驱动表和被驱动表,其他地方和inner join相同，将记录少的dept表放到前面做驱动表，emp做被驱动表，然后给被驱动表建索引
   explain
   select d.id '部门ID', d.deptName '部门名', count(*) '年龄大于40的员工数'
   from t_dept d
            straight_join  t_emp e1
                       on e1.deptId = d.id
   where e1.age > 40
   group by d.deptName ,d.id
   having count(*) > 2;
   
   # 创建索引
   create index idx_deptId_age on t_emp (deptId, age);
   create index idx_deptName on t_dept (deptName);
   ```

4. 列出至少有2个非CEO成员的部门

   ```mysql
   # 优化前
   explain
   select d2.deptName, d2.id, count(*)
   from t_emp e1
   inner join t_dept d2 on e1.deptId = d2.id
   left join t_dept d on e1.empno = d.ceo
   where d.id is null
   group by d2.deptName, d2.id
   having count(*) >= 2;
   
   # 优化
   #1.还是inner join中由于d2 中id的主键索引被选择称为了被驱动表，使用straight_join手动设置
   select d2.deptName, d2.id, count(*)
   from t_dept d2
   straight_join t_emp e1 on d2.id = e1.deptId
   left join t_dept d on e1.empno = d.ceo
   where d.id is null
   group by d2.deptName, d2.id
   having count(*) >= 2;
   # 现在将记录数少的 d2选为了驱动表，接下来就是关于索引的创建
   
   # groupby中 deptName 需要创建一个索引
   create index idx_deptName on t_dept (deptName);
   # 在straight_join中 e1是被驱动表 关联字段deptId要建索引
   create index idx_deptId on t_emp (deptId);
   # 在left join中 d是被驱动表，所以关联字段ceo要创建索引
   create index idx_ceo on t_dept (ceo);
   ```

5. 列出全部成员，并增加一列备注是否为CEO(是:不是)

   ```mysql
   # 条件语句的  两种写法
   explain
   select e.empno '员工ID', e.name '员工姓名', if(isnull(d.id),'否','是') '是否为CEO'
   from t_emp e
            left join t_dept d on e.empno = d.ceo;
   
   explain
   select e.empno '员工ID', e.name '员工姓名', (case when d.id is null then '否' else '是' end) '是否为CEO'
   from t_emp e
            left join t_dept d on e.empno = d.ceo;
            
   # 优化
   # 既然出现了关联查询，那么就可以对被驱动表的关联字段建立索引
   create index idx_ceo on t_dept (ceo);
   ```
   
6. 列出所有部门，并增加一列 ’老鸟or菜鸟‘ 若部门中平均年龄 >50 为老鸟，其余为菜鸟

   ```mysql
   explain
   select d.deptName '部门名', d.id '部门id', avg(e.age) '部门平均年龄', if(avg(e.age)>35,'老鸟','菜鸟') '老鸟or菜鸟'
   from t_dept d
   inner join t_emp e on d.id = e.deptId
   group by d.deptName, d.id;
   
   # 同样的优化手段，先调整好驱动表和被驱动表，然后增加索引
   # 直接通过增加索引变换 两表的角色
   create index idx_deptid on t_emp (deptId);
   # 为groupby 字段增加索引
   create index idx_deptnam on t_dept (deptName);
   ```

7. 显示每个部门年龄最大的员工

   ```mysql
   explain
   select e2.deptId '部门id', e2.name '员工姓名', e2.empno '员工编号', e3.maxage '年龄'
   from t_emp e2
            inner join (
       select e1.deptId, max(e1.age) maxage
       from t_emp e1
       where e1.deptId is not null
       group by e1.deptId
   ) e3
   on e3.deptId = e2.deptId and e2.age = e3.maxage;
   
   # 出现了联表查询 并且联表是子查询 话不多说子查询放驱动表, 被驱动表项建上索引
   create index idx_deptid_age on t_emp (deptId, age);
   ```

   

## 22、排序分组优化

### OrderBy

前面在索引优化分析中的索引选择中提到，==对OrderBy中的字段建立索引会大大调高排序速度！==

没有索引的情况下：

![image-20200516163615882](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516163615882.png)

分析结果中出现了Using fileSort表示MySQL并没有使用我们的索引进行排序，而是自建一个外部索引进行排序，所以我们应当==避免出现Using fileSort,尽可能让MySQL使用我们自己建立的索引==，以下分析几种情况说明如何避免出现Using fileSort



#### 情况一：使用了过滤条件 VS 未使用

首先我们创建索引

```mysql
create index idx_age_deptid on emp (age, deptId);
```

执行未使用过滤的查询：

```mysql
explain
select sql_no_cache *
from emp
order by age, deptId;
```



![image-20200516163834417](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516163834417.png)

结果没有变化，我增加一个过滤条件：

```mysql
explain
select sql_no_cache *
from emp
where age = 34
order by age, deptId;
```

![image-20200516164433724](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516164433724.png)

继续使用其他条件：

```mysql
explain
select sql_no_cache *
from emp
where deptId = 2244
order by age, deptId;
```

![image-20200516164732011](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516164732011.png)

有点问题，明明使用了条件过滤为什么还是出现了Using filesort，再尝试：

```mysql
explain
select sql_no_cache *
from emp
where age = 34 and deptId = 2244
order by age, deptId;
```

![image-20200516164937075](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516164937075.png)

奇了怪，现在又消失了（待会再说）,然后我们尝试以下limit：

```mysql
explain
select sql_no_cache *
from emp
order by age, deptId
limit 30;
```

![image-20200516164624495](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516164624495.png)



> 综合上面五次尝试：可以总结出一个大概的规律：
>
> ==当使用了条件过滤时就可以消灭掉Using filesort使用我们自己的索引==
>
> 这里的过滤条件就包括了
>
> - where查询条件过滤
> - limit分页查询过滤
>
> 但是对于where查询条件好像并非所有的都可以，个人猜测规律：
>
> ==当where的条件中但凡有一个字段用到了索引（为排序字段的建立的索引）就能消除Using filesort，反之如果条件所有的字段都没有用到索引势必出现Using filesort。==

奇怪现象：

因为我们索引是（age,deptid）,那么age单个字段条件查询肯定是能够用上索引的，所以没有出现Usingfilesort

而单个deptid 是不满足索引的左前缀原则的，所以没有用上索引，所以出现了Usingfilesort



#### 情况二：OrderBy中字段顺序与索引顺序不同，或者出现了非索引字段

一样我们先创建索引：

```mysql
create index idx_age_deptid_name on emp (age, deptId, name);
```

先测试执行顺序相同的：

```mysql
explain
select sql_no_cache *
from emp
where age = 34
order by age, deptId, name;
```

![image-20200516171951844](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516171951844.png)

没有出现，我来调整一下顺序：

```mysql
explain
select sql_no_cache *
from emp
where age = 34
order by age, name, deptId;
```

![image-20200516172101960](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516172101960.png)

它来了它来了，我们还原顺序，然后使用一个非索引字段替换其中一个

```mysql
explain
select sql_no_cache *
from emp
where age = 34
order by age, deptId, empno;
```

![image-20200516172313347](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516172313347.png)

还是一样，我们再还原，减去其中一个字段：

```mysql
# 情况一
explain
select sql_no_cache *
from emp
where age = 34
order by deptId,name;

#情况二
explain
select sql_no_cache *
from emp
where age = 34
order by age,name;

# 情况三
explain
select sql_no_cache *
from emp
order by deptId, name
limit 20
```

情况一![image-20200516172553492](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516172553492.png)

情况二

![image-20200516172814038](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516172814038.png)

情况三

![image-20200516173549116](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516173549116.png)

> 结合上面的测试结果总结：
>
> 1. OrderBy中的字段一定要保证和索引创建时的字段顺序相同。
>
>    > 可能会疑问，前面索引不是说MySQL可以帮我们调整吗？但是自动调整的前题是查询结果不会变化，OrderBy中字段顺序变化就可能会引起结果顺序变化，但是也不是绝对的，比如如果列的值是统一的话，还是可以为我们进行优化的
>    >
>    > ```mysql
>    > explain
>    > select sql_no_cache *
>    > from emp
>    > where age = 34
>    > order by deptId, name, age;
>    > # 这里age 固定是34！ 所以age的位置变化不会导致结果变化，所以结果就是：
>    > ```
>    >
>    > ![image-20200516174752149](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516174752149.png)
>
> 2. OrderBy中字段最好也==保证索引字段的最左前缀原则。==
>
> 3. 索引字段中部分字段出现在where条件中且是==等值查询==，也可以等效出现在OrderBy中，然后结合第二条（示例：最后一次测试的三种情况）
>
> 4. OrderBy中不要出现非索引字段。

这里对于第三条额外解释一下：

```mysql
# 我们的索引顺序是 age + deptId + name

explain
select sql_no_cache *
from emp
where age = 34
order by deptId,name;
# 
```

以上这种情况 没有出现 Using filesort 原因就是age作为了where条件且是等值查询，（换成范围查询也会出现Usingfilesort，但是可以解决）如果将age加入orderby中，age这一列也是固定的34，所以这种where的等值判断的字段可以视为加入了orderby中，也就符合第二条规则。

前面说范围查询，出现Using filesort,解决方式就是：显式将此字段按顺序加到OrderBy中就可以：

```mysql
explain
select sql_no_cache *
from emp
where age = 34 and deptId in (2323,2422) # deptId 使用了范围查询
order by deptId,name; # 显式加入OrderBy中
```

![image-20200516175129112](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516175129112.png)



#### 情况三：同升/降序 VS 混合使用

还是使用idx_age_deptid_name作为索引

由于默认OrderBy是使用升序(ASC)，可以直接跳过

```mysql
explain
select sql_no_cache *
from emp
where age = 34
order by age ASC , deptId ASC , name;

explain
select sql_no_cache *
from emp
where age = 34
order by age DESC , deptId DESC , name DESC ;
```

同升序![image-20200516175933721](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516175933721.png)

同降序![image-20200516180319904](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516180319904.png)

将其中一个改为降序，就变为了升降序混合使用

```mysql
explain
select sql_no_cache *
from emp
where age = 34
order by age DESC , deptId , name;
```

![image-20200516180033671](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516180033671.png)

难道混合使用没有影响？我们再加一个降序试试：

```mysql
explain
select sql_no_cache *
from emp
where age = 34
order by age DESC , deptId DESC , name;
```

![image-20200516180137316](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200516180137316.png)

其实并不是，原因是第二次测试中我们的age是固定值 34 所以升降序对它没有影响，所以产生了错觉。

> 总结：
>
> - 同为升序或者同为降序，可以消除Using filesort
> - 升序和降序混用，会出现Using filesort
>
> 注意：升降序对于查询结果中固定不变的列来说，是无效的。



### filesort算法

#### 双路排序

> MySQL 4.1 之前使用双路排序，两次扫描磁盘，最终得到数据，读取行指针和ORDER BY列，对他们进行排序，然后扫描已经排好序的列表，按照列表中的值重新从列表中读取对数据输出。也就是==从磁盘读取排序字段，在buffer进行排序，再从磁盘读取其他字段。==文件的磁盘IO非常耗时的，所以在Mysql4.1之后，出现了第二种算法，就是单路排序。

#### 单路排序

> ==从磁盘读取查询所需要的所有列，按照ORDER BY在buffer对它进行排序，然后扫描排序后的列表进行输出==，它的效率更快一些，避免了第二次读取数据。并且把随机IO变成了顺序IO，但是它会使用更多的空间，因为它把每一行都==保存在了内存里==。



1. 由于以上两种排序都是在buffer中进行排序，都有超出sort_buffer容量的风险，而相对来言，单路排序是去整条记录进行排序，所以风险更大，要==增大一些sort_buffer_size==，在1M~8M之间调整。
2. ==提高max_length_for_sort_data==（1024~8192间调整）,会增加使用改进算法的概率，但是如果设置太高，数据总容量超出sort_buffer的概率也会增加，会导致高的磁盘I/O活动和低的处理器利用率。



### GroupBy

与OrderBy使用大致相同，唯一区别是，在没有条件过滤的情况下也能用到索引。两者可以进行对比学习。



### 覆盖索引

当以上的优化都无法进行或应用时，我们还有最后的招就是使用覆盖索引，前面也有提到并讲解了什么是覆盖索引。

当我们的select列表中的字段，能够被某个索引中的字段覆盖时，即使查询没有可用的索引，也会使用使用这个索引作为覆盖索引，同时避免了回表操作，直接通过索引的key获取值。所以==跟select * 说再见把==



## 23、查询截取分析

### 慢查询日志

> 需要通过MySQL配置开启，默认是关闭的，将查询时间超过预定时间的sql语句以日志的形式输出导文件中
>
> `show variables like '%slow_query_log%'`：用这个命令查看慢查询日志的状态，以及日志文件的存放位置
>
> ![image-20200517101942774](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517101942774.png)
>
> ![image-20200517102413489](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517102413489.png)

配置使用：

1. 开启慢查询日志

   ```mysql
   set global slow_query_log = true;
   ```

2. 配置过滤值

   超时时间：默认是10s

   使用`show variables  like 'long_query_time%';`查看

   ![image-20200517102917295](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517102917295.png)

   修改成0.1s

   ```mysql
   set long_query_time = 0.1;
   ```

3. 然后执行几个SQL后,查看我们的日志文件

   ![image-20200517104128696](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517104128696.png)

   每一个框对应一次慢查询的记录，但是实际开发中这些日志量很大，不可能人为分析，需要机器为我们继续筛选处理数据。

4. mysql提供了日志分析工具：==mysqldumpslow==

   ![image-20200517110019937](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517110019937.png)



```shell
mysqldumpslow -s ct -t 3 -a /var/lib/mysql/localhost-slow.log
```

意思是 统计查询次数(c)、查询时间(t)，然后取出前3条(-t 3)，关闭‘N’/‘S’替换(-a)

![image-20200517110609092](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517110609092.png)



### show processlist

![image-20200517111056989](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517111056989.png)

显示连接进程



## 24、视图

> 将一段查询sql封装成一个虚拟表。
>
> 只保存SQL逻辑，==不保存结果。==

作用

- 当一段查询的结果被频繁使用时候使用视图，提高SQL的复用性

创建视图：`create view xxx as`

```mysql
create view view_test_06
as
select xx
from xx
where xx
...
```

![image-20200517112413404](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517112413404.png)

更新视图：

```mysql
create or replace view view_test_06
as
```

可以在已有的视图的基础上，进行更新。

注意：==MySQL5.5中创建视图的sql中不允许from后有子查询==



## 25、主从复制（重难点）

### 过程以及特点

![img](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/timg.jfif)

1. Binarylog 是主机生成的二进制日志文件。
2. 从机通过I/O读取这个二进制日志文件，然后写入到自己的Relaglog(也叫中继日志)中，同时保存下主机的信息。
3. 然后从机的SQL线程再去读取中继日志（Relaylog），然后执行以达到更新/同步 数据库的效果。



**注意**：

1. MySQL的主从复制与Redis的主从复制不同，Redis主从复制，是将主机中所有的内容进行“复制”，而MySQL的==从机只会保存当连接到主机之后，所有主机的修改操作。==
2. 由于多次的文件IO操作，会导致主从机之间会有短暂的==延迟==。

其他原则不变：

- 每个slave只能有一个master
- 每个slave只能有一个且是唯一的服务器ID
- 每个master可以有多个slave



**我们这里使用Docker来搭建主从复制的环境**

1. 从镜像仓库pull下来mysql:5.7

   ```shell
   docker pull mysql:5.7
   
   # run mysql
   docker run -it -p 3301:3306 -d -e MYSQL_ROOT_PASSWORD=123456 --name mysql_master  mysql:5.7
   
   # into shell
   docker exec -it mysql_master /bin/bash
   ```

2. 运行起来以后，我们进入其命令行，发现是没有vim编辑器的

   ```shell
   # yum安装也是没有的 只能使用apt
   
   # 直接按照会报 '找不到可用的安装包' 先对apt进行更新
   apt-get update
   # 再次执行安装，使用海外的镜像仓库下载可能会有些慢。
   apt-get install vim
   ```

3. 然后修改一下mysql的配置文件（/etc/mysql目录下）

   其中的配置文件是多个配置文件组合而成的

   ![image-20200517172328297](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517172328297.png)其中基本参数比如:`datadir,socket,pidfile`都配置好了（在/etc/mysql/mysql.conf.d/mysql.cnf中）

   ![image-20200517172501438](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517172501438.png)

   我们需要在my.cnf中增加配置一些字符编码的配置即可

   ![image-20200517172628014](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517172628014.png)

4. 配置完成后，我们重启一下容器的mysql，这会导致docker关闭我们的容器。

   ```shell
   # 重启mysql服务，容器会被docker关闭
   service mysql restart
   
   # 重新启动容器
   docker start mysql_master
   # 重新进入命令行,前面提到了怎么进入，这里省略
   ...
   ```

5. 尝试连接（本地and远程）

   本地连接：`mysql -h主机号 -P端口号(3301) -uroot -p` 然后输入密码如果能进入就可以。

   远程连接通过ip和我们使用的端口3301连接即可。

   查看一下字符编码修改情况：

   ![image-20200517173239190](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517173239190.png)

6. 为了避免我们反复配置，我们将这个已经配置好的mysql容器提交 成为新的镜像供我们自己使用

   ```shell
   docker commit mysql_master imageName
   ```

   此后我们直接使用我们提交的镜像创建主从机就可以，也可以将主机，从机配置好，再次提交，但是会占用一些空间。

   

> ==提示：==
>
> - 如果因为修改配置文件而导致容器无法启动，可以通过docker start 的-a选项，查看错误信息。
>
> - 然后通过`docker inspect 容器名`，可以找到容器的文件路径
>
> ![image-20200517175910949](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517175910949.png)
>
> 在这个目录下 /diff/etc/mysql/mysql.cnf中就存放着你之前修改的配置文件，撤销你刚才的修改，然后重新启动就OK了。



### 具体步骤

1. **修改主从机的配置文件（my.ini/my.cnf）**

    **主机**

    1. 设置日志文件存放位置

       `log-bin=xxxx/mysqlbin`

       > 注意点：
       >
       > 当使用docker配置时候，务必保证对此路径下文件夹的读写权限，最好是777;
       >
       > 否者会报错启动失败：
       >
       > ![image-20200517181457355](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517181457355.png)

    2. 设置好server-id,保证全局唯一

    3. 设置那些表输出日志，那些表不输入日志

       `binlog-ignore-db=xx`:复制时候忽略的表

       `binlog-do-db=xx`:仅复制的表

       原则上两者配置一个就可以。

    4. 配置日志输出格式

       `binlog_format=STATEMENT`:标准日志输出格式

       > 存在问题是，由于延迟原因，当主机中使用now()函数，在从机中执行时两者结果不同，就会造成数据不一致问题。

       或者`binlog_format=ROW`:行模式

       > 直接复制主机中已有的数据行，但是在大片数据更新的时候显得效率极低。

       `binglog_format=MIXED`:混合模式

       > 当数据中使用函数生成的，直接复制，其他使用日志生成，但是也存在问题是当sql中使用环境变量的时候，就无能为力了。

    **从机**

    1. server-id，配置为全局唯一

    2. 开启中继日志

       `relay-log=mysql-relay`
    
    ==修改主从机的配置文件后，均要重新启动服务。==



2. 主机授权

   在主机上为从机创建一个可以用于复制用户。

   ```mysql
   # 授权所有库的所有表的复制权限，给'slave'@'%'(所有远程连接) 用户密码是123123 
   grant replication slave on *.* to 'slave'@'%' identified by '123123'
   ```

   查看主机状态`show status master`

   ![image-20200517190737058](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517190737058.png)

   每一次对主机修改，position都会变化，也就证明从机确实是从绑定主机开始复制之后的数据库内容的。

   而每一次主机服务重启就会重新创建一个mysqlbin文件，序号递增。

   ==所以在主从复制环境搭建时，尽量不要对主机数据库进行修改，以免数据不一致==

3. 从机‘拜大哥’

   ```mysql
   change master to master_host = '172.17.0.2',# 容器内部的ip
   	master_port = 3306, # 容器内部的端口
       master_user = 'slave', # 这个用户对应我们在主机授权的用户
       master_password = '123123', # 密码同上
       master_log_file = 'mysqlbin.000005', # 对应我们要读取的二进制文件名
       master_log_pos = 154; # 对应起始位置
   ```

   里面的参数均对应我们在主机中的设置，以及主机的状态。

   当想要解除主从关系时使用`reset master`即可

   ==当我们使用docker搭建时host和port应该填写docker容器的IP而非宿主机的ip!==

   `docker inspect 容器名`可以查看

   或者`docker inspect --format='{{.NetworkSettings.IPAddress}}' 容器名`

   ![image-20200517201007491](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517201007491.png)

   

4. 启动从机`start slave`

   使用`show slave status \G`命令可以查看到一下内容证明连接成功

   ![image-20200517201802082](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517201802082.png)

   - Slave_IO_Running和Slave_SQL_Running 必须全为YES

   这里可以看到错误信息

   ![image-20200517194957976](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517194957976.png)

   - Slave_IO_Running出现Connecting一般有一下几种情况：
     1. 网络不通——检查ip,端口==注意注意 一定是容器的ip和端口==
     2. 连接的用户密码不对
     3. master_log_pos不对

   ![image-20200517193612203](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517193612203.png)

   > 这个原因就是ip和端口填写的是宿主机的而非容器的。

   我们可以随时停止从机的复制`stop slave`,然后查看状态

   ![image-20200517202036759](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517202036759.png)

5. 测试

   ==保证我们配置的要进行复制的库没有在主从复制之前被创建，如果在此之前被创建，从机是无法复制到的==。一定在主从搭建好以后对指定库进行操作。

   **对其他非指定主从复制的库进行操作也不会被从机读取到噢。**

   ![image-20200517211531699](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517211531699.png)

   > 当我们对主机进行修改时，可以通过主机的status中的`Position`的变化来判断我们的操作，是否被写入到了二进制日志中，同时再看从机的`Real_Master_Log_Pos`是否也变化来判断从机是否正确读取到了主机上的二进制日志。
   >
   > ![image-20200517211328178](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517211328178.png)![image-20200517211344806](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517211344806.png)

   ![image-20200517212235488](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517212235488.png)

   Bingo!!完成!

参考博客：https://www.cnblogs.com/songwenjie/p/9371422.html



### 从库只读设置

> 上面主从复制搭建完毕后，发现使用从库还是可以对数据库进行修改，就这就涉及了后面的读写分离。垂直拆分，水平拆分。

==slave上配置只读，在配置文件中的mysqld中配置read_only=1==

**注意**：

- read_only=1只读模式，可以**限定普通用户进行数据修改的操作，但不会限定具有super权限的用户（如超级管理员root用户）的数据修改操作。**

- **如果想保证super用户也不能写操作，就可以就需要执行给所有的表加读锁的命令 “flush tables with read lock;”。**



## 26、MyCat

官网：http://www.mycat.org.cn/

### 概述

**是什么**

- 数据库中间件（Java程序和MySQL之间的中间件）
- 国人开发！骄傲！前身是阿里的cobar

**干什么的**

1. 读写分离

   > 应对大流量访问，将读操作的压力分到从机上，写操作由主机完成。

2. 数据分片

   - 水平拆分（分表）
   - 垂直拆分（分库）

   > 分到多台机器，降低单台机器的压力

3. 多数据源整合

   - nosql:redis等
   - dbms: oracle、mysql

**原理**

- **拦截+转发**

  ![img](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/timg-1589724364252.jfif)



### Linux下安装MyCat

从官网下载tar.gz压缩包解压安装即可

![image-20200517221122762](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517221122762.png)

1. 将解压文件拷贝到/usr/local目录下

![image-20200517221904054](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517221904054.png)

2. 修改配置文件3个（conf目录下）

   - schema.xml :定义逻辑库，表，分片节点等内容

     2. 当中是无关的table

        <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517222952687.png" alt="image-20200517222952687" style="zoom:80%;" />

        将其删除

     3. 然后为schema设置数据结点`dataNode=‘dn1’`

        ![image-20200517223344184](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517223344184.png)

     4. 修改dataNode信息并删除多余的dataNode

        将dataHost的localhost改一下避免引起歧义，==database改为我们刚才主从复制选择的库==

        然后将多余的数据结点dn2,dn3删掉

        ![image-20200517223612563](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517223612563.png)

     5. 配置一下dataHost信息

        ![image-20200517224810378](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517224810378.png)

        > 时间开发中，mycat与我们的数据库是分机器部署的，所以尽量使用远程连接。
        >
        > 这里的ip还是使用容器的ip; 不过宿主机ip启动也可以。

     6. 配置完成

        ![image-20200517225041423](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517225041423.png)

   - rule.xml :定义分片规则 后面学习分片时候进行配置。

   - server.xml :定义用户以及相关系统变量，例如端口等。

     <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517222405589.png" alt="image-20200517222405589" style="zoom: 67%;" />

     1. 为了与mysql区别，将root用户改名为mycat

   

   上面所有配置完成之后，我们再启动前测试一下我们在逻辑库中配置的两台mysql主机的访问情况如何

   如果均能正常登录就没有问题。

   

3. 启动mycat

   两种启动方式，首先进入mycat/bin目录下

   - 控制台(前台)启动：`./mycat console`
   - 后台启动：`./mycat start`

   

   启动报错：拒绝连接，ip配置错误，由于是国人写的，且是java编写所以排错很容易。

   ![image-20200517231735779](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517231735779.png)

   成功：

   ![image-20200517231345599](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517231345599.png)

   

4. 登录

   - 后台管理窗口(9066)：`mysql -u用户名 -p密码 -h mycat的主机号 -P 9066 `
   - 数据窗口(8066)：`mysql -u用户名 -p密码 -h mycat的主机号 -P8066`

   > 这里的用户名和密码参考server.xml
   >
   > ![image-20200517232613026](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517232613026.png)

   成功登录后：

   ![image-20200517232753918](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517232753918.png)

   我是不是走错片场了??这是mysql? 其实只是mycat仿造出来的mysql,也就是我们之前所说的逻辑库。

   看一下有什么库：

   ![image-20200517233011084](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517233011084.png)

   这刚好对应server.xml中配置的，我们再来看看这个库中又哪些表：

   ![image-20200517233312211](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517233312211.png)

   奇迹发生，这就是我们配置给mycat的我们MySQL主从复制的库中创建的表。现在我们通过mycat,也能够对其进行操作。数据可以正常读取出来：

   ![image-20200517233504881](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517233504881.png)



### 读写分离

现在我们执行一个操作，使得主从机中数据库内容不一致

```mysql
insert into test(test) values(@@hostname);
# 将主机名插入，由于每个机器的主机名是不一致的，就造成了主从机数据不一致情况。
```

![image-20200517234705596](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517234705596.png)

我们再来看mycat查出来的数据：

![image-20200517234851368](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517234851368.png)

MyCat显示的是master的数据库表信息。证明我们的读写分离没有开启！！

我们要重新回到==schema.xml进行修改 dataHost中的balance参数==：

![image-20200517235205588](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200517235205588.png)



**balance=“0”**(默认): 表示不启用读写分离

**balance=“1”**: 表示全部的readHost，以及stand by writeHost(备选写主机，只有一台主机是用于写的)都参与select的负载均衡

> 例如：双主双从（M1->S1，M2->S2）再正常情况下，M1和M2是互为主备，只有M1执行写操作，M2就是备选写主机即Stand by writeHost，故 M2，S1，S2都参与select的负载均衡。

**balance=“2”**:所有的读操作再 writeHost和readHost上随机分发。

**balence=“3”**:所有的读操作分发给readHost执行，writeHost不参与承担。



我们配置balance=“2”,看MyCat是怎样变化的，修改配置文件后要重启mycat哦！

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518000529802.png" alt="image-20200518000529802" style="zoom:80%;" />

发现再次使用MyCat来查询数据就是在主从库之间来回随机切换。



### 分库

当单库的数据量达到了瓶颈时后就需要对单库中的表分解出来分到不同的库中。

==分库原则：==

> 当两个库在同一台机器上时，时可以进行跨库join的，但是一旦分库一般是放在不同的机器上的，就不能进行跨库join了，所以：==将有可能进行关联查询的表尽量放在同一个库中==，例如订单表与商品信息表

分库操作并不是我们人去去干预数据的库表，而是通过MyCat进行拆分。修改schema.xml。

![image-20200518140106237](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518140106237.png)

1. 在schema中配置一个table 并设置数据节点dn2
2. 配置数据节点dn1,dn2，由于我们是进行分库，不再使用主从复制，而是将它们两个主机，所以两个dataNode的库名保证一致！不要写主从复制的库。dataHost使用不用的host,达到不同主机的效果。
3. 修改dataHost的host1,增加host2
   - 由于不使用读写分离，将balance改回0
   - 没有读写分离也就没有读主机和写主机之分，所以直接删掉readHost
   - 两个dataHost中的writeHost的ip分别对应两台主机的IP。

```xml
<schema name="TESTDB" checkSQLschema="false" sqlMaxLimit="100" dataNode='dn1' >
    <table name = 'customer' dataNode = 'dn2'></table>
</schema>

<dataNode name="dn1" dataHost="host1" database="order" />
<dataNode name="dn2" dataHost="host2" database="order" />

<dataHost name="host1" maxCon="1000" minCon="10" balance="0"
          writeType="0" dbType="mysql" dbDriver="native" switchType="1"  slaveThreshold="100">
    <heartbeat>select user()</heartbeat>
    <!-- can have multi write hosts -->
    <writeHost host="hostM1" url="172.17.0.2:3306" user="root"
               password="123456">
    </writeHost>
</dataHost>

<dataHost name="host2" maxCon="1000" minCon="10" balance="0"
          writeType="0" dbType="mysql" dbDriver="native" switchType="1"  slaveThreshold="100">
    <heartbeat>select user()</heartbeat>
    <!-- can have multi write hosts -->
    <writeHost host="hostM1" url="172.17.0.3:3306" user="root"
               password="123456">
    </writeHost>
</dataHost>

```



修改完配置文件先别急着启动，我们配置中的order库在两个主机上都还没有，我需要分别创建一个干净的order库。

然后启动MyCat，进去发现：

![image-20200518141819337](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518141819337.png)

show tables;看到有一张表，但实际上我们并没有创建这张表。

由于我们在配置文件中写了这个customer表是放在dn2数据节点下的，也就是172.17.0.3主机(后面简称主机B)上的，所以当我们在MyCat中创建这个表的时候，对应的SQL语句会被拦截然后转发到主机B上执行，其余的表创建一律发给172.17.0.2主机（后面简称主机A）执行。

![image-20200518142707168](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518142707168.png)

![image-20200518143715928](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518143715928.png)



### 分表

#### 数据量大的表

**怎么分？按什么规则分**

> 尽量保证分出的表的数据量平均，访问频率相近，才可以达到减轻单库压力的目的。

首先依然是配置 schema.xml，由于是分表，所以表应该是在多个主机(dataNode)上都有，并且要指定一下分表的规则(rule)

![image-20200518151922985](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518151922985.png)

```xml
<table name = 'orders' dataNode = 'dn1,dn2' rule = 'mod_rule' ></table>
```



原本这order张表只在dn1上，我们现在计划把他拆分到两台主机上，dataNode有两个，并且指定了拆分规则rule=‘mod_rule’。

> 我们通过使用customer_id对节点数取模(mod)，就可以实现根据customer_id进行分表，对应的MyCat中的算法是mod-long。

保存，然后我们要去我们最开始使用MyCat提到的三个配置文件之二： ==rule.xml==

![image-20200518152552625](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518152552625.png)

这个配置文件中，提供了很多预备的分表规则，并且指明了使用的算法，以及算法的class文件：

![image-20200518152749382](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518152749382.png)

==如果我们需要自定义分表规则，只需要使用其中已有的算法，然后对rule中的相关参数设置成为我们自己的就行。==

```xml
<!--自定义算法 rule_mod -->
<tableRule name="rule_mod">
    <rule>
        <!--根据哪个字段分表-->
        <columns>customer_id</columns>
        <!--使用的修改后的算法-->
        <algorithm>mod-long</algorithm>
    </rule>
</tableRule>

<!--对原有进行修改-->
<function name="mod-long" class="io.mycat.route.function.PartitionByMod">
    <!-- 节点数量 修改为2 -->
    <property name="count">2</property>
</function>
```

修改完成，**在启动前，还要去dn2把指定拆分的表(orders)建上**，否者MyCat启动会报错。



启动成功以后，我们使用MyCat进行数据插入测试：

```mysql
INSERT INTO orders(id,order_type,customer_id,amount)
VALUES
(1,101,100,100100),
(2,101,100,100300),
(3,101,101,120000),
(4,101,101,103000),
(5,102,101,100400),
(6,102,100,100020);
```

> 插入过程注意，我们==用于分表的字段必须要在表信息中标出来==，否则插入报错。

![image-20200518155750453](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518155750453.png)

果然是通过customer_id取模进行分表操作的。



#### 联合查询的表

**问题来了**：如果我现在要对orders表进行联表查询能成吗？

![image-20200518161837956](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518161837956.png)

事实证明使用MyCat进行跨库join是不行的，那么为什么会报这个诡异的错误呢？

![image-20200518162521315](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518162521315.png)

问题就在于虽然对orders进行了分表，但是进行联表查询，其中一方并没有这个表就直接报错。

**解决方式：**将进行关联的表也进行分表！

那么问题又来了：怎么分才不会导致关联查询**漏掉结果**呢？

> 如果分表后，dn1中的od表和dn2中odt表进行关联查询也是可以得到结果的，但是由于跨库join无法实现，就会漏掉数据导致查询结果不准确。

最好的办法就是，==让关联表通过 关联字段绑定，你去哪我就去哪==，这样就可以解决这个问题了。

> 比如例子中，odt和od 通过odt.orderid和 od.id产生关联关系，那么由于我们对od进行了分表，那么分表后每个od.id也就有固定的去向，对应的odt.orderid屁颠屁颠跟着跑就对了。

我们来看看在schema.xml中这种配置如何实现：

![image-20200518163854953](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518163854953.png)

```xml
<table name = 'orders' dataNode = 'dn1,dn2' rule = 'mod_rule' >
            <childTable name = 'orders_detail' primaryKey = 'id' joinKey = 'order_id' parentKey = 'id'/>
</table>

```



就是在之前分表的标签中加一个`<childTable>`标签，并指定表名(`name`)，主键名(`primaryKey`)，自身用于和父表进行关联的字段名(`joinKey`)，以及父表中对应的关联字段名(`parentKey`)。



同样**启动之前先去把dn2中缺失的表补上。**

启动然后使用MyCat插入odt数据，查看结果：

![image-20200518165214217](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518165214217.png)

这样就完成了对关联表的分表，我们查询一下：

![image-20200518165359812](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518165359812.png)

解决了联合查询漏数据的问题，即使是分了库分了表。



#### 全局通用的表

还有一些表，其中的数据是全局共享的，但是进行了分库，我们需要将这些表在每个库中都放置一份完整的。这就是全局表。一般这种表的数据量不会很大，会产生冗余但是影响并不大。

看看全局表在schema.xml配置：

![image-20200518170657787](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518170657787.png)

```xml
<table name = 'dict_order_type' dataNode = 'dn1,dn2' type = 'global'></table>
```

为table设置type属性为‘global’即可；

然后去补建上这张表。启动MyCat并插入数据，查看结果：

![image-20200518171607025](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518171607025.png)

两个库中都有这个表，并且数据的统一的。



### 全局序列

分表之后为了使我们的主键值不重复，需要MyCat为我们生成全局序列，之前分布式中生成全局唯一序列的方式有UUID、雪花算法等。

MyCat提供的生成方式：

- 本地文件生成，将计数器存放在本地文件中（稳定性不高，不推荐）

- 时间戳方式（18位，太长占用空间）

- 数据库方式

  > 创建一个数据库，专门用于统计全局序列，每次提供若干个序列供MyCat分配，一旦MyCat宕机，备用机上线，再次从数据库中获取新批次，之前没有用完的直接丢弃，保证了稳定性和可用性。

- 自主生成

  > 使用Redis的单线程特点，每次插入执行一次INCR，也可以保证序列唯一。

  



#### 数据库方式

1. 建表

   ```mysql
   create table MYCAT_SEQUENCE (
       name varchar(50) not null,
       current_value int not null ,
       increment int not null default 100,
       primary key (name)
   )engine = innoDB;
   ```

2. 官方给出的函数

   ```mysql
   #取当前squence的值
   DROP FUNCTION IF EXISTS mycat_seq_currval;
   DELIMITER $$
   CREATE FUNCTION mycat_seq_currval(seq_name VARCHAR(50))RETURNS VARCHAR(64) CHARSET 'utf8'
   BEGIN
       DECLARE retval VARCHAR(64);
       SET retval='-999999999,NULL';
       SELECT CONCAT(CAST(current_value AS CHAR),',',CAST(increment AS CHAR)) INTO retval FROM
           MYCAT_SEQUENCE WHERE NAME = seq_name;
       RETURN retval;
   END$$
   DELIMITER ;
   
   #设置 sequence 值
   DROP FUNCTION IF EXISTS mycat_seq_setval;
   DELIMITER $$
   CREATE FUNCTION mycat_seq_setval(seq_name VARCHAR(50),VALUE INTEGER) RETURNS VARCHAR(64) CHARSET 'utf8'
   BEGIN
       UPDATE MYCAT_SEQUENCE SET current_value = VALUE    WHERE NAME = seq_name;
       RETURN mycat_seq_currval(seq_name);
   END$$
   DELIMITER ;
   
   #取下一个sequence的值
   DROP FUNCTION IF EXISTS mycat_seq_nextval;
   DELIMITER $$
   CREATE FUNCTION mycat_seq_nextval(seq_name VARCHAR(50)) RETURNS VARCHAR(64) CHARSET 'utf8'
   BEGIN
       UPDATE MYCAT_SEQUENCE SET current_value = current_value + increment
       WHERE NAME = seq_name;
       RETURN mycat_seq_currval(seq_name);
   END$$
   DELIMITER ;
   ```

   > 若报错1418：
   >
   > ```mysql
   > show variables like '%log_bin_trust%';
   > # 为off再执行这条语句，将其打开然后创建函数。
   > SET GLOBAL log_bin_trust_function_creators = 1;
   > ```

3. 表中插入数据

   ```mysql
   insert into MYCAT_SEQUENCE(name, current_value) VALUES 
   ('ORDERS',400000,100);
   # 统计列是 orders
   # 当前计数值是 400000
   # 每次下发100个（步长）
   ```

4. 修改MyCat配置文件

   - sequence_db_conf.properties

     `ORDERS=dn1`,指向我们计数的数据库节点。

   - server.xml

     `<property name="sequnceHandlerType">1</property>`

     0：文件方式

     1：数据库方式

     2：时间戳方式（默认）

     3：自主生成

5. 在MyCat中向orders表插入数据

   ```mysql
   INSERT INTO orders(id,order_type,customer_id,amount)
   VALUES
   (next value for MYCATSEQ_ORDERS,102,100,100900);
   ...
   ```

   ![image-20200518181044365](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518181044365.png)

   对应的MYCAT_SEQUENCE中数据也产生了变化：

   ![image-20200518181152399](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518181152399.png)

6. 模拟宕机：重启MyCat，再次插入数据：

   ![image-20200518181321721](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518181321721.png)

   直接放弃了上一批次未用完的数据，重新分配新的批次；

   ![image-20200518181414460](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200518181414460.png)