package widebase.io.column

import java.io. { File, RandomAccessFile }

import widebase.db.column.TypedColumn
import widebase.io.VariantReader
import widebase.io.filter.StreamFilter

/** Load column from directory table.
 *
 * @param path of database
 *
 * @author myst3r10n
 */
abstract class FileColumnLoad(path: String) {

  import widebase.io

  /** Load column from directory table.
    *
    * @note Only seamless columns
    *
    * @param name of table
    * @param label label of column
    * @param indexable column
    * @param amount values to load, 0 load all
    * @param parted partition name
    * @param segmented path of segment
    *
    * @return [[widebase.db.column.TypedColumn]]
   */
  def apply(
    name: String,
    label: Any,
    indexable: Boolean = false,
    amount: Int = 0)
    (implicit parted: String = null, segmented: File = null): TypedColumn[_] = {

    var filename =
      if(segmented == null)
        path
      else
        segmented.getPath

    if(parted != null)
      filename += "/" + parted

    filename += "/" + name + "/" + label.toString

    val channel = new RandomAccessFile(filename, "r").getChannel

    var companion: VariantReader = null

    if(indexable) {

      def lookupCompanion: String = {

        val companionFiles = Array(
          new File(filename + ".sym"),
          new File(filename + ".str"))

        companionFiles.foreach(companionFile =>
          if(companionFile.exists)
            return companionFile.getPath)

        null

      }

      val companionFilename = lookupCompanion

      if(companionFilename != null) {

        val channel = new RandomAccessFile(companionFilename, "rw").getChannel

        companion = new VariantReader(channel, StreamFilter.None) {

          override val charset = props.charsets.loader

          override def capacity = props.capacities.loader
          override def order = props.orders.loader

        }
      }
    }

    val vreader = new VariantReader(channel, StreamFilter.None) {

      override val charset = props.charsets.loader

      override def capacity = props.capacities.loader
      override def order = props.orders.loader

    }

    val reader = new ColumnReader(vreader, companion)(filename)

    try {

      reader.read(amount)

    } finally {

      reader.close

    }
  }
}

