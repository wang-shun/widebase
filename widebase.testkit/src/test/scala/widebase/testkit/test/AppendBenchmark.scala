package widebase.testkit.test

import net.liftweb.common. { Loggable, Logger }

import org.joda.time.LocalTime

import scala.collection.mutable.ArrayBuffer

import widebase.db.table.Table

object AppendBenchmark extends Logger with Loggable {

  // Init DB
  val dbi =
    widebase.db.instance(System.getProperty("user.dir") + "/usr/data/test/db")

  // Init API
  import dbi.tables._

  // Init DSL
  import widebase.dsl.conversion._
  import widebase.dsl.datatype._
  import widebase.dsl.function._

  def diff(started: Long, records: Int) =
    "%.3f records mio/sec".format((((records / (System.currentTimeMillis - started).toDouble) * 1000) / 1000000))

  def main(args: Array[String]) {

    val records = 1000000

    tableInsert(records)

  }

  def tableInsert(records: Int) {

    println("Table insert with " + (records / 1000) + " K")

    var started = 0L

    val fixedTime = new org.joda.time.LocalTime

    var trade = Table(
      string("time", "symbol", "price", "size"),
      time(),
      string(),
      float(),
      int())

    var timeCol = trade("time").T
    var symbolCol = trade("symbol").S
    var priceCol = trade("price").f
    var sizeCol = trade("size").i

    var timeBulk = ArrayBuffer[LocalTime]()
    var symbolBulk = ArrayBuffer[String]()
    var priceBulk = ArrayBuffer[Float]()
    var sizeBulk = ArrayBuffer[Int]()

    // Allocate Memory
    for(i <- 1 to records) {

      timeCol += fixedTime
      symbolCol += "s"
      priceCol += 12.34f
      sizeCol += 100

      timeBulk += fixedTime
      symbolBulk += "s"
      priceBulk += 12.34f
      sizeBulk += 100

    }

    timeCol.clear
    symbolCol.clear
    priceCol.clear
    sizeCol.clear

    timeBulk.clear
    symbolBulk.clear
    priceBulk.clear
    sizeBulk.clear

    // Run insertion now
    started = System.currentTimeMillis
    for(i <- 1 to records) {

      timeCol += fixedTime
      symbolCol += "s"
      priceCol += 12.34f
      sizeCol += 100

    }
    println("Insert +1: " + diff(started, records))

    timeCol.clear
    symbolCol.clear
    priceCol.clear
    sizeCol.clear

    for(i <- 1 to 10) {

      timeBulk += fixedTime
      symbolBulk += "s"
      priceBulk += 12.34f
      sizeBulk += 100

    }

    var _records = records / 10

    started = System.currentTimeMillis
    for(i <- 1 to _records) {

      timeCol ++= timeBulk
      symbolCol ++= symbolBulk
      priceCol ++= priceBulk
      sizeCol ++= sizeBulk

    }
    println("Bulk insert +10: " + diff(started, records))

    timeCol.clear
    symbolCol.clear
    priceCol.clear
    sizeCol.clear

    timeBulk.clear
    symbolBulk.clear
    priceBulk.clear
    sizeBulk.clear

    for(i <- 1 to 100) {

      timeBulk += fixedTime
      symbolBulk += "s"
      priceBulk += 12.34f
      sizeBulk += 100

    }

    _records = records / 100

    started = System.currentTimeMillis
    for(i <- 1 to _records) {

      timeCol ++= timeBulk
      symbolCol ++= symbolBulk
      priceCol ++= priceBulk
      sizeCol ++= sizeBulk

    }
    println("Bulk insert +100: " + diff(started, records))

    timeCol.clear
    symbolCol.clear
    priceCol.clear
    sizeCol.clear

    timeBulk.clear
    symbolBulk.clear
    priceBulk.clear
    sizeBulk.clear

    for(i <- 1 to 1000) {

      timeBulk += fixedTime
      symbolBulk += "s"
      priceBulk += 12.34f
      sizeBulk += 100

    }

    _records = records / 1000

    started = System.currentTimeMillis
    for(i <- 1 to _records) {

      timeCol ++= timeBulk
      symbolCol ++= symbolBulk
      priceCol ++= priceBulk
      sizeCol ++= sizeBulk

    }
    println("Bulk insert +1000: " + diff(started, records))

    timeCol.clear
    symbolCol.clear
    priceCol.clear
    sizeCol.clear

    timeBulk.clear
    symbolBulk.clear
    priceBulk.clear
    sizeBulk.clear

    for(i <- 1 to 10000) {

      timeBulk += fixedTime
      symbolBulk += "s"
      priceBulk += 12.34f
      sizeBulk += 100

    }

    _records = records / 10000

    started = System.currentTimeMillis
    for(i <- 1 to _records) {

      timeCol ++= timeBulk
      symbolCol ++= symbolBulk
      priceCol ++= priceBulk
      sizeCol ++= sizeBulk

    }
    println("Bulk insert +10000: " + diff(started, records))

    timeCol.clear
    symbolCol.clear
    priceCol.clear
    sizeCol.clear

    timeBulk.clear
    symbolBulk.clear
    priceBulk.clear
    sizeBulk.clear

    for(i <- 1 to 100000) {

      timeBulk += fixedTime
      symbolBulk += "s"
      priceBulk += 12.34f
      sizeBulk += 100

    }

    _records = records / 100000

    started = System.currentTimeMillis
    for(i <- 1 to _records) {

      timeCol ++= timeBulk
      symbolCol ++= symbolBulk
      priceCol ++= priceBulk
      sizeCol ++= sizeBulk

    }
    println("Bulk insert +100000: " + diff(started, records))

    timeCol.clear
    symbolCol.clear
    priceCol.clear
    sizeCol.clear

    timeBulk.clear
    symbolBulk.clear
    priceBulk.clear
    sizeBulk.clear

    for(i <- 1 to 1000000) {

      timeBulk += fixedTime
      symbolBulk += "s"
      priceBulk += 12.34f
      sizeBulk += 100

    }

    _records = records / 1000000

    started = System.currentTimeMillis
    for(i <- 1 to _records) {

      timeCol ++= timeBulk
      symbolCol ++= symbolBulk
      priceCol ++= priceBulk
      sizeCol ++= sizeBulk

    }
    println("Bulk insert +100000: " + diff(started, records))

    timeCol.clear
    symbolCol.clear
    priceCol.clear
    sizeCol.clear

    timeBulk.clear
    symbolBulk.clear
    priceBulk.clear
    sizeBulk.clear

  }
}

