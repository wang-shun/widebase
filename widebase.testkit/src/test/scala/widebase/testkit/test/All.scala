package widebase.testkit.test

import net.liftweb.common. { Loggable, Logger }

/* Test all units.
 *
 * @author myst3r10n
 */
object All extends Logger with Loggable {

  def main(args: Array[String]) {

    Column.main(args)
    Record.main(args)
    Segment.main(args)
    Upsert.main(args)
    Serialization.main(args)
    Filter.main(args)
    ParallelFilter.main(args)
    Sort.main(args)

    DslCheck.main(args)

    Capacity.main(args)
    TableBenchmark.main(args)

  }
}

