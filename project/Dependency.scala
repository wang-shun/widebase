import sbt._

object Dependency {

  object lib {

    val varioCollectionMutable =
      Seq("com.github.vario" %% "vario-collection-mutable" % "0.2.0")

    val varioFilter = Seq("com.github.vario" %% "vario-filter" % "0.2.0")

  }

  object testlib {

    val log = Seq(
      "log4j" % "log4j" % "1.2.16",
      "net.liftweb" %% "lift-common" % "2.4",
      "org.slf4j" % "slf4j-log4j12" % "1.6.4")

  }
}

