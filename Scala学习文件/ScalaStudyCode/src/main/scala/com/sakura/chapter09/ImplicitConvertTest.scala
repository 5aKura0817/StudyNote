package com.sakura.chapter09

/**
 * @author sakura
 * @date 2020/11/18 下午5:56
 */
object ImplicitConvertTest {

  def main(args: Array[String]): Unit = {

    // 隐式函数 在遇到要数据Double转换为Int的时候，自动应用！
    implicit def intToDouble (value:Double) :Int = {
      ((value * 10 + 5) / 10 ).toInt // 四舍五入
    }

    // 自动调用隐式函数～
    var number:Int = 3.7
    println(number) //  4

    // 使用常规的toInt～
    var number2:Int = 3.7.toInt
    println(number2) // 3
  }
}


