package com.sakura.chapter01

object PrintDemo {
  def main(args: Array[String]): Unit = {
    val str: String = "Hello"
    val str2: String = "World"
    println("-----以下是类Java方式输出-----")
    println(str + "," + str2)


    val name: String = "zhangsan"
    val age: Int = 22
    val salary: Double = 10502.27
    println("-----以下是格式化输出(printf)-----")
    printf("My name is %s. \nI am %d years old. \nmy salary is %.2f", name, age, salary)


    println()
    println("-----以下是使用$ 引用输出-----")
    println(s"My name is $name. \nI am $age years old. \nmy salary is $salary")
    println("还可以引用表达式")
    println(s"My name is $name. \nI am ${age + 1} years old. \nmy salary is ${salary * 2}")


  }

}
