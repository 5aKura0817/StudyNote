select
    sum(case deptno when 1 then 1 else 0 end) yanfabu,
    sum(case deptno when 2 then 1 else 0 end) renshibu,
    sum(case deptno when 3 then 1 else 0 end) caiwubu,
    sum(case deptno when 4 then 1 else 0 end) chanpinbu
from staff;

select
    sum(if(sal<=6000,1,0)) xinren,
    sum(if(sal>6000,1,0)) laogou
from staff;

select
    concat(sport,',',sex) sport_sex,
    name
from 
    stu_sport;

select 
    t1.sport_sex,
    concat_ws('|',collect_set(t1.name))
from
    (
        select
            concat(sport,',',sex) sport_sex,
            name
        from 
            stu_sports
    ) t1
group by t1.sport_sex;

select 
    name, 
    movie_genre
from
    movies lateral view explode(genres) table_tmp as movie_genre;

select 
    name 
from 
    marketorder 
where 
    substring(orderdate,1,7)='2017-04' 
group by
    name

select
    t1.name,
    count(*)
from
    (
        select 
            name 
        from 
            marketorder 
        where 
            substring(orderdate,1,7)='2017-04' 
        group by
            name
    ) t1
group by t1.name;

select
    name,
    count(*) over()
from
    marketorder
where
    substring(orderdate,1,7)='2017-04'
group by
    name;

select
    *,
    sum(cost) over(order by orderdate)
from
    marketorder;


select
    *,
    sum(cost) over(distribute by name sort by orderdate)
from
    marketorder;

select
    *,
    lag(orderdate,1,'1970-01-01') over(distribute by name sort by orderdate) lasttime
from
    marketorder;


select
    name,
    orderdate,
    cost
from
    (
        select
            name,
            orderdate,
            cost,
            ntile(5) over(order by orderdate)
        from
            marketorder
    ) t1
where
    t1.ntile_10=1;


select
    name,
    subject,
    score,
    rank() over(partition by subject order by score) rank,
    dense_rank() over(partition by subject order by score) dense_rank,
    row_number() over(partition by subject order by score) row_number
from
    score;


-- 动态分区
create table dynamic_partition(name string,age int)
partitioned by (grade string)
row format delimited
fields terminated by '\t';