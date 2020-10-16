import sbt._

object CompilerPlugins {

  object Versions {
    val bm4 = "0.3.1"
    val kindProjector = "0.11.0"
  }

  val kindProjector = compilerPlugin(
    "org.typelevel" % "kind-projector" % Versions.kindProjector cross CrossVersion.full
  )

  val betterMonadicFor = compilerPlugin("com.olegpy" %% "better-monadic-for" % Versions.bm4)

  val compileDependencies = Seq(kindProjector, betterMonadicFor)

}
