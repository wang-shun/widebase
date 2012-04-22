import sbt._

object Dependency {

  object lib {

    val jodaTime = Seq(
      "org.joda" % "joda-convert" % "1.2",
      "joda-time" % "joda-time" % "2.0")

    val varioCollectionMutable =
      Seq("vario" %% "vario-collection-mutable" % "0.1.0-SNAPSHOT")
    val varioFile = Seq("vario" %% "vario-file" % "0.1.0-SNAPSHOT")
    val varioFilter = Seq("vario" %% "vario-filter" % "0.1.0-SNAPSHOT")

  }

  object testlib {

    val log = Seq(
      "log4j" % "log4j" % "1.2.16",
      "net.liftweb" %% "lift-common" % "2.4",
      "org.slf4j" % "slf4j-log4j12" % "1.6.4")

  }
}

