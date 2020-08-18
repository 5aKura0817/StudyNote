package com.sakura.chapter05

/**
 * @author 5akura
 * @create 2020/2020/8/15 20:32
 * @description
 **/
object FuncNotice02 {
  def main(args: Array[String]): Unit = {

    def func(msg: String): Unit = { // private final func$1
      println(msg)
      def func (msg:String): Unit = { // private final func$2
        println(msg)
      }
    }

  }

  def func(msg: String): Unit = {
    println(msg)
  }

}
