package com.sakura.chapter07.method

/**
 * @author sakura
 * @date 2020/10/22 下午6:38
 */
object MethodOverrideTest {
  def main(args: Array[String]): Unit = {
    val student = new Student
    student.showInfo
  }
}

class Person {

  def showInfo: Unit = {
    println("I am a Person")
  }
}


class Student extends Person {

  // 使用override修饰 表示对父类方法的重写
  override def showInfo: Unit = {
    // 使用super关键字调用父类的方法，以区别子类中重写的方法
    super.showInfo
    println("and I am a Student")
  }

}
