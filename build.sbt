name := "ant-colony-optimization-scala"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  // ScalaNLP (Natural Language Processing)
  "org.scalanlp" %% "breeze" % "0.12",
  // ScalaNLP（プロット用）
  "org.scalanlp" %% "breeze-viz" % "0.12",
  "org.scalanlp" %% "breeze-natives" % "0.12" // これがないと「Failed to load implementation from: com.github.fommil.netlib.NativeRefBLAS」という警告が実行時に出る（エラーではないので計算はちゃんとしてくれる）（多分これがあると高速に行列演算ができるのだと思う）
)