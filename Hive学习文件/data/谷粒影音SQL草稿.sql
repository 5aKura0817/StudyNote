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


-- 2. 统计视频类别的Top10
-- 3. 统计视频观看数Top20的所有视频的所属类别，及其类别中包含的Top20中的视频个数
-- 4. 统计视频观看数Top50的视频的相关视频的类别排行。
-- 5. 统计每个类别中视频热度Top10
-- 6. 统计每个类别中视频流量Top10
-- 7. 统计上传视频数量的用户Top10，以及他们上传的视频在观看次数前20的视频中的数量。
-- 8. 统计每个类别视频观看数Top10

