package widebase.io.column

import java.io. { File, RandomAccessFile }

import vario.filter.StreamFilter
import vario.io.VariantReader

import widebase.db.column.TypedColumn

/** Loads columns from directory table.
 *
 * @param path of database
 *
 * @author myst3r10n
 */
abstract class FileColumnLoad(path: String) {

  import widebase.io

  /** Loads columns from directory table.
    *
    * @param name of table
    * @param label label of column
    * @param parted partition name
    * @param segmented path of segment
    *
    * @return [[widebase.db.column.TypedColumn]]
   */
  def apply(
    name: String,
    label: Any)
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

    val vreader = new VariantReader(channel, StreamFilter.None) {

      override val charset = props.charsets.loader

      override def capacity = props.capacities.loader
      override def order = props.orders.loader

    }

    val reader = new ColumnReader(vreader)(filename)

    try {

      reader.read

    } finally {

      reader.close

    }
  }
}

