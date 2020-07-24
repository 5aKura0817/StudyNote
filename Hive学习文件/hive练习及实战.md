# 习题、蚂蚁森林

## 一、数据准备

`user_low_carbon`表：

|    字段    |           描述           |
| :--------: | :----------------------: |
|  user_id   |           用户           |
|  data_dt   |           日期           |
| low_carbon | 减少碳排放积攒的低碳能量 |

建表语句:

```sql
create table user_low_carbon(
    user_id string,
    data_dt string,
    low_carbon int) 
row format delimited 
fields terminated by '\t';
```

数据：[user_low_carbon数据文件](F:\GitProject\GitLearn\MarkDown\Hive学习文件\data/user_low_carbon.txt)

----

`plant_carbon`表：

|    字段    |          描述          |
| :--------: | :--------------------: |
|  plant_id  |        植物编号        |
| plant_name |         植物名         |
| low_carbon | 换购植物需要的低碳能量 |

建表语句

```sql
create table plant_carbon(
    plant_id string,
    plant_name string,
    low_carbon int) 
row format delimited 
fields terminated by '\t';
```

数据：[plant_carbon数据文件](F:\GitProject\GitLearn\MarkDown\Hive学习文件\data\plant_carbon.txt)



为了提高运行速度开启本地模式
`set hive.exec.mode.local.auto=true;`



## 二、案例需求一：蚂蚁森林植物申领统计

### 题目

> 问题：假设2017年1月1日开始记录低碳数据（user_low_carbon），假设2017年10月1日(不包含10月1日)之前满足申领条件的用户**都申领了一颗 p004-胡杨**，剩余的能量全部用来领取“p002-沙柳”。统计在10月1日累计申领“p002-沙柳”**排名前10**的用户信息；以及他**比后一名多领了几颗沙柳**。
> 得到的统计结果如下表样式：
>
> ```markdown
> user_id plant_count less_count(比后一名多领了几颗沙柳)
> u_101--->1000--->100
> u_088--->900--->400
> u_103--->500--->xxx…
> ```

### 解题步骤

1. 第一步统计出所有用户截至10月1号攒下的能量(`tlc表`)

   ```sql
   select
       user_id ,
       SUM(low_carbon) total_low_carbon 
   FROM 
       user_low_carbon
   where
       date_format(regexp_replace(data_dt ,'/','-'),'yyyy-MM') < '2017-10'
   GROUP BY 
       user_id ;
   ```

2. 获取兑换p002-沙柳(`sl`表)、p004-胡杨(`hy`表)所需的能量

   ```sql
   select
       plant_name,
       low_carbon need
   from
       plant_carbon
   where
       plant_id = 'p004'; hy
   
   select
       plant_name,
       low_carbon need
   from
       plant_carbon
   where
       plant_id = 'p002'; sl
   ```

3. 统计出每个用户兑换p002-沙柳的数量，并排序(**向下取整**)`ts`表

   ```sql
   select
       user_id,
       floor((total_low_carbon-hy.need)/sl.need) total_sl
   from
       tlc,hy,sl
   order by
       total_sl desc
   limit 11; ts -- 提前过滤数据集
   ```

   根据前两步的得出表，进行子程序嵌套。

4. 步骤3的基础上将下一行数据，输出到当前行

   ```sql
   select
       user_id,
       total_sl,
       lead(total_sl,1) over(order by total_sl desc) next_sl
   from
   	ts;
   ```

5. 在4的基础上稍作修改

   ```sql
   select
       user_id,
       total_sl,
       total_sl-lead(total_sl,1) over(order by total_sl desc) less_count
   from
       ts
   limit 10;
   ```

6. 输出结果

   ```markdown
   user_id	total_sl	less_count
   u_007	66	3
   u_013	63	10
   u_008	53	7
   u_005	46	1
   u_010	45	1
   u_014	44	5
   u_011	39	2
   u_009	37	5
   u_006	32	9
   u_002	23	1
   Time taken: 9.158 seconds, Fetched: 10 row(s)
   ```

   



----

## 三、案例需求二：蚂蚁森林低碳用户排名分析

### 题目

> 问题：查询user_low_carbon表中每日流水记录，条件为：
>
> 用户在2017年，**连续三天（或以上）**的天数里，
> 每天减少碳排放（low_carbon）都**超过100g的用户低碳**流水。
> 需要查询返回满足以上条件的user_low_carbon表中的记录流水。
>
> 例如：用户u_002符合条件的记录如下，因为2017/1/2~2017/1/5连续四天的碳排放量之和都大于等于100g：
>
> ```markdown
> seq（key） user_id data_dt low_carbon
> xxxxx10 u_002 2017/1/2 150
> xxxxx11 u_002 2017/1/2 70
> xxxxx12 u_002 2017/1/3 30
> xxxxx13 u_002 2017/1/3 80
> xxxxx14 u_002 2017/1/4 150
> xxxxx14 u_002 2017/1/5 101
> ```

### 解题分析

#### 解法一

