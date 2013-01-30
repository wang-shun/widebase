package widebase.io.filter

import java.util.zip.Deflater

/** Supported compression levels.
 *
 * @author myst3r10n
 */
object CompressionLevel {

  /** Best compression. */
  val Best = Deflater.BEST_COMPRESSION

  /** Default compression. */
  val Default = 6

  /** Fastest compression. */
  val Fast = Deflater.BEST_SPEED

  /** No compression. */
  val None = Deflater.NO_COMPRESSION

}

