package com.sakura.chapter02

/**
 * @author 5akura
 * @create 2020/2020/8/12 21:36
 * @description
 **/
object CharDemo {
  def main(args: Array[String]): Unit = {
    var ch:Char = 97
    println(s"ch=$ch")

    var ch2:Char = 'a'
    println(s"$ch2 + 1.2 = " + {ch2+1.2})

    // var ch3:Char = 97+1  error
    // var ch4:Char = 'a'+1 error
  }
}
