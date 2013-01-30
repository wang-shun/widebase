package widebase.io.column

import java.io. { File, RandomAccessFile }

import widebase.data.Datatype
import widebase.io.VariantReader
import widebase.io.filter. { MagicId, StreamFilter }

/** Finds columns within directory table.
 *
 * @param path of database
 *
 * @author myst3r10n
 */
abstract class FileColumnFind(path: String) {

  /** Checks whether column exists within directory table.
    *
    * @note Supports optional partitioned tables.
    *
    * @param name of table
    * @param parted partition name
    * @param segmented path of segment
    *
    * @return true if exists, else false
   */
  def apply(name: String)
  (implicit parted: String = null, segmented: File = null): Boolean = {

    var file =
      if(segmented == null)
        new File(path)
      else
        segmented

    if(parted != null)
      file = new File(file.getPath + "/" + parted)

    file = new File(file.getPath + "/" + name + "/.d")

    if(!file.exists)
      return false

    val magicLength =
      MagicId.Column.toString.getBytes(props.charsets.finder).size

    val channel = new RandomAccessFile(file.getPath, "r").getChannel

    val reader = new VariantReader(channel, StreamFilter.None) {

      override val charset = props.charsets.finder

      override def capacity = magicLength
      override def order = props.orders.finder

    }

    // Read magic
    reader.mode = Datatype.String

    val magic =
      reader.readString(magicLength)

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
}

