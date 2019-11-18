package org.khomenko.maga.collections

class IrisDatasetHelper(dataset: List[Iris]) {
  def getAverage(func: (Iris) => Double): Double =
    dataset.map(func).sum / dataset.size

  def getAverageWithFilter(func: (Iris) => Double,
                           filter: (Iris) => Boolean): Double = {
    val filtered = dataset.filter(filter)
    filtered.map(func).sum / filtered.size
  }

  def getGroupedBy[C](func: (Iris) => C): Map[C, List[Iris]] =
    dataset.groupBy(func)

  def maxFromGroupedBy[C](groupFunc: (Iris) => C,
                          func: (Iris) => Double): Map[C, Double] =
    dataset.groupBy(groupFunc)
      .flatMap(item => Map(item._1 -> item._2.map(func).max(Ordering.Double.IeeeOrdering)))
}