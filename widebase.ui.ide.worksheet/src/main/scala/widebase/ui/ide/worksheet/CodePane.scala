/*
 *  CodePane.scala
 *  (ScalaInterpreterPane)
 *
 *  Copyright (c) 2010-2012 Hanns Holger Rutz. All rights reserved.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.scalainterpreter

import actions.CompletionAction
import javax.swing.{JScrollPane, ScrollPaneConstants, AbstractAction, JEditorPane, KeyStroke, JComponent}
import java.awt.event.{InputEvent, ActionEvent, KeyEvent}
import jsyntaxpane.{SyntaxStyle, TokenType, SyntaxStyles, DefaultSyntaxKit, SyntaxDocument}
import java.awt.{Dimension, Color}
import jsyntaxpane.util.Configuration
import javax.swing.text.PlainDocument

object CodePane {
  object Config {
    implicit def build(b: ConfigBuilder): Config = b.build

    def apply(): ConfigBuilder = new ConfigBuilderImpl
  }

  sealed trait ConfigLike {
    /**
     * The initial text to be shown in the pane
     */
    def text: String

    /**
     * The color scheme to use
     */
    def style: Style

    /**
     * A map of custom keyboard action bindings
     */
    def keyMap: Map[KeyStroke, () => Unit]

    /**
     * A pre-processor function for key events
     */
    def keyProcessor: KeyEvent => KeyEvent

    /**
     * A list of preferred font faces, given as pairs of font name and font size.
     * The code pane tries to find the first matching font, therefore put the
     * preferred faces in the beginning of the sequence, and the fall-back faces
     * in the end.
     */
    def font: Seq[(String, Int)]

    /**
     * Preferred width and height of the component
     */
    def preferredSize: (Int, Int)

    //      def toBuilder : ConfigBuilder
  }

  sealed trait Config extends ConfigLike

  object ConfigBuilder {
    def apply(config: Config): ConfigBuilder = {
      import config._
      val b = new ConfigBuilderImpl
      b.text = text
      b.keyMap = keyMap
      b.keyProcessor = keyProcessor
      b.font = font
      b.style = style
      b.preferredSize = preferredSize
      b
    }
  }

  sealed trait ConfigBuilder extends ConfigLike {
    var text: String
    var style: Style
    var keyMap: Map[KeyStroke, () => Unit]
    var keyProcessor: KeyEvent => KeyEvent
    var font: Seq[(String, Int)]
    var preferredSize: (Int, Int)
    def build: Config
  }

  private final class ConfigBuilderImpl extends ConfigBuilder {
    var text          = ""
    var style: Style  = Style.BlueForest
    var keyMap        = Map.empty[KeyStroke, () => Unit]
    var keyProcessor: KeyEvent => KeyEvent = identity
    var font          = Helper.defaultFonts
    var preferredSize = (500, 500)

    def build: Config = ConfigImpl(text, keyMap, keyProcessor, font, style, preferredSize)

    override def toString = "CodePane.ConfigBuilder@" + hashCode().toHexString
  }

  private final case class ConfigImpl(text: String, keyMap: Map[KeyStroke, () => Unit],
                                      keyProcessor: KeyEvent => KeyEvent, font: Seq[(String, Int)],
                                      style: Style, preferredSize: (Int, Int))
    extends Config {
    override def toString = "CodePane.Config@" + hashCode().toHexString
  }

  private def put(cfg: Configuration, key: String, pair: (Color, Style.Face)) {
    val value = "0x" + (pair._1.getRGB | 0xFF000000).toHexString.substring(2) + ", " + pair._2.code
    cfg.put(key, value)
  }

  private def put(cfg: Configuration, key: String, color: Color) {
    val value = "0x" + (color.getRGB | 0xFF000000).toHexString.substring(2)
    cfg.put(key, value)
  }

  def initKit(config: Config) {
    DefaultSyntaxKit.initKit()
    DefaultSyntaxKit.registerContentType("text/scala", "de.sciss.scalainterpreter.ScalaSyntaxKit")
    //      val synDef = DefaultSyntaxKit.getConfig( classOf[ DefaultSyntaxKit ])
    val syn = DefaultSyntaxKit.getConfig(classOf[ScalaSyntaxKit])
    val style = config.style
    put(syn, "Style.DEFAULT",           style.default)
    put(syn, "Style.KEYWORD",           style.keyword)
    put(syn, "Style.OPERATOR",          style.operator)
    put(syn, "Style.COMMENT",           style.comment)
    put(syn, "Style.NUMBER",            style.number)
    put(syn, "Style.STRING",            style.string)
    put(syn, "Style.STRING2",           style.string)
    put(syn, "Style.IDENTIFIER",        style.identifier)
    put(syn, "Style.DELIMITER",         style.delimiter)
    put(syn, "Style.TYPE",              style.tpe)

    put(syn, "LineNumbers.CurrentBack", style.lineBackground)
    put(syn, "LineNumbers.Foreground",  style.lineForeground)
    syn.put("SingleColorSelect",        style.singleColorSelect.toString) // XXX TODO currently broken - has no effect
    //      synDef.put( "SingleColorSelect", style.singleColorSelect.toString )
    put(syn, "SelectionColor",          style.selection)
    put(syn, "CaretColor",              style.caret)
    put(syn, "PairMarker.Color",        style.pair)

    // ssssssssssuckers - we need to override the default which is black here
    SyntaxStyles.getInstance().put(TokenType.DEFAULT, new SyntaxStyle(style.default._1, style.default._2.code))
  }

  def apply(config: Config = Config().build): CodePane = {
    initKit(config)
    val res = createPlain(config)
    res.init()
    res
  }

  private def createPlain( config: Config ) : Impl = {
    val ed: JEditorPane = new JEditorPane() {
      override protected def processKeyEvent(e: KeyEvent) {
        super.processKeyEvent(config.keyProcessor(e))
      }
    }
    ed.setPreferredSize(new Dimension(config.preferredSize._1, config.preferredSize._2))
    val style = config.style
    ed.setBackground(style.background) // stupid... this cannot be set in the kit config
    ed.setForeground(java.awt.Color.RED)
    ed.setSelectedTextColor(java.awt.Color.RED)

    val imap = ed.getInputMap(JComponent.WHEN_FOCUSED)
    val amap = ed.getActionMap

    config.keyMap.iterator.zipWithIndex.foreach {
      case (spec, idx) =>
        val name = "de.sciss.user" + idx
        imap.put(spec._1, name)
        amap.put(name, new AbstractAction {
          def actionPerformed(e: ActionEvent) {
            spec._2.apply()
          }
        })
    }

    new Impl(ed, config)
  }

  private final class Impl(val editor: JEditorPane, config: Config) extends CodePane {
    def docOption: Option[SyntaxDocument] = {
      val doc = editor.getDocument
      if (doc == null) return None
      doc match {
        case sd: SyntaxDocument => Some(sd)
        case _ => None
      }
    }

    val component: JComponent = new JScrollPane(editor,
      ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS)

    def init() {
      editor.setContentType("text/scala")
      editor.setText(config.text)
      editor.setFont(Helper.createFont(config.font))
      editor.getDocument.putProperty(PlainDocument.tabSizeAttribute, 2)
    }

    def getSelectedText: Option[String] = {
      val txt = editor.getSelectedText
      if (txt != null) Some(txt) else None
    }

    def getCurrentLine: Option[String] = docOption.map(_.getLineAt(editor.getCaretPosition))

    def getSelectedTextOrCurrentLine: Option[String] = getSelectedText.orElse(getCurrentLine)

    def installAutoCompletion(interpreter: Interpreter) {
      val imap = editor.getInputMap(JComponent.WHEN_FOCUSED)
      val amap = editor.getActionMap
      imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_MASK), "de.sciss.comp")
      amap.put("de.sciss.comp", new CompletionAction(interpreter.completer))
    }

    override def toString = "CodePane@" + hashCode().toHexString
  }
}

trait CodePane {
  /**
   * The peer swing component which can be added to the parent swing container.
   */
  def component: JComponent

  def editor: JEditorPane

  /**
   * The currently selected text, or `None` if no selection has been made.
   */
  def getSelectedText: Option[String]

  /**
   * The text on the current line, or `None` if the document is empty or unavailable.
   */
  def getCurrentLine: Option[String]

  /**
   * Convenience method for `getSelectedText orElse getCurrentLine`.
   */
  def getSelectedTextOrCurrentLine: Option[String]

  def installAutoCompletion(interpreter: Interpreter): Unit
}
