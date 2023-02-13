package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

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

  public Canvas(int rows, int columns, int pixelsPerCell, Color backgroundColor) {
    super();

    this.height = rows * pixelsPerCell;
    this.width = columns * pixelsPerCell;
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
    offScreenGraphics2D.setStroke(new BasicStroke(1.5f / pixelsPerCell));

    // flip Y axis and scale properly
    offScreenGraphics2D.translate(0, getHeight());
    offScreenGraphics2D.scale(pixelsPerCell, -pixelsPerCell);

    this.imageIcon = new ImageIcon(onScreenBufferedImage);
    setIcon(imageIcon);
  }

  public Graphics2D graphics2D() {
    return offScreenGraphics2D;
  }

  public abstract void paint(Canvas canvas);

  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);

    // Clear canvas
    offScreenGraphics2D.setColor(backgroundColor);
    offScreenGraphics2D.fillRect(0, 0, width, height);

    // Draw on canvas
    paint(this);
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

  public static Builder Builder() {
    return new Builder();
  }

  public static final class Builder {
    int rows = 20, columns = 20, pixelsPerCell = 5;
    Color color = Color.white;
    Consumer<Canvas> paint = canvas -> {
    };


    public Builder rows(int rows) {
      this.rows = rows;
      return this;
    }

    public Builder columns(int columns) {
      this.columns = columns;
      return this;
    }

    public Builder pixelsPerCell(int pixelsPerCell) {
      this.pixelsPerCell = pixelsPerCell;
      return this;
    }

    public Builder background(Color color) {
      this.color = color;
      return this;
    }

    public Builder paint(Consumer<Canvas> paint) {
      this.paint = paint;
      return this;
    }

    public Canvas build() {
      return new Canvas(rows, columns, pixelsPerCell, color) {
        @Override
        public void paint(Canvas canvas) {
          paint.accept(canvas);
        }
      };
    }
  }
}