package scala

object language {

  import languageFeature._

  implicit lazy val dynamics: dynamics = languageFeature.dynamics

  implicit lazy val postfixOps: postfixOps = languageFeature.postfixOps

  implicit lazy val reflectiveCalls: reflectiveCalls = languageFeature.reflectiveCalls

  implicit lazy val implicitConversions: implicitConversions = languageFeature.implicitConversions

  implicit lazy val higherKinds: higherKinds = languageFeature.higherKinds

  implicit lazy val existentials: existentials = languageFeature.existentials

  object experimental {

    import languageFeature.experimental._
    implicit lazy val macros: macros = languageFeature.experimental.macros
  }
}
