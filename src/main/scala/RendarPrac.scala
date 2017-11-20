object RendarPrac {
  def main(args: Array[String]): Unit = {
    import breeze.linalg._
    import breeze.plot._

    val f = Figure("Cos function")
    f.visible = false
//    val x = (-1.0 to 1.0 by 0.01).toList
//    val y = x.map(x => math.cos(x * 2*math.Pi))

    val p1: (Double, Double) = (1, 5)
    val p2: (Double, Double) = (3, 4)

    val (x1, y1) = p1
    val (x2, y2) = p2

    val a: Double = (y1-y2)/(x1-x2)
    val b: Double = y1 - a*x1

    val xStart =  Math.min(x1, x2)
    val xEnd   =  Math.max(x1, x2)
    val x = (xStart to xEnd by 0.01).toList
    val y = x.map{x => a * x + b}


    val p = f.subplot(0)


    p += plot(x, y)
    f.saveas("cos.png")



  }
}
