@echo off

rem Use JAVA_HOME if set, otherwise look for java in PATH
if defined JAVA_HOME set JAVA=%JAVA_HOME%/bin/java
if not defined JAVA_HOME set JAVA=java

cd /d %~dp0%\..
set WIDEBASE_NOTIFY_HOME=%cd%

rem From Play framework
setlocal enabledelayedexpansion
set p=%WIDEBASE_PLANT_HOME%
set p=%p:\=/%
set fp=file:///!p: =%%20!

"%JAVA%" ^
  -Xmx1000m ^
  -XX:+UseFastAccessorMethods ^
  -Dsbt.boot.directory="%WIDEBASE_NOTIFY_HOME%/var/sbt/boot" ^
  -Dwidebase.log="%WIDEBASE_NOTIFY_HOME%/var/log" ^
  -jar "%WIDEBASE_NOTIFY_HOME%/lib/sbt-launch.jar" ^
  @"%fp%/etc/widebase.notify.configuration" ^
  "%*"

