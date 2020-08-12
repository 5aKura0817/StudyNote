package com.sakura.chapter02

/**
 * @author 5akura
 * @create 2020/2020/8/12 19:26
 * @description
 **/
object VarAndVal {
  def main(args: Array[String]): Unit = {
    /**
     * var:声明的变量可以修改值
     * val:声明的变量不可以修改值 不存在线程安全的问题
     */
    var i = 10
    i = 28 // ok

    val j = 17
    // j = 23 这样是不允许的

    val myDog = new Dog
    // 修改属性
    myDog.age = 2
    myDog.name = "小花"
    // 修改对象引用，不允许
    // myDog = new Dog


  }
}

class Dog {

  var age:Int = _ //赋默认值

  var name:String = _

}