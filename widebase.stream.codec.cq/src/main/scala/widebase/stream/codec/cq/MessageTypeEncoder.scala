package widebase.stream.codec.cq

import org.jboss.netty.buffer.ChannelBuffer

import widebase.db.table.Table

/** Encode message.
 *
 * @author myst3r10n
 */
trait MessageTypeEncoder extends widebase.stream.codec.MessageTypeEncoder {

  import vario.data.sizeOf

  /** Write table into buffer.
   *
   * @param buffer of channel
   * @param table self-explanatory
   */
  protected def writeTable(buffer: ChannelBuffer, table: Table) {

    val encodedTable = table.toBytes()(table.columns.last.length)

    buffer.writeInt(encodedTable.length)
    buffer.writeBytes(encodedTable)

  }
}

