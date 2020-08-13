package com.sakura.chapter04

/**
 * @author 5akura
 * @create 2020/2020/8/13 22:56
 * @description
 **/
object ForDemo05 {
  def main(args: Array[String]): Unit = {
    for (i <- 1 to 10; j <- 1 to i) {
      print(j + " ")
      if (j == i) {
        println()
      }
    }

    /**
     * 1
     * 1 2
     * 1 2 3
     * 1 2 3 4
     * 1 2 3 4 5
     * 1 2 3 4 5 6
     * 1 2 3 4 5 6 7
     * 1 2 3 4 5 6 7 8
     * 1 2 3 4 5 6 7 8 9
     * 1 2 3 4 5 6 7 8 9 10
     */

    for (i <- 1 to 10) {
      for (j <- 1 to i) {
        print(j + " ")
        if (j == i) {
          println()
        }
      }
    }
  }
}
