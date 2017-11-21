import scala.util.Random

object AcoSolver {
  def genRandomeEgeToPheromone(random: Random, allCityNames: Seq[CityName]): Map[(CityName, CityName), Double] =
    (for{
      c1 <- allCityNames
      c2 <- allCityNames
      if c1 != c2
    } yield {
      ((c1, c2), random.nextDouble())
    }).toMap


  def assessment(edgeToPheromone: Map[(CityName, CityName), Double],
                 edgeToDistance: Map[(CityName, CityName), Double],
                 alpha: Double,
                 beta: Double,
                 notVisied: Seq[CityName]
                )
                (i: CityName,
                 j: CityName
                ): Double =
  {
    val numerator  : Double = Math.pow(edgeToPheromone(i, j), alpha) * Math.pow(1.0/edgeToDistance(i, j), beta)
    val denominator: Double = notVisied.map{l => l
      Math.pow(edgeToPheromone(i, l), alpha) * Math.pow(1.0/edgeToDistance(i, l), beta)
    }.sum
    numerator / denominator
  }


  def probability(edgeToPheromone: Map[(CityName, CityName), Double],
                  edgeToDistance: Map[(CityName, CityName), Double],
                  notVisied: Seq[CityName],
                  alpha: Double,
                  beta: Double
                 )
                 (i: CityName
                 ): Map[CityName, Double] =
    (for{
      m <- notVisied
    } yield {
      val ass     : (CityName, CityName) => Double = assessment(edgeToPheromone, edgeToDistance, alpha, beta, notVisied)
      val mAsses  : Double                         = ass(i, m)
      val sumAsses: Double                         = notVisied.map{n => ass(i, n)}.sum
      (m, mAsses / sumAsses)
    }).toMap


  def deltaPhero(q                   : Double,
                 visitedCityNames    : Seq[CityName],
                 edgeToDistance  : Map[(CityName, CityName), Double],
                 edgeToPheromone     : Map[(CityName, CityName), Double],
                 edgeToIsVisited     : Map[(CityName, CityName), Boolean]
                ): Map[(CityName, CityName), Double] = {

    val visitedDistance: Double = calcVisitedDistance(visitedCityNames, edgeToDistance)

    (for(i <- edgeToPheromone.keys) yield {
      val v =
        if(edgeToIsVisited(i))
          q / visitedDistance
        else
          0
      (i, v)
    }).toMap
  }

  def calcVisitedDistance(visitedCityNames  : Seq[CityName],
                          edgeToDistance    : Map[(CityName, CityName), Double]
                         ): Double =

    if(visitedCityNames.length <= 1){
      0
    } else {
      (for((i, j) <- visitedCityNames.zip(visitedCityNames.drop(1)))
        yield edgeToDistance(i, j))
      .sum
    }


  def updatedPhero(ro                 : Double,
                   edgeToPheromone    : Map[(CityName, CityName), Double],
                   edgeToPheromoneSeq : Seq[Map[(CityName, CityName), Double]]
                  ): Map[(CityName, CityName), Double] =
  {
    for((i, pheromon) <- edgeToPheromone) yield {
      (i, ro * pheromon + (1.0 - ro) * edgeToPheromoneSeq.map(k => k(i)).sum)
    }
  }
}
