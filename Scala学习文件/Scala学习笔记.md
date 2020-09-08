[toc]

# Scala简介

==面向对象编程==结合==函数式编程==，**运行在JVM上**，吸取了Java的优点并对Java复杂的地方做了简化。





# 快速入门之HelloWorld

1. 创建`HelloScala.scala`

   ```scala
   object HelloScala {
   	def main(args:Array[String]):Unit = {
   		println("hello scala")
   	}
   }
   ```

2. `scalac .\HelloScala.scala`编译

3. `scala HelloScala`运行

----

也可以直接使用`scala .\HelloScala.scala`运行



> Java程序编译后，一般都是一个class文件，但是Scala编译后却有两个class文件：
> ![image-20200811192737587](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200811192737.png)
>
> 反编译后，看看两个文件的内容吧：
>
> HelloScala.class
>
> ```java
> import scala.reflect.ScalaSignature;
> 
> public final class HelloScala {
> public static void main(String[] paramArrayOfString) {
>  HelloScala$.MODULE$.main(paramArrayOfString);
> }
> }
> ```
>
> 
>
> HelloScala$.class
>
> ```java
> import scala.Predef$;
> 
> public final class HelloScala$ {
> public static final HelloScala$ MODULE$ = new HelloScala$();
> 
> public void main(String[] args) {
>  Predef$.MODULE$.println("hello scala");
> }
> }
> ```
>
> 
>
> 两者之间是一个相互调用的关系。HelloScala类的main中调用HelloScala\$类中静态变量 MODULE$的main方法，
> 而HelloScala\$类中 MODULE\$是一个当前类的实例化对象，然后调用main方法，其main方法中又调用了Predef\$类中MODULE\$的println方法。

 

通过第一个快速入门程序做一下小分析：

- `def`是定义方法的关键字
- `main`是方法名，main方法是程序的入口
- `args`是参数名
- `Array[String]`参数类型，表示参数是一个String类型的Array
- Scala的特点，参数名在前，类型在后
- `:Unit=`表示函数返回值为空 void
- println("..")输出内容





# IDEA开发Scala

## 工程创建

1. 创建一个Maven工程

2. 在src/main目录下创建一个scala文件夹。并标记为source文件夹

   ![image-20200811195503445](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200811195503.png)

3. 添加Scala框架支持

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200811200556.png" alt="image-20200811200556562" style="zoom:67%;" />

   配置好Scala SDK,即可创建ScalaObject：
   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200811200736.png" alt="image-20200811200736039" style="zoom:67%;" /> 

4. 老规矩，先建包!!!然后创建Object

5. 在IDEA中写代码不要太爽！！

   ![image-20200811200922848](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200811200922.png)

   可以直接运行！



## Scala开发规范

1. 源文件以`.scala`作为文件后缀名
2. 每条语句末尾可以不用分号，但是多条语句同行，需要使用分号分隔
3. Scala严格区分大小写

------



# Scala基础语言学习



## Chap01.内容输出与文档查看

> 三种输出内容的方式

1. 类似Java的输出方式

   ```scala
   val str: String = "Hello"
   val str2: String = "World"
   println("-----以下是类Java方式输出-----")
   println(str + "," + str2)
   ```

2. 格式化输出，有点像c语言的`printf`

   ```scala
   val name: String = "zhangsan"
   val age: Int = 22
   val salary: Double = 10502.27
   println("-----以下是格式化输出(printf)-----")
   printf("My name is %s. \nI am %d years old. \nmy salary is %.2f", name, age, salary)
   ```

   ==注意：这种输出方式只能使用printf!!!==

3. 使用类似EL表达的引用输出

   ```scala
   val name: String = "zhangsan"
   val age: Int = 22
   val salary: Double = 10502.27
   
   println("-----以下是使用$ 引用输出-----")
   println(s"My name is $name. \nI am $age years old. \nmy salary is $salary")
   println("还可以引用表达式")
   println(s"My name is $name. \nI am ${age + 1} years old. \nmy salary is ${salary * 2}")
   ```

   ==千万不要以为括号里面那个s是你误敲的，不信你删了试试看。这个是这种输出方式的标志！！==



> 官方API文档查阅

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200811213622.png" alt="image-20200811213622270" style="zoom:50%;" />

解压找到帮助文档所在目录

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200811214845.png" alt="image-20200811214845852" style="zoom: 67%;" />

