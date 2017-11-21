import java.io.{File, PrintWriter}

import breeze.plot.Series

object PngSaver {

  /**
    * Save route in .png file
    * @param filePath
    * @param figureTitle
    * @param route
    * @param positions
    */
  def savePng(filePath: String, figureTitle: String, route: Seq[Int], positions: Seq[(Double, Double)]): Unit = {
    import breeze.plot._

    val f = Figure(figureTitle)
    f.visible = false
    val p = f.subplot(0)

    var prePos: (Double, Double) = positions(route.head)

    for(i <- route.drop(1)){
      val position: (Double, Double) = positions(i)

      println(s"${prePos} to ${position}")
      p += drawedLine(prePos, position)


      prePos = position
    }

    p += drawedLine(prePos, positions(route.head))

    println(s"${prePos} to ${positions(route.head)}")



    f.saveas(filePath)
  }

  /**
    * Draw line from p1 to p2
    * @param p1
    * @param p2
    * @return
    */
  def drawedLine(p1: (Double, Double), p2: (Double, Double)): Series = {
    import breeze.plot._
    import breeze.linalg._

    val (x1, y1) = p1
    val (x2, y2) = p2



    val xStart =  Math.min(x1, x2)
    val xEnd   =  Math.max(x1, x2)

    val x = linspace(xStart, xEnd)

    val y = if(x1 == x2){
      val yStart = Math.min(y1, y2)
      val yEnd   = Math.max(y1, y2)
      val y = linspace(yStart, yEnd)
      y
    } else {
      val a: Double = (y1-y2)/(x1-x2)
      val b: Double = y1 - a*x1
      val y = a * x + b
      y
    }



    println(x)
    println(y)

    plot(x, y)
  }

}
