package com.sakura.chapter08

import com.sakura.chapter08.OuterClass.StaticInnerClass

/**
 * @author sakura
 * @date 2020/11/16 下午1:47
 */
object InnerClassDemo01 {
  def main(args: Array[String]): Unit = {
    val outer = new OuterClass
    val outer2 = new OuterClass
    outer.showNumber // 20

    outer.number = 30

    val inner = new outer.InnerClass
    inner.showInnerNumber // 31

    outer2.fixInnerNumber(inner)
    inner.showInnerNumber
    

    val staticInnerClass = new StaticInnerClass
    staticInnerClass.showStaticInnerNumber // 2020

  }
}

class OuterClass {

  Outer =>
  var number:Int = 20
  def showNumber: Unit = {
    println(number)
  }

  class InnerClass {
    var innerNumber:Int = Outer.number + 1
    def showInnerNumber: Unit = {
      println(innerNumber)
    }

    def fixOuterNumber(outerClass: OuterClass): Unit ={
      outerClass.number = outerClass.number*2
    }
  }

  def fixInnerNumber(inner: OuterClass#InnerClass): Unit ={
    inner.innerNumber += 2000
  }

}

object OuterClass {
  class StaticInnerClass {
    var staticInnerNumber:Int = 2020

    def showStaticInnerNumber: Unit = {
      println(staticInnerNumber)
    }
  }
}
