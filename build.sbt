enablePlugins(ScalaNativePlugin)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" % "scala-asm" % "5.1.0-scala-1",
  "org.apache.ant"         % "ant"       % "1.9.4"
)
