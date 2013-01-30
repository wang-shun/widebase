package widebase.io.file

import java.nio.channels.FileChannel

import widebase.io.VariantWriter
import widebase.io.filter.StreamFilter
import widebase.io.filter.StreamFilter.StreamFilter

/** Write variant types into [[java.lang.channels.FileChannel]].
 *
 * @param channel @see [[java.lang.channels.FileChannel]]
 *
 * @author myst3r10n
 */
class FileVariantWriter(override protected val channel: FileChannel)
  extends VariantWriter(channel, StreamFilter.None)
  with FileChannelLike

