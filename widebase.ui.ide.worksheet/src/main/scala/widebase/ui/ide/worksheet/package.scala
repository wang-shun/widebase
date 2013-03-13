package widebase.ui.ide

/** Worksheet package.
 *
 * @author myst3r10n
 */
package object worksheet {

  /** It's a workaround for Interpreter.scala file.
   * Simple replace `val cset = new CompilerSettings()` with `val cset = new WorkaroundSettings` to prevent errors.
   */
  class WorkaroundSettings extends scala.tools.nsc.Settings {

    embeddedDefaults[WorkaroundSettings]

  }
}

