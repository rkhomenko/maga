-- 1. Выбрать все заказы (SALES_ORDER)
select * from SALES_ORDER;

-- 2. Выбрать все заказы, введенные после 1 января 2016 года
select * from SALES_ORDER
    where ORDER_DATE > TO_DATE('2016-01-01', 'YYYY-MM-DD');

-- 3. Выбрыть все запросы, введенные после 01.01.2016 и до 15.07.2016
select * from SALES_ORDER
    where ORDER_DATE > TO_DATE('2016-01-01', 'YYYY-MM-DD')
        and ORDER_DATE < TO_DATE('2016-07-15', 'YYYY-MM-DD');

-- 4. Найти менеджеров с именем Henry
select * from MANAGER
    where MANAGER_FIRST_NAME = 'Henry';

-- 5. Выбрать все заказы менеджеров с именем Henry
select * from MANAGER inner join SALES_ORDER SO on MANAGER.MANAGER_ID = SO.MANAGER_ID
    where MANAGER.MANAGER_FIRST_NAME = 'Henry';

-- 6. Выбрать все уникальные страны из таблицы CITY
select distinct COUNTRY from CITY;

-- 7. Выбрать все иникальные комбинации страны и региона из таблицы CITY
select distinct COUNTRY, REGION from CITY;

-- 8. Выбрать все страны с количеством городов в них
select COUNTRY, COUNT(CITY_NAME) from CITY
    group by COUNTRY;

-- 9. Выбрать количество товаров, проданное с 1 по 30 января 2016

select sum(PRODUCT_QTY)
from SALES_ORDER_LINE SOL
         inner join SALES_ORDER SO on SOL.SALES_ORDER_ID = SO.SALES_ORDER_ID
where TO_DATE('2016-01-01', 'YYYY-MM-HH') <= ORDER_DATE
  and ORDER_DATE <= TO_DATE('2016-01-30', 'YYYY-MM-DD');

-- 10. Выбрать все уникальные названия городов, регионов и стран в одной колонке

select distinct CITY_NAME from CITY
union all
select distinct REGION from CITY
union all
select distinct COUNTRY from CITY;

-- 11. Вывести имена и фамилии менеджеров, продавщих товаров на наибольщую сумму в январе 2016

with MANAGER_TOTAL as (
    select MANAGER_ID, sum(PRODUCT_QTY * PRODUCT_PRICE) TOTAL_AMMOUNT
    from SALES_ORDER_LINE SOL
             inner join SALES_ORDER SO on SOL.SALES_ORDER_ID = SO.SALES_ORDER_ID
    group by MANAGER_ID
)
select MANAGER_FIRST_NAME, MANAGER_LAST_NAME
from MANAGER_TOTAL MT
         inner join MANAGER M on M.MANAGER_ID = MT.MANAGER_ID
where TOTAL_AMMOUNT = (select max(TOTAL_AMMOUNT) from MANAGER_TOTAL)