----



## Chap02.变量

### 2.1 声明/定义

普通方式的变量声明：

```scala
var num:Int = 10
var num2:Double = 23.2
// 小数默认为 double
var num3:Float = 4.1f
var flag:Boolean = true
```



使用类型推导定义变量

```scala
/**
* 变量类型自动推导
* 与动态语言不同的是：类型一旦确定就不能再修改了(强类型语言)
*/
var i = 10
// i = 3.7 这样修改了变量类型，是不允许的！
var j = 4.56
var k = 2.4f
var m = false
```

不像动态语言，变量的类型能随着类型的变化而变化。Scala声明变量后，类型确定了就不能再赋值其他类型的值了！！



变量声明时，标识符和其他语言规则一样，不可以使用数字作为变量名开头。

### 2.2 val与var

虽然两者都可以用来声明变量，但是使用`val`声明的变量时赋值后，==不可以再改变值。相当于Java中的final变量==

`var`声明和定义的变量是可以修改值。

==变量/对象声明时都需要初始化，要赋初始值！！==

> 我们创建对象后，**一般只是修改对象的属性值，而很少修改对象的引用。**
>
> 我们就可以使用val来声明对象，用var声明对象的属性！！并且使用val的好处是：==val声明的对象不存在线程安全的问题！！！==

```scala
object VarAndVal {
  def main(args: Array[String]): Unit = {
    /**
     * var:声明的变量可以修改值
     * val:声明的变量不可以修改值 不存在线程安全的问题
     */
    var i = 10
    i = 28 // ok

    val j = 17
    // j = 23 这样是不允许的

    val myDog = new Dog
    // 修改属性
    myDog.age = 2
    myDog.name = "小花"
    // 修改对象引用，不允许
    // myDog = new Dog
  }
}

class Dog {

  var age:Int = _ //赋默认值

  var name:String = _

}
```



### 2.3 数据类型

Scala完美诠释了什么是面向对象，在Scala中**一切皆对象**！！它拥有Java中所有的基本类型，Scala中使用基本类型定义的数据也是对象！

Scala所有对象都有同一个祖先对象`Any`,(相当于Java中的Object)。Any又有两个直接子类：`AnyVal(值类型)`、`AnyRef(引用类型)`

- AnyVal: 所有的基本类型(Int,Double,Float,Char,Boolean等)都是它的直接子类
- AnyRef: 所有的Java类，Scala的集合类，以及其他的自定义类属于AnyRef的子类



> 类型的结构图

![img](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200812205933.jpeg)

 

其中有两个特殊的类需要了解和区分一下：

- Null: 表示空值，null,是所有AnyRef类型的子类！
- Nothing：是任意类型的子类

两个类都属于底层类（BottomClass）,**Nothing常用于异常的抛出和处理**，我们知道在Java中异常是区分范围大小的，只有范围更大的（父类，或间接父类）的异常才能接收比其范围小的异常类型。例如Exception类是可以接收和处理所有Exception类的，而IOException则能接收处理的异常就相对较小。那么**如果将异常定义为Nothing类型，那么任何类都可以正常接收和处理到异常了！！**



> 类型表

| 数据类型 | 描述                                                         |
| :------- | :----------------------------------------------------------- |
| Byte     | 8位有符号补码整数。数值区间为 -128 到 127                    |
| Short    | 16位有符号补码整数。数值区间为 -32768 到 32767               |
| Int      | 32位有符号补码整数。数值区间为 -2147483648 到 2147483647     |
| Long     | 64位有符号补码整数。数值区间为 -9223372036854775808 到 9223372036854775807 |
| Float    | 32 位, IEEE 754 标准的单精度浮点数                           |
| Double   | 64 位 IEEE 754 标准的双精度浮点数                            |
| Char     | 16位无符号Unicode字符, 区间值为 U+0000 到 U+FFFF             |
| String   | 字符序列                                                     |
| Boolean  | true或false                                                  |
| Unit     | 表示无值，和其他语言中void等同。用作不返回任何结果的方法的结果类型。Unit只有一个实例值，写成()。 |
| Null     | null 或空引用                                                |
| Nothing  | Nothing类型在Scala的类层级的最底端；它是任何其他类型的子类型。 |
| Any      | Any是所有其他类的超类                                        |
| AnyRef   | AnyRef类是Scala里所有引用类(reference class)的基类           |



