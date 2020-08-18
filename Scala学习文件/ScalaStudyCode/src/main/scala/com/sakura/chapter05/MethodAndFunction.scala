package com.sakura.chapter05

/**
 * @author 5akura
 * @create 2020/2020/8/14 20:28
 * @description
 **/
object MethodAndFunction {
  def main(args: Array[String]): Unit = {
    var calculator = new Calculator
    // 方法的调用
    println("calculator.add(1, 2) = " + calculator.add(1, 2)) // 3

    // 方法转函数
    val function1 = calculator.add _
    // 函数的调用
    println("function1(2,3) = " + function1(2, 3)) // 5

    // 函数的定义
    val function2 = (num1: Int, num2: Int) => {
      // 函数体
      num1 + num2
    }
    // 函数的使用
    println("function2(3,4) = " + function2(3, 4))

  }
}

class Calculator {

  /**
   * 类的一个方法
   *
   * @param num1
   * @param num2
   * @return
   */
  def add(num1: Int, num2: Int): Int = {
    num1 + num2
  }
}