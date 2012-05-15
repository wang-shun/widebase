import sbt._
import Keys._

object WidebaseBuild extends Build {

  import Dependency._

  override val settings = super.settings ++ buildSettings ++ publishSettings

  lazy val widebase = Project(
    "widebase",
    file("."),
    settings = Defaults.defaultSettings ++ Unidoc.settings) aggregate(
      widebaseDb,
      widebaseDbColumn,
      widebaseDbTable,
      widebaseIoColumn,
      widebaseIoFilter,
      widebaseIoTable,
      widebaseUtil)

  /** DB */
  lazy val widebaseDb = Project("widebase-db", file("widebase.db")) dependsOn(
    widebaseIoTable) settings(libraryDependencies ++= testlib.log)

  /** DB Column */
  lazy val widebaseDbColumn = Project(
    "widebase-db-column",
    file("widebase.db.column")) dependsOn(widebaseUtil) settings(
    resolvers +=
      "Sonatype OSS" at "https://oss.sonatype.org/content/groups/public",
    libraryDependencies ++= lib.varioCollectionMutable)

  /** DB Table */
  lazy val widebaseDbTable = Project(
    "widebase-db-table",
    file("widebase.db.table")) dependsOn(widebaseIoColumn)

  /** I/O Column */
  lazy val widebaseIoColumn = Project(
    "widebase-io-column",
    file("widebase.io.column")) dependsOn(
      widebaseDbColumn,
      widebaseIoFilter,
      widebaseUtil)

  /** I/O Filter */
  lazy val widebaseIoFilter = Project(
    "widebase-io-filter",
    file("widebase.io.filter"))

  /** I/O Table */
  lazy val widebaseIoTable = Project(
    "widebase-io-table",
    file("widebase.io.table")) dependsOn(widebaseDbTable)

  /** Utilities */
  lazy val widebaseUtil = Project(
    "widebase-util",
    file("widebase.util")) settings(
    resolvers +=
      "Sonatype OSS" at "https://oss.sonatype.org/content/groups/public",
    libraryDependencies ++= lib.varioFilter)

  /** Log path */
  System.setProperty(
    "widebase.log",
    System.getProperty("user.dir") + "/var/log/widebase")

  /** Build settings */
	def buildSettings = Seq(
		organization := "com.github.widebase",
		version := "0.1.0",
    scalacOptions ++= Seq("-unchecked", "-deprecation"))

  /** Publish settings */
	def publishSettings = Seq(
	  publishMavenStyle := true,
	  publishArtifact in Test := false,
	  pomIncludeRepository := { _ => false },

    publishTo <<= (version) { version: String =>

      val nexus = "https://oss.sonatype.org/"

      if(version.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots/") 
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2/")

    },
    pomExtra := (
      <url>https://github.com/widebase/widebase</url>
      <licenses>
        <license>
          <name>Apache License, Version 2.0</name>
          <url>https://raw.github.com/widebase/widebase/master/LICENSE</url>
        </license>
      </licenses>
      <scm>
        <url>https://github.com/widebase/widebase</url>
        <connection>scm:git:git@github.com:widebase/widebase.git</connection>
      </scm>
      <developers>
        <developer>
          <id>myst3r10n</id>
          <name>myst3r10n</name>
          <url>https://github.com/myst3r10n</url>
        </developer>
      </developers>))

}

