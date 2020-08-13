package com.sakura.chapter02

import scala.collection.StringOps
import scala.io.StdIn

/**
 * @author 5akura
 * @create 2020/2020/8/12 19:17
 * @description
 **/
object Variable {
  def main(args: Array[String]): Unit = {
    /**
     * 普通的变量定义声明
     */
    var num:Int = 10
    var num2:Double = 23.2
    // 小数默认为 double
    var num3:Float = 4.1f
    var flag:Boolean = true

    /**
     * 变量类型自动推导
     * 与动态语言不同的是：类型一旦确定就不能再修改了(强类型语言)
     */
    var i = 10
    // i = 3.7 这样修改了变量类型，是不允许的！
    var j = 4.56
    var k = 2.4f
    var m = false

    

  }
}
