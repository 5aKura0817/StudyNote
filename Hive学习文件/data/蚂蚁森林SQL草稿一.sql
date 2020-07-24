-- 1. ͳ��10��1��ǰ�����û����ܵ�����
select
    user_id ,
    SUM(low_carbon) total_low_carbon 
from 
    user_low_carbon
where
    date_format(regexp_replace(data_dt ,'/','-'),'yyyy-MM') < '2017-10'
group by 
    user_id ; tlc

-- 2. �õ��һ�����/ɳ��������
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


-- 3. ͳ��ÿ���û��һ���ɳ����Ŀ������������

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
limit 11; ts-- ��ǰ�������ݼ�


-- 4. ����3�Ļ����Ͻ���һ�����ݣ��������ǰ��

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

-- 5. ��4�Ļ����������޸�
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