package com.sakura.chapter07


/**
 * @author sakura
 * @date 2020/10/27 下午8:44
 */
object ScalaFieldOverride {
  def main(args: Array[String]): Unit = {
    val a = new A
    val b = new B
    println(a.str) // 我是A类
    println(b.str) // 我是B类

    println(a.testA())
    println(b.testA)
  }
}

class A {
  val str:String = "我是A类"

  def testA(): String = {
    return str;
  }
}

class B extends A {
  override val str:String = "我是B类"

  override val testA:String = "Hello";

}