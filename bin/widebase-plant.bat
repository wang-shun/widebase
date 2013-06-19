@echo off

rem Use JAVA_HOME if set, otherwise look for java in PATH
if defined JAVA_HOME set JAVA=%JAVA_HOME%/bin/java
if not defined JAVA_HOME set JAVA=java

cd /d %~dp0%\..
set WIDEBASE_PLANT_HOME=%cd%

rem From Play framework
setlocal enabledelayedexpansion
set p=%WIDEBASE_PLANT_HOME%
set p=%p:\=/%
set fp=file:///!p: =%%20!

"%JAVA%" ^
  -Xmx1000m ^
  -XX:+UseFastAccessorMethods ^
  -Dsbt.boot.directory="%WIDEBASE_PLANT_HOME%/var/sbt/boot" ^
  -Dwidebase.log="%WIDEBASE_PLANT_HOME%/var/log" ^
  -Djava.security.auth.login.config="%WIDEBASE_PLANT_HOME%/etc/jaas.conf" ^
  -Djava.security.manager ^
  -Djava.security.policy="%WIDEBASE_PLANT_HOME%/etc/java.policy" ^
  -jar "%WIDEBASE_PLANT_HOME%/lib/sbt-launch.jar" ^
  @"%fp%/etc/widebase.plant.configuration" ^
  "%*"

