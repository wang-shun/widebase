import sbt._
import Keys._

object Dependency {

  object lib {

    val actors = scalaVersion { "org.scala-lang" % "scala-actors" % _ }
    val commonsCli = Seq("commons-cli" % "commons-cli" % "1.2")
    val eval = Seq("com.twitter" %% "util-eval" % "6.2.0")
    val interpreterPane = Seq("de.sciss" %% "scalainterpreterpane" % "1.4.0")
    val jaas = Seq("org.apache.activemq" % "activemq-jaas" % "5.8.0")
    val jfreechart = Seq("org.jfree" % "jfreechart" % "1.0.14")

    val jodaTime = Seq(
      "org.joda" % "joda-convert" % "1.3.1",
      "joda-time" % "joda-time" % "2.2")

    val log = Seq(
      "log4j" % "log4j" % "1.2.17",
      "net.liftweb" %% "lift-common" % "2.5-RC2",
      "org.slf4j" % "slf4j-log4j12" % "1.7.3")

    val moreswing = Seq("com.github.myst3r10n" %% "moreswing-swing" % "0.1.3")
    val moreswingI18n = Seq("com.github.myst3r10n" %% "moreswing-swing" % "0.1.3")
    val netty = Seq("io.netty" % "netty" % "3.6.3.Final")

    val sbtLauncher = Seq(
      "org.scala-sbt" % "launcher-interface" % "0.12.2" % "provided")

    val swing = scalaVersion { "org.scala-lang" % "scala-swing" % _ }

  }

  object testlib {

    val log = Seq(
      "log4j" % "log4j" % "1.2.17" % "test",
      "net.liftweb" %% "lift-common" % "2.5-RC2" % "test",
      "org.slf4j" % "slf4j-log4j12" % "1.7.3" % "test")

  }
}

