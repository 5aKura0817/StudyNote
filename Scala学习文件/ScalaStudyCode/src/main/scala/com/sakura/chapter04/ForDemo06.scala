package com.sakura.chapter04

/**
 * @author 5akura
 * @create 2020/2020/8/14 18:46
 * @description
 **/
object ForDemo06 {
  def main(args: Array[String]): Unit = {
    var numbers = for (i <- 1 to 10) yield {
      if (i % 2 == 0) 0 else math.pow(i, 2).toInt
    }
    println(numbers)

    /**
     * Vector(1, 0, 9, 0, 25, 0, 49, 0, 81, 0)
     */
  }
}
