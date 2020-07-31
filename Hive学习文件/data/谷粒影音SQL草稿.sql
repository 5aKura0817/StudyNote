-- 建表 video表
create table guliVideo(
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

-- 用户表
create table guliUser(
    uploader string,
    videos int,
    friends int
) row format delimited
fields terminated by '\t'
stored as textfile;


-- 1. 统计视频观看数的Top10
select videoId,views
from gulivideo_video_orc
order by views DESC
limit 10;


-- 2. 统计视频类别热度的Top10
将类别中视频个数作为视频类别的热度！
1）将每个电影的类别使用explode()分离开
select
    videoId,
    category_name
from
    gulivideo_video_orc
    lateral view explode(category) tmp_category as category_name;  category_tbl;

2）按类别分类，统计每个分类中视频的数量
select
    category_name,
    count(*) category_count
from
    category_tbl
group by 
    category_name
order by
    category_count DESC
limit
    10;

3) 完整SQL:
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


-- 3. 统计视频观看数Top20的所有视频的所属类别，及其类别中包含的Top20中的视频个数
1) 统计出观看数Top20的视频
select
    videoId,
    category,
    views
from
    gulivideo_video_orc
order by 
    views DESC
limit 20; tmp_video

2) 分离Top20中视频所属的类别
select
    videoId,
    category_name
from
    tmp_video
    lateral view explode(tmp_video.category) tmp_category as category_name; tmp_category

3) 按照类别进行分组，然后使用count求和
select
    category_name
    count(*) category_count
from
    tmp_category
group by
    category_name;

4) 最终SQL

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

-- 4. 统计视频观看数Top50的视频的相关视频的类别排行(Rank)。
- 取出播放量Top50的视频以及相关视频ID(relatedId)
select
    relatedId,
    views
from
    gulivideo_video_orc
order by
    views DESC
limit 50; t1

- 对相关视频的ID进行拆分，并使用groupby去重
select
    related_id
from
    t1
    lateral view explode(relatedId) tmp_related as related_id
group by
    related_id; t2

- 关联原表（join）取出所有相关视频的类别
select
    category
from
    t2
join 
    gulivideo_video_orc video
on
    t2.related_id=video.videoId; t3

- 对类别进行拆分
select
    explode(category) category_name
from
    t3; t4

- 分组统计类别，使用rank() over()进行排名
select
    category_name,
    count(*) category_count,
    rank() over(order by count(*) DESC) category_rank
from
    t4
group by
    category_name;

最终SQL
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

-- 5. 统计每个类别中视频热度Top10

-- 6. 统计每个类别中视频流量Top10

-- 8. 统计每个类别视频观看数Top10

- 创建临时表gulivideo_category
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

- 插入数据
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
    lateral view explode(category) tmp_category as categoryName;

- 需求实现以需求八为准
1. 按照类别分组，为每个分组的视频添加rank字段
select
    categoryName,
    videoId,
    views,
    rank() over(partition by categoryName order by views DESC) rk_views
from
    gulivideo_category; t1

2. 取出每个分组中rk_views<=10 记录
select
    categoryName,
    videoId,
    views
from
    t1
where
    rk_views<=10;

最终SQL：
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

-- 7. 统计上传视频数量的用户Top10，以及他们上传的视频在观看次数前20的视频中的数量。
1. 取出上传数量Top10的用户
select
    uploader,
    videos
from
    gulivideo_user_orc
order by
    videos DESC
limit 10; t1

2. 取出这些用户的所有上传视频，并按照观看数排序取出top20
select
    video.videoId,
    video.views,
    t1.uploader
from
    t1
join
    gulivideo_video_orc video
on
    t1.uploader=video.uploader
order by
    views DESC
limit 20;

3. 最终SQL：
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