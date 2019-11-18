package org.khomenko.maga.sandbox

object HelloWorld extends App {
    println("Hello, world!")

    val ints = Array(1, 2, 3, 4)
    val appArray = Array(Arrays)

    appArray.foreach(app => app.main(args))
}
