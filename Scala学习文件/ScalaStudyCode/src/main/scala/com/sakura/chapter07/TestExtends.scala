package com.sakura.chapter07

/**
 * @author sakura
 * @date 2020/10/20 下午11:18
 */
object TestExtends {
  def main(args: Array[String]): Unit = {
    val student = new HbueStudent
    student.id = "18130311"
    student.name = "sakura"
    //student.age = 20
    student.sex = "boy"
    student.introduce
    student.sayHi
  }
}

class HbuePerson {
  var name:String = _
  private var age:Int = _
  var sex:String = _

  private def showInfo: Unit = {
    println(s"My name is $name, $age years old, a $sex")
  }

  def sayHi: Unit = {
    println(s"Hi, my name is $name")
  }
}

class HbueStudent extends HbuePerson {
  var id:String = _

  def introduce: Unit = {
    println(s"I am a student, my name is $name, my id is $id")
  }
}