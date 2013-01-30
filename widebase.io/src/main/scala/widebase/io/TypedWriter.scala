package widebase.io

import java.nio.channels.WritableByteChannel

import widebase.data.Datatype
import widebase.io.filter. { CompressionLevel, StreamFilter }
import widebase.io.filter.StreamFilter.StreamFilter

/** Write datatypes into [[java.nio.channels.WritableByteChannel]].
 *
 * @param channel @see [[java.lang.channels.WritableByteChannel]]
 * @param filter of writer, default is [[widebase.io.filter.StreamFilter.None]]
 * @param level of compression 0-9, default is [[CompressionLevel.Default]]
 *
 * @author myst3r10n
 */
class TypedWriter(
  channel: WritableByteChannel,
  filter: StreamFilter = StreamFilter.None,
  level: Int = CompressionLevel.Default)
  extends ByteWriter(channel, filter, level)
  with ToggleTypedMode {

  import widebase.data.Datatype.Datatype

  /** Flushes and forces any buffered output bytes to be written out. */
  override def flush {

    var backupMode: Datatype = null

    if(mode != Datatype.Byte) {

      backupMode = mode
      mode = Datatype.Byte

    }

    super.flush

    if(backupMode != null)
      mode = backupMode

  }

  /** Write [[scala.Boolean]] into buffer
   *
   * @param value self-explanatory
  */
  def write(value: Boolean) {

    if(value)
      super.write(1: Byte)
    else
      super.write(0: Byte)

  }

  /** Write array of [[scala.Boolean]]s into buffer
   *
   * @param values self-explanatory
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

    super.write(bytes)

  }

  override def write(value: Byte) {

    super.write(value)

  }

  /** Write [[scala.Char]] into buffer
   *
   * @param value self-explanatory
  */
  def write(value: Char) {

    if(!charBuffer.hasRemaining)
      flush

    charBuffer.put(value)

  }

  /** Write array of [[scala.Char]]s into buffer
   *
   * @param values self-explanatory
  */
  def write(values: Array[Char]) {

    if(values.length < charBuffer.remaining)
      charBuffer.put(values)
    else {

      var i = 0

      while(i < values.length) {

        var length = values.length - i

        if(length >= charBuffer.remaining)
          length = charBuffer.remaining

        charBuffer.put(values, i, length)

        if(!charBuffer.hasRemaining)
          flush

        i += length
    
      }
    }
  }

  /** Write [[scala.Double]] into buffer
   *
   * @param value self-explanatory
  */
  def write(value: Double) {

    if(!doubleBuffer.hasRemaining)
      flush

    doubleBuffer.put(value)

  }

  /** Write array of [[scala.Double]]s into buffer
   *
   * @param values self-explanatory
  */
  def write(values: Array[Double]) {
  
    if(values.length < doubleBuffer.remaining)
      doubleBuffer.put(values)
    else {

      var i = 0

      while(i < values.length) {

        var length = values.length - i

        if(length >= doubleBuffer.remaining)
          length = doubleBuffer.remaining

        doubleBuffer.put(values, i, length)

        if(!doubleBuffer.hasRemaining)
          flush

        i += length
    
      }
    }
  }

  /** Write [[scala.Float]] into buffer
   *
   * @param value self-explanatory
  */
  def write(value: Float) {

    if(!floatBuffer.hasRemaining)
      flush

    floatBuffer.put(value)

  }

  /** Write array of [[scala.Float]]s into buffer
   *
   * @param values self-explanatory
  */
  def write(values: Array[Float]) {
  
    if(values.length < floatBuffer.remaining)
      floatBuffer.put(values)
    else {

      var i = 0

      while(i < values.length) {

        var length = values.length - i

        if(length >= floatBuffer.remaining)
          length = floatBuffer.remaining

        floatBuffer.put(values, i, length)

        if(!floatBuffer.hasRemaining)
          flush

        i += length
    
      }
    }
  }

  /** Write [[scala.Int]] into buffer
   *
   * @param value self-explanatory
  */
  def write(value: Int) {

    if(!intBuffer.hasRemaining)
      flush

    intBuffer.put(value)

  }

  /** Write array of [[scala.Int]]s into buffer
   *
   * @param values self-explanatory
  */
  def write(values: Array[Int]) {
  
    if(values.length < intBuffer.remaining)
      intBuffer.put(values)
    else {

      var i = 0

      while(i < values.length) {

        var length = values.length - i

        if(length >= intBuffer.remaining)
          length = intBuffer.remaining

        intBuffer.put(values, i, length)

        if(!intBuffer.hasRemaining)
          flush

        i += length
    
      }
    }
  }

  /** Write [[scala.Long]] into buffer
   *
   * @param value self-explanatory
  */
  def write(value: Long) {

    if(!longBuffer.hasRemaining)
      flush

    longBuffer.put(value)

  }

  /** Write array of [[scala.Long]]s into buffer
   *
   * @param values self-explanatory
  */
  def write(values: Array[Long]) {
  
    if(values.length < longBuffer.remaining)
      longBuffer.put(values)
    else {

      var i = 0

      while(i < values.length) {

        var length = values.length - i

        if(length >= longBuffer.remaining)
          length = longBuffer.remaining

        longBuffer.put(values, i, length)

        if(!longBuffer.hasRemaining)
          flush

        i += length
    
      }
    }
  }

  /** Write [[scala.Short]] into buffer
   *
   * @param value self-explanatory
  */
  def write(value: Short) {

    if(!shortBuffer.hasRemaining)
      flush

    shortBuffer.put(value)

  }

  /** Write array of [[scala.Short]]s into buffer
   *
   * @param values self-explanatory
  */
  def write(values: Array[Short]) {
  
    if(values.length < shortBuffer.remaining)
      shortBuffer.put(values)
    else {

      var i = 0

      while(i < values.length) {

        var length = values.length - i

        if(length >= shortBuffer.remaining)
          length = shortBuffer.remaining

        shortBuffer.put(values, i, length)

        if(!shortBuffer.hasRemaining)
          flush

        i += length
    
      }
    }
  }
}

