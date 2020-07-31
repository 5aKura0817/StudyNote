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
| related ids |    相关视频的ID（可为空）    |





---

## 二、需求描述

1. 统计视频**观看数的Top10**
2. 统计视频**类别热度的Top10**
3. 统计视频**观看数Top20的所有视频的所属类别，及其类别中包含的Top20中的视频个数****
4. 统计视频**观看数Top50的视频的相关视频的类别排行**。
5. 统计**每个类别中视频热度Top10**
6. 统计**每个类别中视频流量Top10**
7. 统计**上传视频数量的用户Top10**，以及他们**上传的视频在观看次数前20的视频中的数量**。
8. 统计**每个类别视频观看数Top10**



## 三、数据清洗（ELT）

当前我们所拿到的数据中存在个问题：

1. 在**视频类别**和**相关视频ID**两个字段的数据，在数据结构描述中都属于**array类型**，但是真实数据中两者使用的间隔符并不一致！这样的数据在Hive导入数据的时候是不允许的，因为row format里面每类集合使用的间隔符是固定的！！
2. 存在缺少字段的数据记录需要剔除



我们使用一个MapReduce来完成一个简单的数据清洗：

> Mapper阶段
>
> 由于数据中没有可以作为Key区分数据的字段，也不需要做合并汇总处理。
> 所以Mapper的写出数据类型就采用NullWritable,Text;
> 同时直接省去了Reducer

```java
public class ETLMapper extends Mapper<LongWritable, Text, NullWritable, Text> {

    private Text valueOut = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();

        // 数据清洗
        String str = ETLUtils.etlData(line);

        if (str == null) {
            return;
        }
        valueOut.set(str);
        context.write(NullWritable.get(), valueOut);
    }

}
```



> 数据清洗工具类（ELTUtils）

```java
public class ETLUtils {
    /**
     * 1. 判断字段数 >=9
     * 2. 去除第四个字段中多余的space
     * 3. 从第10个"字段"开始 使用`&`连接
     *
     * @description 数据清洗
     * @param data 原始数据
     * @return 清洗后的数据
     */
    public static String etlData(String data) {
        StringBuffer stringBuffer = new StringBuffer();

        // 1.切割数据
        String[] fields = data.split("\t");

        // 2.过滤字段数
        if (fields.length < 9) {
            return null;
        }

        // 3.去除字段三中多余的space
        fields[3] = fields[3].replaceAll(" ", "");

        // 4.对所有字段进行重组，前九个字段(index=8)以 \t分割，第十个字段开始都使用 &分割
        for (int i = 0; i < fields.length; i++) {
            if (i < 9) {
                stringBuffer.append(fields[i]).append("\t");
                // 刚好只有9个字段，去除末尾的\t
                if (i == fields.length - 1) {
                    stringBuffer.deleteCharAt(stringBuffer.length()-1);
                }
            } else {
                // index>=9 第十个字段开始统一使用'&'连接
                stringBuffer.append(fields[i]).append("&");
                // 去除末尾的 &
                if (i == fields.length - 1) {
                    stringBuffer.deleteCharAt(stringBuffer.length()-1);
                }
            }
        }
        data = stringBuffer.toString();
        return data;
    }
}
```



> Driver类（普通写法）

```java
public class ETLDriver {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        job.setJarByClass(ETLDriver.class);
        job.setMapperClass(ETLMapper.class);

        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        boolean result = job.waitForCompletion(true);
        System.exit(result ? 0 : 1);
    }
}
```

> Driver标准写法  实现Tool接口

