package widebase.io.column

import java.io. { File, RandomAccessFile }
import java.nio.channels.FileChannel

import vario.data.Datatype
import vario.filter.StreamFilter
import vario.io.VariantWriter

import widebase.db.column.VariantColumn

/** Saves columns into directory table.
 *
 * @param path of database
 *
 * @author myst3r10n
 */
abstract class FileColumnSave(path: String) {

  /** Saves columns into directory table.
    *
    * @param name of table
    * @param label label of column
    * @param column self-explanatory
    * @param parted partition name
    * @param segmented path of segment
   */
  def apply(
    name: String,
    label: String,
    column: VariantColumn,
    seamless: Boolean = false)
    (implicit parted: String = null, segmented: File = null) {

    var dir =
      if(segmented == null)
        new File(path)
      else
        segmented

    if(parted != null) {

      dir = new File(dir.getPath + "/" + parted)

      if(!dir.exists)
        dir.mkdir

    }

    dir = new File(dir.getPath + "/" + name)

    if(!dir.exists)
      dir.mkdir

    val file = new File(dir.getPath + "/" + label)

    if(file.exists)
      file.delete

    val channel = new RandomAccessFile(file.getPath, "rw").getChannel
    channel.tryLock

    var companion: VariantWriter = null

    if(
      !seamless &&
      (column.typeOf == Datatype.String || column.typeOf == Datatype.Symbol)) {

      var companionFile: File = null

      if(column.typeOf == Datatype.Symbol)
        companionFile = new File(file.getPath + ".sym")
      else if(column.typeOf == Datatype.String)
        companionFile = new File(file.getPath + ".str")

      if(companionFile.exists)
        companionFile.delete

      val channel =
        new RandomAccessFile(companionFile.getPath, "rw").getChannel

      channel.tryLock

      companion = new VariantWriter(channel, StreamFilter.None) {

        override val charset = props.charsets.saver

        override def capacity = props.capacities.saver
        override def order = props.orders.saver

      }
    }

    val vwriter = new VariantWriter(channel, StreamFilter.None) {

      override val charset = props.charsets.saver

      override def capacity = props.capacities.saver
      override def order = props.orders.saver

    }

    val writer = new ColumnWriter(vwriter, companion)

    // Write columns
    writer.write(column)

    writer.close

  }
}

