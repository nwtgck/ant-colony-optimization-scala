case class AcoOption(nIters         : Int=100,
                     nAnts          : Int =20,
                     alpha          : Double=1.0,
                     beta           : Double=3.0,
                     q              : Double=100.0,
                     ro             : Double=0.4,
                     outPath        : String="./output",
                     randomSeed     : Int=2,
                     realtimeFigure : Boolean=false,
                     tspPath        : String="./tsp/wi29.tsp"
                    )