#### 2.3.1Char类型使用注意

- Char类型对象，存储空间为**2个字节**，赋值范围是（**0~65535**）。

- 输出Char类型的数据时，会默认将其转化为对应的Unicode字符，例如：

  ```scala
  var ch:Char = 97
  println("ch="ch) // ch=a
  ```

   

- 但是一旦Char类型用于计算，就会隐式转化为其他数值类型，并且计算结果也会转化为对应的数值类型。另外Scala中**所有的计算表达式都会做隐式类型转换！**

  ```scala
  var ch2:Char = 'a'
  println(s"$ch2 + 1 = " + {ch2+1}) // a + 1 = 98
  println(s"$ch2 + 1.2 = " + {ch2+1.2}) // a + 1.2 = 98.2
  ```

  所以，这种 var ch:Char = 97+1，var ch:Char = 'a'+1都是错误的写法！！！



#### 2.3.2、Unit、Null和Nothing

- Unit相当于其他语言中的void类型，其有唯一实例值`()`

  ```scala
  object UnitAndNullAndNothing {
    def main(args: Array[String]): Unit = {
      var test = testUnit()
      println(test) // ()
    }
  
    def testUnit(): Unit = {
      println("Hello Unit")
    }
  }
  ```

   

- Null类型也有唯一实例：`null`,但是只能赋值给AnyRef类型。不能赋给AnyVal类型，运行时会报错。

  ```scala
  object NullTest {
    def main(args: Array[String]): Unit = {
      var test = null //test's type is Null
  
      var ch:Char = null // 运行报错
      println(ch)
    }
  }
  ```

   

- Nothing是一切类的子类，且是一个不可实例化的抽象类，并且不可以被继承



#### 2.3.3、类型转换

> 隐式类型转换

**Scala中完全保留了Java的隐式类型转换机制。即更小范围的类型可以转化为更大范围的类型，反之则不行。**（例如：Float转Double可以，反之不行）**Byte、Short和Char类型的数据之间不会自动转换。三者类型的数据放在一起计算都先转为Int**

当数据进行计算的时候，都会先进行类型隐式转换，先向上转换为最近的类型，然后计算。

```scala
var s:Short = 5
s = s+2 //error,计算时因为2是Int,所以都先转化为Int类型然后计算，结果为Int类型, 将Int类型赋值给Short类型（error）
```



> 强制类型转换

任何基本类型的数据，都有`toInt`，`toDouble`，`toFloat`这样的方法，使用这种方法可以进行强制类型转换！！==强制类型转换就可能导致精度丢失或值溢出==

String类型的数据，转数值类型的时候，确保数据的规范性以及类型的正确。否者会抛`NumberFormatException`异常







## Chap03.运算符

> 取模运算的原则

- a%b <=> a - (a/b) * b
- 取模运算的结果，总与被取模数的符号一致！



> ++ 和 --

在Scala中不能使用++ 和 -- 改而使用 `+=` 和 `-=`



> 关系运算符

浮点型数据进行比较，哪怕值相同，类型不同结果也是false

```scala
var d:Double = 2.2
var f:Float = 2.2f
println(d==f) // false
```

 

> 赋值运算符

| 运算符      |               |
| ----------- | ------------- |
| x \>>= n    | 右移n位后赋值 |
| x <<=n      | 左移n位后赋值 |
| &=、\|=、^= | …             |



> 三目运算

Scala中不支持三目运算，改为if-else完成(**Scala设计概念：同一件事情尽量只有一种解决方法，保证代码风格统一**)

```scala
// var i = (5>3)?5:3 
var i = if (5>3) 5 else 3
```



> 获取键盘输入

`StdIn.readxxx` （trait scala.io.StdIn的方法，拿来即用）

```scala
var age = StdIn.readInt()
var name = StdIn.readLine()
```



## Chap04.流程控制

> 范围数据的for循环

for(item <- start **to** end){ … }

```scala
object ForDemo {
  def main(args: Array[String]): Unit = {
    for (i <- 1 to 10){
      println(s"$i, hello world")
    }
    /**
     * 1, hello world
     * 2, hello world
     * 3, hello world
     * 4, hello world
     * 5, hello world
     * 6, hello world
     * 7, hello world
     * 8, hello world
     * 9, hello world
     * 10, hello world
     */
  }
}
```

