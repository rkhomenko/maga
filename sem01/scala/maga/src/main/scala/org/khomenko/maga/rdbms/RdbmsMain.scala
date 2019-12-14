package org.khomenko.maga.rdbms

import java.io.PrintWriter

import scopt.OptionParser
import org.log4s._
import scalikejdbc._
import scalikejdbc.config._

case class Config(
                   commandLoad: String = "",
                   commandClear: String = "",
                   commandInit: String = "",
                   commandExtract: String = "",
                   file: String = "",
                   append: Boolean = false,
                   dropTables: Boolean = false,
                   force: Boolean = false,
                   query: String = "",
                   extractFile: String = ""
                 )

object RdbmsMain {
  private[this] val logger = getLogger

  val db = Symbol("so")

  def main(args: Array[String]): Unit = {
    val parser = new OptionParser[Config]("StackOverflowLoader") {

      // название утилиты при распечатке
      head("StackOverflowLoader", "1.0")

      //обрабатывает команду load и нужные команде аргументы --file и --append
      cmd("load")
        .action((_, c) => c.copy(commandLoad = "load")) //копируем команду в объект конфигурации
        .text("Load - это команда загрузки данных из файла") //описание команды
        .children( // разбор сопутствующих аргументов
          opt[String]("file") // проверяем наличие аргумента --file. Должен иметь тип String
            .required() // аргумент обязателен
            .action((f, c) => c.copy(file = f)) // при наличии копируем в объект конфигурации
            .text("Путь к файлу с данными"), // описание аргумента
          opt[Unit]("append") // аргумент без значения (флаг) --append
            .abbr("a") // сокрщённая форма -a
            .action((f, c) => c.copy(append = true)) // при наличии копируем в объект конфигурации
            .text("Не удалять данные при загрозке. По умолчанию данные будут перезатираться") // описание аргумента
        )
      cmd("clean")
        .action((_, c) => c.copy(commandClear = "clean"))
        .text("Удалить данные из базы данных")
        .children(
          opt[Unit]("dropTables")
            .abbr("dt")
            .action((_, c) => c.copy(dropTables = true))
            .text("Удалить таблицы")
        )
      cmd("init")
        .action((_, c) => c.copy(commandInit = "init"))
        .text("Создать таблицы")
        .children(
          opt[Unit]("force")
            .abbr("f")
            .action((_, c) => c.copy(force = true))
            .text("Пересоздать таблицы, если существуют")
        )
      cmd("extract")
        .action((_, c) => c.copy(commandExtract = "extract"))
        .text("Выгрузить данные")
        .children(
          opt[String]("query")
            .abbr("q")
            .action((q, c) => c.copy(query = q)),
          opt[String]("file")
            .abbr("f")
            .action((f, c) => c.copy(extractFile = f))
        )

      // дополнительные проверки целостности команд
      // в данном случае проверяем, что задана хотя бы одна команда
      checkConfig { c =>
        if (c.commandInit.isEmpty && c.commandLoad.isEmpty && c.commandClear.isEmpty && c.commandExtract.isEmpty) {
          failure("Нужно указать хотя бы одну комманду")
        } else {
          success
        }
      }
    }

    parser.parse(args, Config()) match {
      case Some(config) =>

        DBs.setup(db)

        if (!config.commandClear.isEmpty) {
          if (config.dropTables) {
            dropTables()
          } else {
            clearData()
          }
        }

        if (!config.commandInit.isEmpty) {
          if (config.force) {
            dropTables()
          }
          createTables()
        }

        if (!config.commandLoad.isEmpty) {
          if (!config.append) {
            clearData()
          }

          loadDataToDB(Logic.splitEntities(DataLoader.loadData(config.file)))
        }

        if (!config.commandExtract.isEmpty) {
          new PrintWriter(config.extractFile) {
            extractDataFromDb(config.query).foreach(s => println(s));
            close()
          }
        }

        DBs.closeAll()

      case None =>
      // если командная строка не соответствует логике описанной в парсере
      // будет распечатана справка по использованию приложения
    }
  }

  def createTables(): Unit = NamedDB(db).autoCommit { implicit session =>
    sql"""
         create table users (
            id integer primary key ,
            display_name varchar2(100),
            location varchar2(100),
            about varchar2(10000),
            reputation integer,
            views integer,
            up_votes integer,
            down_votes integer,
            account_id integer,
            creation_date timestamp,
            last_access_date timestamp
         );

         create table posts (
            id integer primary key ,
            title varchar2(1000),
            body varchar2(50000),
            score integer,
            view_count integer,
            answer_count integer,
            comment_count integer,
            owner_user_id integer,
            last_editor_user_id integer,
            accepted_answer_id integer,
            creation_date timestamp,
            last_edit_date timestamp,
            last_activity_date timestamp
         );

         create table comments(
            id integer primary key,
            post_id integer,
            score integer,
            text varchar2(10000),
            creation_date timestamp,
            user_id integer
        );
       """.execute.apply()
  }

  def dropTables(): Unit = NamedDB(db).autoCommit { implicit session =>
    sql"""
          drop table if exists users;
          drop table if exists posts;
          drop table if exists comments;
      """.execute.apply()
  }

