package org.tmotte.keyboard;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;

/**
 * Use gridwidth and gridheight to make a component span extra columns/rows
 * Use gridx and gridy to control position; also addX() and addY()
 * Use fill + weightx and weighty to control expansion; also setFill() and weightXY()
 * Finally use insets to control padding; also setInsets()
 * Borrowed from Klonk.
 */
class GridBug extends GridBagConstraints {
  private static final long serialVersionUID = 1L;

  GridBagLayout gbl=new GridBagLayout();
  Container container;

  GridBug(Container c) {
    this.container=c;
    container.setLayout(gbl);
    gridx=1;
    gridy=1;
  }

  Container getContainer() {
    return container;
  }
  GridBug add(Component c) {
    gbl.setConstraints(c, this);
    container.add(c);
    return this;
  }

  GridBug setInsets(int i) {
    return setInsets(i, i, i, i);
  }
  GridBug setInsets(int top, int right, int bottom, int left) {
    insets.top=top;
    insets.right=right;
    insets.bottom=bottom;
    insets.left=left;
    return this;
  }
  GridBug insets(int i) {
    return setInsets(i, i, i, i);
  }
  GridBug insets(int top, int right, int bottom, int left) {
    return setInsets(top, right, bottom, left);
  }
  GridBug insetTop(int inset) {insets.top=inset; return this;}
  GridBug insetRight(int inset) {insets.right=inset; return this;}
  GridBug insetBottom(int inset) {insets.bottom=inset; return this;}
  GridBug insetLeft(int inset) {insets.left=inset; return this;}

  GridBug setY(int gridy){
    this.gridy=gridy;
    return this;
  }
  GridBug y(int gridy){
    this.gridy=gridy;
    return this;
  }
  GridBug addY(Component... cs) {
    for (Component c: cs)
      addY(c);
    return this;
  }
  GridBug addY(Component c) {
    if (container.getComponentCount()!=0)
      gridy++;
    return add(c);
  }

  GridBug setX(int gridx){
    this.gridx=gridx;
    return this;
  }
  GridBug x(int gridx){
    this.gridx=gridx;
    return this;
  }
  GridBug addX(Component... cs) {
    for (Component c: cs)
      addX(c);
    return this;
  }
  GridBug addX(Component c) {
    if (container.getComponentCount()!=0)
      gridx++;
    return add(c);
  }

  GridBug weightXY(double x, double y) {
    weightx=x; weighty=y; return this;
  }
  GridBug weightXY(double xy) {
    return weightXY(xy, xy);
  }
  GridBug weightX(double x) {
    weightx=x; return this;
  }
  GridBug weightY(double y) {
    weighty=y; return this;
  }
  GridBug gridXY(int x, int y) {
    gridx=x; gridy=y; return this;
  }
  GridBug gridXY(int xy) {
    return gridXY(xy, xy);
  }
  GridBug gridX(int x) {
    this.gridx=x; return this;
  }
  GridBug gridY(int y) {
    this.gridy=y; return this;
  }
  GridBug setFill(int fill) {
    this.fill=fill;
    return this;
  }
  GridBug fill(int fill) {
    this.fill=fill; return this;
  }
  GridBug anchor(int anchor) {
    this.anchor=anchor; return this;
  }
  GridBug gridWidth(int gridWidth) {
    this.gridwidth=gridWidth; return this;
  }
  GridBug gridHeight(int gridHeight) {
    this.gridheight=gridHeight; return this;
  }

}