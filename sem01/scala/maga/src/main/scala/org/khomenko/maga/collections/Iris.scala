package org.khomenko.maga.collections

import scala.util.Try

object PetalSize extends Enumeration {
  type PetalSize = Value
  val Small, Medium, Large = Value
}

case class Iris(sepalLength: Double, sepalWidth: Double,
                petalLength: Double, petalWidth: Double,
                species: String) {
  def getPetalSquare(): Double = petalLength * petalWidth

  def getPetalSize(): PetalSize.PetalSize = {
    val petalSquare = petalLength * petalWidth
    if (petalSquare < 2.0) {
      PetalSize.Small
    } else if (petalSquare < 5.0) {
      PetalSize.Medium
    } else {
      PetalSize.Large
    }
  }
}

object IrisUtils {
  implicit class StringToIris(str: String) {
    def toIris: Option[Iris] = str.split(",") match {
      case Array(a, b, c, d, e) if isDouble(a) && isDouble(b) && isDouble(c) && isDouble(d) =>
        Some(
          Iris(
            a.toDouble,
            b.toDouble,
            c.toDouble,
            d.toDouble,
            e))
      case others => None
    }

    def isDouble(str: String): Boolean = Try(str.toDouble).isSuccess
  }
}