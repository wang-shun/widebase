package widebase.io.file.test

import java.io. { File, RandomAccessFile }

import net.liftweb.common. { Loggable, Logger }

import org.joda.time.LocalDateTime

import widebase.data.Datatype
import widebase.io.file.FileVariantWriter

/* Just a little example of Widebase file i/o.
 *
 * @author myst3r10n
 */
object Column extends Logger with Loggable {

  val debug = false

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    val amount = 250000

    val dir = new File("usr")

    if(!dir.exists)
      dir.mkdir

    val file = new File(dir.getPath + "/writeColumn")

    if(file.exists)
      file.delete

    val started = System.currentTimeMillis

    // Init file writer
    val channel = new RandomAccessFile(file.getPath, "rw").getChannel
    val writer = new FileVariantWriter(channel)

    // Toggle mode and write a String
    writer.mode = Datatype.String
    writer.write("Timestamp", true)

    // Toggle mode and write many timestamps
    writer.mode = Datatype.DateTime
    for(i <- 1 to amount)
      writer.write(new LocalDateTime)

    writer.close

    info("Written in " + diff(started, System.currentTimeMillis))

  }
}

