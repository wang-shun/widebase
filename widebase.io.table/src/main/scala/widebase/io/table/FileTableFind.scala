package widebase.io.table

import java.io. { File, RandomAccessFile }

import vario.data.Datatype
import vario.filter.StreamFilter
import vario.io.VariantReader

import widebase.io.column.FileColumnFinder
import widebase.io.filter.MagicId

/** Finds tables within database.
 *
 * @param path of database
 *
 * @author myst3r10n
 */
abstract class FileTableFind(path: String) {

  import vario.filter.StreamFilter.StreamFilter

  /** Checks whether table exists.
    *
    * @param name of table
    * @param filter self-explanatory
    * @param segmented path of segment
    *
    * @return true if exists, else false
   */
  def apply(
    name: String,
    filter: StreamFilter = props.filters.finder)
    (implicit segmented: File = null): Boolean = {

    val fileExtension =
      filter match {

        case StreamFilter.Gzip => ".gz"
        case StreamFilter.Zlib => ".zip"
        case _ => ""

      }

    var dir =
      if(segmented == null)
        path
      else
        segmented.getPath

    val file = new File(dir + "/" + name + fileExtension)

    if(!file.exists)
      return false

    val magicLength =
      MagicId.Column.toString.getBytes(props.charsets.finder).size

    val channel = new RandomAccessFile(file.getPath, "r").getChannel

    val reader = new VariantReader(channel, filter) {

      override val charset = props.charsets.finder

      override def capacity = magicLength
      override def order = props.orders.finder

    }

    // Read magic
    reader.mode = Datatype.String

    val magic = reader.readString(magicLength)

    try {

      if(magic.isEmpty)
        return false

      if(MagicId.withName(magic) != MagicId.Column)
        return false

    } catch {

      case e: NoSuchElementException => return false

    } finally {

      reader.close

    }

    true

  }

  /** Checks whether directory table exists.
    *
    * @note Supports optional partitioned tables.
    *
    * @param name of table
    * @param parted partition name
    * @param segmented path of segment
    *
    * @return true if exists, else false
   */
  def dir(name: String)
  (implicit parted: String = null, segmented: File = null): Boolean =
    (new FileColumnFinder(path)).find(name)(parted, segmented)

}

