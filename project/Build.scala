import sbt._
import Keys._

object WidebaseBuild extends Build {

  import Dependency._

  override val settings = super.settings ++ buildSettings

  lazy val widebase = Project(
    "widebase",
    file("widebase"),
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
    widebaseDbColumn,
    widebaseDbTable,
    widebaseIoTable) settings(
      libraryDependencies ++= lib.jodaTime ++ testlib.log)

  /** DB Column */
  lazy val widebaseDbColumn = Project(
    "widebase-db-column",
    file("widebase.db.column")) dependsOn(widebaseUtil) settings(
    libraryDependencies ++= lib.jodaTime ++ lib.varioCollectionMutable)

  /** DB Table */
  lazy val widebaseDbTable = Project(
    "widebase-db-table",
    file("widebase.db.table")) dependsOn(widebaseIoColumn) settings(
    libraryDependencies ++= lib.jodaTime)

  /** I/O Column */
  lazy val widebaseIoColumn = Project(
    "widebase-io-column",
    file("widebase.io.column")) dependsOn(
      widebaseDbColumn,
      widebaseIoFilter,
      widebaseUtil) settings (libraryDependencies ++= lib.varioFile)

  /** I/O Filter */
  lazy val widebaseIoFilter = Project(
    "widebase-io-filter",
    file("widebase.io.filter")) settings (
    libraryDependencies ++= lib.jodaTime ++ lib.varioFile)

  /** I/O Table */
  lazy val widebaseIoTable = Project(
    "widebase-io-table",
    file("widebase.io.table")) dependsOn(
    widebaseDbTable,
    widebaseIoColumn,
    widebaseUtil) settings (
    libraryDependencies ++= lib.jodaTime ++ lib.varioFile)

  /** Utilities */
  lazy val widebaseUtil = Project(
    "widebase-util",
    file("widebase.util")) settings(
      libraryDependencies ++= lib.varioFilter)

  /** Log path */
  System.setProperty(
    "widebase.log",
    System.getProperty("user.dir") + "/usr/log/widebase")

  /** Build settings */
	def buildSettings = Seq(
		organization := "widebase",
		version := "0.1.0-SNAPSHOT",
    scalacOptions ++= Seq("-unchecked", "-deprecation"))

}

