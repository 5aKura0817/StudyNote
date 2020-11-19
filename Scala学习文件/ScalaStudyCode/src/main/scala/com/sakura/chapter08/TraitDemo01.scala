package com.sakura.chapter08


/**
 * @author sakura
 * @date 2020/11/4 下午10:41
 */
object TraitDemo01 {
  def main(args: Array[String]): Unit = {
    val pig = new Pig
    pig.snore()
    pig.sleepTalk()

  }
}

class Pig extends Sleep {
  override def snore(): Unit = {
    println("呼～～～～")
  }

  override def sleepTalk(): Unit = {
    println("我要吃螃蟹～～吃披萨～～")
  }

}

// 这是一个trait
trait Sleep {

  var name:String = "噜噜"

  // 下面是待实现的抽象方法
  def snore()
  def sleepTalk()
}
