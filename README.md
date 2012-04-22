Widebase
========

Column-oriented database for large time series...

# Build from source

To build this code, get and install SBT from https://github.com/harrah/xsbt and Vario from https://github.com/vario/vario.

Use these commands to build:

    > git clone git@github.com:widebase/widebase.git
    > cd widebase
    > sbt publish-local

And specify as a dependency in your project file:

```scala
libraryDependencies ++= "widebase" %% "widebase-db" % "0.1.0-SNAPSHOT"
```

# Generating ScalaDoc

Use this command to generating docs:

    > sbt "project widebase" "unidoc"

Open `./target/scala-2.9.1/unidoc/index.html` with any web browser.
