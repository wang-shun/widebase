@echo off

cd /d %~dp0%\..
set WIDEBASE_HOME=%cd%

start javaw ^
  -Dsbt.main.class=sbt.ScriptMain ^
  -Dsbt.boot.directory="%WIDEBASE_HOME%/var/sbt/boot" ^
  -jar "%WIDEBASE_HOME%/lib/sbt-launch.jar" ^
  -Dwidebase.home="%WIDEBASE_HOME%" ^
  -Dwidebase.log="%WIDEBASE_HOME%/var/log" ^
  "%WIDEBASE_HOME%/bin/widebase-ide-script" ^
  "%*"

