alter session set "_ORACLE_SCRIPT"= true;

create table FACT_POST
(
    ID         number,
    TAG_ID     number,
    POST_ID    number,
    DATE_ID    number,
    VIEW_COUNT int,
    POST_COUNT int,
    constraint PK_FACTS primary key (ID)
);

create table DIM_TAGS
(
    ID       number            not null,
    TAG_TEXT varchar2(35 char) not null,
    constraint PK_DIM_TAGS primary key (ID)
);

create table DIM_POSTS
(
    ID          number,
    BODY        clob,
    IS_QUESTION char(1),
    constraint PK_DIM_POSTS PRIMARY KEY (ID)
);

create table DIM_DATE
(
    ID         number,
    DATE_VALUE date,
    DATE_LEVEL char,
    constraint PK_DIM_DATE primary key (ID)
);

alter table FACT_POST
    add constraint FK_FACT_POST_TAG_ID foreign key (TAG_ID) REFERENCES DIM_TAGS (id);
alter table fact_post
    add constraint FK_FACT_POST_POST_ID foreign key (POST_ID) references DIM_POSTS (id);
alter table fact_post
    add constraint FK_FACT_POST_DATE_ID foreign key (DATE_ID) references DIM_DATE (id);

select P.id,
       POSTTYPEID,
       CREATIONDATE,
       SCORE,
       VIEWCOUNT,
       BODY,
       T.ID as TAG_ID,
       T.TAGNAME
from SO_RU.POSTS p
         left join SO_RU.POSTTAGS PT on PT.POSTID = P.ID
         left join SO_RU.TAGS T on T.ID = PT.TAGID;

insert into DIM_POSTS(id, body, is_question)
select P.ID,
       Null,
       case when POSTTYPEID = 1 then 'q' else 'a' end
from SO_RU.POSTS P
where POSTTYPEID = 1
   or POSTTYPEID = 2;
commit;

select count(*)
from (
         select post_id, date_id, SCORE, VIEWCOUNT
         from post_stg
         group by post_id, date_id, SCORE, VIEWCOUNT
     );
-- 493 571
select count(distinct id)
from SO_RU.POSTS;
-- 496 698

-- Date
create sequence SEQ_DIM_PERIOD;
select min(CREATIONDATE), max(CREATIONDATE)
from SO_RU.POSTS;
-- 2010-10-10 15:56:41,	2018-09-02 02:53:38

declare
    cdate DATE := TO_DATE('01.10.2010', 'DD.MM.YYYY');
begin
    loop
        insert into dim_date(id, DATE_VALUE, date_level)
        values (SEQ_DIM_PERIOD.nextval, cdate, 'd');
        cdate := cdate + 1;
        exit when cdate >= TO_DATE('01.12.2018', 'DD.MM.YYYY');
    end loop;
end;
select count(*)
from dim_date;
commit;

drop table POST_STG;

create table POST_STG as
select *
from (
         with tag_SYNONYMS_table as (
             select t1.id      as source_id,
                    t1.TAGNAME as source_name,
                    t2.id      as target_id,
                    t2.tagname as target_name
             from SO_RU.TAGSYNONYMS ts
                      inner join SO_RU.tags t1 on ts.SOURCETAGNAME = t1.TAGNAME
                      inner join SO_RU.tags t2 on ts.TARGETTAGNAME = t2.TAGNAME
             where t1.TAGNAME != 'php')

         select p.ID                                 as post_id,
                d.id                                 as date_id,
                p.CREATIONDATE,
                p.SCORE,
                p.VIEWCOUNT,
                coalesce(tst.target_id, t.id)        as tag_id,
                coalesce(tst.target_name, t.TAGNAME) as TAGNAME
         from SO_RU.POSTS p
                  left join SO_RU.POSTTAGS pt on p.ID = pt.POSTID
                  left join SO_RU.TAGS t on pt.TAGID = t.ID
                  left join tag_SYNONYMS_table tst on tst.source_name = t.TAGNAME
                  left join dim_date d on d.date_value = trunc(p.CREATIONDATE, 'day')
         where POSTTYPEID = 1
            or POSTTYPEID = 2
     );

-- Tags
delete
from DIM_TAGS;

insert into DIM_TAGS (id, tag_text)
select nvl(tag_id, -1), nvl(TAGNAME, 'noname')
from post_stg
group by tag_id, TAGNAME;
commit;

-- Month
insert into dim_date (id, DATE_VALUE, date_level)
select SEQ_DIM_PERIOD.nextval, date_value, 'm'
from (
         select trunc(d.date_value, 'month') as date_value
         from dim_date d
         group by trunc(d.date_value, 'month')
     );
-- Year
insert into dim_date (id, DATE_VALUE, date_level)
select SEQ_DIM_PERIOD.nextval, date_value, 'y'
from (
         select trunc(d.date_value, 'year') as date_value
         from dim_date d
         group by trunc(d.date_value, 'year')
     );
commit;

create sequence SEQ_fast_post;

insert into fact_post (fact_post.id, fact_post.tag_id, fact_post.post_id,
                       fact_post.date_id, fact_post.VIEW_COUNT, fact_post.POST_COUNT)
select SEQ_fast_post.nextval,
       tag_id,
       post_id,
       date_id,
       VIEWCOUNT,
       1
from (select tag_id, post_id, date_id, VIEWCOUNT
      from post_stg
      group by tag_id, post_id, date_id, VIEWCOUNT
     );
