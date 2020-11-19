package com.sakura.chapter07

import com.sakura.chapter07.TestVisibility.object_name

/**
 * @author sakura
 * @date 2020/10/4 上午11:34
 */
object TestVisibility {

  val object_name:String = "companion object"
  println("Hello, I am the companion object!")

  def sayHi: Unit = {
    println("Hello World")
  }

  def main(args: Array[String]): Unit = {
    val s1:Student = new Student("Sakura","18130311")

    println(Student.count)

    val person = new Person

  }

}

class TestVisibility {
  val class_name:String = "companion class"

  println("Hi, I am the companion class")

  def saySomething(words:String): Unit = {
    println(s"companion class: ${words}");
  }

}


object Student {
  println("创建了一个学生")

  var count:Int = 56

  def sayHi: Unit = {
    println("你好，我是一名学生！")
  }
}

class Student(inName:String, inId:String) {
  private var name:String = inName
  private var id:String = inId

  def introduce: Unit = {
    println("My Name Is " + name + ", And My Id Is " + id)
  }
}


package a {
  object test {
    val s1 = new Student("sakura", "18130311")

  }
}


class Person {
  var name:String = "Sakura"
  private var age:Int = 20
  protected var sex:String = "Male"
}