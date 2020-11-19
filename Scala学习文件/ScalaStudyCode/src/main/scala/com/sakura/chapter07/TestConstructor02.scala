package com.sakura.chapter07.constractor

/**
 * @author sakura
 * @date 2020/10/23 上午12:36
 */
object TestConstructor02 {
  def main(args: Array[String]): Unit = {
    val student = new Student()
  }
}

class Person(inName:String, inAge:Int) {
  var name:String = inName
  var age:Int = inAge
  println("Person(inName,inAge)")

  def this(inName:String) {
    this(inName,18);
    println("Person this(inName)")
  }

  def this() {
    this("sakura",18)
    println("Person this()")
  }

}

class Student(stuName:String, stuAge:Int, stuId:String) extends Person(stuName,stuAge) {
  var id:String = stuId
  println("Student(stuName, stuAge, stuId)")

  def this (stuName:String, stuAge:Int) {
    this(stuName,stuAge,"181303xx")
    println("Student this(stuName, stuAge)")
  }

  def this() {
    this("sakura",18,"18130311")
    println("Student this()")
  }
}