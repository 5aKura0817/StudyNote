package com.sakura.chapter05

/**
 * @author sakura
 * @date 2020/9/10 上午9:43
 */
object ExceptionDemo01 {

  def main(args: Array[String]): Unit = {

    try {
      var i: Int = 10 / 0
    } catch {
      case exception: Exception => {
        println("捕获到一个异常")
      };
      case exception: ArithmeticException => println("捕获到一个算数异常")
    } finally {
      // ...
    }

    try {
      exceptionFunc()
    } catch {
      case ex: Exception => println("捕获到异常:" + ex.getMessage)
    }
    println("continue")

  }


  /**
   * 模拟异常
   */
  def exceptionFunc(): Nothing = {
    throw new Exception("异常出现～～～")
  }

  @throws(classOf[ArithmeticException])
  @throws(classOf[NumberFormatException])
  def exceptionFunc02(): Unit = {
    var i: Int = "abc".toInt
    var a: Int = 10 / 0
  }
}
