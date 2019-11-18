-- 1. Вывести все директории в виде:
-- ID, Название, Путь до корня
select ID, NAME, SYS_CONNECT_BY_PATH(NAME, '/') as PATH
from FILE_SYSTEM
connect by PARENT_ID = prior id
start with PARENT_ID is null
order siblings by NAME;

/*
0	Dep806	/Dep806
1	231854.pdf	/Dep806/231854.pdf
2	244020.pdf	/Dep806/244020.pdf
3	Autumn 2011	/Dep806/Autumn 2011
4	DWH	/Dep806/Autumn 2011/DWH
5	12.+Optimization.mht	/Dep806/Autumn 2011/DWH/12.+Optimization.mht
6	Analytic functions by Example _ Oracle FAQ.mht	/Dep806/Autumn 2011/DWH/Analytic functions by Example _ Oracle FAQ.mht
7	Ask Tom _Difference Between ROLLUP and CUBE_.mht	/Dep806/Autumn 2011/DWH/Ask Tom _Difference Between ROLLUP and CUBE_.mht
17	F_FULL_NAME.fnc	/Dep806/Autumn 2011/DWH/F_FULL_NAME.fnc
18	F_FULL_NAME.~fnc	/Dep806/Autumn 2011/DWH/F_FULL_NAME.~fnc
 */

-- 2. Для каждой директории посчитать объем занимаемого места на диске (с учетом всех вложенных папок)
-- ID, Название, Путь до корня, total_size

select ID,
       NAME,
       SYS_CONNECT_BY_PATH(NAME, '/') as PATH,
       FILE_SIZE + (
           select nvl(sum(FILE_SIZE), 0)
           from FILE_SYSTEM FS_SUB
           start with FS_SUB.PARENT_ID = FS.ID
           connect by FS_SUB.PARENT_ID = prior FS_SUB.ID
       )                                 SUM
from FILE_SYSTEM FS
start with FS.PARENT_ID is null
connect by PARENT_ID = prior ID;

/*
0	Dep806	/Dep806	110292993816
1	231854.pdf	/Dep806/231854.pdf	7507850
2	244020.pdf	/Dep806/244020.pdf	6321866
3	Autumn 2011	/Dep806/Autumn 2011	12394850
4	DWH	/Dep806/Autumn 2011/DWH	12394850
5	12.+Optimization.mht	/Dep806/Autumn 2011/DWH/12.+Optimization.mht	481415
6	Analytic functions by Example _ Oracle FAQ.mht	/Dep806/Autumn 2011/DWH/Analytic functions by Example _ Oracle FAQ.mht	162602
7	Ask Tom _Difference Between ROLLUP and CUBE_.mht	/Dep806/Autumn 2011/DWH/Ask Tom _Difference Between ROLLUP and CUBE_.mht	601159
8	dataload	/Dep806/Autumn 2011/DWH/dataload	10001199
9	companies.ctl	/Dep806/Autumn 2011/DWH/dataload/companies.ctl	600
10	list-of-all-companies.csv	/Dep806/Autumn 2011/DWH/dataload/list-of-all-companies.csv	1555421
 */

-- 3. Добавить в запрос: сколько процентов директория занимает места относительно всех среди
-- своих соседей (siblings)
-- ID, Название, Путь до корня, total_size, ratio

select ID,
       NAME,
       PATH,
       FILE_SIZE,
       FILE_SIZE * 100 / case when DIR_SIZE = 0 then 1 else DIR_SIZE end RATIO
from (
         select ID,
                NAME,
                PATH,
                FILE_SIZE,
                sum(FILE_SIZE) over ( partition by PARENT_ID) DIR_SIZE,
                PARENT_ID
         from (
                  select ID,
                         NAME,
                         SYS_CONNECT_BY_PATH(NAME, '/') as PATH,
                         PARENT_ID,
                         FILE_SIZE + (
                             select nvl(sum(FILE_SIZE), 0)
                             from FILE_SYSTEM FS_SUB
                             start with FS_SUB.PARENT_ID = FS.ID
                             connect by FS_SUB.PARENT_ID = prior FS_SUB.ID
                         )                                 FILE_SIZE
                  from FILE_SYSTEM FS
                  start with FS.PARENT_ID is null
                  connect by PARENT_ID = prior ID
              )
     );

/*
1	231854.pdf	/Dep806/231854.pdf	7507850	0.006807187200095411209883052897996867086787
2	244020.pdf	/Dep806/244020.pdf	6321866	0.005731884003532086667125546740018495593609
3	Autumn 2011	/Dep806/Autumn 2011	12394850	0.0112381126776777116797510549908078168864
42	Autumn 2013	/Dep806/Autumn 2013	77715032	0.0704623522160678869755283033392408294717
3418	Autumn 2014	/Dep806/Autumn 2014	4762452	0.004318000798560012694357234766300735317195
3506	Autumn 2015	/Dep806/Autumn 2015	25459434	0.0230834570811182849152390808254107327401
4651	Autumn 2016	/Dep806/Autumn 2016	200107727	0.1814328679814576636906727826526789861089
6268	Autumn 2017	/Dep806/Autumn 2017	44922987	0.0407305929255982147407354511420747066311
7127	Autumn 2018	/Dep806/Autumn 2018	42298147	0.038350713565954849574922411114799508458

 */