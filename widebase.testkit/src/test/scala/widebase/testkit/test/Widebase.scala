package widebase.testkit.test

import net.liftweb.common. { Loggable, Logger }

/* Test all Widebase units.
 *
 * @author myst3r10n
 */
object Widebase extends Logger with Loggable {

  def main(args: Array[String]) {

    // I/O
    Sequential.main(args)
    println("")
    Bulk.main(args)
    println("")
    Flushes.main(args)

    println("")

    // Core
    Column.main(args)
    println("")
    Record.main(args)
    println("")
    Segment.main(args)
    println("")
    Upsert.main(args)
    println("")
    Serialization.main(args)
    println("")
    Filter.main(args)
    println("")
    ParallelFilter.main(args)
    println("")
    Sort.main(args)

    println("")

    // DSL
    DslCheck.main(args)

    println("")

    // Performance
    AppendBenchmark.main(args)
    println("")
    Capacity.main(args)
    println("")
    TableBenchmark.main(args)

  }
}