1. 筛选统计出，所有用户2017年日减排量>=100的记录（`rc`表）;并统计达标总天数（total_count）

   ```sql
   select
       user_id,
       data_dt,
       count(data_dt) over(partition by user_id) total_count
   from
       user_low_carbon
   where
       substring(data_dt,1,4)='2017'
   group by
       user_id,data_dt
   having
       sum(low_carbon)>=100; 
   ```

2. 提前过滤掉天数小于3的用户记录（`rc2`表）

   ```sql
   select
       user_id,
       -- 为了方便后续计算，提前格式化
       date_format(regexp_replace(data_dt,'/','-'),'yyyy-MM-dd') dt
   from
       rc
   where
       rc.total_count>=3;
   ```

3. 解决**连续三天**的逻辑问题

   ```markdown
   # 首先连续三天在数据中有三种表现
   x,x+1,x+2
   x-1,x,x+1
   x-2,x-1,x
   > 所以我们需要在当前行，拿到前两行和后两行的数据，然后与当前行做差值计算得到四个值（lag2,lag1,lead1,lead2）
   四个值满足以下任意一种情况即视为有效
   
   - (lag2=2 and lag1=1)
   - (lag1=1 and lead1=-1)
   - (lead1=-1 and lead2=-2)
   ```

4. 按用户分区，将每行数据的前两行和后两行的时间拿到本行，空值以'1970-01-01'替换（`pass`表）

   ```sql
   select
       user_id,
       dt,
       lag(dt,2,'1970-01-01') over(partition by user_id order by dt) lag2_date,
       lag(dt,1,'1970-01-01') over(partition by user_id order by dt) lag1_date,
       lead(dt,1,'1970-01-01') over(partition by user_id order by dt) lead1_date,
       lead(dt,2,'1970-01-01') over(partition by user_id order by dt) lead2_date
   from
    	rc2;
   
   ```

5. 在这4的基础上，select上加上日期的计算(**datediff**)就可以得到那四个值。

   ```sql
   select
       user_id,
       dt,
       datediff(dt,lag(dt,2,'1970-01-01') over(partition by user_id order by dt)) lag2_date,
       datediff(dt,lag(dt,1,'1970-01-01') over(partition by user_id order by dt)) lag1_date,
      -- ..
   ```

6. 对pass表的四个数值进行判断过滤，筛选出最终符合要求的数据

   ```sql
   select
       user_id,
       dt
   from
   	pass
   where
       (lag2_date=2 and lag1_date=1)
       or
       (lag1_date=1 and lead1_date=-1)
       or
       (lead1_date=-1 and lead2_date=-2);
   ```

   

#### 解法二（简述）

解法一的方式有所限制：==当要求是取出连续10天或者更多的时候，光是要取出的数据就有18个，更不用说符合条件的情况，一一列举出来就有些不合理了。==

这里给出一个思路，借助等差数列的特点，以用户分区，区内按日期排序，然后使用一列作为标记（可使用rank()，按情况而定），用日期列减去标记，这样两个等差数列且公差相同的时候，就会形成连续的日期变成了相同的日期值。后续判断简单，只需要和上一行的变化后日期对比，相同则表示连续。求连续n天，标记列等差数列就有n行。

```markdown
# 数据演示
date			rank()		date_sub
2017-01-03		1			2017-01-02
2017-01-04		2			2017-01-02
2017-01-05		3			2017-01-02
2017-01-07		4			2017-01-03
2017-01-09		5			2017-01-04
2017-01-010		6			2017-01-04
...

# 结论
前三行计算后数据一致，表明原始数据中前三行是连续的！
```







# 习题、谷粒影音

## 一、数据准备

> 用户表

|  字段名  |     字段描述     |
| :------: | :--------------: |
| uploader | 视频上传用户的ID |
|  videos  |   上传视频数量   |
| friends  |     好友数量     |

```sql
create table user(
    userid string,
    friends int
)
```



> 影视表

|   字段名    |           字段描述           |
| :---------: | :--------------------------: |
|  video id   |     视频ID（11位字符串）     |
|  uploader   | 视频上传者（上传者的用户ID） |
|     age     | 视频年龄（上传至今的总天数） |
|  category   |           视频类别           |
|   length    |           视频长度           |
|    views    |         视频观看次数         |
|    rate     |           视频评分           |
|   ratings   |             流量             |
|  comments   |           评论数量           |
| related ids |         相关视频的ID         |

```sql

```





---

## 二、需求描述

1. 统计视频**观看数的Top10**
2. 统计视频**类别的Top10**
3. 统计视频**观看数Top20的所有视频的所属类别，及其类别中包含的Top20中的视频个数****
4. 统计视频**观看数Top50的视频的相关视频的类别排行**。
5. 统计**每个类别中视频热度Top10**
6. 统计**每个类别中视频流量Top10**
7. 统计**上传视频数量的用户Top10**，以及他们**上传的视频在观看次数前20的视频中的数量**。
8. 统计**每个类别视频观看数Top10**



