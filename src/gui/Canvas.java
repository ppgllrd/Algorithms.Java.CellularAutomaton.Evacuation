package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A drawing class using double buffering.
 *
 * @author Pepe Gallardo
 */
public abstract class Canvas extends JLabel {
  private final int height, width;
  private final Color backgroundColor;
  private final BufferedImage offScreenBufferedImage, onScreenBufferedImage;
  private final Graphics2D offScreenGraphics2D, onScreenGraphics2D;
  private final ImageIcon imageIcon;

  public Canvas(int height, int width, Color backgroundColor) {
    super();

    this.width = width;
    this.height = height;
    this.backgroundColor = backgroundColor;

    setPreferredSize(new Dimension(width, height));

    this.offScreenBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    this.onScreenBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    this.offScreenGraphics2D = offScreenBufferedImage.createGraphics();
    this.onScreenGraphics2D = onScreenBufferedImage.createGraphics();

    RenderingHints hints = new RenderingHints(null);
    hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    offScreenGraphics2D.addRenderingHints(hints);

    this.imageIcon = new ImageIcon(onScreenBufferedImage);
    setIcon(imageIcon);
  }

  public Canvas(int height, int width) {
    this(height, width, Color.white);
  }

  public abstract void paint(Graphics2D graphics2D, Canvas canvas);

  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);

    // Clear canvas
    offScreenGraphics2D.setColor(backgroundColor);
    offScreenGraphics2D.fillRect(0, 0, width, height);

    // Draw on canvas
    paint(offScreenGraphics2D, this);
  }

  public void update() {
    // Show canvas on screen
    onScreenGraphics2D.drawImage(offScreenBufferedImage, 0, 0, null);
    repaint();
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public int getWidth() {
    return width;
  }
}