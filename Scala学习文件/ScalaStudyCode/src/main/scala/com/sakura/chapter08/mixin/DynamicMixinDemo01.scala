package com.sakura.chapter08.mixin

/**
 * @author sakura
 * @date 2020/11/12 下午10:34
 */
object DynamicMixinDemo01 {
  def main(args: Array[String]): Unit = {
    val animal_A = new Animal
    animal_A.sayHi() // 普通对象，未进行扩展

    // 动态混入 Hunt
    val animal_B = new Animal with Hunt
    animal_B.catchMice

    // 动态混入 MakeNoise
    val animal_C = new Animal with MakeNoise
    animal_C.shout

    // ...
    val animal_D = new Animal with Hunt with MakeNoise
    animal_D.catchMice
    animal_D.shout
    // 以上是使用动态混入 进行了扩展了对象

  }
}

class Animal {
  def sayHi(): Unit = {
    println("我是一个动物！")
  }
}

trait Hunt {
  def catchMice: Unit = {
    println("抓到一只耗子")
  }
}

trait MakeNoise {
  def shout: Unit = {
    println("miao~~miao~~~")
  }
}