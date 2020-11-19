package com.sakura.chapter08.mixin

/**
 * @author sakura
 * @date 2020/11/14 下午10:21
 */
object DynamicMixinDemo03 {
  def main(args: Array[String]): Unit = {
    val obj = new SimpleClass with MyTrait4 with MyTrait2 with MyTrait3
    obj.func()
  }
}


trait MyTrait1 {
  def func()
}

trait MyTrait2 extends MyTrait1 {
  abstract override def func(): Unit = {
    println("MyTrait2 func()")
    super.func()
  }
}

trait MyTrait3 extends MyTrait1 {
  abstract override def func(): Unit = {
    println("MyTrait3 func()")
    super.func()
  }
}

trait MyTrait4 extends MyTrait1 {
  override def func(): Unit = {
    println("MyTrait4 func()")
  }
}