@echo off

cd /d %~dp0%\..
set WIDEBASE_HOME=%cd%

java ^
  -Xmx1024m ^
  -XX:+UseFastAccessorMethods ^
  -Dsbt.main.class=sbt.ConsoleMain ^
  -Dsbt.boot.directory="%WIDEBASE_HOME%/var/sbt/boot" ^
  -jar "%WIDEBASE_HOME%/lib/sbt-launch.jar" ^
  -Dwidebase.home="%WIDEBASE_HOME%" ^
  -Dwidebase.log="%WIDEBASE_HOME%/var/log" ^
  -Dlog4j.configuration="%WIDEBASE_HOME%/etc/widebase-cli-log4j.xml" ^
  "Sonatype OSS at https://oss.sonatype.org/content/groups/public" ^
  "Typesafe Repository at http://repo.typesafe.com/typesafe/releases/" ^
  "com.github.widebase%%%%widebase-dsl%%0.3.4-SNAPSHOT" ^
  "com.github.widebase%%%%widebase-stream-socket-cq%%0.3.4-SNAPSHOT" ^
  "com.github.widebase%%%%widebase-stream-socket-rq%%0.3.4-SNAPSHOT" ^
  "com.github.widebase%%%%widebase-testkit%%0.3.4-SNAPSHOT" ^
  "com.github.widebase%%%%widebase-ui%%0.3.4-SNAPSHOT" ^
  "%*"

