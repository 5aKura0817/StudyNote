package com.sakura.chapter04

/**
 * @author 5akura
 * @create 2020/2020/8/13 22:53
 * @description
 **/
object ForDemo04 {
  def main(args: Array[String]): Unit = {
    for (i <- 1 to 10; j = 2 * i){
      println(s"$i * 2 = $j")
    }

    /**
     * 1 * 2 = 2
     * 2 * 2 = 4
     * 3 * 2 = 6
     * 4 * 2 = 8
     * 5 * 2 = 10
     * 6 * 2 = 12
     * 7 * 2 = 14
     * 8 * 2 = 16
     * 9 * 2 = 18
     * 10 * 2 = 20
     */
  }
}
