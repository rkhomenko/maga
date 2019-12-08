package org.khomenko.maga.rdbms

import scopt.OptionParser
import org.log4s._
import scalikejdbc._
import scalikejdbc.config._

case class Config(
                   commandLoad: String = "",
                   commandClear: String = "",
                   commandInit: String = "",
                   file: String = "",
                   append: Boolean = false,
                   dropTables: Boolean = false,
                   forse: Boolean = false
                 )

object RdbmsMain {
  private[this] val logger = getLogger

  val db = Symbol("so")

  def main(args: Array[String]): Unit = {
    val parser = new OptionParser[Config]("IrisLoader") {

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
            .action((_, c) => c.copy(forse = true))
            .text("Пересоздать таблицы, если существуют")
        )

      // дополнительные проверки целостности команд
      // в данном случае проверяем, что задана хотя бы одна команда
      checkConfig { c =>
        if (c.commandInit.isEmpty && c.commandLoad.isEmpty && c.commandClear.isEmpty) failure("Нужно указать хотя бы одну комманду") else success
      }
    }

    parser.parse(args, Config()) match {
      case Some(config) =>

        DBs.setup(db)

        if (!config.commandClear.isEmpty) {
          if (config.dropTables) {
            dropTables
          } else {
            clearData
          }
        }

        if (!config.commandInit.isEmpty) {
          if (config.forse) {
            dropTables
          }
          createTables
        }

        if (!config.commandLoad.isEmpty) {
          if (!config.append) {
            clearData
          }

          loadDataToDB(Logic.splitEntities(DataLoader.loadData(config.file)))
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
            about varchar(400),
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
            title varchar2(100),
            body varchar2(1000),
            score integer,
            viewCount integer,
            answerCount integer,
            commentCount integer,
            ownerUserId integer,
            lastEditorUserId integer,
            acceptedAnswerId integer,
            creationDate timestamp,
            lastEditDate timestamp,
            lastActivityDate timestamp
         );

         create table comments(
            id integer primary key,
            postId integer,
            score integer,
            text varchar2(500),
            creationDate timestamp,
            userId integer
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
          )
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
//        rs.int(user.id),
//        rs.string(user.title),
//        rs.string(user.body),
//        rs.int(user.score),
//        rs.int(user.viewCount),
//        rs.int(user.commentCount),
//        rs.int(user.ownerUserId),
//        rs.int(user.acceptedAnswerId),
//        rs.localDate(user.creationDate),
//        rs.localDate(user.lastEditD)
      )
    }
  }
}
