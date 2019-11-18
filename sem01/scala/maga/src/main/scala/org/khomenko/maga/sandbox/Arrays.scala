package org.khomenko.maga.sandbox

object Arrays extends App {
  println("Arrays")

  val someInts = new Array[Int](10)
  for (i <- someInts.indices) {
    someInts(i) = i
  }

  for (i <- 0 until someInts.length) {
    someInts(i) += i
  }

  someInts.foreach(println)
}
