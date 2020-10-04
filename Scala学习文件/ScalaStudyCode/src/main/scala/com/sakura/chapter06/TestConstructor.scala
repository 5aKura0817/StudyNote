package com.sakura.chapter06

import scala.beans.BeanProperty

/**
 * @author sakura
 * @date 2020/9/21 下午3:13
 */
object TestConstructor {
  def main(args: Array[String]): Unit = {
//    val p1:Person = new Person("sakura",20)
//    println(p1)

//    val p2:Person = new Person("sakura")
//    println(p2)

    val s1:Student = new Student("sakura",20);
    println(s1.inName)
    s1.inName = "xiaohua"
//    s1.inAge = 19  错误用法，用val定义的inAge为只读的
    println(s1.name)


  }
}

class Person private(inName:String, inAge:Int) {

  var name:String = inName
  var age:Int = inAge

  println("调用了主构造器")

  override def toString: String = {
    "name: " + name + "; age: " + age
  }

  println("构造完成！")

  private def this() {
    this("sakura",20)
    println("空参构造")
  }

  def this(inName:String) {
    this
    name = inName
  }
}

// 将形参inName变为了Student的一个可读可写成员变量,inAge是只读的！
class Student (var inName:String,val inAge:Int) {
  @BeanProperty
  var name:String = inName

  @BeanProperty
  var age:Int = inAge
}
