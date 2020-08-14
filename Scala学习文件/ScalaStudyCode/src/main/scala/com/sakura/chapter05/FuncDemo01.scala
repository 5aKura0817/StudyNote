package com.sakura.chapter05

/**
 * @author 5akura
 * @create 2020/2020/8/14 21:15
 * @description
 **/
object FuncDemo01 {
  def main(args: Array[String]): Unit = {
    println("1 * 2 = " + calculate(1, 2, '*'))
    println("9 / 2 = " + calculate(9, 2, '/'))

  }


  def calculate(operand1: Int, operand2: Int, operator: Char) = {
    if (operator == '+') {
      operand1 + operand2
    } else if (operator == '-') {
      operand1 - operand2
    } else if (operator == '*') {
      operand1 * operand2
    } else if (operator == '/') {
      if (operand2 == 0) {
        null
      } else {
        operand1 / operand2
      }
    } else {
      null
    }
  }
}
