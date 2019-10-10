package org.htlanich.htlcatcher.utils;

/**
 * Created by albert on 06.11.17.
 */

public class NumberUtils {

  public static int rnd(int from, int to) {
    int span = to - from;
    return from + (int) (Math.random() * span);
  }

  public static int rnd(int to) {
    return rnd(0, to);
  }

}
