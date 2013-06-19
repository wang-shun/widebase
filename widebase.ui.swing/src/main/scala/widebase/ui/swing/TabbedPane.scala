package widebase.ui.swing

import event.PageDetached

import java.awt.event.MouseEvent
import java.beans. { PropertyChangeEvent, PropertyChangeListener }

import javax.swing. { Icon, ImageIcon, JTabbedPane, WindowConstants }
import javax.swing.event. { ChangeEvent, ChangeListener }

import moreswing.swing.plaf.Redesign

import scala.collection.mutable.Buffer

import scala.swing. {

  Alignment,
  Button,
  Color,
  Component,
  FlowPanel,
  Frame,
  Label,
  Rectangle,
  Publisher

}

import scala.swing.event. {

  ButtonClicked,
  MouseDragged,
  MouseMoved,
  MousePressed,
  MouseReleased,
  SelectionChanged,
  WindowClosing

}

/** Companion of [[scala.swing.TabbedPane]].
 *
 * @author myst3r10n
 */
object TabbedPane {

  class Page protected[TabbedPane](
    parent0: TabbedPane,
    title0: String,
    icon0: Icon,
    content0: Component,
    tip0: String)
    extends Proxy {

    def self = content0

    def this(
      title0: String,
      icon0: ImageIcon,
      content0: Component,
      tip0: String) = this(null, title0, icon0, content0, tip0)

    def this(title0: String,
      icon0: ImageIcon,
      content0: Component) = this(title0, icon0, content0, "")

    content = content0
    title = title0
    icon = icon0
    tip = tip0

    protected[TabbedPane] var parent: TabbedPane = parent0

    protected var _background: Color = null

    /** @see [[javax.swing.JTabbedPane]]. */
    def background = _background

    /** @see [[javax.swing.JTabbedPane]]. */
    def background_=(c: Color) {

      _background = c

      if(parent != null)
        parent.peer.setBackgroundAt(index, c)

    }

    /** @see [[javax.swing.JTabbedPane]]. */
    def bounds: Rectangle = parent.peer.getBoundsAt(index)

    protected var _content = content0

    /** @see [[javax.swing.JTabbedPane]]. */
    def content = _content

    /** @see [[javax.swing.JTabbedPane]]. */
    def content_=(c: Component) {

      if(parent == null)
        _content = c
      else {

        parent.peer.setComponentAt(index, c.peer)
        _content = c // must be after, else index return -1

      }
    }

    protected var _enabled = true

    /** @see [[javax.swing.JTabbedPane]]. */
    def enabled = _enabled

    /** @see [[javax.swing.JTabbedPane]]. */
    def enabled_=(b: Boolean) {

      _enabled = b

      if(parent != null)
        parent.peer.setEnabledAt(index, b)

    }

    protected var _foreground: Color = null

    /** @see [[javax.swing.JTabbedPane]]. */
    def foreground = _foreground

    /** @see [[javax.swing.JTabbedPane]]. */
    def foreground_=(c: Color) {

      _foreground = c

      if(parent != null)
        parent.peer.setForegroundAt(index, c)

    }

    protected var _icon: Icon = null

    /** @see [[javax.swing.JTabbedPane]]. */
    def icon = _icon

    /** @see [[javax.swing.JTabbedPane]]. */
    def icon_=(i: Icon) {

      _icon = i

      if(parent != null)
        parent.peer.setIconAt(index, i)

    }

    /** @see [[javax.swing.JTabbedPane]]. */
    def index =
      if(parent == null)
        0
      else
        parent.peer.indexOfComponent(content.peer)

    protected var _mnemonic = -1

    /** @see [[javax.swing.JTabbedPane]]. */
    def mnemonic = _mnemonic

    /** @see [[javax.swing.JTabbedPane]]. */
    def mnemonic_=(k: Int) {

      _mnemonic = k

      if(parent != null)
        parent.peer.setMnemonicAt(index, k)

    }

    protected var _tip = tip0

    /** @see [[javax.swing.JTabbedPane]]. */
    def tip = _tip

    /** @see [[javax.swing.JTabbedPane]]. */
    def tip_=(t: String) {

      _tip = t

      if(parent != null)
        parent.peer.setToolTipTextAt(index, t)

    }

    protected var _title = title0

    /** @see [[javax.swing.JTabbedPane]]. */
    def title = _title

    /** @see [[javax.swing.JTabbedPane]]. */
    def title_=(t: String) {

      _title = t

      if(parent != null)
        parent.peer.setTitleAt(index, t)

    }

  }

}

/** Like [[scala.swing.TabbedPane]] but with draggable, detachable, reattachable, arrangeable and closeable tabs.
 *
 * @author myst3r10n
 */
class TabbedPane extends Component with Publisher {

  override lazy val peer: JTabbedPane = new JTabbedPane with SuperMixin

  import TabbedPane.Page
  import scala.swing.TabbedPane.Layout

  var _closeable = true

  def closeable = _closeable

  def closeable_= (b: Boolean) {

    _closeable = b
    rebuildTabBars

  }

