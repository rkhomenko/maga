package org.khomenko.maga.rdbms

import java.time.LocalDateTime

import scala.xml.XML

object DataLoader {

  private[this] val filesToFunc = Map[String, String => Seq[Entity]](
    "Users.xml" -> loadUsers,
    "Posts.xml" -> loadPosts,
    "Comments.xml" -> loadComments
  )

  def loadData(basePath: String): Seq[Entity] = {
    filesToFunc
      .flatMap{
        case (file, func) =>
          val path = basePath+"/"+file
          func(path)
      }
      .toSeq
  }

  private[this] def loadUsers(path: String): Seq[User] = {
    val usersXml = XML.load(path)

    for(
      userRow <- usersXml \\ "row"
    ) yield {
      User(
        (userRow \@ "Id").toInt,
        userRow \@ "DisplayName",
        userRow \@ "location",
        userRow \@ "AboutMe",
        matchInt(userRow \@ "Reputation"),
        matchInt(userRow \@ "Views"),
        matchInt(userRow \@ "UpVotes"),
        matchInt(userRow \@ "DownVotes"),
        matchInt(userRow \@ "AccountId"),
        parseDate(userRow \@ "CreationDate"),
        parseDate(userRow \@ "LastAccessDate")
      )
    }
  }

  private[this] def loadPosts(path: String): Seq[Post] = {
    val postsXml = XML.load(path)

    for(
      postRow <- postsXml \\ "row"
    ) yield {
      Post(
        matchInt(postRow \@ "Id"),
        postRow \@ "Title",
        postRow \@ "Body",
        matchInt(postRow \@ "Score"),
        matchInt(postRow \@ "ViewCount"),
        matchInt(postRow \@ "AnswerCount"),
        matchInt(postRow \@ "CommentCount"),
        matchInt(postRow \@ "OwnerUserId"),
        matchInt(postRow \@ "LastEditorUserId"),
        matchInt(postRow \@ "AcceptedAnswerId"),
        parseDate(postRow \@ "CreationDate"),
        parseDate(postRow \@ "LastEditDate"),
        parseDate(postRow \@ "LastActivityDate"),
        (postRow \@ "Tags").stripPrefix("&lt;").stripSuffix("&gt;").split("&gt;&lt;").toSeq
      )
    }
  }

  private[this] def loadComments(path: String): Seq[Comment] = {
    val commentsXml = XML.load(path)

    for(
      row <- commentsXml \\ "row"
    ) yield {
      Comment(
        matchInt(row \@ "Id"),
        matchInt(row \@ "PostId"),
        matchInt(row \@ "Score"),
        row \@ "Text",
        parseDate(row \@ "CreationDate"),
        matchInt(row \@ "UserId")
      )
    }
  }

  private[this] def matchInt(s: String): Int = {
    val intMatch = "(\\d+)".r
    s match {
      case intMatch(i) => i.toInt
      case _ => Int.MinValue
    }
  }

  private[this] def parseDate(s: String): LocalDateTime = {
    if(s == "")
      null
    else
      LocalDateTime.parse(s)
  }
}

abstract class Entity(id: Int)

case class User(
                 id: Int,
                 displayName: String,
                 location: String,
                 about: String,
                 reputation: Int,
                 views: Int,
                 upVotes: Int,
                 downVotes: Int,
                 accountId: Int,
                 creationDate: LocalDateTime,
                 lastAccessDate: LocalDateTime) extends Entity(id)

case class Post(
                 id: Int,
                 title: String,
                 body: String,
                 score: Int,
                 viewCount: Int,
                 answerCount: Int,
                 commentCount: Int,
                 ownerUserId: Int,
                 lastEditorUserId: Int,
                 acceptedAnswerId: Int,
                 creationDate: LocalDateTime,
                 lastEditDate: LocalDateTime,
                 lastActivityDate: LocalDateTime,
                 tags: Seq[String]) extends Entity(id)

case class Comment(
                    id: Int,
                    postId: Int,
                    score: Int,
                    text: String,
                    creationDate: LocalDateTime,
                    userId: Int) extends Entity(id)