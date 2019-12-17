package org.khomenko.maga.rdbms

object Logic {

  //split entities by type
  def splitEntities(entities: Seq[Entity]): (Seq[User], Seq[Post], Seq[Comment]) = {
    entities.foldLeft((Seq[User](), Seq[Post](), Seq[Comment]())) { case((users, posts, comments), entity) =>
      entity match {
        case u: User => (u+:users, posts, comments)
        case p: Post => (users, p+:posts, comments)
        case c: Comment => (users, posts, c+:comments)
      }
    }
  }

}