这种，类似于Java的增强for循环，可以用于对集合元素的遍历！！

```scala
var list = List("Hello","World",10,30,false)
for (item <- list){
    println(item)
}

/**
* Hello
* World
* 10
* 30
* false
*/
```



> 范围数据循环2

for(item <- start **until** end){ … }

```scala
object ForDemo02 {
  def main(args: Array[String]): Unit = {
    for (i <- 1 until 6){
      println(i)
    }

    /**
     * 1
     * 2
     * 3
     * 4
     * 5
     */
  }
}
```



> For循环，循环守卫

案例：

```scala
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
```

for循环中同行的if判断式，就是循环保卫式，为true时，执行循环中的语句，为false则跳过当前循环值（类似continue）



> For循环 引入变量

可以直接在for循环的同行中，使用循环变量进行操作。

```scala
object ForDemo04 {
  def main(args: Array[String]): Unit = {
    for (i <- 1 to 10; j = 2 * i){
      println(s"$i * 2 = $j")
    }

    /**
     * 1 * 2 = 2
     * 2 * 2 = 4
     * 3 * 2 = 6
     * 4 * 2 = 8
     * 5 * 2 = 10
     * 6 * 2 = 12
     * 7 * 2 = 14
     * 8 * 2 = 16
     * 9 * 2 = 18
     * 10 * 2 = 20
     */
  }
}
```



> 嵌套For循环简写

```scala
object ForDemo05 {
  def main(args: Array[String]): Unit = {
    for (i <- 1 to 10; j <- 1 to i){
      print(j+" ")
      if (j==i){
        println()
      }
    }

    /**
     * 1 
     * 1 2 
     * 1 2 3 
     * 1 2 3 4 
     * 1 2 3 4 5 
     * 1 2 3 4 5 6 
     * 1 2 3 4 5 6 7 
     * 1 2 3 4 5 6 7 8 
     * 1 2 3 4 5 6 7 8 9 
     * 1 2 3 4 5 6 7 8 9 10 
     */
  }
}
```

等价写法：

```scala
for (i <- 1 to 10) {
    for (j <- 1 to i) {
        print(j + " ")
        if (j == i) {
            println()
        }
    }
}
```

当i的for循环中有业务逻辑，就会出现问题，还是要使用传统的循环。



> For循环 yield暂存

```scala
object ForDemo06 {
  def main(args: Array[String]): Unit = {
    var numbers = for (i <- 1 to 10) yield {
      if (i % 2 == 0) 0 else math.pow(i, 2).toInt
    }
    println(numbers)

    /**
     * Vector(1, 0, 9, 0, 25, 0, 49, 0, 81, 0)
     */
  }
}
```



==for循环中的`()`可以使用`{}`替换==

> for循环，步长控制`Range(m,n,x)` m到n,步长为x

```scala
object ForDemo07 {
  def main(args: Array[String]): Unit = {
    for (i <- Range(1,10,2)){
      println(i)
    }
   	/**
     * 1
     * 3
     * 5
     * 7
     * 9
     */
  }
}
```

 

==Scala的设计者不推荐使用While循环，因为While循环是没有返回值的，不像for循环可以将循环中计算的结果返回出来直接使用，而While想要达到等同的效果就要在循环外额外定义变量，并在循环中修改。其作者认为循环内不应该影响的外部的变量，所以推荐使用for!例如递归的思想==

 



> Breakable 循环中断

```scala
// 需要手动导入此包！！
import util.control.Breaks._

object Breakable {
  def main(args: Array[String]): Unit = {
    var n = 1;
    while (n < 20) {
      if (n == 15) {
        break()
      }
      println(n)
      n += 1
    }
    println("hello world")
  }
}
```

以上这种写法，执行break()后，程序会异常中断，后面的代码无法执行。我们需要使用`breakable`来包裹代码，来处理中断异常：

```scala
object Breakable {
  def main(args: Array[String]): Unit = {
    var n = 1;
    breakable {
      while (n < 20) {
        if (n == 15) {
          break()
        }
        println(n)
        n += 1
      }
    }
    println("hello world")
  }
}
```

使用breakable包裹后的代码，能够处理中断异常，并且不影响后续的代码执行。其实==breakable是一个高阶函数==

==scala中去除了break和continue!!==



# Scala函数式编程学习

## Chap05.函数编程入门

==scala中，将函数式编程和面向对象编程融为了一体==

