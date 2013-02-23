package widebase.testkit.test

import net.liftweb.common. { Loggable, Logger }

object TableBenchmark extends Logger with Loggable {

  // Init DB
  val dbi =
    widebase.db.instance(System.getProperty("user.dir") + "/usr/data/test/db")

  // Init API
  import dbi.tables._
  import widebase.db.table.Table

  // Init DSL
  import widebase.dsl.conversion._
  import widebase.dsl.datatype._
  import widebase.dsl.function._

  // Init Testkit
  import widebase.testkit._

  def main(args: Array[String]) {

    val records = 25000

    bulkTableReadIterateWrite(records)
    println("")
    userTableReadIterateWrite(records)

  }

  def bulkTableReadIterateWrite(records: Int) {

    println("Bulk table with " + (records / 1000) + " K")

    var started = 0L

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

    val fixedTime = new org.joda.time.LocalTime

    started = System.currentTimeMillis
    for(i <- 1 to records) {

      timeCol += fixedTime
      symbolCol += "a"
      priceCol += 12.34f
      sizeCol += 100

    }
    println("Bulk table filled in " + diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    save("bulkTrade", trade)
    println("Bulk table saved in " + diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    save.dir("bulkTradeDir", trade)
    println("Bulk table dir saved in " + diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    val loaded = load("bulkTrade")
    println("Bulk table loaded in " + diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    val mapped = map("bulkTradeDir")
    println("Bulk table dir mapped in " + diff(started, System.currentTimeMillis))

    timeCol = loaded("time").T
    symbolCol = loaded("symbol").S
    priceCol = loaded("price").f
    sizeCol = loaded("size").i

    started = System.currentTimeMillis

    for(r <- 0 to loaded.records.length - 1) {

      assert(timeCol(r) == fixedTime, error("Value unexpected: " + timeCol(r)))
      assert(symbolCol(r) == "a", error("Value unexpected: " + symbolCol(r)))
      assert(priceCol(r) == 12.34f, error("Value unexpected: " + priceCol(r)))
      assert(sizeCol(r) == 100, error("Value unexpected: " + sizeCol(r)))

    }

    println("Bulk table for(<-) in " + diff(started, System.currentTimeMillis))

    loaded.columns.foreach(column => column.clear)

    timeCol = mapped("time").T
    symbolCol = mapped("symbol").S
    priceCol = mapped("price").f
    sizeCol = mapped("size").i

    started = System.currentTimeMillis

    for(r <- 0 to mapped.records.length - 1) {

      assert(timeCol(r) == fixedTime, error("Value unexpected: " + timeCol(r)))
      assert(symbolCol(r) == "a", error("Value unexpected: " + symbolCol(r)))
      assert(priceCol(r) == 12.34f, error("Value unexpected: " + priceCol(r)))
      assert(sizeCol(r) == 100, error("Value unexpected: " + sizeCol(r)))

    }

    println("Bulk table map for(<-) in " + diff(started, System.currentTimeMillis))

  }

  def userTableReadIterateWrite(records: Int) {

    println("User table with " + (records / 1000) + " K")

    var started = 0L

    var trade = Table(string("time", "symbol", "price", "size"))

    var counter = 0
    val countTo = 100000
    var multiplier = 0
    val fixedTime = new org.joda.time.LocalTime

    started = System.currentTimeMillis

    for(i <- 1 to records)
      trade += (fixedTime, "a", 12.34f, 100)

    println("User table filled in " + diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    save("userTrade", trade)
    println("User table saved in " + diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    save.dir("userTradeDir", trade)
    println("User table dir saved in " + diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    val loaded = load("userTrade")
    println("User table loaded in " + diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    val mapped = map("userTradeDir")
    println("User table dir mapped in " + diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis

    loaded.records.foreach { record =>

      assert(record("time") == fixedTime, error("Value unexpected: " + record("time")))
      assert(record("symbol") == "a", error("Value unexpected: " + record("symbol")))
      assert(record("price") == 12.34f, error("Value unexpected: " + record("price")))
      assert(record("size") == 100, error("Value unexpected: " + record("size")))

    }

    println("User table foreach in " + diff(started, System.currentTimeMillis))

    loaded.columns.foreach(column => column.clear)

    started = System.currentTimeMillis

    mapped.records.foreach { record =>

      assert(record("time") == fixedTime, error("Value unexpected: " + record("time")))
      assert(record("symbol") == "a", error("Value unexpected: " + record("symbol")))
      assert(record("price") == 12.34f, error("Value unexpected: " + record("price")))
      assert(record("size") == 100, error("Value unexpected: " + record("size")))

    }

    println("User table map foreach in " + diff(started, System.currentTimeMillis))

  }
}

