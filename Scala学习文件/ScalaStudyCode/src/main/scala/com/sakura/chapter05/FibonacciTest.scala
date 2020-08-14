package com.sakura.chapter05

/**
 * @author 5akura
 * @create 2020/2020/8/14 21:48
 * @description
 **/
object FibonacciTest {
  def main(args: Array[String]): Unit = {
    println("f(0) = " + fibonacci(0))
    println("f(1) = " + fibonacci(1))
    println("f(2) = " + fibonacci(2))
    println("f(10) = " + fibonacci(10))
    println("---------------------------")
    println("myFun(4) = " + myFunc(4))
    println("myFun(5) = " + myFunc(5))
  }

  def fibonacci(num: Int): Int = {
    if (num <= 1) {
      if (num < 0) 0 else 1
    } else {
      fibonacci(num - 1) + fibonacci(num - 2)
    }
  }

  def myFunc(num: Int): Int = {
    if (num == 1) 1 else 2 * myFunc(num - 1) + 1
  }
}
