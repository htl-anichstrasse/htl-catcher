package org.htlanich.htlcatcher;

import android.graphics.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a point in the HTLCatcher GameView
 *
 * Created by albert on 06.11.17.
 */
public class PlPoint extends Point {

  PlPoint(int x, int y) {
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
  List<PlPoint> intersect(final List<PlPoint> points, final double radius) {
    List<PlPoint> returnPoints = new ArrayList<>();
    for (PlPoint point : points) {
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
  private boolean intersect(PlPoint point, double radius) {
    return
        Math.pow((this.x - point.x) * (this.x - point.x) + (this.y - point.y) * (this.y - point.y),
            0.5) < radius;
  }

  @Override
  public String toString() {
    return "PlPoint{x=" + x + ", y=" + y + "}";
  }

}