```java
public class ETLDriver implements Tool {

    private Configuration conf;

    @Override
    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(conf);
        job.setJarByClass(ETLDriver.class);
        job.setMapperClass(ETLMapper.class);

        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        boolean result = job.waitForCompletion(true);
        return result ? 0 : 1;
    }

    @Override
    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    @Override
    public Configuration getConf() {
        return this.conf;
    }

    public static void main(String[] args) {
        try {
            ToolRunner.run(new ETLDriver(), args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

之前我们直接在Main方法里面把所有配置写在一起，固然是没有问题也可以正常运行，但是==运行的时候是无法接受hadoop的参数的。==
这种写法:

- 实现`Tool`接口，并实现`run()`,`getConf()`,`setConf()`方法，之前大部分main方法写的东西都可以写到run方法中，==在源码中其实最终还是调用run()方法，只是中间做了一些配置参数的解析处理。==
- main方法中调用`ToolRunner.run()`，传入一个Tool对象（即当前的Driver）和main方法的参数args

查看源码，其实相比较于我们之前的写法，只是多做了一个配置解析工作：

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200729103229.png" alt="image-20200729103229577" style="zoom: 67%;" />

正是这个配置解析工作，让我们==在集群上运行的时候能够额外接受Hadoop传来的参数。==



> 集群运行

将项目打包放到集群上，数据上传到HDFS上作为输入。

```shell
hadoop jar jars/guli-vedio-1.0.jar com.sakura.mr.ETLDriver /guliVideo/video /guliVideo/output
# hadoop jar jar包路径 类全限名 参数1(输入数据) 参数2(输出路径) ...
```





## 四、建表数据导入

> 建表语句

- 影视表guliVideo

  ```sql
  create table gulivideo_video(
      videoId string,
      uploader string,
      age int,
      category array<string>,
      length int,
      views int,
      rate float,
      ratings int,
      comments int,
      relatedId array<string>
  ) row format delimited
  fields terminated by '\t'
  collection items terminated by '&'
  stored as textfile;
  ```

- 用户表guliuser

  ```sql
  create table gulivideo_user(
      uploader string,
      videos int,
      friends int
  ) row format delimited
  fields terminated by '\t'
  stored as textfile;
  ```

----

> 数据导入

```shell
load data inpath '/guliVideo/output/part-r-00000' into table gulivideo;
load data inpath '/guliVideo/user/' into table guliuser;
```

视频表的数据不要导错了！！是MR输出的数据！



### ORC表创建和数据导入

> 修改建表语句

将建表语句中最后的`stored as textfile`改为`stored as orc`即可，我们将表名字设为

- gulivideo_user_orc
- gulivideo_user_orc

> 从原始表导入数据

`insert into table gulivideo_video_orc select * from gulivideo_video`

`insert into talbe gulivideo_user_orc select * from gulivideo_user`

> 为什么使用ORC格式数据表？

参考Hive学习笔记中**8.2.2数据存储格式**，使用ORC格式存储数据由于==自带数据压缩功能==可以==减小数据的空间占用。==
当前案例中200MB的数据文件使用orc格式存储占用空间不足100MB!!



## 五、需求代码实现

### 5.1、需求一

观看数Top10的视频

```sql
select videoId,views
from gulivideo_video_orc
order by views DESC
limit 10;
```

result:

```
dMH0bHeiRNg	42513417
0XxI-hvPRRA	20282464
1dmVU08zVpA	16087899
RB-wUgnyGv0	15712924
QjA5faZF1A8	15256922
-_CSo1gOd48	13199833
49IDp76kjPw	11970018
tYnn51C3X_w	11823701
pv5zWaTEVkI	11672017
D2kJZOfq7zk	11184051
```



### 5.2、堆内存溢出解决

> 这里可能出现堆内存溢出的问题，需要通过查看日志确定，并进行相应的修改！！
>
> ![image-20200729120126762](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200729120126.png)
>
> 查看日志信息：访问`hadoop103:8088`
>
> ![image-20200729120223864](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200729120223.png)
>
> 历史服务器的配置参看Hadoop的第一篇学习笔记，开启历史服务器
>
> `sbin/mr-jobhistory-daemon.sh start historyserver`
>
> ![image-20200729120842126](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200729120842.png)
>
> 进入查看log
>
> ![image-20200729120917431](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200729120917.png)
>
> ![image-20200729121017162](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200729121017.png)

解决方式：

在yarn-site.xml中加入以下配置，然后重启Hadoop集群

```xml
<property>
	<name>yarn.scheduler.maximum-allocation-mb</name>
    <value>2048</value>
</property>
<property>
	<name>yarn.scheduler.minimum-allocation-mb</name>
    <value>2048</value>
</property>
<property>
	<name>yarn.nodemanager.vmen-pmen-ratio</name>
    <value>2.1</value>
</property>
<property>
	<name>mapred.child.java.opts</name>
    <value>-Xmx1024m</value>
</property>
```



> 然后又来一个错：
>
> ![image-20200729124045818](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200729124045.png)
>
> 建议把
>
> `yarn.nodemanager.vmen-pmen-ratio`或者`yarn.scheduler.minimum-allocation-mb`以及`yarn.scheduler.maximum-allocation-mb`继续调高。这里我测试使用2.8即可





### 5.3、需求二

视频类别热度的Top10

首先我们将类别中的视频个数作为类别的热度，那么这个需求就分为两步：

- 使用`explode()`将每个视频的类别单独分离
- 对处理后的数据，按照视频类别分组，使用`count(*)`进行数量统计，然后倒序排序取前十

```sql
select
    category_name,
    count(*) category_count
from
    (
       select
            videoId,
            category_name
        from
            gulivideo_video_orc
            lateral view explode(category) tmp_category as category_name 
    ) category_tbl
group by 
    category_name
order by
    category_count DESC
limit
    10;
```

result

```
category_name   category_count
Music   179049
Entertainment   127674
Comedy  87818
Animation   73293
Film    73293
Sports  67329
Gadgets 59817
Games   59817
Blogs   48890
People  48890
```



### 5.4、需求三

统计视频观看数Top20的所有视频的所属类别，及其类别中包含的Top20中的视频个数。分三步完成：

- 筛选出观看数前20的视频及其种类
- 将种类分离
- 对分离的类别做分组统计

```sql
select
    category_name,
    count(*) category_count
