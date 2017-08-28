import scalanative.tools.LinkerReporter
import scalanative.sbtplugin.ScalaNativePluginInternal.nativeLinkerReporter

val shared = Seq(
  scalaVersion := "2.11.11",
  // nativeLinkerReporter in Compile := LinkerReporter.toFile(
  //     target.value / "out.dot"),
  nativeCompileOptions ++= Seq("-O2"),
  nativeGC := "none",
  nativeEnableProfiling := true,
  nativeProfilingLocation := new File("profile.data"),
  nativeMode := "release"
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
