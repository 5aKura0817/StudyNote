package com.sakura.chapter08

/**
 * @author sakura
 * @date 2020/11/5 上午12:03
 *
 */
object TraitDemo02 {
  def main(args: Array[String]): Unit = {
    val dog = new Dog
    dog.findFood
  }
}

class Dog extends Eat {
  override def findFood: Unit = {
    println("发现骨头！！汪汪汪～～")
  }

  override def eatFood(): Unit = {
    println("恰骨头！！")
  }
}

trait Eat {
  def findFood: Unit = {
    println("发现食物！！")
  }
  def eatFood()
}
