-- 1. Каждый месяц компания выдает премию в размере 5% от суммы продаж менеджеру,
-- который за предыдущие 3 месяца продал товаров на самую большую сумму
-- Выведите месяц, manager_id, manager_first_name, manager_last_name,
-- премию за период с января по декабрь 2014 года

select MONTH, MANAGER_ID, MANAGER_FIRST_NAME, MANAGER_LAST_NAME, AVG_SUM * 0.05 sal
from (
         select trunc(MONTHS_BETWEEN(SALE_DATE, to_date('01.10.2013', 'DD-MM-YY'))) MONTH
              , MANAGER_ID
              , MANAGER_FIRST_NAME
              , MANAGER_LAST_NAME
              , SALE_AMOUNT
              , AVG_SUM
              , dense_rank()
                 over (partition by trunc(MONTHS_BETWEEN(SALE_DATE, to_date('01-10-2013', 'DD-MM-YY'))) order by AVG_SUM
                     desc)                                     k
         from (
                  select SALE_DATE
                       , SALE_AMOUNT
                       , MANAGER_ID
                       , MANAGER_FIRST_NAME
                       , MANAGER_LAST_NAME
                       , sum(SALE_AMOUNT)
                             over (partition by MANAGER_ID ORDER BY SALE_DATE range between INTERVAL '3' MONTH PRECEDING AND current row ) AVG_SUM
                  from v_fact_sale
                  where SALE_DATE between to_date('01-10-2013', 'DD-MM-YY') and to_date('31-12-2014', 'DD-MM-YY')
              )
         where SALE_DATE between to_date('01-01-2014', 'DD-MM-YY') and to_date('31-12-2014', 'DD-MM-YY')
     )
where k = 1
group by MONTH, MANAGER_ID, MANAGER_FIRST_NAME, MANAGER_LAST_NAME, AVG_SUM;

/* ------------------------------------------------------------------------------
7	510	Jacqueline	Alvarez	11597.9105
11	958	Sharon	Jackson	10160.778
14	287	Peter	Bowman	9886.2555
10	66	Jesse	Graham	11778.4245
12	945	Norma	Sanders	12020.18
4	969	Gregory	Cox	15049.106
6	214	Lawrence	Gordon	12527.494
3	362	Eugene	Miller	13942.096
5	191	Clarence	Woods	14948.0415
9	366	Michael	Evans	11110.552
8	1	Walter	Ford	12245.8705
13	65	Michael	White	10247.8135
   ------------------------------------------------------------------------------ */

-- 2. Компания хочет оптимизировать количество офисов, проанализировав относительные объемы продаж
-- по офисам в течение периода с 2013-2014 гг.
-- Выведите год, office_id, city_name, country, относительный объем продаж за текущий год
-- Офисы, которые демонстрируют наименьший относительной объем в течение двух лет скорее всего будут закрыты.

select distinct to_char(SALE_DATE, 'YYYY')                                                    YEAR,
                OFFICE_ID,
                CITY_NAME,
                COUNTRY,
                (sum(SALE_AMOUNT) over (partition by OFFICE_ID, to_char(SALE_DATE, 'YYYY')))
                    / (sum(SALE_AMOUNT) over (partition by to_char(SALE_DATE, 'YYYY'))) * 100 total_sale
from V_FACT_SALE
where to_char(SALE_DATE, 'YYYY') between 2013 and 2014;

/* ------------------------------------------------------------------------------
2013	280	Bafang	Cameroon	0.2353479987193085283164098683327477550755
2013	326	Pailiao	China	0.7502313524268205249526524068779070622408
2013	342	Nyachera	Uganda	0.0541881298858933580558664988405981097111
2013	343	Thị Trấn Nghèn	Vietnam	0.0302377830195176268886439329607982944916
2013	369	Santa Rosa de Viterbo	Brazil	0.0143485295455553327706716300158413768624
2013	383	Blaye	France	1.31575526633586412065645566346711388773
2013	399	Valašská Polanka	Czech Republic	0.4524211727737842872242265179822594983657
2013	414	Lukolela	Democratic Republic of the Congo	0.700705484840209205740117485511302138616
2013	439	Hiroshima-shi	Japan	0.8160799289701514039979214627534838058145
2013	442	Chaem Luang	Thailand	0.3274548411494210969674479076063767593033

   ------------------------------------------------------------------------------ */

