package com.sakura.chapter08.mixin

/**
 * @author sakura
 * @date 2020/11/13 下午9:44
 */
object DynamicMixinDemo02 {
  def main(args: Array[String]): Unit = {
    val obj = new SimpleClass with MyTraitD with MyTraitC
    println("==================")
//    obj.showInfo()
//    val obj2 = new SimpleClass with MyTraitB with MyTraitA
//    obj2.showInfo()
    val obj2 = new MyClassA

  }
}

class SimpleClass {
  println("SimpleClass created")
}

trait MyTraitA {
  println("Mixin MyTraitA~")

  def showInfo(): Unit = {
    println("I mixed MyTraitA")
  }
}

trait MyTraitB extends MyTraitA {
  println("Mixin MyTraitB~")

  override def showInfo(): Unit = {
    super.showInfo()

    println("I mixed MyTraitB")
  }
}

trait MyTraitC extends MyTraitB {
  println("Mixin MyTraitC~")

  override def showInfo(): Unit ={
    super.showInfo()
    println("I mixed MyTraitC")
  }
}

trait MyTraitD extends MyTraitB {
  println("Mixin MyTraitD~")

  override def showInfo(): Unit = {
    super.showInfo()
    println("I mixed MyTraitD")
  }
}

class MyClassA extends MyTraitD with MyTraitC {
  println("MyClassA created")
}