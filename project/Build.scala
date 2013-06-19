import sbt._
import Keys._

/** Build Widebase by sbt.
 *
 * Top APIs:
 *
 * * widebase.collectoin.mutable
 * * widebase.db
 * * widebase.dsl
 * * widebase.stream.socket
 * * widebase.stream.socket.cq
 * * widebase.stream.socket.rq
 * * widebase.testkit
 * * widebase.toolbox.core
 * * widebase.toolbox.finance
 * * widebase.ui.chart
 * * widebase.ui.i18n
 * * widebase.ui.swing
 * * widebase.workspace
 * * widebase.workspace.ide
 * * widebase.workspace.ide.cli
 * * widebase.workspace.ide.editor
 * * widebase.workspace.ide.explorer
 *
 * Top Apps:
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
      widebaseTestkit,
      widebaseToolboxCore,
      widebaseToolboxFinance,
      widebaseUiChartAnnotations,
      widebaseUiChartData,
      widebaseUiChartDataTime,
      widebaseUiChartDataTimeOHLC,
      widebaseUiChartDataXY,
      widebaseUiChartEvent,
      widebaseUiChartUtil,
      widebaseUiI18n,
      widebaseUiSwing,
      widebaseUiSwingEvent,
      widebaseWorkspace,
      widebaseWorkspaceEvent,
      widebaseWorkspaceIde,
      widebaseWorkspaceIdeApp,
      widebaseWorkspaceIdeCli,
      widebaseWorkspaceIdeEditor,
      widebaseWorkspaceIdeEditorEvent,
      widebaseWorkspaceIdeExplorer,
      widebaseWorkspaceRuntime,
      widebaseWorkspaceUtil,
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
  .settings(libraryDependencies <+= lib.actors)

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
  .dependsOn(
    widebaseDbColumn,
    widebaseIoFilter,
    widebaseUtil)

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
  .dependsOn(
    widebaseDbTable,
    widebaseStreamCodecRq,
    widebaseStreamHandler)
  .settings(libraryDependencies ++= lib.eval ++ lib.log)

  /** Stream Socket */
  lazy val widebaseStreamSocket = Project(
    "widebase-stream-socket",
    file("widebase.stream.socket"))
  .dependsOn(widebaseStreamHandler)
  .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

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

  /** Testkit */
  lazy val widebaseTestkit = Project(
    "widebase-testkit",
    file("widebase.testkit"))
  .dependsOn(
    widebaseDsl,
    widebaseStreamSocketCq,
    widebaseStreamSocketRq)
  .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  /** Toolbox Core */
  lazy val widebaseToolboxCore = Project(
    "widebase-toolbox-core",
    file("widebase.toolbox.core"))
  .dependsOn(
    widebaseDsl % "test",
    widebaseUiChartAnnotations,
    widebaseUiChartDataTime,
    widebaseUiChartDataXY,
    widebaseUiChartUtil,
    widebaseUiI18n,
    widebaseUiSwing)
  .settings(libraryDependencies ++= lib.jfreechart)
  .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  /** Toolbox Finance */
  lazy val widebaseToolboxFinance = Project(
    "widebase-toolbox-finance",
    file("widebase.toolbox.finance"))
  .dependsOn(
    widebaseDsl % "test",
    widebaseToolboxCore,
    widebaseUiChartDataTimeOHLC)
  .settings(libraryDependencies ++= lib.jfreechart)
  .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  /** UI Chart Annotations */
  lazy val widebaseUiChartAnnotations = Project(
    "widebase-ui-chart-annotations",
    file("widebase.ui.chart.annotations"))
  .settings(libraryDependencies ++= lib.jfreechart ++ lib.jodaTime)

  /** UI Chart Data */
  lazy val widebaseUiChartData = Project(
    "widebase-ui-chart-data",
    file("widebase.ui.chart.data"))

  /** UI Chart Data Time */
  lazy val widebaseUiChartDataTime = Project(
    "widebase-ui-chart-data-time",
    file("widebase.ui.chart.data.time"))
  .dependsOn(
    widebaseDbColumn,
    widebaseUiChartData)
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
  .dependsOn(
    widebaseDbColumn,
    widebaseUiChartData)
  .settings(libraryDependencies ++= lib.jfreechart)

  /** UI Chart Event */
  lazy val widebaseUiChartEvent = Project(
    "widebase-ui-chart-event",
    file("widebase.ui.chart.event"))
  .settings(libraryDependencies <+= lib.swing)

  /** UI Chart Utilities */
  lazy val widebaseUiChartUtil = Project(
    "widebase-ui-chart-util",
    file("widebase.ui.chart.util"))

  /** UI i18n */
  lazy val widebaseUiI18n = Project(
    "widebase-ui-i18n",
    file("widebase.ui.i18n"))

  /** UI Swing */
  lazy val widebaseUiSwing = Project(
    "widebase-ui-swing",
    file("widebase.ui.swing"))
    .dependsOn(
      widebaseDbTable,
      widebaseUiSwingEvent)
    .settings(libraryDependencies ++= lib.jodaTime ++ lib.moreswing)

  /** UI Swing Event */
  lazy val widebaseUiSwingEvent = Project(
    "widebase-ui-swing-event",
    file("widebase.ui.swing.event"))
    .settings(libraryDependencies <+= lib.swing)

  /** Workspace */
  lazy val widebaseWorkspace = Project(
    "widebase-workspace",
    file("widebase.workspace"))
  .dependsOn(
    widebaseUiSwing,
    widebaseWorkspaceEvent,
    widebaseWorkspaceRuntime)

  /** Workspace Event */
  lazy val widebaseWorkspaceEvent = Project(
    "widebase-workspace-event",
    file("widebase.workspace.event"))
  .settings(libraryDependencies <+= lib.swing)

  /** Workspace IDE */
  lazy val widebaseWorkspaceIde = Project(
    "widebase-workspace-ide",
    file("widebase.workspace.ide"))
  .dependsOn(
    widebaseDsl % "test",
    widebaseStreamSocketCq % "test",
    widebaseStreamSocketRq % "test",
    widebaseTestkit % "test",
    widebaseToolboxFinance % "test",
    widebaseUiI18n,
    widebaseWorkspaceIdeApp % "test",
    widebaseWorkspaceIdeCli % "test",
    widebaseWorkspaceIdeEditor % "test",
    widebaseWorkspaceIdeExplorer % "test",
    widebaseWorkspaceRuntime)
  .settings(libraryDependencies ++= lib.log ++
    Seq("org.bitbucket.t1ck" %% "t1ck-widebase-io-csv-swfx" % "0.1.2-SNAPSHOT" % "test") ++ /* REMOVE IT LATER */
    Seq("org.bitbucket.t1ck" %% "t1ck-math-fnrwarp" % "0.1.0-SNAPSHOT") ++ /* REMOVE IT LATER */
    Seq("com.jcraft" % "jsch" % "0.1.50" /* REMOVE IT LATER */))

  /** Workspace IDE App */
  lazy val widebaseWorkspaceIdeApp = Project(
    "widebase-workspace-ide-app",
    file("widebase.workspace.ide.app"))
  .dependsOn(widebaseWorkspace)
  .settings(libraryDependencies ++= lib.log)

  /** Workspace IDE CLI */
  lazy val widebaseWorkspaceIdeCli = Project(
    "widebase-workspace-ide-cli",
    file("widebase.workspace.ide.cli"))
  .dependsOn(
    widebaseWorkspace,
    widebaseWorkspaceRuntime)

  /** Workspace IDE Edit */
  lazy val widebaseWorkspaceIdeEditor = Project(
    "widebase-workspace-ide-editor",
    file("widebase.workspace.ide.editor"))
  .dependsOn(
    widebaseWorkspace,
    widebaseWorkspaceIdeApp,
    widebaseWorkspaceIdeEditorEvent,
    widebaseWorkspaceRuntime,
    widebaseWorkspaceUtil)

  /** Workspace IDE Editor Event */
  lazy val widebaseWorkspaceIdeEditorEvent = Project(
    "widebase-workspace-ide-editor-event",
    file("widebase.workspace.ide.editor.event"))
  .settings(libraryDependencies <+= lib.swing)

  /** Workspace IDE Explorer */
  lazy val widebaseWorkspaceIdeExplorer = Project(
    "widebase-workspace-ide-explorer",
    file("widebase.workspace.ide.explorer"))
  .dependsOn(
    widebaseWorkspace,
    widebaseWorkspaceIdeEditor,
    widebaseWorkspaceRuntime)

  /** Workspace Runtime */
  lazy val widebaseWorkspaceRuntime = Project(
    "widebase-workspace-runtime",
    file("widebase.workspace.runtime"))
  .dependsOn(widebaseWorkspaceUtil)
  .settings(libraryDependencies <+= lib.actors)
  .settings(libraryDependencies ++=
    lib.eval ++
    lib.interpreterPane ++
    lib.log ++
    lib.moreswing)

  /** Workspace Util */
  lazy val widebaseWorkspaceUtil = Project(
    "widebase-workspace-util",
    file("widebase.workspace.util"))
  .settings(libraryDependencies <+= lib.swing)

  /** Utilities */
  lazy val widebaseUtil = Project(
    "widebase-util",
    file("widebase.util"))
    .dependsOn(widebaseIoFilter)

  /** Home path */
  System.setProperty("widebase.home", System.getProperty("user.dir"))

  /** Log path (maybe deprecated, see javaOptions) */
  System.setProperty(
    "widebase.log",
    System.getProperty("widebase.home") + "/var/log")

  /** JAAS path */
  System.setProperty(
    "java.security.auth.login.config",
    System.getProperty("widebase.home") + "/etc/jaas.conf")

  /** Security policy */
  System.setProperty("java.security.manager", "true")
  System.setProperty(
    "java.security.policy",
    System.getProperty("widebase.home") + "/etc/java.policy")

  /** Build settings */
	def buildSettings = Seq(
		organization := "com.github.widebase",
		version := "0.3.4-SNAPSHOT",
    javaOptions ++= Seq(

      /** Log path */
      "-Dwidebase.log=" + System.getProperty("widebase.home") + "/var/log"),
    scalaVersion := "2.10.1",
    crossScalaVersions := Seq(
      "2.10.1",
      "2.9.2"),
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
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

