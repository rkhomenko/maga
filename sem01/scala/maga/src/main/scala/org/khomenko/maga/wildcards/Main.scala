package org.khomenko.maga.wildcards

object Main extends App {
  val loader = new DataLoader {
    override def basePath: String = "/home/rk/source/repos/maga/sem01/scala/maga/src/test/resources/stackoverflow"
  }

  val entities = loader.loadData

  println("************************ Comments ************************")
  val cmts = Logic.getComments(entities)
  cmts take(10) foreach println

  val (users, posts, comments, votes, badges, tags) = Logic.splitEntities(entities)

  println("************************ Enreached posts ************************")
  val reachPosts = Logic.enreachPosts(posts, users, tags);
  reachPosts take(10) foreach println

  println("************************ Enreached comments ************************")
  val reachComments = Logic.enreachComments(comments, posts, users)
  reachComments take(10) foreach println

  println("************************ User links ************************")
  val userLinks = Logic.findAllUserLinks(users)
  userLinks take(10) foreach println

  println("************************ Top users by badge ************************")
  val topUsersByBadge = Logic.findTopUsersByBadge(users, badges, "Student", 100)
  topUsersByBadge take(10) foreach println
}
