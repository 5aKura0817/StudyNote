package com.sakura.chapter05

/**
 * @author 5akura
 * @create 2020/2020/8/15 20:53
 * @description
 **/
object FuncNotice04 {
  def main(args: Array[String]): Unit = {
    connectMysql() // localhost:3306 root 123456
    println("--------------")
    connectMysql("192.168.1.1",6666) // 192.168.1.1:6666 root 123456
    println("--------------")
    //如果我只想改用户名和密码呢？
    //connectMysql("sakura","170312") ?因为是从左向右覆盖，，所以行不通 除非你把函数的后两个参数写到前面

    // 使用带名参数
    connectMysql(username = "sakura",password = "170312") // localhost:3306 sakura 170312
  }

  def connectMysql(host:String = "localhost", port:Int = 3306,
                   username:String = "root", password:String = "123456"): Unit ={
    println(s"$host:$port")
    println(s"username: $username")
    println(s"password: $password")
  }
}
