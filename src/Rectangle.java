/**
 * Simple axis aligned rectangle.
 *
 * @param left   left coordinate of rectangle
 * @param bottom bottom coordinate of rectangle
 * @param width  width of rectangle
 * @param height height of rectangle
 * @author Pepe Gallardo
 */
public record Rectangle(int left, int bottom, int width, int height) {
  public int top() {
    return bottom + height - 1;
  }

  public int right() {
    return left + width - 1;
  }

  public boolean intersects(Rectangle that) {
    return this.left <= that.right() && this.right() >= that.left && this.top() >= that.bottom && this.bottom <= that.top();
  }

  public boolean intersects(int row, int column) {
    return row >= bottom && row <= top() && column >= left && column <= right();
  }

  public boolean intersects(Location location) {
    return intersects(location.row(), location.column());
  }

  public boolean contains(Rectangle that) {
    return that.bottom >= this.bottom && that.top() <= this.top() && that.left >= this.left && that.right() <= this.right();
  }

  private int dist(int x1, int y1, int x2, int y2) {
    return Math.abs(x1 - x2) + Math.abs(y1 - y2);
  }

  public int manhattanDistance(int row, int column) {
    var left = column < this.left;
    var right = this.right() < column;
    var bottom = row < this.bottom;
    var top = this.top() < row;

    if (top && left) {
      return dist(this.left, this.top(), column, row);
    }
    if (left && bottom) {
      return dist(this.left, this.bottom, column, row);
    }
    if (bottom && right) {
      return dist(this.right(), this.bottom, column, row);
    }
    if (right && top) {
      return dist(this.right(), this.top(), column, row);
    }
    if (left) {
      return this.left - column;
    }
    if (right) {
      return column - this.right();
    }
    if (bottom) {
      return this.bottom - row;
    }
    if (top) {
      return row - this.top();
    }

    // rectangles intersect
    return 0;
  }

  public int manhattanDistance(Location location) {
    return manhattanDistance(location.row(), location.column());
  }
}
