package widebase.io.table

import java.io. { File, RandomAccessFile }

import vario.filter.StreamFilter
import vario.io.VariantReader

import widebase.db.table.Table
import widebase.io.column.ColumnReader

/** Loads tables from database.
 *
 * @param path of database
 *
 * @author myst3r10n
 */
abstract class FileTableLoad(path: String) {

  import vario.filter.StreamFilter.StreamFilter

  /** Load table from database.
    *
    * @param name of table
    * @param filter self-explanatory
    * @param segmented path of segment
    *
    * @return [[widebase.db.Table]]
   */
  def apply(
    name: String,
    filter: StreamFilter = props.filters.loader)
    (implicit segmented: File = null): Table = {

    val fileExtension =
      filter match {

        case StreamFilter.Gzip => ".gz"
        case _ => ""

      }

    var filename =
      if(segmented == null)
        path
      else
        segmented.getPath

    filename += "/" + name + fileExtension

    val channel = new RandomAccessFile(filename, "r").getChannel

    val vreader = new VariantReader(channel, filter) {

      override val charset = props.charsets.loader

      override def order = props.orders.loader

    }

    val reader = new ColumnReader(vreader)(filename)

    val table = new Table

    // Read column labels
    val labels = reader.read.strings

    // Read column values
    labels.foreach(label => table ++= (label, reader.read))

    reader.close

    table

  }
}

