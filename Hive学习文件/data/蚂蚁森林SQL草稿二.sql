-- 1. 找到2017年 日减排量>=100 的用户记录
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
    sum(low_carbon)>=100; rc（record count）


-- 2. 通过记录数再次过滤掉天数小于三天的用户记录
select
    user_id,
    date_format(regexp_replace(data_dt,'/','-'),'yyyy-MM-dd') dt
from
    (
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
            sum(low_carbon)>=100
    )
    rc
where
    rc.total_count>=3; rc2


-- 3. 按用户分区，将每行数据的前两行和后两行的时间拿到本行，空值以'1970-01-01'替换
select
    user_id,
    dt,
    datediff(dt,lag(dt,2,'1970-01-01') over(partition by user_id order by dt)) lag2_date,
    datediff(dt,lag(dt,1,'1970-01-01') over(partition by user_id order by dt)) lag1_date,
    datediff(dt,lead(dt,1,'1970-01-01') over(partition by user_id order by dt)) lead1_date,
    datediff(dt,lead(dt,2,'1970-01-01') over(partition by user_id order by dt)) lead2_date
from
    (
        select
            user_id,
            date_format(regexp_replace(data_dt,'/','-'),'yyyy-MM-dd') dt
        from
            (
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
                    sum(low_carbon)>=100
            )
            rc
        where
            rc.total_count>=3
    )
    rc2; pass

-- 4. 过滤连续三天的用户记录
select
    user_id,
    dt
from
    (
        select
            user_id,
            dt,
            datediff(dt,lag(dt,2,'1970-01-01') over(partition by user_id order by dt)) lag2_date,
            datediff(dt,lag(dt,1,'1970-01-01') over(partition by user_id order by dt)) lag1_date,
            datediff(dt,lead(dt,1,'1970-01-01') over(partition by user_id order by dt)) lead1_date,
            datediff(dt,lead(dt,2,'1970-01-01') over(partition by user_id order by dt)) lead2_date
        from
            (
                select
                    user_id,
                    date_format(regexp_replace(data_dt,'/','-'),'yyyy-MM-dd') dt
                from
                    (
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
                            sum(low_carbon)>=100
                    )
                    rc
                where
                    rc.total_count>=3
            )
            rc2
    )
    pass
where
    (lag2_date=2 and lag1_date=1)
    or
    (lag1_date=1 and lead1_date=-1)
    or
    (lead1_date=-1 and lead2_date=-2);
