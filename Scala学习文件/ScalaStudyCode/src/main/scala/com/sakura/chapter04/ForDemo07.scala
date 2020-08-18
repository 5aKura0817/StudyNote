package com.sakura.chapter04

/**
 * @author 5akura
 * @create 2020/2020/8/14 19:16
 * @description
 **/
object ForDemo07 {
  def main(args: Array[String]): Unit = {
    for (i <- Range(1,10,2)){
      println(i)
    }

    /**
     * 1
     * 3
     * 5
     * 7
     * 9
     */
  }
}
