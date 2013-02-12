import sbt._
import Keys._

/** Build Widebase by sbt.
 *
 * APIs:
 *
 * * widebase.collectoin.mutable
 * * widebase.db
 * * widebase.dsl
 * * widebase.stream.socket.cq
 * * widebase.stream.socket.rq
 *
 * Apps:
 *
 * * widebase.notify
 * * widebase.plant
 *
 * @author myst3r10n
 */
object WidebaseBuild extends Build {

  import Dependency._

  override val settings = super.settings ++ buildSettings ++ publishSettings

  lazy val widebase = Project(
    "widebase",
    file("."),
    settings = Defaults.defaultSettings ++ Unidoc.settings)
    .aggregate(
      widebaseCollectionMutable,
      widebaseData,
      widebaseDb,
      widebaseDbColumn,
      widebaseDbTable,
      widebaseDsl,
      widebaseIo,
      widebaseIoColumn,
      widebaseIoCsv,
      widebaseIoCsvFilter,
      widebaseIoFile,
      widebaseIoFilter,
      widebaseIoTable,
      widebaseNotify,
      widebasePlant,
      widebaseStreamCodec,
      widebaseStreamCodecCq,
      widebaseStreamCodecRq,
      widebaseStreamHandler,
      widebaseStreamHandlerCq,
      widebaseStreamHandlerRq,
      widebaseStreamSocket,
      widebaseStreamSocketCq,
      widebaseStreamSocketRq,
      widebaseUiChart,
      widebaseUiChartDataTime,
      widebaseUiChartDataTimeOHLC,
      widebaseUiChartDataXY,
      widebaseUiChartEvent,
      widebaseUiChartLive,
      widebaseUiChartPlot,
      widebaseUiChartUtil,
      widebaseUiI18n,
      widebaseUiTable,
      widebaseUiTableEvent,
      widebaseUiTableLive,
      widebaseUtil)

  /** Collection Mutable */
  lazy val widebaseCollectionMutable = Project(
    "widebase-collection-mutable",
    file("widebase.collection.mutable"))
    .dependsOn(widebaseIoFile)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  /** Data */
  lazy val widebaseData = Project(
    "widebase-data",
    file("widebase.data"))
    .settings(libraryDependencies ++= lib.jodaTime)

  /** DB */
  lazy val widebaseDb = Project(
    "widebase-db",
    file("widebase.db"))
    .dependsOn(widebaseIoTable)
    .settings(libraryDependencies ++= testlib.log)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  /** DB Column */
  lazy val widebaseDbColumn = Project(
    "widebase-db-column",
    file("widebase.db.column"))
    .dependsOn(widebaseCollectionMutable, widebaseUtil)

  /** DB Table */
  lazy val widebaseDbTable = Project(
    "widebase-db-table",
    file("widebase.db.table"))
    .dependsOn(widebaseIoColumn)

  /** DSL */
  lazy val widebaseDsl = Project(
    "widebase-dsl",
    file("widebase.dsl"))
    .dependsOn(widebaseIoCsv)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  /** I/O */
  lazy val widebaseIo = Project(
    "widebase-io",
    file("widebase.io"))
    .dependsOn(widebaseData, widebaseIoFilter)

  /** I/O Column */
  lazy val widebaseIoColumn = Project(
    "widebase-io-column",
    file("widebase.io.column"))
    .dependsOn(widebaseDbColumn, widebaseIoFilter, widebaseUtil)

  /** I/O CSV */
  lazy val widebaseIoCsv = Project(
    "widebase-io-csv",
    file("widebase.io.csv"))
    .dependsOn(widebaseDb, widebaseIoCsvFilter)
    .settings(libraryDependencies ++= lib.log)

  /** I/O CSV Filter */
  lazy val widebaseIoCsvFilter = Project(
    "widebase-io-csv-filter",
    file("widebase.io.csv.filter"))
    .dependsOn(widebaseDbTable)

  /** I/O File */
  lazy val widebaseIoFile = Project(
    "widebase-io-file",
    file("widebase.io.file"))
    .dependsOn(widebaseIo)

  /** I/O Filter */
  lazy val widebaseIoFilter = Project(
    "widebase-io-filter",
    file("widebase.io.filter"))

  /** I/O Table */
  lazy val widebaseIoTable = Project(
    "widebase-io-table",
    file("widebase.io.table"))
    .dependsOn(widebaseDbTable)

  /** Notify */
  lazy val widebaseNotify = Project(
    "widebase-notify",
    file("widebase.notify"))
    .dependsOn(widebaseStreamSocketRq)
    .settings(
      resolvers <+= sbtResolver,
      libraryDependencies ++= lib.commonsCli ++ lib.sbtLauncher)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  /** Plant */
  lazy val widebasePlant = Project(
    "widebase-plant",
    file("widebase.plant"))
    .dependsOn(widebaseStreamSocketRq)
    .settings(
      resolvers <+= sbtResolver,
      libraryDependencies ++= lib.commonsCli ++ lib.sbtLauncher)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  /** Stream Codec */
  lazy val widebaseStreamCodec = Project(
    "widebase-stream-codec",
    file("widebase.stream.codec"))
    .dependsOn(widebaseData, widebaseUtil)
    .settings(libraryDependencies ++= lib.netty)

  /** Stream Codec CQ */
  lazy val widebaseStreamCodecCq = Project(
    "widebase-stream-codec-cq",
    file("widebase.stream.codec.cq"))
    .dependsOn(widebaseDbTable, widebaseStreamCodec)

  /** Stream Codec RQ */
  lazy val widebaseStreamCodecRq = Project(
    "widebase-stream-codec-rq",
    file("widebase.stream.codec.rq"))
    .dependsOn(widebaseStreamCodec)

