package com.sakura.chapter05

/**
 * @author 5akura
 * @create 2020/2020/8/15 22:15
 * @description
 **/
object LazyFunc {
  def main(args: Array[String]): Unit = {

    lazy val res = sum(20,30)
    println("--------")
    println(res)

    /**
     * --------
     * sum执行了。。。。
     * 50
     */
  }

  def sum(num1:Int,num2:Int) = {
    println("sum执行了。。。。")
    num1 + num2
  }
}
