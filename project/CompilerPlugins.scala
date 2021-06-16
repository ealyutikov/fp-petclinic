import sbt._

object CompilerPlugins {
  val kindProjector = compilerPlugin("org.typelevel" %% "kind-projector" % "0.13.0").cross(CrossVersion.full)
  val bm4 = compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

  val Plugins: Seq[ModuleID] = List(bm4, kindProjector)
}
