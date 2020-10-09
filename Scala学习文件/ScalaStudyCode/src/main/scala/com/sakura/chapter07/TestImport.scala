package com.sakura.chapter07




/**
 * @author sakura
 * @date 2020/10/9 下午7:50
 */
object TestImport {

  object testA {

    import java.util.{HashMap => _, _}
    import scala.collection.mutable.HashMap

    val list = new ArrayList[Int]()
    val map2 = new HashMap[Int, String]()

  }

  object testB {

  }

}
