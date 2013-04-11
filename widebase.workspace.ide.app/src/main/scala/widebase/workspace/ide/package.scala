package widebase.workspace.ide

/** App package.
 *
 * @author myst3r10n
 */
package object app {

  import widebase.workspace.runtime

  def plugin = runtime.plugin(Plugin.id).asInstanceOf[Plugin]

}

