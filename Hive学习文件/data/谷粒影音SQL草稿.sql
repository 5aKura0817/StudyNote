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

-- 4. 统计视频观看数Top50的视频的相关视频的类别排行。

-- 5. 统计每个类别中视频热度Top10

-- 6. 统计每个类别中视频流量Top10

-- 7. 统计上传视频数量的用户Top10，以及他们上传的视频在观看次数前20的视频中的数量。

-- 8. 统计每个类别视频观看数Top10