  var _detachable = true

  def detachable = _detachable

  def detachable_= (b: Boolean) {

    _detachable = b
    rebuildTabBars

  }

  protected var _shiftable = true

  /** Shift mode. */
  def shiftable = _shiftable

  /** Toggle shift mode.
   *
   * @param b If this shift mode is enabled.
   */
  def shiftable_= (b: Boolean) { _shiftable = b }

  protected var _flotableShift = false

  /** Shift mode flotable. */
  def flotableShift = _flotableShift

  /** Toggle shift mode flotable.
   *
   * @param b If this shift mode is flotable.
   */
  def flotableShift_= (b: Boolean) { _flotableShift = b }

  /** @see [[scala.swing.TabbedPane]]. */
  object pages extends Buffer[Page] {

    /** Appends a tab to this buffer.
     *
     * @param t The tab to append.
     *
     * @return The buffer itself.
     */
    def +=(t: Page): this.type = {

      insert(if(size < 1) 0 else size, t)
      this

    }

    /** Prepends a tab to this buffer.
     *
     * @param t The tab to prepend.
     *
     * @return The buffer itself.
     */
    def +=:(t: Page): this.type = {

      insert(0, t)
      this

    }

    /** Select a tab by its index in the buffer.
     *
     * @param n The index where tabs are selected.
     *
     * @return The tab at index n.
     */
    def apply(n: Int): Page =
      new Page(
        TabbedPane.this,
        peer.getTitleAt(n),
        peer.getIconAt(n),
        new Component {

          override lazy val peer =
            TabbedPane.this.peer.getComponentAt(n).asInstanceOf[javax.swing.JComponent]

        } ,
        peer.getToolTipTextAt(n))

    /** Clears the buffer's contents. */
    def clear {

      for(i <- 0 to size - 1)
        remove(i)

    }

    /** Detach tab at given index.
     * 
     * @param n The index where tabs are detached.
     *
     * @return The detached frame.
     */
    def detach(n: Int) = {
    
      val frame = new Frame with Redesign {

        // Save tab.
        val tab = pages(/*n*/0)

        // Use better close operation.
        peer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)

        // Set look.
        title = tab.title
        if(pages(n).icon != null)
          iconImage = pages(n).icon.asInstanceOf[ImageIcon].getImage

        // Move tab content to frame.
        peer.setContentPane(tab.content.peer)

        // Event handling.
        var reattached = false
        reactions += { case WindowClosing(_) => 

          if(!reattached) {

            reattached = true
            reattach(tab)

          }
        }

        pack
        visible = true

      }

      TabbedPane.this.publish(PageDetached(TabbedPane.this, frame))

      frame

    }

    /** Insert tab at given index into this buffer.
     *
     * @param n The index where new tab are inserted.
     * @param t The tab to insert.
     */
    def insert(n: Int, t: Page) {

      t.parent = TabbedPane.this
      peer.insertTab(t.title, t.icon, t.content.peer, t.tip, n)
      overrideTabBar(n, t)

    }

    /** Inserts new tabs at a given index into this buffer.
     *
     * @param n The index where new tabs are inserted.
     * @param tabs The tabs to insert.
     */
    def insertAll(n: Int, tabs: Traversable[Page]) {

      var i = n
      for(t <- tabs) {

        insert(i, t)
        i += 1

      }
    }

    def iterator = Iterator.range(0, length).map(apply(_))

    /** The length of the buffer.
     *
     * @return The length of the buffer.
     */
    def length = peer.getTabCount

    /** Reattach the frame.
     *
     * @param t The detached tab.
     */
    def reattach(t: Page) { pages += t }

    /** Removes the tab at a given index from this buffer.
     *
     * @param n The index which refers to the tab to delete.
     *
     * @return The previous tab at index n.
     */
    def remove(n: Int): Page = {

      val t = apply(n)
      peer.removeTabAt(n)
      t.parent = null
      t

    }

    /** Removes inatvice tabs from this buffer. */
    def removeInactive {

      shift(selection.index, 0)

      var i = 1

      while(i < size)
        remove(i)

    }

    /** Shifts tab within buffer.
     *
     * @param from The index of tab.
     * @param to The index where tab should be shifted.
     */
    def shift(from: Int, to: Int) { insert(to, remove(from)) }

