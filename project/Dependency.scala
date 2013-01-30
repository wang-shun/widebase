import sbt._

object Dependency {

  object lib {

    val commonsCli = Seq("commons-cli" % "commons-cli" % "1.2")

    val jaas = Seq("org.apache.activemq" % "activemq-jaas" % "5.6.0")

    val log = Seq(
      "log4j" % "log4j" % "1.2.17",
      "net.liftweb" %% "lift-common" % "2.5-M1",
      "org.slf4j" % "slf4j-log4j12" % "1.7.1")

    val netty = Seq("io.netty" % "netty" % "3.5.7.Final")

    val sbtLauncher = Seq(
      "org.scala-tools.sbt" % "launcher-interface_2.9.1" % "0.11.2" % "provided")

    val varioCollectionMutable =
      Seq("com.github.vario" %% "vario-collection-mutable" % "0.4.4")

    val varioData = Seq("com.github.vario" %% "vario-data" % "0.4.4")

    val varioFilter = Seq("com.github.vario" %% "vario-filter" % "0.4.4")

  }

  object testlib {

    val log = Seq(
      "log4j" % "log4j" % "1.2.17" % "test",
      "net.liftweb" %% "lift-common" % "2.5-M1" % "test",
      "org.slf4j" % "slf4j-log4j12" % "1.7.1" % "test")

  }
}

