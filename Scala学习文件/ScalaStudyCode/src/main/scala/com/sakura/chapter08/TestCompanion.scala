package com.sakura.chapter08

import com.sakura.chapter08.Student.cnt

/**
 * @author sakura
 * @date 2020/11/3 下午7:10
 */
object TestCompanion {
  def main(args: Array[String]): Unit = {
    val zs = new Student("张三", 18)
    val ls = new Student("李四", 20)

    zs.introduce()
    Student.countStu()
  }
}

class Student(inName:String, inAge:Int) {
  var naem:String = inName
  var age:Int = inAge
  cnt = cnt + 1

  def introduce(): Unit = {
    println(s"My name is $naem, I am $age years old.")
  }
}

object Student {
  var cnt:Int = 0;

  def countStu(): Unit = {
    println(s"现在共有${cnt}个学生!")
  }
}
