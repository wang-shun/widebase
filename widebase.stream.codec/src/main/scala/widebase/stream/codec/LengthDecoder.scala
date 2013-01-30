package widebase.stream.codec

import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder

import vario.data.sizeOf

/** Decode length.
 *
 * @author myst3r10n
 */
class LengthDecoder
  extends LengthFieldBasedFrameDecoder(
    Int.MaxValue,
    0,
    sizeOf.int,
    0,
    sizeOf.int)

