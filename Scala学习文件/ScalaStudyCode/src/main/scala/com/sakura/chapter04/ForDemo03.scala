package com.sakura.chapter04

/**
 * @author 5akura
 * @create 2020/2020/8/13 22:47
 * @description
 **/
object ForDemo03 {
  def main(args: Array[String]): Unit = {
    for (i <- 1 to 10 if i%2==0){
      println(s"${i} is a even number")
    }

    /**
     * 2 is a even number
     * 4 is a even number
     * 6 is a even number
     * 8 is a even number
     * 10 is a even number
     */
  }
}
