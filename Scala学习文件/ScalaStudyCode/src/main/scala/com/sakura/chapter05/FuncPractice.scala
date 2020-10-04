package com.sakura.chapter05

/**
 * @author sakura
 * @date 2020/9/10 上午10:52
 */
object FuncPractice {

  def main(args: Array[String]): Unit = {
    drawTriangle(6                                                                                                                                                                                                                                                                                                                                                                                                                                        )
    drawMultiplicationTable(6)
    
  }

  /**
   * 按层级输出金字塔
   *
   * @param heigh
   */
  def drawTriangle(heigh: Int = 5): Unit = {
    for (i <- 0 to heigh) {
      for (j <- 1 to heigh - i) {
        print(" ")
      }
      for (k <- 1 to i * 2 + 1) {
        print("*")
      }
      println()
    }
  }

  /**
   * 按层级输出乘法表
   *
   * @param level
   */
  def drawMultiplicationTable(level: Int = 9): Unit = {
    for (i <- 1 to level) {
      for (j <- 1 to i) {
        printf("%d×%d=%-2d", j, i, i * j)
        print("  ")
      }
      println()
    }
  }
}
