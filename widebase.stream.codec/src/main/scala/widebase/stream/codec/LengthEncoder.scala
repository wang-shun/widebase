package widebase.stream.codec

import org.jboss.netty.handler.codec.frame.LengthFieldPrepender

import widebase.data.sizeOf

/** Encode length.
 *
 * @author myst3r10n
 */
class LengthEncoder extends LengthFieldPrepender(sizeOf.int)

