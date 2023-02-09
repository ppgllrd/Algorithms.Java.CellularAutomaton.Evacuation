package gui;

import javax.swing.*;
import java.awt.*;

public class Canvas extends JComponent {
  private final int width, height;

  public Canvas(int width, int height) {
    super();
    this.width = width;
    this.height = height;
    setPreferredSize(new Dimension(width, height));
  }
  public void paint(Graphics graphics, Canvas canvas) {}

  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    paint(graphics, this);
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }
}