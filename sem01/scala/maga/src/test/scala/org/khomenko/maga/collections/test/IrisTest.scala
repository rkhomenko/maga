package org.khomenko.maga.collections.test

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

import org.khomenko.maga.collections.Iris
import org.khomenko.maga.collections.IrisDatasetHelper
import org.khomenko.maga.collections.IrisUtils._

import scala.io.Source

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IrisTest {
  val delta = 1e-6
  var datasetHelper: IrisDatasetHelper = null

  @BeforeAll def loadDataset(): Unit = {
    var dataset: List[Iris] = List()
    Source.fromResource("iris.data").getLines()
      .foreach(x => dataset = x.toIris.getOrElse(null) :: dataset)

    datasetHelper = new IrisDatasetHelper(dataset)
  }

  @Test def avgSepalLengthTest: Unit = {
    val expected = 5.843333333333334;
    val result = datasetHelper.getAverage(iris => iris.sepalLength)
    Assertions.assertEquals(expected, result, delta)
  }

  @Test def avgPetalSquareTest: Unit = {
    val expected = 5.793133333333332;
    val result = datasetHelper.getAverage(iris => iris.getPetalSquare)
    Assertions.assertEquals(expected, result, delta)
  }

  @Test def avgPetalSquareForSepalWithGTFourTest: Unit = {
    val expected = 0.3433333333333333;
    val result = datasetHelper.getAverageWithFilter(iris => iris.getPetalSquare,
      iris => iris.sepalWidth > 4)
    Assertions.assertEquals(expected, result, delta)
  }

  @Test def groupByPetalSizeTest: Unit = {
    val expectedSizes = List(50, 13, 87);
    val result = datasetHelper.getGroupedBy(iris => iris.getPetalSize)
    val resultSizes = result.map(l => l._2.size)
    Assertions.assertEquals(expectedSizes, resultSizes)
  }

  @Test def maxFromGroupedBySepalWidthTest: Unit = {
    val expected = Map("Iris-setosa" -> 4.4,
      "Iris-versicolor" -> 3.4,
      "Iris-virginica" -> 3.8);
    val result = datasetHelper.maxFromGroupedBy(iris => iris.species,
      iris => iris.sepalWidth)
    Assertions.assertEquals(expected, result)
  }
}