package com.sakura.chapter05

/**
 * @author 5akura
 * @create 2020/2020/8/15 20:46
 * @description
 **/
object FuncNotice03 {
  def main(args: Array[String]): Unit = {
    sayHi() // Bob: Hello
    sayHi("Sakura") // Sakura: Hello
    // say() 报错，需要指定msg
    // 使用带名参数
    say(msg = "My name is mike!")
  }

  def sayHi(name:String = "Bob"): Unit = {
    println(s"$name: Hello")
  }

  def say(name:String = "Mike", msg:String): Unit ={
    println(s"$name : $msg")
  }
}
