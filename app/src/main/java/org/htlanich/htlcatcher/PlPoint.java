package org.htlanich.htlcatcher;

import android.graphics.Point;
import java.util.ArrayList;

/**
 * Created by albert on 06.11.17.
 */

public class PlPoint extends Point {

  public PlPoint(int x, int y) {
    super(x, y);
  }

  /**
   * @param radius the radius each point has (including actual point)
   * @return null, if no point intersects, otherwise the intersecting point
   */
  public ArrayList<PlPoint> intersect(ArrayList<PlPoint> points, double radius) {
    ArrayList<PlPoint> ps = new ArrayList<>();
    for (PlPoint p : points) {
      if (intersect(p, radius)) {
        ps.add(p);
      }
    }
    return ps;
  }

  public boolean intersect(PlPoint p, double radius) {
    double distance = Math
        .pow((this.x - p.x) * (this.x - p.x) + (this.y - p.y) * (this.y - p.y), 0.5);
    return (distance < radius);
  }

  @Override
  public String toString() {
    return "PlPoint{" +
        "x=" + x +
        ", y=" + y +
        '}';
  }
}
