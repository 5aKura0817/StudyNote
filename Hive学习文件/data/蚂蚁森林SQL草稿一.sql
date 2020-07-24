-- 1. 统计10月1日前所有用户积攒的能量
select
    user_id ,
    SUM(low_carbon) total_low_carbon 
from 
    user_low_carbon
where
    date_format(regexp_replace(data_dt ,'/','-'),'yyyy-MM') < '2017-10'
group by 
    user_id ; tlc

-- 2. 得到兑换胡杨/沙柳的能量
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


-- 3. 统计每个用户兑换的沙柳数目并按降序排序

select
    user_id,
    floor((total_low_carbon-hy.need)/sl.need) total_sl
from
    (
        select
            user_id ,
            SUM(low_carbon) total_low_carbon 
        from 
            user_low_carbon
        where
            date_format(regexp_replace(data_dt ,'/','-'),'yyyy-MM') < '2017-10'
        group by 
            user_id 
    ) tlc,
    (
        select
            plant_name,
            low_carbon need
        from
            plant_carbon
        where
            plant_id = 'p004'
    ) hy,
    (
        select
            plant_name,
            low_carbon need
        from
            plant_carbon
        where
            plant_id = 'p002'
    ) sl
order by
    total_sl desc
limit 11; ts-- 提前过滤数据集


-- 4. 步骤3的基础上将下一行数据，输出到当前行

select
    user_id,
    total_sl,
    lead(total_sl,1) over(order by total_sl desc) next_sl
from
    (
        select
            user_id,
            floor((total_low_carbon-hy.need)/sl.need) total_sl
        from
        (
            select
                user_id ,
                SUM(low_carbon) total_low_carbon 
            from 
                user_low_carbon
            where
                date_format(regexp_replace(data_dt ,'/','-'),'yyyy-MM') < '2017-10'
            group by 
                user_id 
        ) tlc,
        (
            select
                plant_name,
                low_carbon need
            from
                plant_carbon
            where
                plant_id = 'p004'
        ) hy,
        (
            select
                plant_name,
                low_carbon need
            from
                plant_carbon
            where
                plant_id = 'p002'
        ) sl
        order by
            total_sl desc
        limit 11
    ) 
    ts;

-- 5. 在4的基础上稍作修改
select
    user_id,
    total_sl,
    total_sl-lead(total_sl,1) over(order by total_sl desc) less_count
from
    (
        select
            user_id,
            floor((total_low_carbon-hy.need)/sl.need) total_sl
        from
        (
            select
                user_id ,
                SUM(low_carbon) total_low_carbon 
            from 
                user_low_carbon
            where
                date_format(regexp_replace(data_dt ,'/','-'),'yyyy-MM') < '2017-10'
            group by 
                user_id 
        ) tlc,
        (
            select
                plant_name,
                low_carbon need
            from
                plant_carbon
            where
                plant_id = 'p004'
        ) hy,
        (
            select
                plant_name,
                low_carbon need
            from
                plant_carbon
            where
                plant_id = 'p002'
        ) sl
        order by
            total_sl desc
        limit 11
    )
    ts
limit 10;