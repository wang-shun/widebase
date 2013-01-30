package widebase.stream.codec.cq

import org.jboss.netty.buffer.ChannelBuffer

import widebase.db.table.Table

/** Decode message.
 *
 * @author myst3r10n
 */
trait MessageTypeDecoder extends widebase.stream.codec.MessageTypeDecoder {

  import vario.data.sizeOf

  /** Read table from buffer.
   *
   * @param buffer of channel
   *
   * @return table or `null`
   */
  protected def readTable(buffer: ChannelBuffer): Table = {

    if(buffer.readableBytes < sizeOf.int) {

      buffer.resetReaderIndex
      return null

    }

    val length = buffer.readInt

    if(buffer.readableBytes < length) {

      buffer.resetReaderIndex
      return null

    }

    val decodedTable = Array.ofDim[Byte](length)

    buffer.readBytes(decodedTable)

    Table.fromBytes(decodedTable)

  }
}