-- 3. Для планирования закупок, компанию оценивает динамику роста продаж по товарам.
-- Динамика оценивается как отношение объема продаж в текущем месяце к предыдущему.
-- Выведите товары, которые демонстрировали наиболее высокие темпы роста продаж в течение первого полугодия 2014 года.

select distinct PRODUCT_ID, PRODUCT_NAME, DENSE_RANK() over (order by PRODUCT_AVG_INC desc), PRODUCT_AVG_INC
from (
         select PRODUCT_ID, PRODUCT_NAME, avg(INCR) over (partition by PRODUCT_ID) PRODUCT_AVG_INC
         from (
                  select PRODUCT_ID,
                         PRODUCT_NAME,
                         MONTH,
                         SALE_AMOUNT / LAG(SALE_AMOUNT) over (ORDER BY MONTH) INCR
                  from (SELECT PRODUCT_ID, PRODUCT_NAME, to_char(SALE_DATE, 'MM') MONTH, sum(SALE_AMOUNT) SALE_AMOUNT
                        from V_FACT_SALE
                        where to_char(SALE_DATE, 'YYYY') = 2014
                          and to_char(SALE_DATE, 'Q') BETWEEN 1 AND 2
                        group by PRODUCT_ID, PRODUCT_NAME, to_char(SALE_DATE, 'MM'))
              )
         order by MONTH)
order by PRODUCT_AVG_INC desc;

/* ------------------------------------------------------------------------------
942	Aurum Onopordon	1	241.061312762316096181166326468805898344
761	Octinoxate, Octisalate, and Titanium Dioxide	2	35.29757788248665797084661038503044656183
136	travoprost	3	22.66445847785553996154701470316708379917
932	famotidine	4	22.52033340438273936760337375165496022483
885	Levofloxacin	5	10.87921528672237628270057626629174294054
306	Western Juniper	6	10.81239324278478052053036309302558070506
572	Belladonna	7	5.65799511382790313449179091924758615339
891	Sulfacetamide Sodium and Prednisolone Sodium Phosphate	8	5.60441008687495529874898129099083317451
810	Miconazole Nitrate	9	2.36196948226012770627676863580440075979
518	stannous fluoride	10	1.09020987214173549037388993703122403094
   ------------------------------------------------------------------------------ */

-- 4. Напишите запрос, который выводит отчет о прибыли компании за 2014 год: помесячно и поквартально.
-- Отчет включает сумму прибыли за период и накопительную сумму прибыли с начала года по текущий период.

select MONTH,
       QUARTER,
       SALE_AMOUNT                                  MONTH_AMOUNT,
       sum(SALE_AMOUNT) over (order by MONTH)       MONTH_AMOUNT_CUM,
       sum(SALE_AMOUNT) over (partition by QUARTER) QUARTER_AMOUNT,
       sum(SALE_AMOUNT) over (order by QUARTER)     QUARTER_AMOUNT_CUM
from (select to_char(SALE_DATE, 'MM')   MONTH,
             to_char(SALE_DATE, 'Q') as QUARTER,
             sum(SALE_AMOUNT)           SALE_AMOUNT
      from V_FACT_SALE
      where to_char(SALE_DATE, 'YYYY') = 2014
      group by to_char(SALE_DATE, 'MM'), to_char(SALE_DATE, 'Q')
     )
order by MONTH;

/* ------------------------------------------------------------------------------
01	1	3578273.01	3578273.01	11686029.56	11686029.56
02	1	3920216.37	7498489.38	11686029.56	11686029.56
03	1	4187540.18	11686029.56	11686029.56	11686029.56
04	2	3118397.31	14804426.87	11528379.7	23214409.26
05	2	4428701.85	19233128.72	11528379.7	23214409.26
06	2	3981280.54	23214409.26	11528379.7	23214409.26
07	3	3073504.86	26287914.12	10118861.06	33333270.32
08	3	3388595.84	29676509.96	10118861.06	33333270.32
09	3	3656760.36	33333270.32	10118861.06	33333270.32
10	4	3577055.56	36910325.88	9906633.63	43239903.95
   ------------------------------------------------------------------------------ */