    /** Replaces tab at given index with a new tab.
     *
     * @param n The index where tab are updated.
     * @param t The new tab where replaces old tab.
     */
    def update(n: Int, t: Page) {

      remove(n)
      insert(n, t)

    }

  }

  protected var _popupMenu: PopupMenu = null

  /** Popup menu. */
  def popupMenu = _popupMenu

  /** Set popup menu.
  *
  * @param popupMenu Popup menu.
  */
  def popupMenu_= (popup: PopupMenu) { _popupMenu = popup }

  object selection extends Publisher {

    def page = pages(index)
    def page_=(p: Page) { index = p.index }

    def index = peer.getSelectedIndex
    def index_=(n: Int) { peer.setSelectedIndex(n) }

    peer.addChangeListener(new ChangeListener {

      def stateChanged(e: ChangeEvent) { publish(SelectionChanged(TabbedPane.this)) }

    } )

  }

  /** @see [[javax.swing.JTabbedPane]]. */
  def tabLayoutPolicy = Layout(peer.getTabLayoutPolicy)

  /** @see [[javax.swing.JTabbedPane]]. */
  def tabLayoutPolicy_=(p: Layout.Value) { peer.setTabLayoutPolicy(p.id) }

  /** @see [[javax.swing.JTabbedPane]]. */
  def tabPlacement = Alignment(peer.getTabPlacement)

  /** @see [[javax.swing.JTabbedPane]]. */
  def tabPlacement_=(b: Alignment.Value) { peer.setTabPlacement(b.id) }

  // Event handling.
  private var mouseOverTab = -1
  private var tabDepartFrom = -1
  listenTo(mouse.clicks, mouse.moves, selection)
  reactions += {

    case MouseMoved(_, point, _) =>
      // Save tab where mouse hovers.
      mouseOverTab = peer.indexAtLocation(point.x, point.y)

      // Enable popup menu if mouse over tab.
      if(popupMenu != null &&
        mouseOverTab > -1 &&
        peer.getComponentPopupMenu == null)
        peer.setComponentPopupMenu(popupMenu.peer)

      // Disable popup menu if mouse not over tab.
      else if(mouseOverTab < 0 && peer.getComponentPopupMenu != null)
        peer.setComponentPopupMenu(null)

    case e: MousePressed =>
      // Save tab where mouse hovers.
      mouseOverTab = peer.indexAtLocation(e.point.x, e.point.y)

      // Enable motion mode.
      if(shiftable &&
        e.peer.getButton == MouseEvent.BUTTON1 &&
        tabDepartFrom < 0 &&
        mouseOverTab > -1)
        tabDepartFrom = mouseOverTab

    case e: MouseDragged =>
      // Save tab where mouse hovers.
      mouseOverTab = peer.indexAtLocation(e.point.x, e.point.y)

      val tabArriveIn = mouseOverTab

      // Move tab if flotable shift enabled, destination valid and unequal start.
      if(shiftable &&
        flotableShift &&
        tabDepartFrom > -1 &&
        tabArriveIn > -1 &&
        tabDepartFrom != tabArriveIn) {

        // Shift tab.
        pages.shift(tabDepartFrom, tabArriveIn)
        selection.index = tabArriveIn

        // Sync moved tab.
        tabDepartFrom = tabArriveIn

      }

    case e: MouseReleased =>
      // Save tab where mouse hovers.
      mouseOverTab = peer.indexAtLocation(e.point.x, e.point.y)

      // Pass if button 1 released.
      if(shiftable &&
        e.peer.getButton == MouseEvent.BUTTON1) {

        val tabArriveIn = mouseOverTab

        // Move tab if flotable shift enabled, destination valid and unequal start.
        if(!flotableShift &&
          tabDepartFrom > -1 &&
          tabArriveIn > -1 &&
          tabDepartFrom != tabArriveIn) {

          // Shift tab.
          pages.shift(tabDepartFrom, tabArriveIn)
          selection.index = tabArriveIn
  
          // Sync moved tab.
          tabDepartFrom = tabArriveIn

        }
      }

      // Disable motion mode.
      if(tabDepartFrom != -1)
        tabDepartFrom = -1

  }

  protected def overrideTabBar(n: Int, t: Page) {

    // Modify tab component.
    peer.setTabComponentAt(n, (new FlowPanel {

      opaque = false

      // Label.
      contents += new Label(pages(n).title, pages(n).icon, Alignment.Left)

      // Detach Button.
      if(detachable)
        contents += new Button {

          border = null
          icon = new ImageIcon(getClass.getResource("/icon/tab_detach.png"))
          opaque = false

          listenTo(this)
          reactions += { case ButtonClicked(_) => pages.detach(t.index) }

          peer.addPropertyChangeListener(new PropertyChangeListener {

            def propertyChange(event: PropertyChangeEvent) {

              event.getPropertyName match {

                case "border" => if(event.getNewValue != null) border = null
                case "opaque" => if(event.getNewValue.asInstanceOf[Boolean]) opaque = false
                case _ =>

              }
            }
          } )
        }

      // Close Button.
      if(closeable)
        contents += new Button {

          border = null
          icon = new ImageIcon(getClass.getResource("/icon/tab_close.png"))
          opaque = false

          listenTo(this)
          reactions += { case ButtonClicked(_) => pages.remove(n) }

          peer.addPropertyChangeListener(new PropertyChangeListener {

            def propertyChange(event: PropertyChangeEvent) {

              event.getPropertyName match {

                case "border" => if(event.getNewValue != null) border = null
                case "opaque" => if(event.getNewValue.asInstanceOf[Boolean]) opaque = false
                case _ =>

              }
            }
          } )
        }
    } ).peer)
  }

  protected def rebuildTabBars {

    for(i <- 0 to pages.size -1)
      overrideTabBar(i, pages(i))

  }
}

