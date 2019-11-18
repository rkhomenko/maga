package org.khomenko.maga.wildcards

object Logic {

  //obtain all commetns from entities
  def getComments(entities: Seq[Entity]): Seq[Comment] = {
    entities.collect{
      case x: Comment => x
    }
  }

  //split entities by type
  def splitEntities(entities: Seq[Entity]): (Seq[User], Seq[Post], Seq[Comment], Seq[Vote], Seq[Badge], Seq[Tag]) = {
    (Seq(), Seq(), Seq(), Seq(), Seq(), Seq())
  }

  //populate fields owner, lastEditor, tags with particular users from Seq[Post] and tags from Seq[Tag]
  def enreachPosts(posts: Seq[Post], users: Seq[User], tags: Seq[Tag]): Seq[EnreachedPost] = {
    Seq()
  }

  //populate fields post and owner with particular post from Seq[Post] and user from Seq[User]
  def enreachComments(comments: Seq[Comment],posts: Seq[Post], users: Seq[User]): Seq[EnreachedComment] = {
    Seq()
  }

  //find all links (like http://example.com/examplePage) in aboutMe field
  def findAllUserLinks(users: Seq[User]): Seq[(User, Seq[String])] = {
    Seq[(User, Seq[String])]()
  }

  //find all users with the reputation bigger then reputationLImit with particular badge
  def findTopUsersByBadge(users: Seq[User], basges: Seq[Badge], badgeName: String, reputationLimit: Int): Seq[User] = {
    Seq()
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
