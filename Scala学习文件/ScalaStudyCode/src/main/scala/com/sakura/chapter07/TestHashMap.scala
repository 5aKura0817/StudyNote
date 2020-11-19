package com.sakura.chapter07



/**
 * @author sakura
 * @date 2020/10/18 下午5:19
 */
object TestHashMap {

  import java.util.{HashMap => JavaHashMap}
  import collection.mutable.{HashMap => ScalaHashMap}

  def main(args: Array[String]): Unit = {
    val javaHashMap = new JavaHashMap[Int,String]() // Scala中泛型使用[]包裹
    javaHashMap.put(1,"one")
    javaHashMap.put(2,"two")
    javaHashMap.put(3,"three")
    javaHashMap.put(4,"four")

    val scalaHashMap = new ScalaHashMap[Int,String]()

    for (key <- javaHashMap.keySet().toArray()){
      // key.asInstanceOf[Int] 将key转化为Int类型
      // scalaHashMap += ... 将javaHashMap中取出的key->value 加入到scalaHashMap
      scalaHashMap += (key.asInstanceOf[Int] -> javaHashMap.get(key))
    }

    // mkString 输出集合中所有成员，并使用指定的分隔符隔开
    println(scalaHashMap.mkString(","))
  }

}
