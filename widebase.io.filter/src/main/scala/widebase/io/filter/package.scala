package widebase.io

import java.nio.channels.FileChannel.MapMode

/** widebase's filter package.
 *
 * @author myst3r10n
 */
package object filter {

  import ByteOrder.ByteOrder
  import MapFilter.MapFilter

  /** Converts byte order to [[java.nio.ByteOrder]].
   *
   * @param order self-explanatory
   *
   * @return [[java.nio.ByteOrder]]
   */
  def asJavaByteOrder(order: ByteOrder) =
    order match {

      case ByteOrder.BigEndian => java.nio.ByteOrder.BIG_ENDIAN
      case ByteOrder.LittleEndian => java.nio.ByteOrder.LITTLE_ENDIAN
      case ByteOrder.Native => java.nio.ByteOrder.nativeOrder

    }

  /** Converts map filter to [[java.nio.channels.FileChannel.MapMode]].
   *
   * @param filter self-explanatory
   *
   * @return [[java.nio.channels.FileChannel.MapMode]]
   */
  def asJavaMapMode(filter: MapFilter) =
    filter match {

      case MapFilter.Private => MapMode.PRIVATE
      case MapFilter.ReadOnly => MapMode.READ_ONLY
      case MapFilter.ReadWrite => MapMode.READ_WRITE

    }
}