### 5.1、函数function 和 方法method

**几乎**可以等同，定义、使用、运行机制都是一样的。函数的使用更加灵活，方法也可以轻松转化为函数！

```scala
package com.sakura.chapter05

/**
 * @author 5akura
 * @create 2020/2020/8/14 20:28
 * @description
 **/
object MethodAndFunction {
  def main(args: Array[String]): Unit = {
    var calculator = new Calculator
    // 方法的调用
    println("calculator.add(1, 2) = " + calculator.add(1, 2)) // 3

    // 方法转函数
    val function1 = calculator.add _
    // 函数的调用
    println("function1(2,3) = " + function1(2, 3)) // 5

    // 函数的定义
    val function2 = (num1: Int, num2: Int) => {
      // 函数体
      num1 + num2
    }
    // 函数的使用
    println("function2(3,4) = " + function2(3, 4))

  }
}

class Calculator {

  /**
   * 类的一个方法
   *
   * @param num1
   * @param num2
   * @return
   */
  def add(num1: Int, num2: Int): Int = {
    num1 + num2
  }
}
```



### 5.2、函数的定义

```scala
def 函数名(参数1:参数类型,参数2:参数类型,...)[:返回值类型 = ]{
    /* 函数体 */
    [return] 返回值
}
```

中间有连个部分需要注意：

- 函数的返回类型，有三种写法
  1. `:返回值类型=`, 固定了返回值类型。
  2. `=`,直接使用等于, 返回值类型自动推断==不可以使用return！！==
  3. 什么都不写, 表示没有返回值
- 函数体的返回值（在函数要求有返回值的情况下：）
  - 使用return 则返回指定的值
  - 不使用return 默认使用函数体中执行的最后一行代码的结果作为返回值！
  - 如果函数不要求返回值，使用return也是白瞎

**案例演示**

```scala
def calculate(operand1: Int, operand2: Int, operator: Char) = {
    if (operator == '+') {
      operand1 + operand2
    } else if (operator == '-') {
      operand1 - operand2
    } else if (operator == '*') {
      operand1 * operand2
    } else if (operator == '/') {
      if (operand2 == 0) {
        null
      } else {
        operand1 / operand2
      }
    } else {
      null
    }
  }
```

 

### 5.3、函数的使用注意

- 单函数没有形参的时候，参数列表的括号可直接省略

- 函数的参数、返回值可以是值类型，也可以是引用类型！(即当传入一个对象时，用的是对象的引用，直接操作源对象！)

- Scala的语法中，任何的结构都可以嵌套其他所有结构：类中可以再定义类，函数中可以再定义函数！同名函数在不同的位置，编译后修饰符不同罢了：

   ```scala
  object FuncNotice02 {
    def main(args: Array[String]): Unit = {
  
      def func(msg: String): Unit = { // private static final func$1
        println(msg)
        def func (msg:String): Unit = { // private static final func$2
          println(msg)
        }
      }
  
    }
  
    def func(msg: String): Unit = {
      println(msg)
    }
  }
  ```

  编译后的：

  <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200815203845.png" alt="image-20200815203845794" style="zoom:67%;" />

  虽然三个位置的函数处于同等地位，都是此类的成员函数。
  但是写在main方法中的函数变为了两个私有的静态不可变的函数，函数名分别加上了`$1、$2`
  写在main方法外面的函数，成功成为了类的成员方法。

  

- 形参设置默认值！

  函数的参数是可以设置默认值的，调用时不传参数的时候，使用默认值，但是没有设置默认值的参数仍需要传递参数

  ```scala
  object FuncNotice03 {
    def main(args: Array[String]): Unit = {
      sayHi() // Bob: Hello
      sayHi("Sakura") // Sakura: Hello
      // say() 报错，需要指定msg
    }
  
    def sayHi(name:String = "Bob"): Unit = {
      println(s"$name: Hello")
    }
    
    def say(name:String = "Mike", msg:String): Unit ={
      println(s"$name : $msg")
    }
  }
  ```

  对于上面的案例，如何为设置参数，请往下看

  

