package com.sakura.chapter07

/**
 * @author sakura
 * @date 2020/10/28 下午10:02
 */
object AbstractClassTest {
  def main(args: Array[String]): Unit = {
    val anonymousObj = new MyAbstractClass {
      override var str: String = "Hello World"

      override def abstractMethod(param: String): Unit = {
        println(param + ", i am not a abstract method")
      }
    }
  }
}

abstract class MyAbstractClass {
  // 这是一个抽象属性，没有初始值！
  var str:String
  // 这是一个普通属性
  var num:Int = 10;
  // 这是一个抽象方法
  def abstractMethod(param:String)
}

class MySimpleClass extends MyAbstractClass {
  override var str: String = "Hello World"

  override def abstractMethod(param: String): Unit = {
    for (i <- 1 to 10) {
      println(i + ":" + str)
    }
  }
}