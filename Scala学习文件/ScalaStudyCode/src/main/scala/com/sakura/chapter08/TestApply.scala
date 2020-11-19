package com.sakura.chapter08

/**
 * @author sakura
 * @date 2020/11/3 下午10:38
 */
object TestApply {
  def main(args: Array[String]): Unit = {
    // 传统创建对象的方法
    val cat1 = new Cat("来宝")

    // 使用apply触发伴生类创建对象
    val cat2 = Cat("书宝")
    val cat3 = Cat()

    println("cat1: " + cat1.name) // 来宝
    println("cat2: " + cat2.name) // 书宝
    println("cat3: " + cat3.name) // 无名猫
  }
}

class Cat(inName:String) {
  var name:String = inName
}

object Cat {
  def apply(inName: String): Cat = new Cat(inName)

  def apply(): Cat = new Cat("无名猫")
}
