package widebase.workspace.ide

/** Chart package.
 *
 * @author myst3r10n
 */
package object chart {

  import widebase.workspace.runtime

  def plugin = runtime.plugin(Plugin.id).asInstanceOf[Plugin]

}

