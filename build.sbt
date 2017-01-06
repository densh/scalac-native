val shared = Seq(
  scalaVersion := "2.11.11"
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
