package widebase.io.column

import java.io. { File, RandomAccessFile }
import java.nio.channels.FileChannel

import vario.data.Datatype
import vario.file. { FileVariantMapper, FileVariantReader }
import vario.filter. { MapFilter, StreamFilter }

import widebase.db.column.VariantColumn

import widebase.io.filter. {

  InvalidMagicException,
  MagicId,
  MagicNotFoundException,
  WrongMagicException

}

/** Map columns from directory table.
 *
 * @param path of database
 *
 * @author myst3r10n
 */
class FileColumnMap(path: String) {

  import vario.data

  /** Map columns from directory table.
    *
    * @param name of table
    * @param label label of column
    * @param parted partition name
    * @param segmented path of segment
    *
    * @return [[widebase.db.column.VariantColumn]]
   */
  def apply(
    name: String,
    label: Any)
    (implicit parted: String = null, segmented: File = null): VariantColumn = {

    var filename =
      if(segmented == null)
        path
      else
        segmented.getPath

    if(parted != null)
      filename += "/" + parted

    filename +=  "/" + name + "/" + label.toString

    val reader = new FileVariantReader(
      new RandomAccessFile(filename, "r").getChannel,
      StreamFilter.None) {

      override val charset = props.charsets.mapper

      override def order = props.orders.mapper

    }

    // Read magic
    reader.mode = Datatype.String
    val magic =
      reader.readString(MagicId.Column.toString.getBytes(reader.charset).size)

    if(magic.isEmpty)
      throw MagicNotFoundException(filename)

    try {

      if(MagicId.withName(magic) != MagicId.Column)
        throw WrongMagicException(filename, magic)

    } catch {
      case e: NoSuchElementException =>
        throw InvalidMagicException(filename, magic)
    }

    // Read column type
    if(reader.mode != Datatype.Byte)
      reader.mode = Datatype.Byte

    val typeOf = Datatype(reader.read)

    // Read column length
    reader.mode = Datatype.Int
    val records = reader.readInt

    reader.close

    // Map column
    val offset = (MagicId.Column.toString.getBytes(reader.charset)).size + 1 +
      data.sizeOf.int

    val mapper = new FileVariantMapper(
      new RandomAccessFile(filename, "rw").getChannel,
      offset)(MapFilter.Private) {

      override val charset = props.charsets.mapper

      override def order = props.orders.mapper

    }

    mapper.close // Only file channel ;)

    var companion: FileChannel = null

    if(typeOf == Datatype.String)
      companion = new RandomAccessFile(filename + ".str", "rw").getChannel
    else if(typeOf == Datatype.Symbol)
      companion = new RandomAccessFile(filename + ".sym", "rw").getChannel

    new VariantColumn(typeOf)(mapper, records, companion)

  }
}