from
    (
        select
            videoId,
            category_name
        from
            (
                select
                    videoId,
                    category,
                    views
                from
                    gulivideo_video_orc
                order by 
                    views DESC
                limit 20
            ) tmp_video
            lateral view explode(tmp_video.category) tmp_category as category_name
    ) tmp_category
group by
    category_name;
```



### 5.5、需求四

统计视频观看数Top50的视频的相关视频的类别排行(Rank)。分以下五个步骤：

- 取出播放量Top50的视频以及相关视频ID(relatedId)
- 对相关视频的ID进行拆分，并使用groupby去重（这里不推荐使用distinc，参考hive学习笔记 9.3.5）
- 关联原表（join）取出所有相关视频的类别
- 对类别进行拆分
- 分组统计类别，使用rank() over()进行排名

```sql
select
    category_name,
    count(*) category_count,
    rank() over(order by count(*) DESC) category_rank
from
    (
        select
            explode(category) category_name 
        from
            (    
                select
                    category
                from
                    (
                        select
                            related_id
                        from
                            (
                                select
                                    relatedId,
                                    views
                                from
                                    gulivideo_video_orc
                                order by
                                    views DESC
                                limit 50
                            )t1
                            lateral view explode(relatedId) tmp_related as related_id
                        group by
                            related_id
                    )t2
                join 
                    gulivideo_video_orc video
                on
                    t2.related_id=video.videoId
            )t3
    )t4
group by
    category_name;
```

result

```
category_name	category_count	category_rank
Comedy	232	1
Entertainment	216	2
Music	195	3
Blogs	51	4
People	51	4
Film	47	6
Animation	47	6
News	22	8
Politics	22	8
Games	20	10
Gadgets	20	10
Sports	19	12
Howto	14	13
DIY	14	13
UNA	13	15
Places	12	16
Travel	12	16
Animals	11	18
Pets	11	18
Autos	4	20
Vehicles	4	20
```



### 5.6、需求五、六、八

统计每个类别中视频热度Top10、
统计每个类别中视频流量Top10、
统计每个类别视频观看数Top10。
三个需求都设计了对类别的分组，也就是基本上我们都需要对类别进行拆分，==像这样多次复用的表，我们可以创建一个临时表来存储数据，以减小MR任务的负担。==



`gulivideo_category`表

```sql
create table gulivideo_category(
    videoId string,
    uploader string,
    age int,
    categoryName string,
    length int,
    views int,
    rate float,
    ratings int,
    comments int,
    relatedId array<string>
) row format delimited
fields terminated by '\t'
collection items terminated by '&'
stored as orc;

-- 插入数据
insert into gulivideo_category
select 
	videoId,
    uploader,
    age,
    categoryName,
    length,
    views,
    rate,
    ratings,
    comments,
    relatedId
from
	gulivideo_video_orc
	lateral
```



三个需求都过程都差不多，这里以需求八作为示范：

```sql
select
    categoryName,
    videoId,
    views
from
    (
        select
            categoryName,
            videoId,
            views,
            rank() over(partition by categoryName order by views DESC) rk_views
        from
            gulivideo_category
    )
    t1
where
    rk_views<=10;
```

==这种类型的需求(分组TopN)，一般可以通过使用`over()`开窗计算，先将数据分组(partition)然后使用排序(order)==



### 5.7、需求七

统计上传视频数量的用户Top10，以及他们上传的视频在观看次数前20的视频中的数量。

这个需求的表述并不明确，我们暂定为：**上传视频数量Top10用户的所有上传视频的播放量的Top20的视频。**

- 取出上传数量Top10的用户
- 关联video表取出这些用户的所有上传视频，按照观看数分类并取出Top20

```sql
select
    video.videoId,
    video.views,
    t1.uploader
from
    (
        select
            uploader,
            videos
        from
            gulivideo_user_orc
        order by
            videos DESC
        limit 10
    )t1
join
    gulivideo_video_orc video
on
    t1.uploader=video.uploader
order by
    views DESC
limit 20;
```

result:

```
video.videoid	video.views	t1.uploader
-IxHBW0YpZw	39059	expertvillage
BU-fT5XI_8I	29975	expertvillage
ADOcaBYbMl0	26270	expertvillage
yAqsULIDJFE	25511	expertvillage
vcm-t0TJXNg	25366	expertvillage
0KYGFawp14c	24659	expertvillage
j4DpuPvMLF4	22593	expertvillage
Msu4lZb2oeQ	18822	expertvillage
ZHZVj44rpjE	16304	expertvillage
foATQY3wovI	13576	expertvillage
-UnQ8rcBOQs	13450	expertvillage
crtNd46CDks	11639	expertvillage
D1leA0JKHhE	11553	expertvillage
NJu2oG1Wm98	11452	expertvillage
CapbXdyv4j4	10915	expertvillage
epr5erraEp4	10817	expertvillage
IyQoDgaLM7U	10597	expertvillage
tbZibBnusLQ	10402	expertvillage
_GnCHodc7mk	9422	expertvillage
hvEYlSlRitU	7123	expertvillage
```

