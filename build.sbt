import scalanative.tools.LinkerReporter
import scalanative.sbtplugin.ScalaNativePluginInternal.nativeLinkerReporter

val shared = Seq(
  scalaVersion := "2.11.11"
  // nativeLinkerReporter in Compile := LinkerReporter.toFile(
  //     target.value / "out.dot"),
)

lazy val asm =
  project.in(file("asm"))
    .enablePlugins(ScalaNativePlugin)
    .settings(shared)

lazy val scalac =
  project.in(file("scalac"))
    .dependsOn(asm)
    .enablePlugins(ScalaNativePlugin)
    .settings(shared)

lazy val scalacmain =
  project.in(file("scalacmain"))
    .enablePlugins(ScalaNativePlugin)
    .dependsOn(scalac)
    .settings(shared)
