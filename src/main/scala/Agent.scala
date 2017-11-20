import scala.collection.mutable
import scala.util.Random
import scala.util.control.Breaks

class Agent(random: Random, towns: Seq[Int], roads : Map[(Int, Int), Double], start: Int, pheromone: Map[(Int, Int), Double]) {
  var current: Int    = start
  val alpha  : Double = 1.0
  val beta   : Double = 3
  val Q      : Int    = 100

  val whole    : Seq[Int] = towns
  var notVisied: Seq[Int] = dropFirstMatch(towns, start)
  var visited  : Seq[Int] = Seq(towns(start))

  var way      : mutable.Map[(Int, Int), Boolean] = mutable.Map.empty // TODO Not to use mutable
  for{
    i <- towns
    j <- towns
    if i != j
  } {
    way((i, j)) = false
  }

  def assessment(j: Int): Double = {
    val numerator  : Double = Math.pow(pheromone(current, j), alpha) * Math.pow(1.0/roads(current, j), beta)
    val denominator: Double = notVisied.map{l => l
        Math.pow(pheromone(current, l), alpha) * Math.pow(1.0/roads(current, l), beta)
    }.sum
    numerator / denominator
  }

  def probability: Map[Int, Double] = {
    val p: mutable.Map[Int, Double] = mutable.Map.empty // TODO Not to use mutable
    for(m <- notVisied){
      val mAsses  : Double = assessment(m)
      val sumAsses: Double = notVisied.map{n => assessment(n)}.sum
      p(m) = mAsses / sumAsses
    }
    // Convert p from mutable to immutable (from: https://stackoverflow.com/a/5050653)
    Map(p.toSeq: _*)
  }

  def agentwalk(): Seq[Int] = {
    for(i <- whole){
      val prob  : Map[Int, Double] = probability
      var choice: Double           = random.nextDouble()
      var nextT : (Int, Double)    = null // TODO Not to use null
      val b     : Breaks           = new Breaks
      b.breakable {
        for (i <- prob) {
          choice = choice - i._2
          if (choice < 0) {
            nextT = i
            b.break()
          }
        }
      }
      way((current, nextT._1)) = true
      current                  = nextT._1
      visited   :+= nextT._1
      notVisied = dropFirstMatch(notVisied, nextT._1)

      if(notVisied.isEmpty){
        return visited
      }
    }
    visited
  }

  def getDeltaphero: Map[(Int, Int), Double] = {
    val sumoflen      : Double = getLength
    val deltaPheromone: mutable.Map[(Int, Int), Double] = mutable.Map.empty
    for(i <- pheromone.keys){
      if(way(i)){
        deltaPheromone(i) = Q.toDouble / sumoflen
      } else {
        deltaPheromone(i) = 0
      }
    }
    Map(deltaPheromone.toSeq: _*)
  }

  def getPhero(startpheromone: mutable.Map[(Int, Int), Double], pheromones: Seq[Map[(Int, Int), Double]], ro: Double): mutable.Map[(Int, Int), Double] = { // TODO Not to use mutable
    for(i <- startpheromone.keys){
      startpheromone(i) = ro * startpheromone(i) + (1.0 - ro) * pheromones.map{k => k(i)}.sum
    }
    startpheromone
  }

  def getLength: Double = {
    var sumoflen: Double = 0
    for(i <- 1 until visited.length){
      sumoflen += roads(visited(i-1), visited(i))
    }
    sumoflen
  }

  def getWay: Seq[Int] = visited


  // (from https://alvinalexander.com/scala/how-to-drop-filter-remove-first-matching-element-in-sequence-list)
  private def dropFirstMatch[A](ls: Seq[A], value: A): Seq[A] = {
    val index = ls.indexOf(value)  //index is -1 if there is no match
    if (index < 0) {
      ls
    } else if (index == 0) {
      ls.tail
    } else {
      // splitAt keeps the matching element in the second group
      val (a, b) = ls.splitAt(index)
      a ++ b.tail
    }
  }
}