-- 5. Найдите вклад в общую прибыль за 2014 год 10% наиболее дорогих товаров и 10% наиболее дешевых товаров.
-- Выведите product_id, product_name, total_sale_amount, percent

select PRODUCT_ID
     , PRODUCT_NAME
     , TOTAL_SALE_AMOUNT
     , round(PROPORTION * 100, 4) PERSENT
from (
         select PRODUCT_ID
              , PRODUCT_NAME
              , sum(SALE_AMOUNT)                              TOTAL_SALE_AMOUNT
              , cume_dist() over ( order by sum(SALE_AMOUNT)) dist
              , RATIO_TO_REPORT(sum(SALE_AMOUNT)) OVER () AS  PROPORTION
         from V_FACT_SALE
         where SALE_DATE between to_date('01-01-2014', 'DD-MM-YY') and to_date('31-12-2014', 'DD-MM-YY')
         group by PRODUCT_ID, PRODUCT_NAME
     )
where dist >= 0.9
   or dist <= 0.1;

/* ------------------------------------------------------------------------------
981	Japanese Black Pine	26483.1	0.0612
755	STRYCHNOS NUX-VOMICA SEED	33074.2	0.0765
136	travoprost	4858892.05	11.2371
761	Octinoxate, Octisalate, and Titanium Dioxide	6053124.09	13.9989
942	Aurum Onopordon	12649783.75	29.2549
   ------------------------------------------------------------------------------ */

-- 6. Компания хочет премировать трех наиболее продуктивных (по объему продаж, конечно)
-- менеджеров в каждой стране в 2014 году. Выведите country,
-- <список manager_last_name manager_first_name, разделенный запятыми> которым будет выплачена премия

select COUNTRY, LISTAGG(MANAGER_LAST_NAME || ' ' || MANAGER_FIRST_NAME || ', ') WITHIN GROUP (ORDER BY MANAGER_LAST_NAME)
from (
         select COUNTRY,
                MANAGER_FIRST_NAME,
                MANAGER_LAST_NAME,
                DENSE_RANK() over (partition by COUNTRY order by SALE_AMOUNT desc) M_RANK
         from (
                  select COUNTRY, MANAGER_FIRST_NAME, MANAGER_LAST_NAME, sum(SALE_AMOUNT) SALE_AMOUNT
                  from V_FACT_SALE
                  where to_char(SALE_DATE, 'YYYY') = 2014
                  group by COUNTRY, MANAGER_ID, MANAGER_FIRST_NAME, MANAGER_LAST_NAME
              )
     )
where M_RANK < 4
group by COUNTRY;

/* ------------------------------------------------------------------------------
Aland Islands	Ramos Kathryn, Reynolds Shirley, Simpson Alice,
Albania	Black Douglas, Day Martha, Stephens Henry,
Armenia	Barnes Jerry, Henry Margaret, Hudson Carolyn,
Belarus	Fox Jeffrey, Kim Louise, Perry Adam,
Bosnia and Herzegovina	Alvarez Donna,
Brazil	Gardner Rebecca, Morrison Ryan, Walker Thomas,
Bulgaria	Carroll Sandra, Gutierrez Joe, Marshall Andrew,
Cambodia	Larson Joan, Simpson Catherine,
Cameroon	Carter Paula, Mcdonald Adam, Wright Patrick,
China	Gonzalez Janet, Gordon Lawrence, Sanders Norma,
   ------------------------------------------------------------------------------ */

-- 7. Выведите самый дешевый и самый дорогой товар, проданный за каждый месяц в течение 2014 года.
-- cheapest_product_id, cheapest_product_name, expensive_product_id, expensive_product_name, month, cheapest_price,
--      expensive_price
-- средняя цена;

