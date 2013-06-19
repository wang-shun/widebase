package widebase.toolbox.core

import graphics.impl. { AxesPanel, FigureFrame }

import java.awt.BorderLayout
import java.awt.event. { WindowEvent, WindowFocusListener }

import javax.swing.SwingUtilities

import moreswing.swing.i18n.LocaleManager

import scala.collection.mutable.LinkedHashMap
import scala.concurrent.Lock

import scala.swing. {

  Dimension,
  Frame,
  Point,
  RootPanel,
  SequentialContainer,
  UIElement

}

import scala.swing.event.WindowClosing

import widebase.toolbox.core.uitools.impl.Tab

/** Graphics Package.
 *
 * @author myst3r10n
 */
package object graphics {

  import scala.util.control.Breaks. { break, breakable }

  /** Hold all figures global. */
  protected val figureMap = LinkedHashMap[String, FigureFrame]()

  /** Lock `figureMap` by access. */
  protected val lock = new Lock

  /** Required by scaladoc. */
  protected object ignore

  /** Creates new axes into current figure.
   * If no figure exists, creates one.
   *
   * @return axes handle
   */
  def axes: AxesPanel = axes(gcf)

  /** Creates a new axes into parent element.
   *
   * @param parent element
   *
   * @return axes handle
   */
  def axes(parent: UIElement) = {

    val axes = new AxesPanel

    parent match {

      case parent: Frame =>

        parent.peer.toFront
        parent.peer.getContentPane.add(axes.peer, BorderLayout.CENTER)
        parent.peer.revalidate
        parent.peer.repaint()
        gcf.currentAxes = axes

      case parent: RootPanel =>

        SwingUtilities.windowForComponent(parent.peer).toFront
        parent.peer.getContentPane.add(axes.peer, 0)
        parent.peer.validate

      case parent: SequentialContainer =>

        SwingUtilities.windowForComponent(parent.peer).toFront
        parent.contents.prepend(axes)

      case parent: Tab =>

        SwingUtilities.windowForComponent(parent.content.peer).toFront
        parent.content = axes

    }

    axes

  }

  /** Makes the `axes` current and set the associated figure to front.
   *
   * @param axes handle
   *
   * @return axes handle
   **/
  def axes(axes: AxesPanel) = {

    SwingUtilities.windowForComponent(axes.peer).toFront
    gcf.currentAxes = axes
    axes

  }

  /** Creates a new figure.
   *
   * @return figure handle
   */
  def figure: FigureFrame = figure(nextNumber)

  /** Set figure to front by `number` or if not exists creates one by `number`.
   *
   * @param number of figure
   *
   * @return figure handle
   */
  def figure(number: Int): FigureFrame =
    figure(LocaleManager.text("Figure_?", number))

  /** Set figure to front by `id` or if not exists creates one by `id`.
   *
   * @param id of figure
   *
   * @return figure handle
   */
  def figure(id: String) = {

    var figureHandle: FigureFrame = null

    try {

      lock.acquire

      // If figure exists
      if(figureMap.contains(id)) {

        figureHandle = figureMap(id)

        figureMap -= figureHandle.id
        figureMap += id -> figureHandle

        figureHandle.peer.toFront

        figureHandle

      // If figure doesn't exists
      } else {

        figureHandle = new FigureFrame(id) {

          self =>

          title = id

          location = new Point(0, 0)
          preferredSize = new Dimension(800, 600)

          reactions += {

            case WindowClosing(source) =>

              try {

                lock.acquire

                figureMap -= id

              } finally {

                lock.release

              }
          }

          peer.addWindowFocusListener(new WindowFocusListener {

            /** Solved overlapping. */
            private var ignore = true

            def windowGainedFocus(e: WindowEvent) {

              if(ignore)
                ignore = false
              else
                try {

                  lock.acquire

                  figureMap -= id
                  figureMap += id -> self

                } finally {

                  lock.release

                }
            }

            def windowLostFocus(e: java.awt.event.WindowEvent) {}

          } )
        }

        figureHandle.pack
        figureHandle.visible = true

        figureMap += figureHandle.id -> figureHandle

      }
    } finally {

      lock.release

    }

    figureHandle

  }

  /** Set figure front by `number` or if not exists creates one by `number`.
   *
   * @param number of figure
   *
   * @return figure handle
   */
  def figure_=(number: Int) = figure(number)

  /** Set figure to front by `id` or if not exists creates one by `id`.
   *
   * @param id of figure
   *
   * @return figure handle
   */
  def figure_=(id: String) = figure(id)

  /** Get current axes of current figure.
   * If no axes or figure exists, creates one.
   *
   * @return axes handle
   */
  def gca = {

    if(gcf.currentAxes == null)
      axes

    gcf.currentAxes

  }

  /** Get current figure or creates one and set current.
   *
   * @return figure handle
   */
  def gcf: FigureFrame = {

    var currentFigure: FigureFrame = null

    try {

      lock.acquire

      if(figureMap.size > 0)
        currentFigure = figureMap.values.last

    } finally {

      lock.release

    }


    if(currentFigure != null)
      return currentFigure

    figure

  }

  /** Is hold state of current axes on?
   * If no figure exists, creates one and return `false`.
   *
   * @return true if hold state is on, else false
   */
  def ishold =
    if(gcf.currentAxes == null)
      false
    else
      gcf.currentAxes.hold

  /** Find unused figure number.
   *
   * @return unused figure number
   */
  protected def nextNumber = {

    var count = 0
    var found = true

    try {

      lock.acquire

      do {

        count += 1
        found = true

        breakable {

          figureMap.keys.foreach { id =>

            if(id == LocaleManager.text("Figure_?", count)) {

              found = false
              break

            }
          }
        }
      } while(!found)
    } finally {

      lock.release

    }

    LocaleManager.text("Figure_?", count)

  }
}

