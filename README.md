Widebase
========

A column-oriented database for large time series, <a href="https://github.com/widebase/widebase-doc/raw/master/widebase.handbook/pdf/widebase-handbook.pdf">read more</a>.

# Build from source

To build this code, get and install Vario from https://github.com/vario/vario.

Use these commands to build:

    > git clone git@github.com:widebase/widebase.git
    > cd widebase
    > sbt publish-local

And specify as a dependency in your project file:

```scala
libraryDependencies += "widebase" %% "widebase-db" % "0.1.0-SNAPSHOT"
```

# Demos/Testing

Some demo/test codes are in `widebase.db/src/test/scala/widebase/db/test`. Example how to run `Record.scala` test:

    > sbt
    > project widebase-db
    > test:run-main widebase.db.test.Record

# Generating ScalaDoc

Use this command to generating docs:

    > sbt "project widebase" "unidoc"

Open `./target/scala-2.9.1/unidoc/index.html` with any web browser.
