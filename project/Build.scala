import sbt._
import Keys._

object WidebaseBuild extends Build {

  import Dependency._

  override val settings = super.settings ++ buildSettings ++ publishSettings

  lazy val widebase = Project(
    "widebase",
    file("."),
    settings = Defaults.defaultSettings ++ Unidoc.settings) aggregate(
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
      widebaseUtil)

  /** Collection Mutable */
  lazy val widebaseCollectionMutable = Project(
    "widebase-collection-mutable",
    file("widebase.collection.mutable")) dependsOn(widebaseIoFile)

  /** Data */
  lazy val widebaseData = Project("widebase-data", file("widebase.data")) settings(
    libraryDependencies ++= lib.jodaTime)

  /** DB */
  lazy val widebaseDb = Project("widebase-db", file("widebase.db")) dependsOn(
    widebaseIoTable) settings(libraryDependencies ++= testlib.log)

  /** DB Column */
  lazy val widebaseDbColumn = Project(
    "widebase-db-column",
    file("widebase.db.column")) dependsOn(
    widebaseCollectionMutable,
    widebaseUtil)

  /** DB Table */
  lazy val widebaseDbTable = Project(
    "widebase-db-table",
    file("widebase.db.table")) dependsOn(widebaseIoColumn)

  /** DB Table */
  lazy val widebaseDsl = Project(
    "widebase-dsl",
    file("widebase.dsl")) dependsOn(widebaseIoCsv)

  /** I/O */
  lazy val widebaseIo = Project(
    "widebase-io",
    file("widebase.io")) dependsOn(widebaseData, widebaseIoFilter)

  /** I/O Column */
  lazy val widebaseIoColumn = Project(
    "widebase-io-column",
    file("widebase.io.column")) dependsOn(
      widebaseDbColumn,
      widebaseIoFilter,
      widebaseUtil)

  /** I/O CSV */
  lazy val widebaseIoCsv = Project(
    "widebase-io-csv",
    file("widebase.io.csv")) dependsOn(
    widebaseDb,
    widebaseIoCsvFilter) settings(libraryDependencies ++= lib.log)

  /** I/O CSV Filter */
  lazy val widebaseIoCsvFilter = Project(
    "widebase-io-csv-filter",
    file("widebase.io.csv.filter")) dependsOn(widebaseDbTable)

  /** I/O File */
  lazy val widebaseIoFile = Project(
    "widebase-io-file",
    file("widebase.io.file")) dependsOn(widebaseIo) settings(
    libraryDependencies ++= testlib.log)

  /** I/O Filter */
  lazy val widebaseIoFilter = Project(
    "widebase-io-filter",
    file("widebase.io.filter"))

  /** I/O Table */
  lazy val widebaseIoTable = Project(
    "widebase-io-table",
    file("widebase.io.table")) dependsOn(widebaseDbTable)

  /** Notify */
  lazy val widebaseNotify = Project(
    "widebase-notify",
    file("widebase.notify")) dependsOn(widebaseStreamSocketRq) settings(
    resolvers <+= sbtResolver,
    libraryDependencies ++= lib.commonsCli ++ lib.sbtLauncher)

  /** Plant */
  lazy val widebasePlant = Project(
    "widebase-plant",
    file("widebase.plant")) dependsOn(widebaseStreamSocketRq) settings(
    resolvers <+= sbtResolver,
    libraryDependencies ++= lib.commonsCli ++ lib.sbtLauncher)

  /** Stream Codec */
  lazy val widebaseStreamCodec = Project(
    "widebase-stream-codec",
    file("widebase.stream.codec")) dependsOn(
    widebaseData,
    widebaseUtil) settings(libraryDependencies ++= lib.netty)

  /** Stream Codec CQ */
  lazy val widebaseStreamCodecCq = Project(
    "widebase-stream-codec-cq",
    file("widebase.stream.codec.cq")) dependsOn(
    widebaseDbTable,
    widebaseStreamCodec)

  /** Stream Codec RQ */
  lazy val widebaseStreamCodecRq = Project(
    "widebase-stream-codec-rq",
    file("widebase.stream.codec.rq"))  dependsOn(widebaseStreamCodec)

  /** Stream Handler */
  lazy val widebaseStreamHandler = Project(
    "widebase-stream-handler",
    file("widebase.stream.handler")) dependsOn(widebaseStreamCodec) settings(
    libraryDependencies ++= lib.jaas ++ lib.log)

  /** Stream Handler CQ */
  lazy val widebaseStreamHandlerCq = Project(
    "widebase-stream-handler-cq",
    file("widebase.stream.handler.cq")) dependsOn(
    widebaseStreamCodecCq,
    widebaseStreamHandler)

  /** Stream Handler RQ */
  lazy val widebaseStreamHandlerRq = Project(
    "widebase-stream-handler-rq",
    file("widebase.stream.handler.rq")) dependsOn(
    widebaseDbTable,
    widebaseStreamCodecRq,
    widebaseStreamHandler) settings(
    libraryDependencies ++= lib.eval ++ lib.log)

  /** Stream Socket */
  lazy val widebaseStreamSocket = Project(
    "widebase-stream-socket",
    file("widebase.stream.socket")) dependsOn(widebaseStreamHandler)

  /** Stream Socket CQ */
  lazy val widebaseStreamSocketCq = Project(
    "widebase-stream-socket-cq",
    file("widebase.stream.socket.cq")) dependsOn(
    widebaseStreamHandlerCq,
    widebaseStreamSocket)

  /** Stream Socket RQ */
  lazy val widebaseStreamSocketRq = Project(
    "widebase-stream-socket-rq",
    file("widebase.stream.socket.rq")) dependsOn(
    widebaseDsl % "test",
    widebaseStreamHandlerRq,
    widebaseStreamSocket)

  /** Utilities */
  lazy val widebaseUtil = Project(
    "widebase-util",
    file("widebase.util")) dependsOn(widebaseIoFilter)

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
		version := "0.3.2",
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

