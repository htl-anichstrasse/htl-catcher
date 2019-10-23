package org.htlanich.htlcatcher.util;

import android.graphics.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a point in the HTL Catcher GameView canvas
 *
 * @author Albert Greinöcker
 * @since 06.11.17
 */
public class ViewPoint extends Point {

  /**
   * Creates a new point on the GameView canvas
   *
   * @param x the x coordinate of the point
   * @param y the y coordinate of the point
   */
  public ViewPoint(int x, int y) {
    super(x, y);
  }

  /**
   * Checks if a provided list of points intersects with this point
   *
   * @param points a list of points which need to be checked for intersection
   * @param radius the radius each point has (including actual point)
   * @return null, if no point intersects, otherwise the intersecting points
   */
  @SuppressWarnings("SameParameterValue")
  public List<ViewPoint> intersect(final List<ViewPoint> points, final double radius) {
    List<ViewPoint> returnPoints = new ArrayList<>();
    for (ViewPoint point : points) {
      if (intersect(point, radius)) {
        returnPoints.add(point);
      }
    }
    return returnPoints;
  }

  /**
   * Checks if this point intersects with another one
   *
   * @param point the point to be checked
   * @param radius the radius of the point
   * @return true if this point intersects with the provided point, false otherwise
   */
  private boolean intersect(ViewPoint point, double radius) {
    return
        Math.pow((this.x - point.x) * (this.x - point.x) + (this.y - point.y) * (this.y - point.y),
            0.5) < radius;
  }

  @Override
  public String toString() {
    return "ViewPoint{x=" + x + ", y=" + y + "}";
  }

}
