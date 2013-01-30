package widebase.io.file

import java.nio.channels.FileChannel

import widebase.io. { ToggleModeLike, TypedBufferLike, TypeMismatchException }
import widebase.io.filter.MapFilter
import widebase.io.filter.MapFilter.MapFilter

/** Map a [[java.nio.channels.FileChannel]] for datatypes.
 *
 * @param channel @see [[java.lang.channels.FileChannel]]
 * @param offset within the file at which the mapped region is to start; must be non-negative
 * @param size of the region to be mapped; must be non-negative and no greater than [[java.lang.Integer.MAX_VALUE]]
 * @param filter of mapper, default is [[widebase.io.filter.MapFilter.ReadOnly]]
 *
 * @author myst3r10n
 */
class FileTypedMapper(
  channel: FileChannel,
  offset: Long = 0L,
  size: Long = -1L)
  (implicit filter: MapFilter = MapFilter.ReadOnly)
  extends FileByteMapper(channel, offset, size)(filter)
  with ToggleModeLike
  with TypedBufferLike {

  import widebase.data
  import widebase.data.Datatype
  import widebase.data.Datatype.Datatype

  override def position: Int = {

    mode match {

      case mode if
        mode == Datatype.None ||
        mode == Datatype.Bool ||
        mode == Datatype.Byte => super.position

      case Datatype.Char => super.position +
        (charBuffer.position * data.sizeOf.char)

      case Datatype.Double => super.position +
        (doubleBuffer.position * data.sizeOf.double)

      case Datatype.Float => super.position +
        (floatBuffer.position * data.sizeOf.float)

      case Datatype.Int => super.position +
        (intBuffer.position * data.sizeOf.int)

      case Datatype.Long => super.position +
        (longBuffer.position * data.sizeOf.long)

      case Datatype.Short => super.position +
        (shortBuffer.position * data.sizeOf.short)

    }
  }

  override def position_=(replace: Int) {

    super.position = replace

    mode match {

      case replace if
        replace == Datatype.None ||
        replace == Datatype.Bool ||
        replace == Datatype.Byte =>

      case Datatype.Char => charBuffer = buffer.asCharBuffer
      case Datatype.Double => doubleBuffer = buffer.asDoubleBuffer
      case Datatype.Float => floatBuffer = buffer.asFloatBuffer
      case Datatype.Int => intBuffer = buffer.asIntBuffer
      case Datatype.Long => longBuffer = buffer.asLongBuffer
      case Datatype.Short => shortBuffer = buffer.asShortBuffer

    }
  }

  /** Reads [[scala.Boolean]] from buffer. */
  def readBool: Boolean = {

    val bool = read

    if(bool == (1: Byte))
      return true

    if(bool == (0: Byte))
      return false

    throw TypeMismatchException(mode, bool.toString)

  }

  /** Reads array of [[scala.Boolean]]s from buffer.
   *
   * @param length of [[scala.Boolean]] array to be read
  */
  def readBool(length: Int): Array[Boolean] = {

    val bools =
      for(bool <- read(length))
        yield {

          if(bool == (1: Byte))
            true
          else if(bool == (0: Byte))
            false
          else
            throw TypeMismatchException(mode, bool.toString)

        }

    bools

  }

  /** Reads [[scala.Short]] from buffer. */
  def readChar: Char = charBuffer.get

  /** Reads array of [[scala.Char]]s from buffer.
   *
   * @param length of [[scala.Char]] array to be read
  */
  def readChar(length: Int): Array[Char] = {

    val values = Array.ofDim[Char](length)

    charBuffer.get(values)

    values

  }

  /** Reads [[scala.Double]] from buffer. */
  def readDouble: Double = doubleBuffer.get

  /** Reads array of [[scala.Double]]s from buffer.
   *
   * @param length of [[scala.Double]] array to be read
  */
  def readDouble(length: Int): Array[Double] = {

    val values = Array.ofDim[Double](length)

    doubleBuffer.get(values)

    values

  }

  /** Reads [[scala.Float]] from buffer. */
  def readFloat: Float = floatBuffer.get

  /** Reads array of [[scala.Float]]s from buffer.
   *
   * @param length of [[scala.Float]] array to be read
  */
  def readFloat(length: Int): Array[Float] = {

    val values = Array.ofDim[Float](length)

    floatBuffer.get(values)

    values

  }

  /** Reads [[scala.Int]] from buffer. */
  def readInt: Int = intBuffer.get

  /** Reads array of [[scala.Int]]s from buffer.
   *
   * @param length of [[scala.Int]] array to be read
  */
  def readInt(length: Int): Array[Int] = {

    val values = Array.ofDim[Int](length)

    intBuffer.get(values)

    values

  }

  /** Reads [[scala.Long]] from buffer. */
  def readLong: Long = longBuffer.get

  /** Reads array of [[scala.Long]]s from buffer.
   *
   * @param length of [[scala.Long]] array to be read
  */
  def readLong(length: Int): Array[Long] = {

    val values = Array.ofDim[Long](length)

    longBuffer.get(values)

    values

  }

  /** Reads [[scala.Short]] from buffer. */
  def readShort: Short = shortBuffer.get

  /** Reads array of [[scala.Short]]s from buffer.
   *
   * @param length of [[scala.Short]] array to be read
  */
  def readShort(length: Int): Array[Short] = {

    val values = Array.ofDim[Short](length)

    shortBuffer.get(values)

    values

  }

  /** Writes [[scala.Boolean]] into buffer
   *
   * @param value to write
  */
  def write(value: Boolean) {

    if(value)
      super.write(1: Byte)
    else
      super.write(0: Byte)

  }

  /** Writes array of [[scala.Boolean]]s into buffer.
   *
   * @param values to write
  */
  def write(values: Array[Boolean]) {

    val bytes =
      for(value <- values)
        yield {

          if(value)
            (1: Byte)
          else
            (0: Byte)

        }

    write(bytes)

  }

  override def write(value: Byte) {

    super.write(value)

  }

  override def write(values: Array[Byte]) {

    super.write(values)

  }

  /** Writes [[scala.Char]] into buffer
   *
   * @param value to write
  */
  def write(value: Char) {

    charBuffer.put(value)

  }

  /** Writes array of [[scala.Char]]s into buffer
   *
   * @param values to write
  */
  def write(values: Array[Char]) {

    charBuffer.put(values)

  }

  /** Writes [[scala.Double]] into buffer
   *
   * @param value to write
  */
  def write(value: Double) {

    doubleBuffer.put(value)

  }

  /** Writes array of [[scala.Double]]s into buffer
   *
   * @param values to write
  */
  def write(values: Array[Double]) {

    doubleBuffer.put(values)

  }

  /** Writes [[scala.Float]] into buffer
   *
   * @param value to write
  */
  def write(value: Float) {

    floatBuffer.put(value)

  }

  /** Writes array of [[scala.Float]]s into buffer
   *
   * @param values to write
  */
  def write(values: Array[Float]) {

    floatBuffer.put(values)

  }

  /** Writes [[scala.Int]] into buffer
   *
   * @param value to write
  */
  def write(value: Int) {

    intBuffer.put(value)

  }

  /** Writes array of [[scala.Int]]s into buffer
   *
   * @param values to write
  */
  def write(values: Array[Int]) {

    intBuffer.put(values)

  }

  /** Writes [[scala.Long]] into buffer
   *
   * @param value to write
  */
  def write(value: Long) {

    longBuffer.put(value)

  }

  /** Writes array of [[scala.Long]]s into buffer
   *
   * @param values to write
  */
  def write(values: Array[Long]) {

    longBuffer.put(values)

  }

  /** Writes [[scala.Short]] into buffer
   *
   * @param value to write
  */
  def write(value: Short) {

    shortBuffer.put(value)

  }

  /** Writes array of [[scala.Short]]s into buffer
   *
   * @param values to write
  */
  def write(values: Array[Short]) {

    shortBuffer.put(values)

  }

  override protected def reposition {

    mode match {

      case mode if
        mode == Datatype.None ||
        mode == Datatype.Bool ||
        mode == Datatype.Byte =>

      case Datatype.Char => buffer.position(buffer.position +
        (charBuffer.position * data.sizeOf.char))

      case Datatype.Double => buffer.position(buffer.position +
        (doubleBuffer.position * data.sizeOf.double))

      case Datatype.Float => buffer.position(buffer.position +
        (floatBuffer.position * data.sizeOf.float))

      case Datatype.Int => buffer.position(buffer.position +
        (intBuffer.position * data.sizeOf.int))

      case Datatype.Long => buffer.position(buffer.position +
        (longBuffer.position * data.sizeOf.long))

      case Datatype.Short => buffer.position(buffer.position +
        (shortBuffer.position * data.sizeOf.short))

    }
  }

  override protected def review(replace: Datatype) {

    replace match {

      case replace if
        replace == Datatype.None ||
        replace == Datatype.Bool ||
        replace == Datatype.Byte =>

      case Datatype.Char => charBuffer = buffer.asCharBuffer
      case Datatype.Double => doubleBuffer = buffer.asDoubleBuffer
      case Datatype.Float => floatBuffer = buffer.asFloatBuffer
      case Datatype.Int => intBuffer = buffer.asIntBuffer
      case Datatype.Long => longBuffer = buffer.asLongBuffer
      case Datatype.Short => shortBuffer = buffer.asShortBuffer

    }
  }
}

