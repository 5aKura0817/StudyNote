package com.sakura.chapter08.mixin

/**
 * @author sakura
 * @date 2020/11/15 下午2:24
 */
object DynamicMixinDemo04 {
  def main(args: Array[String]): Unit = {
    val obj1 = new SimpleClass with SimpleTrait
    obj1.num = 2000
    println(obj1.num) // 2000

    val obj2 = new SimpleClass with SimpleTrait
    println(obj2.num) // 2020

  }
}

trait SimpleTrait {
  var num:Int = 2020
  var str:String = "Hello World"
}
