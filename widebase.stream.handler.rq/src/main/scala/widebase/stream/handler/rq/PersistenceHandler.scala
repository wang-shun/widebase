package widebase.stream.handler.rq

import java.io. { ByteArrayInputStream, File, RandomAccessFile }
import java.sql.Timestamp

import org.joda.time. {

  DateTimeZone,
  LocalDate,
  LocalDateTime,
  LocalTime,
  Minutes,
  Seconds,
  YearMonth

}

import scala.collection.mutable.Map

import widebase.data.Datatype
import widebase.db.table.Table
import widebase.io.VariantWriter
import widebase.io.column.FileColumnSaver
import widebase.io.file.FileVariantWriter
import widebase.io.filter.MagicId

/** Handles rollback tables.
 *
 * @author myst3r10n
 */
trait PersistenceHandler {

  /** Path to rollback database. */
  val path: String

  /** Holds persistence writers. */
  protected val persistences: Map[String, PersistenceWriter]

  /** Flush records into rollback table.
   *
   * @param name of rollback table
   */
  def flush(name: String) {

    if(persistences.contains(name)) {

      val persistence = persistences(name)

      persistence.label.close
      persistence.columns.foreach(writer => writer.close)
      persistence.columns.clear

      persistence.symbolCompanions.values.foreach(companion =>
        if(companion.writer.isOpen)
          companion.writer.close)

      persistence.stringCompanions.values.foreach(companion =>
        if(companion.writer.isOpen)
          companion.writer.close)

      persistences -= name

    }
  }

  /** Upsert records into rollback table.
   *
   * @param name of rollback table
   * @param table self-explenatory
   */
  def upsert(name: String, table: Table) {

    if(!persistences.contains(name)) {

      val partition = new LocalDate(table.columns.head.typeOf match {

        case Datatype.Long => table.columns.head.head.asInstanceOf[Long]
        case Datatype.Month => table.columns.head.head.asInstanceOf[YearMonth].toLocalDate(1)
        case Datatype.Date => table.columns.head.head.asInstanceOf[LocalDate]
        case Datatype.Time => table.columns.head.head.asInstanceOf[LocalTime].getMillisOfDay
        case Datatype.DateTime => table.columns.head.head.asInstanceOf[LocalDateTime].toLocalDate
        case Datatype.Timestamp => table.columns.head.head.asInstanceOf[Timestamp].getTime

      } ).toString("yyyy-MM-dd")

      // Write labels
      val saver = new FileColumnSaver(path)
      saver.save(name, ".d", table.labels)(partition)

      val persistence = new PersistenceWriter(partition)

      // Location
      val dir = path + "/" + partition + "/" + name

      // Init label writer
      val outChannel = new RandomAccessFile(dir + "/.d", "rw").getChannel
      outChannel.tryLock
      persistence.label = new FileVariantWriter(outChannel)

      // Init column writers
      table.foreach { case (name, column) =>

        val file = new File(dir + "/" + name)

        if(file.exists)
          file.delete

        val channel = new RandomAccessFile(file.getPath, "rw").getChannel
        channel.tryLock
        persistence.columns += new FileVariantWriter(channel)

        val writer = persistence.columns.last

        // Write magic
        writer.mode = Datatype.String
        writer.write(MagicId.Column.toString)

        // Write column type
        writer.mode = Datatype.Byte
        writer.write(column.typeOf.id.toByte)

        // Write column length
        writer.mode = Datatype.Int
        writer.write(0)

        // Set column value type
        if(
          column.typeOf == Datatype.Symbol ||
          column.typeOf == Datatype.String)
          writer.mode = Datatype.Long
        else
          writer.mode = column.typeOf

        if(
          column.typeOf == Datatype.String ||
          column.typeOf == Datatype.Symbol) {

          var companion: File = null

          if(column.typeOf == Datatype.Symbol)
            companion = new File(file.getPath + ".sym")
          else if(column.typeOf == Datatype.String)
            companion = new File(file.getPath + ".str")

          if(companion.exists)
            companion.delete

          val channel =
            new RandomAccessFile(companion.getPath, "rw").getChannel
          channel.tryLock

          val writer = new FileVariantWriter(channel) {

            mode = column.typeOf

          }

          column.typeOf match {

            case Datatype.Symbol =>
              persistence.symbolCompanions += persistence.columns.size - 1 ->
                new persistence.Companion(writer, 0L)

            case Datatype.String =>
              persistence.stringCompanions += persistence.columns.size - 1 ->
                new persistence.Companion(writer, 0L)

          }
        }
      }

      persistences += name -> persistence

    }

    val persistence = persistences(name)
    persistence.records += 1

    var i = 0

    // Write column values
    table.columns.foreach { column => 

      column.head match {

        case value: Boolean => persistence.columns(i).write(value)
        case value: Byte => persistence.columns(i).write(value)
        case value: Char => persistence.columns(i).write(value)
        case value: Double => persistence.columns(i).write(value)
        case value: Float => persistence.columns(i).write(value)
        case value: Int => persistence.columns(i).write(value)
        case value: Long => persistence.columns(i).write(value)
        case value: Short => persistence.columns(i).write(value)
        case value: YearMonth => persistence.columns(i).write(value)
        case value: LocalDate => persistence.columns(i).write(value)
        case value: Minutes => persistence.columns(i).write(value)
        case value: Seconds => persistence.columns(i).write(value)
        case value: LocalTime => persistence.columns(i).write(value)
        case value: LocalDateTime => persistence.columns(i).write(value)
        case value: Timestamp => persistence.columns(i).write(value)

        case value: Symbol =>
          persistence.symbolCompanions(i).lastEnded +=
            value.toString.getBytes(
              persistence.symbolCompanions(i).writer.charset).size - 1

          persistence.columns(i).write(
            persistence.symbolCompanions(i).lastEnded)
          persistence.symbolCompanions(i).writer.write(value)

        case value: String =>
          persistence.stringCompanions(i).lastEnded +=
            value.getBytes(persistence.stringCompanions(i).writer.charset).size

          persistence.columns(i).write(
            persistence.stringCompanions(i).lastEnded)
          persistence.stringCompanions(i).writer.write(value)

      }

      i += 1

    }

    persistence.label.flush
    persistence.columns.foreach(writer => writer.flush)
    persistence.symbolCompanions.values.foreach(companion => companion.writer.flush)
    persistence.stringCompanions.values.foreach(companion => companion.writer.flush)

    val offset = MagicId.Column.toString.getBytes.size + 1

    persistence.columns.foreach { writer =>

      val backupMode = writer.mode

      writer.mode = Datatype.Byte
      writer.position = offset

      // Write column length
      writer.mode = Datatype.Int
      writer.write(persistence.records)

      writer.flush

      writer.mode = Datatype.Byte
      writer.position = writer.size

      writer.mode = backupMode

    }
  }
}

