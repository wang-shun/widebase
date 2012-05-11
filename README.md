# Widebase

This page contains build instructions.
A introduction about Widebase and more at http://widebase.github.com/.

## Using Widebase with SBT

SBT installation instructions on https://github.com/harrah/xsbt/wiki.

### Build from source

Use these commands to build:

    > git clone git@github.com:widebase/widebase.git
    > cd widebase
    > sbt publish-local

### Generating ScalaDoc

Use this command to generating docs:

    > sbt "project widebase" "unidoc"

Open `./target/scala-2.9.1/unidoc/index.html` with any web browser.