  def clearData(): Unit = NamedDB(db).autoCommit { implicit session =>
    sql"""
          truncate table users;
          truncate table posts;
          truncate table comments;
      """.execute.apply()
  }

  def loadDataToDB(tuple: (Seq[User], Seq[Post], Seq[Comment])): Unit = {
    insertUsers(tuple._1)
    insertPosts(tuple._2)
    insertComments(tuple._3)
  }

  def extractDataFromDb(query: String): List[String] = NamedDB(db) readOnly {
    implicit session => {
      session
        .list(query) {
          rs =>
            if (rs.row == 1) {
              List(
                (1 to rs.metaData.getColumnCount).map(i => {
                  rs.metaData.getColumnName(i)
                }).mkString(","),
                (1 to rs.metaData.getColumnCount).map(i => {
                  if (rs.metaData.getColumnName(i) == "VARCHAR2") {
                    s"""${rs.string(i)}"""
                  } else {
                    rs.string(i)
                  }
                }).mkString(",")
              )
            }else {
              List((1 to rs.metaData.getColumnCount).map(i => {
                if (rs.metaData.getColumnName(i) == "VARCHAR2") {
                  s"""${rs.string(i)}"""
                } else {
                  rs.string(i)
                }
              }).mkString(","))
            }
        }.flatten
    }
  }

  def insertUsers(users: Seq[User]): Unit = NamedDB(db).autoCommit {
    implicit session =>
      val u = User.column
      users.foreach {
        user =>
          withSQL(
            insert.into(User).namedValues(
              u.id -> user.id,
              u.displayName -> user.displayName,
              u.location -> user.location,
              u.about -> user.about,
              u.reputation -> user.reputation,
              u.views -> user.views,
              u.upVotes -> user.upVotes,
              u.downVotes -> user.downVotes,
              u.accountId -> user.accountId,
              u.creationDate -> user.creationDate,
              u.lastAccessDate -> user.lastAccessDate
            )
          ).update().apply()
      }
  }

  def insertPosts(posts: Seq[Post]): Unit = NamedDB(db).autoCommit {
    implicit session => {
      val p = Post.column
      posts.foreach {
        post =>
          withSQL {
            insert.into(Post).namedValues(
              p.id -> post.id,
              p.title -> post.title,
              p.body -> post.body,
              p.score -> post.score,
              p.viewCount -> post.viewCount,
              p.answerCount -> post.answerCount,
              p.commentCount -> post.commentCount,
              p.ownerUserId -> post.ownerUserId,
              p.lastEditorUserId -> post.lastEditorUserId,
              p.acceptedAnswerId -> post.acceptedAnswerId,
              p.creationDate -> post.creationDate,
              p.lastEditDate -> post.lastEditDate,
              p.lastActivityDate -> post.lastActivityDate
            )
          }.update().apply()
      }
    }
  }

  def insertComments(comments: Seq[Comment]): Unit = NamedDB(db).autoCommit {
    implicit session => {
      val c = Comment.column
      comments.foreach {
        comment =>
          withSQL {
            insert.into(Comment).namedValues(
              c.id -> comment.id,
              c.postId -> comment.postId,
              c.score -> comment.score,
              c.text -> comment.text,
              c.creationDate -> comment.creationDate,
              c.userId -> comment.userId
            )
          }.update().apply()
      }
    }
  }

  object User extends SQLSyntaxSupport[User] {
    override val tableName: String = "users"

    override def connectionPoolName: Any = Symbol("so")

    def apply(user: ResultName[User])(rs: WrappedResultSet): User = {
      new User(
        rs.int(user.id),
        rs.string(user.displayName),
        rs.string(user.location),
        rs.string(user.about),
        rs.int(user.reputation),
        rs.int(user.views),
        rs.int(user.upVotes),
        rs.int(user.downVotes),
        rs.int(user.accountId),
        rs.localDateTime(user.creationDate),
        rs.localDateTime(user.lastAccessDate)
      )
    }
  }

  object Post extends SQLSyntaxSupport[Post] {
    override val tableName: String = "posts"

    override def connectionPoolName: Any = Symbol("so")

    def apply(post: ResultName[Post])(rs: WrappedResultSet): Post = {
      new Post(
        rs.int(post.id),
        rs.string(post.title),
        rs.string(post.body),
        rs.int(post.score),
        rs.int(post.viewCount),
        rs.int(post.answerCount),
        rs.int(post.commentCount),
        rs.int(post.ownerUserId),
        rs.int(post.lastEditorUserId),
        rs.int(post.acceptedAnswerId),
        rs.localDateTime(post.creationDate),
        rs.localDateTime(post.lastEditDate),
        rs.localDateTime(post.lastActivityDate),
        Seq()
      )
    }
  }

  object Comment extends SQLSyntaxSupport[Comment] {
    override val tableName: String = "comments"

    override def connectionPoolName: Any = Symbol("so")

    def apply(comment: ResultName[Comment])(rs: WrappedResultSet): Comment = {
      new Comment(
        rs.int(comment.id),
        rs.int(comment.postId),
        rs.int(comment.score),
        rs.string(comment.text),
        rs.localDateTime(comment.creationDate),
        rs.int(comment.userId)
      )
    }
  }

}
