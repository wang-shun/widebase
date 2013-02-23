package widebase

/** Testkit package.
 *
 * @author myst3r10n
 */
package object testkit {

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def io(implicit args: Array[String] = Array[String]()) {

    println("")
    println("// Sequential I/O")
    println("")
    Sequential.main(args)
    println("")
    println("// Bulk I/O")
    println("")
    Bulk.main(args)
    println("")
    println("// Flushes I/O")
    println("")
    Flushes.main(args)

  }

  def core(implicit args: Array[String] = Array[String]()) {

    println("")
    println("// Column (Core)")
    println("")
    Column.main(args)
    println("")
    println("// Record (Core)")
    println("")
    Record.main(args)
    println("")
    println("// Segment (Core)")
    println("")
    Segment.main(args)
    println("")
    println("// Upsert (Core)")
    println("")
    Upsert.main(args)
    println("")
    println("// Serialization (Core)")
    println("")
    Serialization.main(args)
    println("")
    println("// Filter (Core)")
    println("")
    Filter.main(args)
    println("")
    println("// Parallel Filter (Core)")
    println("")
    ParallelFilter.main(args)
    println("")
    println("// Sort (Core)")
    println("")
    Sort.main(args)

  }

  def dsl(implicit args: Array[String] = Array[String]()) {

    println("")
    println("// Check (DSL)")
    println("")
    DslCheck.main(args)

  }

  def performance(implicit args: Array[String] = Array[String]()) {

    println("")
    println("// Buffer Sizes")
    println("")
    Capacity.main(args)
    println("")
    println("// Append Benchmark")
    println("")
    AppendBenchmark.main(args)
    println("")
    println("// Table Benchmark")
    println("")
    TableBenchmark.main(args)

  }
}

