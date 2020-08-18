package com.sakura.chapter05

/**
 * @author 5akura
 * @create 2020/2020/8/15 21:40
 * @description
 **/
object FuncNotice05 {
  def main(args: Array[String]): Unit = {
    println(sum()) // 0
    println(sum(1, 3, 5, 7, 9)) // 25
    println(sub(19)) // 19
    println(sub(19, 1, 3, 5, 7)) //3
    //    sub() 报错，缺少参数
  }


  def sum(numbers:Int*): Int ={
    var res:Int = 0
    for (number <- numbers) {
      res += number
    }
    res
  }

  /**
   *
   * @param minuend 被减数(必须)
   * @param nums 减数(可变)
   * @return
   */
  def sub(minuend:Int, nums:Int*):Int ={
    var res:Int = minuend
    for (num <- nums){
      res -= num
    }
    res
  }
}
