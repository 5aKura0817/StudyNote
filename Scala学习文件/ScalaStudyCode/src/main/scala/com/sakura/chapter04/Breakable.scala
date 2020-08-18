package com.sakura.chapter04

import util.control.Breaks._
/**
 * @author 5akura
 * @create 2020/2020/8/14 19:55
 * @description
 **/
object Breakable {
  def main(args: Array[String]): Unit = {
    var n = 1;
    breakable {
      while (n < 20) {
        if (n == 15) {
          break()
        }
        println(n)
        n += 1
      }
    }
    println("hello world")
  }
}