- 默认值覆盖顺序：

  当参数中有很多默认值的时候，调用时传递的参数 **从左到右**依次覆盖默认值

  ```scala
  object FuncNotice04 {
    def main(args: Array[String]): Unit = {
      connectMysql() // localhost:3306 root 123456
  
      connectMysql("192.168.1.1",6666) // 192.168.1.1:6666 root 123456
  
      //如果我只想改用户名和密码呢？
      //connectMysql("sakura","170312") ?因为是从左向右覆盖，，所以行不通 除非你把函数的后两个参数写到前面
    }
  
    def connectMysql(host:String = "localhost", port:Int = 3306,
                     username:String = "root", password:String = "123456"): Unit ={
      println(s"$host:$port")
      println(s"username: $username")
      println(s"password: $password")
    }
  }
  ```

  如果遇到代码中的问题，多个默认参数我只想修改其中几个。
  或者函数既有带默认值的参数，又有没有默认值的参数，怎么为不带默认值的参数设值？==带名参数就是救世主！==

  

- 带名参数（对应上面两个案例的问题）

  ```scala
  say(msg = "My name is mike!")
  
  connectMysql(username = "sakura",password = "170312")
  // 在调用的时候，用参数名指定值
  ```

   

- ==函数的形参都是 val定义的，是不容修改的！！==

   

- 递归在执行前无法自动推断返回值，所以==递归函数不能使用自动推断返回值类型，必须指定返回值类型！！！==

- Scala函数支持可变参数（==可变形参放在最后！！==）

  ```scala
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
  ```

  可变形参，使用时是一个Sequence（序列）！！可以使用for来遍历！

   

- ==过程(Procedure)==：没有返回值，或者返回值为Unit的函数称之为过程！！

----



### 5.4、惰性函数

==推迟计算，等到真正使用此函数的返回值的时候才临时开始执行函数。==联想单例模式中的懒汉式，在大数据场景中我们可以将一些不必要的计算放到用户需要的时候进行实时计算，在不必要的时候等待，以减少资源的浪费！

使用`lazy`关键字

```scala
object LazyFunc {
  def main(args: Array[String]): Unit = {
    
    lazy val res = sum(20,30)
    println("--------")
    println(res)

    /**
     * --------
     * sum执行了。。。。
     * 50
     */
  }

  def sum(num1:Int,num2:Int) = {
    println("sum执行了。。。。")
    num1 + num2
  }
}
```

可以看到使用lazy定义了一个val变量res=sum(20,30); 但是并没有立即去执行sum函数，因为res变量被标记了懒加载（推迟赋值）。于是就往后执行，当输出的时候要用到res变量了，马上拎出来零时执行函数并赋值。==典型的不见棺材不掉泪==

==注意：由于懒加载是将赋值推迟，那么定义变量只能是`val`类型，不允许中途变化！！==



### 5.5、异常

Java中的编译时异常和运行时异常，被Scala统一为运行时异常！！











# Scala练习

## Practice01

1. 求3开方后，再平方的值与3相差多少？

   ```scala
   3 - math.pow(math.sqrt(3),2)
   // result:
   Double = 4.440892098500626E-16
   ```

    

2. 用字符串乘以一个数字，效果如何？

   可以查看官方文档的`StringOps`的`*`方法描述：
   ![image-20200813202555269](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200813202555.png)

   返回一个原字符拼接n次后的字符串：

   ```scala
   scala> "hello"*3
   val res1: String = hellohellohello
   ```

    

3. `10 max 2`的含义，max方法在哪个类中定义？

   ```scala
   scala> 10 max 2
   val res2: Int = 10
   ```

   很多类中都有max方法。

    

4. 用BigInt计算2的1024次方

   ```scala
   scala> var num:BigInt = 2;
   	 > num.pow(1024);
   var num: BigInt = 2
   val res5: scala.math.BigInt = 179769313486231590772930519078902473361797697894230657273430081157732675805500963132708477322407536021120113879871393357658789768814416622492847430639474124377767893424865485276302219601246094119453082952085005768838150682342462881473913110540827237163350510684586298239947245938479716304835356329624224137216
   ```

    

5. 查找字符串的首位字符

   ```scala
   scala> var str:String = "hello";
   	 > println(str.charAt(0));
        > println(str.charAt(str.length-1))
   
   h
   o
   var str: String = hello
   ```

   `take(n)`,取出字符串的前n个字符！

   `takeRight(n)`,从字符串右边开始，取出前n个字符!
   
   ```scala
   scala> "hello".take(1);
   val res18: String = h
   
   scala> "hello".takeRight(1);
   val res19: String = o
   ```
   
   