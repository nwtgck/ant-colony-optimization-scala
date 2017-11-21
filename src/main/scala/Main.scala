import java.io.File

import scala.util.Random
import scala.util.control.Breaks

case class AcoOption(nIters      : Int=100,
                     nAnts       : Int =20,
                     alpha      : Double=1.0,
                     beta       : Double=3.0,
                     q          : Double=100.0,
                     ro         : Double=0.4,
                     outPath    : String="./output",
                     randomSeed : Int=2,
                     tspPath    : String="./tsp/wi29.tsp"
                    )

object Main {
  def main(args: Array[String]): Unit = {

    // Create an option parser
    val optionParser = new scopt.OptionParser[AcoOption]("TSP solver by Ant Colony Optimization") {

      help("help").text("prints this usage text")

      opt[Int]('i', "n-iters") action {(x, c) =>
        c.copy(nIters=x)
      } text ("The number of iteration")

      opt[Int]('a', "n-ants") action {(x, c) =>
        c.copy(nAnts=x)
      } text ("The number of ants")

      opt[Double]("alpha") action {(x, c) =>
        c.copy(alpha=x)
      } text ("alpha")

      opt[Double]("beta") action {(x, c) =>
        c.copy(beta=x)
      } text ("beta")

      opt[Double]("q") action {(x, c) =>
        c.copy(q=x)
      } text ("Q")

      opt[Double]("ro") action {(x, c) =>
        c.copy(ro=x)
      } text ("ro")

      opt[String]("outpath") action {(x, c) =>
        c.copy(outPath=x)
      } text ("output directory path")

      opt[Int]("seed") action {(x, c) =>
        c.copy(randomSeed=x)
      } text ("random seed")

      arg[String]("<path of .tsp>").optional().action{(x, c) =>
        c.copy(tspPath=x)
      }
    }


    // Parse option
    val acoOptionOpt: Option[AcoOption] = optionParser.parse(args, AcoOption())


    acoOptionOpt match {
      case Some(acoOption) =>
        solveByAco(acoOption)
      case None =>
        sys.exit(1)
    }
  }

  def solveByAco(acoOption: AcoOption): Unit = {
    // Initialize Random generator
    val random: Random = new Random(seed=2)

    // The number of iteration
    val NIters: Int = acoOption.nIters

    // The number of ants
    val NAnts: Int = acoOption.nAnts

    val alpha  : Double = acoOption.alpha
    val beta   : Double = acoOption.beta
    val q      : Double = acoOption.q
    val ro     : Double = acoOption.ro
    val tspFilePath: String = acoOption.tspPath
    val outputDirPath: String = acoOption.outPath

    val tsp: Tsp = TspReader.read(new File(tspFilePath))
    println(tsp)

    // All cities
    val allCityNames         : Seq[CityName]                = tsp.nodeCoordSection.keys.toSeq
    // Key: CityName, Value: Position
    val cityNameToPosition: Map[CityName, (Double, Double)] = tsp.nodeCoordSection

    // Initialize pheromone with random
    var edgeToPheromone: Map[(CityName, CityName), Double]  = AcoSolver.genRandomeEgeToPheromone(random, allCityNames)


    // Key: edge between two cities, Value: Distance
    val edgeToDistance: Map[(CityName, CityName), Double] =
      (for{
        c1 <- allCityNames
        c2 <- allCityNames
        if c1 != c2
      } yield {
        val (x1, y1)         = cityNameToPosition(c1)
        val (x2, y2)         = cityNameToPosition(c2)
        val distance: Double = Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2))
        ((c1, c2), distance)
      }).toMap

    println(s"edgeToDistance: $edgeToDistance")


    // To be minimum length (distance)
    var minDistance: Double           = Double.MaxValue

    var bestOpt: Option[(Seq[CityName], Double)] = None

    var lastPheno: Double           = 0.0


    var edgeToPheromoneSeq: Seq[Map[(CityName, CityName), Double]] = Seq.empty

    // Make output directory if need
    val outputDir = new File(outputDirPath)
    if(!outputDir.exists()){
      outputDir.mkdir()
    }

    for(nItr <- 1 to NIters) {
      for (m <- 1 to NAnts) {

        var current: CityName = CityName("1") // TODO Hard cording

        var visitedCityNames  : Seq[CityName] = Seq(current)
        var unvisitedCityNames: Seq[CityName] = allCityNames.filter(_ != current) // TODO Change to better way

        var edgeToIsVisited: Map[(CityName, CityName), Boolean] =
          (for {
            c1 <- allCityNames
            c2 <- allCityNames
            if c1 != c2
          } yield {
            ((c1, c2), false)
          }).toMap


        while (unvisitedCityNames.nonEmpty) {
          val prob: Map[CityName, Double] = AcoSolver.probability(edgeToPheromone, edgeToDistance, unvisitedCityNames, alpha, beta)(current)

          var nextCityNameOpt: Option[CityName] = None
          val b: Breaks = new Breaks
          b.breakable {
            var choice: Double = random.nextDouble()
            for ((cityName, p) <- prob) {
              choice = choice - p
              if (choice < 0) {
                nextCityNameOpt = Some(cityName)
                b.break()
              }
            }
          }

          nextCityNameOpt match {
            case Some(nextCityName) =>
              edgeToIsVisited = edgeToIsVisited.updated((current, nextCityName), true)
              current = nextCityName
              visitedCityNames :+= nextCityName
              unvisitedCityNames = unvisitedCityNames.filter(_ != nextCityName) // TODO Change to better way
            case None =>
              System.err.println("Warning: nextCityNameOpt is None!")
          }

        }

        val totalDistance: Double = AcoSolver.calcVisitedDistance(visitedCityNames, edgeToDistance)
        edgeToPheromoneSeq :+= AcoSolver.deltaPhero(q, visitedCityNames, edgeToDistance, edgeToPheromone, edgeToIsVisited)

        def updateBest(): Unit = {
          bestOpt = Some((visitedCityNames, totalDistance))
          minDistance = totalDistance

          println(s"visitedCityNames: $visitedCityNames")
          println(s"unvisitedCityNames: $unvisitedCityNames")
          PngSaver.savePng2(s"${outputDirPath}/${nItr}-${m}.png", "TODO title", visitedCityNames, cityNameToPosition)

          println(s"====== minDistance: ${minDistance} ======")
        }

        bestOpt match {
          case Some(best) =>
            if (best._2 > totalDistance) {
              updateBest()
            }
          case None =>
            updateBest()
        }
      }
      // Update edgeToPheromone
      edgeToPheromone = AcoSolver.updatedPhero(ro, edgeToPheromone, edgeToPheromoneSeq)

      if(("%.4f".format(edgeToPheromone.values.sum)).toDouble == "%.4f".format(lastPheno).toDouble){
        edgeToPheromone = AcoSolver.genRandomeEgeToPheromone(random, allCityNames)
      }

      lastPheno = edgeToPheromone.values.sum
    }
  }







}