  /** Stream Handler */
  lazy val widebaseStreamHandler = Project(
    "widebase-stream-handler",
    file("widebase.stream.handler"))
    .dependsOn(widebaseStreamCodec)
    .settings(libraryDependencies ++= lib.jaas ++ lib.log)

  /** Stream Handler CQ */
  lazy val widebaseStreamHandlerCq = Project(
    "widebase-stream-handler-cq",
    file("widebase.stream.handler.cq"))
    .dependsOn(widebaseStreamCodecCq, widebaseStreamHandler)

  /** Stream Handler RQ */
  lazy val widebaseStreamHandlerRq = Project(
    "widebase-stream-handler-rq",
    file("widebase.stream.handler.rq"))
    .dependsOn(widebaseDbTable, widebaseStreamCodecRq, widebaseStreamHandler)
    .settings(libraryDependencies ++= lib.eval ++ lib.log)

  /** Stream Socket */
  lazy val widebaseStreamSocket = Project(
    "widebase-stream-socket",
    file("widebase.stream.socket"))
    .dependsOn(widebaseStreamHandler)

  /** Stream Socket CQ */
  lazy val widebaseStreamSocketCq = Project(
    "widebase-stream-socket-cq",
    file("widebase.stream.socket.cq"))
    .dependsOn(widebaseStreamHandlerCq, widebaseStreamSocket)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  /** Stream Socket RQ */
  lazy val widebaseStreamSocketRq = Project(
    "widebase-stream-socket-rq",
    file("widebase.stream.socket.rq"))
    .dependsOn(
      widebaseDsl % "test",
      widebaseStreamHandlerRq,
      widebaseStreamSocket)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  /** UI Chart */
  lazy val widebaseUiChart = Project(
    "widebase-ui-chart",
    file("widebase.ui.chart"))
    .dependsOn(widebaseUiChartEvent)
    .settings(libraryDependencies ++= lib.morechart ++ lib.moreswing)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  /** UI Chart Event */
  lazy val widebaseUiChartEvent = Project(
    "widebase-ui-chart-event",
    file("widebase.ui.chart.event"))
    .settings(libraryDependencies ++= lib.swing)

  /** UI Chart Data Time */
  lazy val widebaseUiChartDataTime = Project(
    "widebase-ui-chart-data-time",
    file("widebase.ui.chart.data.time"))
    .dependsOn(widebaseDbColumn)
    .settings(libraryDependencies ++= lib.jfreechart)

  /** UI Chart Data Time OHLC */
  lazy val widebaseUiChartDataTimeOHLC = Project(
    "widebase-ui-chart-data-time-ohlc",
    file("widebase.ui.chart.data.time.ohlc"))
    .dependsOn(widebaseDbColumn)
    .settings(libraryDependencies ++= lib.jfreechart)

  /** UI Chart Data XY */
  lazy val widebaseUiChartDataXY = Project(
    "widebase-ui-chart-data-xy",
    file("widebase.ui.chart.data.xy"))
    .dependsOn(widebaseDbColumn)
    .settings(libraryDependencies ++= lib.jfreechart)

  /** UI Chart Live */
  lazy val widebaseUiChartLive = Project(
    "widebase-ui-chart-live",
    file("widebase.ui.chart.live"))
    .dependsOn(
      widebaseDsl % "test",
      widebaseUiChart,
      widebaseUiChartPlot,
      widebaseUiI18n)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  /** UI Chart Plot */
  lazy val widebaseUiChartPlot = Project(
    "widebase-ui-chart-plot",
    file("widebase.ui.chart.plot"))
    .dependsOn(
      widebaseUiChartDataTime,
      widebaseUiChartDataTimeOHLC,
      widebaseUiChartDataXY,
      widebaseUiChartUtil)

  /** UI Chart Utilities */
  lazy val widebaseUiChartUtil = Project(
    "widebase-ui-chart-util",
    file("widebase.ui.chart.util"))

  /** UI Table */
  lazy val widebaseUiTable = Project(
    "widebase-ui-table",
    file("widebase.ui.table"))
    .dependsOn(
      widebaseDsl % "test",
      widebaseDbTable,
      widebaseUiTableEvent)
    .settings(libraryDependencies ++= lib.moreswing)

  /** UI Table Event */
  lazy val widebaseUiTableEvent = Project(
    "widebase-ui-table-event",
    file("widebase.ui.table.event"))
    .settings(libraryDependencies ++= lib.swing)

  /** UI i18n */
  lazy val widebaseUiI18n = Project(
    "widebase-ui-i18n",
    file("widebase.ui.i18n"))

  /** UI Table Live */
  lazy val widebaseUiTableLive = Project(
    "widebase-ui-table-live",
    file("widebase.ui.table.live"))
    .dependsOn(
      widebaseDsl % "test",
      widebaseUiI18n,
      widebaseUiTable)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  /** Utilities */
  lazy val widebaseUtil = Project(
    "widebase-util",
    file("widebase.util"))
    .dependsOn(widebaseIoFilter)

  /** Log path */
  System.setProperty(
    "widebase.log",
    System.getProperty("user.dir") + "/var/log")

  /** JAAS path */
  System.setProperty(
    "java.security.auth.login.config",
    System.getProperty("user.dir") + "/etc/jaas.conf")

  /** Security policy */
  System.setProperty("java.security.manager", "true")
  System.setProperty(
    "java.security.policy",
    System.getProperty("user.dir") + "/etc/java.policy")

  /** Build settings */
	def buildSettings = Seq(
		organization := "com.github.widebase",
		version := "0.3.4-SNAPSHOT",
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    resolvers ++= Seq(
      "Sonatype OSS" at "https://oss.sonatype.org/content/groups/public"))

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
      </developers>),

    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"))

}

