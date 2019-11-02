package tirol.htlanichstrasse.htlcatcher.util;

import android.graphics.Point;

/**
 * Represents a point in the HTL Catcher GameView canvas
 *
 * @author Joshua Winkler
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
   public ViewPoint(final int x, final int y) {
      super(x, y);
   }

   /**
    * Checks if this point intersects with another one
    *
    * @param point the point to be checked
    * @param radius the radius of the point
    * @return true if this point intersects with the provided point, false otherwise
    */
   public boolean intersect(final ViewPoint point, final double radius) {
      return
          Math.pow(
              (this.x - point.x) * (this.x - point.x) + (this.y - point.y) * (this.y - point.y),
              0.5) < radius;
   }

   @Override
   public String toString() {
      return "ViewPoint{x=" + x + ", y=" + y + "}";
   }

}
