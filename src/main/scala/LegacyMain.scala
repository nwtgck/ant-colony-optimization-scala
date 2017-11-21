// (from: https://qiita.com/EnsekiTT/items/9b13ceba391221687f42)

import java.io.{File, PrintWriter}

import scala.collection.mutable
import scala.util.Random

object LegacyMain {
  def main(args: Array[String]): Unit = {
    val tsp: Tsp = TspReader.read(new File("./tsp/wi29.tsp"))
//    val tsp: Tsp = TspReader.read(new File("./tsp/simple.tsp"))
//    val tsp: Tsp = TspReader.read(new File("./tsp/simple2.tsp"))

    println(s"TSP: ${tsp}")

    val NumOfAgent: Int    = 20
    val NumOfSolve: Int    = 500
    val ro        : Double = 0.4

    Random.setSeed(2)
    val random: Random = new Random()


    val sorted = tsp.nodeCoordSection.map{case (key, xy) => (key.value.toInt, xy)}.toSeq.sortBy(_._1)
    val towns     : Seq[Int]              = sorted.map{e => e._1 - 1} // TODO Change better way
    val positions : Seq[(Double, Double)] = sorted.map(_._2) // TODO Change better way

    println(s"towns: ${towns}")
    println(s"positions: ${positions}")

    val roads    : mutable.Map[(Int, Int), Double] = mutable.Map.empty // TODO Not to use mutable
    var pheromone: mutable.Map[(Int, Int), Double] = mutable.Map.empty // TODO Not to use mutable



    // Make output directory if need
    val outputDir = new File("./output") // TODO Hard cording
    if(!outputDir.exists()){
      outputDir.mkdir()
    }

    for{
      i <- towns
      j <- towns
      if i != j
    } {
      roads((i, j))     = Math.sqrt( Math.pow(positions(i)._1 - positions(j)._1, 2) + Math.pow(positions(i)._2 - positions(j)._2, 2))
      pheromone((i, j)) = random.nextDouble()
    }

    var minLength: Double = Double.MaxValue
    var bestAgent: Agent  = null // TODO Not to use null
    var lastPheno: Double = 0.0

    for(i <- 0 until NumOfSolve){
      var pheros  : Seq[Map[(Int, Int), Double]] = Seq.empty
      var topAgent: Agent                        = null // TODO Not to use null
      var k       : Agent                        = null // TODO Not to use null
      for(m <- 0 until NumOfAgent){
        k =  new Agent(
          random,
          towns=towns,
          roads=Map(roads.toSeq: _*),
          start=0,
          pheromone=Map(pheromone.toSeq: _*)
        )
        k.agentwalk()
        val length = k.getLength
        pheros :+= k.getDeltaphero

        if(topAgent == null || topAgent.getLength > length){
          topAgent = k // TODO Deep copy
        }

        if (minLength == null || minLength > length) {
          minLength = length
          bestAgent = k
          println(s"k.way: ${k.getWay}")
          println(s"minLength: ${minLength}")

          saveDat(s"output/${i}-${m}.dat", k.getWay, positions)
          PngSaver.savePng(s"output/${i}-${m}.png", "TODO fig title", bestAgent.getWay, positions)

        }
      }
      println("今" + i + "番目のありんこたちが仕事をしました。")
      pheromone = k.getPhero(pheromone, pheros, ro)

      if(("%.4f".format(pheromone.values.sum)).toDouble == "%.4f".format(lastPheno).toDouble){
        pheromone = mutable.Map.empty
        for{
          i <- towns
          j <- towns
          if i != j
        } {
          pheromone((i, j)) = random.nextDouble()
        }
      }
      lastPheno = pheromone.values.sum
    }
    println(bestAgent.getWay)
    saveDat(s"output/best.dat", bestAgent.getWay, positions)
    PngSaver.savePng(s"output/best.png", "TODO fig title", bestAgent.getWay, positions)
  }

  /**
    * Save route in .dat file to plot by gnuplot
    * @param filePath
    * @param route
    * @param positions
    */
  private def saveDat(filePath: String, route: Seq[Int], positions: Seq[(Double, Double)]): Unit = {
    val out: PrintWriter = new PrintWriter(new File(filePath))
    for(i <- route){
      val position: (Double, Double) = positions(i)
      out.println(s"${position._1} ${position._2}")
    }
    out.println(s"${positions.head._1} ${positions.head._2}")
    out.close()
  }
}
