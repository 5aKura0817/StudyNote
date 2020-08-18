package com.sakura.chapter04

/**
 * @author 5akura
 * @create 2020/2020/8/13 22:37
 * @description
 **/
object ForDemo {
  def main(args: Array[String]): Unit = {
    for (i <- 1 to 10){
      println(s"$i, hello world")
    }
    /**
     * 1, hello world
     * 2, hello world
     * 3, hello world
     * 4, hello world
     * 5, hello world
     * 6, hello world
     * 7, hello world
     * 8, hello world
     * 9, hello world
     * 10, hello world
     */

    var list = List("Hello","World",10,30,false)
    for (item <- list){
      println(item)
    }

    /**
     * Hello
     * World
     * 10
     * 30
     * false
     */
  }
}
