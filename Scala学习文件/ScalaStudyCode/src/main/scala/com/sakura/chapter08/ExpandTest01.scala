package com.sakura.chapter08

/**
 * @author sakura
 * @date 2020/11/15 下午4:04
 */
object ExpandTest01 {
  def main(args: Array[String]): Unit = {
    val logger = new MyLogger
    logger.showErrorMessage()
    logger.debug("这是一条调试信息")
    logger.warning("这是一条警告消息")

  }
}


class Logger {
  def debug(info:String): Unit = {
    println("DEBUG:" + info)
  }

  def warning(info:String): Unit = {
    println("WARNING:" + info)
  }

  def error(info:String): Unit = {
    println("ERROR:" + info)
  }
}



trait ErrorLogger extends Logger {
  def showErrorMessage(): Unit = {
    error("一条错误消息！！")
  }
}

class UnknownClass {

}

class MyLogger extends ErrorLogger {

}

