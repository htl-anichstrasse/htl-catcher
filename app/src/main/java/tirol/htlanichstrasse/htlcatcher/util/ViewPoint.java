package tirol.htlanichstrasse.htlcatcher.util;

import android.graphics.Point;
import lombok.Getter;

/**
 * Represents a point in the HTL Catcher GameView canvas
 *
 * @author Joshua Winkler
 * @author Albert Greinöcker
 * @since 06.11.17
 */
public class ViewPoint extends Point {

   /**
    * The radius of this view point (used for collision)
    */
   @Getter
   private int radius;

   /**
    * Creates a new point on the GameView canvas
    *
    * @param x the x coordinate of the point
    * @param y the y coordinate of the point
    */
   public ViewPoint(final int x, final int y, final int radius) {
      super(x, y);
      this.radius = radius;
   }

   /**
    * Checks if this point intersects with another one
    *
    * @param point the point to be checked
    * @return true if this point intersects with the provided point, false otherwise
    */
   public boolean intersect(final ViewPoint point) {
      return
          ((this.x - point.x) * (this.x - point.x) + (this.y - point.y) * (this.y - point.y))
              <= ((this.radius + point.radius) * (this.radius + point.radius));
   }

   @Override
   public String toString() {
      return "ViewPoint{x=" + x + ", y=" + y + "}";
   }

}
