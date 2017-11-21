// Create an option parser
object AcoOptionParser extends scopt.OptionParser[AcoOption]("TSP solver by Ant Colony Optimization") {

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

  opt[Unit]("realtime-figure") action{(x, c) =>
    c.copy(realtimeFigure = true)
  } text("Render realtime figure")

  arg[String]("<path of .tsp>").optional().action{(x, c) =>
    c.copy(tspPath=x)
  }
}