select count(*)
from fact_post;
commit;

update fact_post
set tag_id = -1
where tag_id is null;
commit;

select *
from post_stg;
update post_stg
set tag_id  = -1,
    TAGNAME = 'noname'
where tag_id is null;
update post_stg
set VIEWCOUNT = 0
where VIEWCOUNT is null;

-- из-за тегов количество просмотров и количество постов умножается на количестов тегов :(
create view post_stg_grouped as
select post_id,
       date_id,
       CREATIONDATE,
       avg(VIEWCOUNT)
           as VIEWCOUNT,
       1   as postcount
from post_stg
group by post_id, date_id, CREATIONDATE;

-- group by tag
insert into fact_post (fact_post.id, fact_post.tag_id, fact_post.post_id,
                       fact_post.date_id, fact_post.VIEW_COUNT, fact_post.POST_COUNT)
select SEQ_fast_post.nextval, tag_id, null, null, viewcount, POSTCOUNT
from (
         select tag_id,
                SUM(p.view_count) as viewcount,
                sum(p.POST_COUNT) as POSTCOUNT
         from fact_post p
         group by tag_id
     );
commit;

-- group by tag and date month
insert into fact_post (fact_post.id, fact_post.tag_id, fact_post.post_id,
                       fact_post.date_id, fact_post.VIEW_COUNT, fact_post.POST_COUNT)
select SEQ_fast_post.nextval, tag_id, null, date_id, viewcount, POSTCOUNT
from (
         select tag_id,
                d2.id             as date_id,
                SUM(p.VIEW_COUNT) as viewcount,
                sum(p.POST_COUNT) as POSTCOUNT
         from fact_post p
                  inner join dim_date dd on p.date_id = dd.id
                  inner join dim_date d2 on d2.date_value = trunc(dd.date_value, 'month')
         where d2.date_level = 'm'
         group by tag_id, d2.id
     );
commit;

-- group by tag and date year
insert into fact_post (fact_post.id, fact_post.tag_id, fact_post.post_id,
                       fact_post.date_id, fact_post.VIEW_COUNT, fact_post.POST_COUNT)
select SEQ_fast_post.nextval, tag_id, null, date_id, viewcount, POSTCOUNT
from (
         select tag_id,
                d2.id             as date_id,
                SUM(p.VIEW_COUNT) as viewcount,
                sum(p.POST_COUNT) as POSTCOUNT
         from fact_post p
                  inner join dim_date dd on p.date_id = dd.id
                  inner join dim_date d2 on d2.date_value = trunc(dd.date_value, 'year')
         where d2.date_level = 'y'
         group by tag_id, d2.id
     );
commit;

-- group by date month
insert into fact_post (fact_post.id, fact_post.tag_id, fact_post.post_id,
                       fact_post.date_id, fact_post.VIEW_COUNT, fact_post.POST_COUNT)
select SEQ_fast_post.nextval, null, null, date_id, viewcount, POSTCOUNT
from (
         select dd.id as date_id, sum(VIEWCOUNT) as viewcount, sum(postcount) as POSTCOUNT
         from post_stg_grouped
                  inner join dim_date dd on trunc(CREATIONDATE, 'month') = dd.date_value
         where dd.date_level = 'm'
         group by dd.id
     );
commit;

-- !group by date year
insert into fact_post (fact_post.id, fact_post.tag_id, fact_post.post_id,
                       fact_post.date_id, fact_post.VIEW_COUNT, fact_post.POST_COUNT)
select SEQ_fast_post.nextval, null, null, date_id, viewcount, POSTCOUNT
from (
         select dd.id as date_id, sum(VIEWCOUNT) as viewcount, sum(postcount) as POSTCOUNT
         from post_stg_grouped
                  inner join dim_date dd on trunc(CREATIONDATE, 'year') = dd.date_value
         where dd.date_level = 'y'
         group by dd.id
     );
commit;

-- all summ
insert into fact_post (fact_post.id, fact_post.tag_id, fact_post.post_id,
                       fact_post.date_id, fact_post.VIEW_COUNT, fact_post.POST_COUNT)
select SEQ_fast_post.nextval, null, null, null, viewcount, POSTCOUNT
from (
         select sum(VIEWCOUNT) as viewcount, sum(postcount) as POSTCOUNT
         from post_stg_grouped
     );
commit;

select trunc(CREATIONDATE, 'year'), sum(VIEWCOUNT), sum(postcount)
from post_stg_grouped
group by trunc(CREATIONDATE, 'year');

select date_value, VIEW_COUNT, post_count
from fact_post
         inner join dim_date dd on fact_post.date_id = dd.id
where date_level = 'y'
  and tag_id is null
  and post_id is null
order by DATE_VALUE;
;

-- raw data
select trunc(CREATIONDATE, 'year') as date_value,
       sum(VIEWCOUNT)              as VIEWCOUNT,
       sum(1)                      as postcount
from SO_RU.POSTS
where POSTTYPEID = 1
   or POSTTYPEID = 2
group by trunc(CREATIONDATE, 'year')
order by trunc(CREATIONDATE, 'year');

select trunc(CREATIONDATE, 'month') as date_value,
       sum(VIEWCOUNT)               as VIEWCOUNT,
       sum(1)                       as postcount
from SO_RU.POSTS
where POSTTYPEID = 1
   or POSTTYPEID = 2
group by trunc(CREATIONDATE, 'month')
order by trunc(CREATIONDATE, 'month');


select Count(*) from FACT_POST;