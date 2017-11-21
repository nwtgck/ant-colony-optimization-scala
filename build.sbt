name := "ant-colony-optimization-scala"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  // ScalaNLP (Natural Language Processing)
  "org.scalanlp" %% "breeze" % "0.12",
  // ScalaNLP (For plotting)
  "org.scalanlp" %% "breeze-viz" % "0.12",
  "org.scalanlp" %% "breeze-natives" % "0.12", // If none, it warns "Failed to load implementation from: com.github.fommil.netlib.NativeRefBLAS"  (this will accelerate speed)

  // Option parser
  "com.github.scopt" %% "scopt" % "3.6.0"
)