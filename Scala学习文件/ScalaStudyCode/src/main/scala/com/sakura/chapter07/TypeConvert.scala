package com.sakura.chapter07.convert


/**
 * @author sakura
 * @date 2020/10/22 下午7:02
 */
object TypeConvert {
  def main(args: Array[String]): Unit = {
    val p1:Person = new Student
    val p2:Person = new Coder

    // 此时p1、p2都是Person类型 无法调用到其引用类型的独有方法！
    // 例如：p1.study   p2.coding 都是无法调用的

    doSomething(p1)
    doSomething(p2)
  }

  def doSomething (p:Person): Unit = {
    if (p.isInstanceOf[Student]){ // 判断p是不是Student类型
      p.asInstanceOf[Student].study
    } else if (p.isInstanceOf[Coder]) { // 判断p是不是Coder类型
      p.asInstanceOf[Coder].coding
    } else {
      println("类型转换失败...")
    }
  }
}

class Person {
  def showInfo: Unit = {
    println("I am a Person")
  }
}

class Student extends Person {
  override def showInfo: Unit = {
    println("I am a Student")
  }

  def study: Unit = {
    println("I am studying...")
  }
}


class Coder extends Person {
  override def showInfo: Unit = {
    println("I am a Coder")
  }

  def coding: Unit = {
    println("I am 996 working...")
  }
}