select distinct to_char(SALE_DATE, 'MM') MONTH,
                last_value(PRODUCT_ID) over(partition by to_char(SALE_DATE, 'MM') order by SALE_PRICE rows between unbounded preceding  and unbounded following ) EXP_PRODUCT_ID,
                last_value(product_name) over(partition by to_char(SALE_DATE, 'MM') order by SALE_PRICE rows between unbounded preceding  and unbounded following ) EXP_PRODUCT_NAME,
                first_value(PRODUCT_ID) over(partition by to_char(SALE_DATE, 'MM') order by SALE_PRICE rows between unbounded preceding  and unbounded following ) CHP_PRODUCT_ID,
                first_value(product_name) over(partition by to_char(SALE_DATE, 'MM') order by SALE_PRICE rows between unbounded preceding  and unbounded following ) CHP_PRODUCT_NAME,
                max(SALE_PRICE) over(partition by to_char(SALE_DATE, 'MM')) EXP_PRICE,
                min(SALE_PRICE) over(partition by to_char(SALE_DATE, 'MM'))  CHP_PRICE
from V_FACT_SALE
where SALE_DATE between to_date('01-01-2014', 'DD-MM-YY') and to_date('31-12-2014', 'DD-MM-YY');

/* ------------------------------------------------------------------------------
04	942	Aurum Onopordon	450	Avobenzone, Homosalate, Octisalate and Octocrylene	978.1	4.82
07	942	Aurum Onopordon	450	Avobenzone, Homosalate, Octisalate and Octocrylene	971.57	2.09
12	942	Aurum Onopordon	450	Avobenzone, Homosalate, Octisalate and Octocrylene	979	0.59
06	942	Aurum Onopordon	450	Avobenzone, Homosalate, Octisalate and Octocrylene	970.86	1.44
01	942	Aurum Onopordon	450	Avobenzone, Homosalate, Octisalate and Octocrylene	965.03	9.37
09	942	Aurum Onopordon	450	Avobenzone, Homosalate, Octisalate and Octocrylene	981.02	3.29
11	942	Aurum Onopordon	450	Avobenzone, Homosalate, Octisalate and Octocrylene	979.34	3.72
02	942	Aurum Onopordon	450	Avobenzone, Homosalate, Octisalate and Octocrylene	977.12	1.03
05	942	Aurum Onopordon	450	Avobenzone, Homosalate, Octisalate and Octocrylene	980.44	8.75
03	942	Aurum Onopordon	450	Avobenzone, Homosalate, Octisalate and Octocrylene	974.18	1.21
   ------------------------------------------------------------------------------ */

-- 8. Менеджер получает оклад в 30 000 + 5% от суммы своих продаж в месяц. Средняя наценка стоимости товара - 10%
-- Посчитайте прибыль предприятия за 2014 год по месяцам (сумма продаж - (исходная стоимость товаров + зарплата))
-- month, sales_amount, salary_amount, profit_amount

select distinct MONTH,
                SALES_AMOUNT,
                sum(SALARY) over (partition by MONTH )                 SALARY_AMOUNT,
                SALES_AMOUNT - sum(SALARY) over (partition by MONTH) PROFIT_AMOUNT
from (
         select to_char(SALE_DATE, 'MM')                                         MONTH,
                sum(SALE_AMOUNT) over (partition by to_char(SALE_DATE, 'MM'))   SALES_AMOUNT,
                sum(SALE_AMOUNT) over (partition by MANAGER_ID, to_char(SALE_DATE, 'MM')) * 0.05 + 30000 SALARY
         from V_FACT_SALE
         where to_char(SALE_DATE, 'YYYY') = 2014
     )
order by MONTH;

/* ------------------------------------------------------------------------------
01	3578273.01	9833520.7935	-6255247.7835
02	3920216.37	10962228.081	-7042011.711
03	4187540.18	11040742.5385	-6853202.3585
04	3118397.31	7971985.1225	-4853587.8125
05	4428701.85	12443854.7435	-8015152.8935
06	3981280.54	10893913.5965	-6912633.0565
07	3073504.86	8915280.618	-5841775.758
08	3388595.84	8652941.0165	-5264345.1765
09	3656760.36	10208977.831	-6552217.471
10	3577055.56	9137559.361	-5560503.801
   ------------------------------------------------------------------------------ */
