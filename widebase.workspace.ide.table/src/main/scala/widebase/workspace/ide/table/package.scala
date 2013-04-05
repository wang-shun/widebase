package widebase.workspace.ide

/** Table package.
 *
 * @author myst3r10n
 */
package object table {

  import widebase.workspace.runtime

  def plugin = runtime.plugin(Plugin.id).asInstanceOf[Plugin]

}

