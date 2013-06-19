package widebase.ui.swing.event

import scala.swing. { Component, Frame }

import scala.swing.event.Event

case class PageDetached(val source: Component, val frame: Frame) extends Event

