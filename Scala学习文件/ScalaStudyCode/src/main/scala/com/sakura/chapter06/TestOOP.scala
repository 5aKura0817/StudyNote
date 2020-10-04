package com.sakura.chapter06

/**
 * @author sakura
 * @date 2020/9/10 下午3:22
 */
object TestOOP {
  def main(args: Array[String]): Unit = {
    val tom:Cat = new Cat
    println(tom.name) // null
    println(tom.age)  // 0

    tom.name = "tom"
    tom.age = 3
    tom.color = "orange"

    println("----赋值后：----")
    println(tom.name) // tom
    println(tom.age)  // 3
    tom.speak()


  }
}

class Cat extends Animal {
  var name:String = _
  var age:Int = _
  var color:String = _

  def speak (): Unit = {
    println("miao~")
  }
}

class Animal {
  def introduce (): Unit ={
    println("I am an Animal")
  }
}
