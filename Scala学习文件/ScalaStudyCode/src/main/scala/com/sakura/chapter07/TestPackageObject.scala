package com.sakura.chapter07

/**
 * @author sakura
 * @date 2020/10/3 上午11:59
 */

package object sakura {
  val name = "sakura"
  var age = 20

  def sayHi () = {
    println("Hello World")
  }
}

package sakura{

  object TestSakura {
    def main(args: Array[String]): Unit = {
      // 直接使用package object中的方法和变量
      println("info-> {" + s"name : $name" + s", age : $age}")
      sayHi()

      println("-----------")

      val root:User = new User
      println(s"username: ${root.username}" + s", age: ${root.user_age}")
      print("User.sendMessage() => ")
      root.sendMessage
    }
  }

  // 包内随处可用
  class User {
    val username = name
    var user_age = age

    def sendMessage: Unit = {
      sayHi()
    }

  }

}


object TestPackageObject {

}
