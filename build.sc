import mill._
import mill.define.Target
import mill.scalalib.scalafmt.ScalafmtModule
import mill.util.Loose
import scalalib._

val scalaVersions = List("2.11.12", "2.12.8")

object core extends Cross[CoreModule](scalaVersions: _*)
class CoreModule(crossVersion: String) extends CrossScalaModule with ScalafmtModule {
  override def crossScalaVersion = crossVersion

  override def ivyDeps: Target[Loose.Agg[Dep]] = Agg(
    ivy"org.scalameta::scalameta:4.1.0",
    ivy"org.scalameta::contrib:4.1.0"
  )

  object test extends Tests {
    override def ivyDeps = Agg(ivy"com.lihaoyi::utest:0.6.0")
    def testFrameworks = Seq("utest.runner.Framework")
  }
}

object cli extends Cross[CLIModule](scalaVersions: _*)
class CLIModule(crossVersion: String) extends CrossScalaModule with ScalafmtModule {
  override def moduleDeps = Seq(core())
  override def crossScalaVersion = crossVersion
}

def unitTest(scalaVersion: String = sys.env("TRAVIS_SCALA_VERSION")) = T.command {
  core(scalaVersion).test.test()
}
