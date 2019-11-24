package org.khomenko.maga.wildcards

object Logic {

  //obtain all commetns from entities
  def getComments(entities: Seq[Entity]): Seq[Comment] = {
    entities.collect {
      case x: Comment => x
    }
  }

  //split entities by type
  def splitEntities(entities: Seq[Entity]): (Seq[User], Seq[Post], Seq[Comment], Seq[Vote], Seq[Badge], Seq[Tag]) = {
    val grouped = entities.groupBy(_.getClass)
    (
      grouped.get(classOf[User]).orNull.asInstanceOf[Seq[User]],
      grouped.get(classOf[Post]).orNull.asInstanceOf[Seq[Post]],
      grouped.get(classOf[Comment]).orNull.asInstanceOf[Seq[Comment]],
      grouped.get(classOf[Badge]).orNull.asInstanceOf[Seq[Vote]],
      grouped.get(classOf[Badge]).orNull.asInstanceOf[Seq[Badge]],
      grouped.get(classOf[Tag]).orNull.asInstanceOf[Seq[Tag]]
    )
  }

  //populate fields owner, lastEditor, tags with particular users from Seq[Post] and tags from Seq[Tag]
  def enreachPosts(posts: Seq[Post], users: Seq[User], tags: Seq[Tag]): Seq[EnreachedPost] = {
    posts.map(post => {
      EnreachedPost(post, users.map {
        case user: User if post.ownerUserId == user.id => user
        case _ => null
      }.find(user => user != null).orNull,
        users.map {
          case user: User if post.lastEditorUserId == user.id => user
          case _ => null
        }.find(user => user != null).orNull,
        tags.filter(tag => {
          tag match {
            case tag: Tag if post.tags.contains(tag.tagName) => true
            case _ => false
          }
        }))
    })
  }

  //populate fields comments and owner with particular post from Seq[Post] and user from Seq[User]
  def enreachComments(comments: Seq[Comment], posts: Seq[Post], users: Seq[User]): Seq[EnreachedComment] = {
    comments.map(comment => {
      EnreachedComment(comment, posts.map {
        case post: Post if comment.postId == post.id => post
        case _ => null
      }.find(_ != null).orNull,
        users.map {
          case user: User if comment.userId == user.id => user
          case _ => null
        }.find(_ != null).orNull)
    })
  }

  //find all links (like http://example.com/examplePage) in aboutMe field
  def findAllUserLinks(users: Seq[User]): Seq[(User, Seq[String])] = {
    users.map(user => {
      (user,
        user.about.split(Array(' ', '<', '>')).map(word => {
          val linkRegex = ".*(http[s]?://.*)[\"]?.*".r
          word match {
            case linkRegex(link) => link
            case _ => null
          }
        }).filter(_ != null)
      )
    })
  }

  //find all users with the reputation bigger then reputationLImit with particular badge
  def findTopUsersByBadge(users: Seq[User], badges: Seq[Badge], badgeName: String, reputationLimit: Int): Seq[User] = {
    users.filter(_.reputation > reputationLimit).sortBy(_.reputation)(Ordering.Int.reverse)
      .filter(user => {
        badges.map {
          case badge: Badge if badge.userId == user.id && badge.name == badgeName => badge
          case _ => null
        }.exists(_ != null)
      })
  }
}

case class EnreachedPost(
                          post: Post,
                          owner: User,
                          lastEditor: User,
                          tags: Seq[Tag]
                        )

case class EnreachedComment(
                             comment: Comment,
                             post: Post,
                             owner: User
                           )
