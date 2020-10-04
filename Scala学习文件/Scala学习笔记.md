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

> 案例对比

Scala顺势沿用了Java所有的异常类型。

我们先来回顾一下Java中try…catch捕获异常的一些规则

```java
public class ExceptionTest {
    public static void main(String[] args) {

        try {
            // ...
        } catch (ArithmeticException | NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // ...
        }
        
    }
}
```

try中放可疑代码块，finally里面存放后续执行代码（例如资源释放等）无论如何都会运行。而在Catch中：==每个Catch可以对没有子父类关系的多个异常进行同样的处理（例如上述代码中的第一个Catch）。**Catch的异常范围必须按从小到大范围来写，否则直接报错！**==



现在我们来看看Scala捕获异常的代码：

```scala
object ExceptionDemo01 {
  def main(args: Array[String]): Unit = {
    
    try {
      var i:Int = 10 / 0
    } catch {
      case exception: Exception => {
        println("捕获到一个异常")
      };
      case exception: ArithmeticException|NumberFormatException => println("捕获到一个算数异常")
    } finally {
      // ...
    }
      
    
  	try {
      exceptionFunc()
    } catch {
      case ex: Exception => println("捕获到异常:" + ex.getMessage)
    }
    println("continue")
  }


  /**
   * 模拟异常
   */
  def exceptionFunc (): Unit = {
    throw new Exception("异常出现～～～")
  }
}
```

try和finally代码块都没有什么区别，对比一下Catch代码块就能看到不同。Scala可以直接使用一个Catch然后分多个case来代替Java中的多个Catch。==并且case之间的异常范围顺序没有硬性要求，但是最终还是会**模式匹配**到最适合的case（例如上述代码还是走第二个case！），但是为了编码的规范性和代码的可读性，**还是建议按照异常的范围来顺序设置case!!**==



> 异常处理的注意点

1. 可疑代码块放在try代码块中！使用catch捕获可能出现的异常
2. Catch中异常的case，最好按照编码规范：范围更大的异常放到最后
3. finally代码块一般用于资源的释放，此外的代码是无论如何都会执行的！！
4. 遇到不处理的异常可以继续使用throw继续向上抛出！



> @throw注解

刚才在Scala的案例代码中，我们使用throw关键字手动抛出异常，我们还可以使用`@throw`注解告诉调用者这个方法会抛出的异常，需要调用者catch处理或者继续抛出！

```scala
@throws(classOf[ArithmeticException])
@throws(classOf[NumberFormatException])
def exceptionFunc02(): Unit = {
    var i: Int = "abc".toInt
    var a: Int = 10 / 0
}
```



## Chap06. Scala面向对象

### 6.1、面向对象基础要点

前面我们创建的都是object，但是在面向对象编程过程中，我们使用的类都是使用`class`定义的。相比于Java而言Scala是纯面向对象的！但是两者的编码规范上还是稍有不同！

```scala
object TestOOP {
  def main(args: Array[String]): Unit = {
    val tom:Cat = new Cat
    println(tom.name) // null
    println(tom.age)  // 0

    tom.name = "tom"
    tom.age = 3
    tom.color = "orange"
    
    println("----赋值后：----")
    println(tom.name) // tom
    println(tom.age)  // 3
  }
}

class Cat {
  var name:String = _
  var age:Int = _
  var color:String = _

  def speak (): Unit = {
    println("miao~")
  }
}
```

在class对象的定义的时候，==成员属性必须赋默认值！！==这里使用`_`表示赋对应值类型的空值。

**问题：这种写法成员变量访问权限修饰是private还是public呢？**

直接来看反编译文件：

通过编译的文件看出来，这个class对象单独生成了一个.class文件，而object对象编译后却有两个.class文件！

![image-20200910153738173](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200910153738173.png)

来看看这个class对象化为Java代码的样子吧

```java
public class Cat {
  private String name;
  
  private int age;
  
  private String color;
  
  public String name() {
    return this.name;
  }
  
  public void name_$eq(String x$1) {
    this.name = x$1;
  }
  
  public int age() {
    return this.age;
  }
  
  public void age_$eq(int x$1) {
    this.age = x$1;
  }
  
  public String color() {
    return this.color;
  }
  
  public void color_$eq(String x$1) {
    this.color = x$1;
  }
  
  public void speak() {
    Predef$.MODULE$.println("miao~");
  }
}
```

1. 所有的成员变量都是`private`修饰的！
2. 每个成员变量貌似都对应两个public方法 xxx()和 xxx_$eq(…)（类似于JavaBean里面的getter和setter）

这种写法是规范的JavaBean定义方式



我们再来看看main方法中的对象赋值和取值是怎么实现的吧：

![image-20200910154454330](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200910154454330.png)

和前面设想的一致，==成员变量都会自动生成“getter and setter”方法==

**class不添加修饰默认是public!**



### 6.2、对象、属性定义的注意点

成员属性的定义标准语法：

`[访问修饰符] var 属性名[:属性值类型] = 默认值`

修饰符省略，默认为private！！

> 属性赋默认值时需要注意的问题

1. 成员属性定义时必须设置默认值！

2. 属性值类型可以省略，根据赋的默认值自动推断，但是==不设置类型，赋值null的话，会将成员属性自动推导为Null类型对象，后续为此成员属性赋值的时候会出现麻烦！！==

   ```scala
   class Cat {
       var name = null
   }
   // name成员属性自动推断为Null类型，后续无法赋值！
   ```

3. 如果实在不想设置默认值，可以使用`_`（系统默认值）

   |          类型          | 系统默认值（ _ ） |
   | :--------------------: | :---------------: |
   | Short、Byte、Int、Long |       **0**       |
   |     Double、Float      |      **0.0**      |
   |  String、其他引用类型  |     **null**      |
   |        Boolean         |     **false**     |

   **使用这种方式赋默认值的话，就不能省略成员类型！！**





> 对象创建注意点

1. var、val合理使用

2. 当引用类型对象创建的时候，对象本身和创建的对象存在父子类关系或者多态关系，对象的类型必须带上。（**当一个子类对象要赋值给一个父类引用时，想要保证引用的类型不变需要指定类型！**）

3. 对象内存布局（与Java类似）

   ==栈中只存放对象的引用，即对象在堆中的内存地址。访问对象时先获取对象所在的堆内存地址，然后到堆中找到对应内存取出对象数值。==

   ![image-20200910165056609](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200910165056609.png)





### 6.3、构造器

在Scala中构建类对象一样需要使用构造器。

在Java中构造器的定义使用类名，并且不写返回值类型，支持重载！

但是在Scala中，构造器分为两种：

- 主构造器
- 辅助构造器

而且定义方式也与Java有些区别！



> 主构造器定义

直接在类定义时候，类名后用括号包裹主构造器的参数：

```scala
object TestConstructor {
  def main(args: Array[String]): Unit = {
    val p1:Person = new Person("sakura",20)
    println(p1)
  }
}

class Person (inName:String, inAge:Int) {
  var name:String = inName
  var age:Int = inAge

  override def toString: String = {
    "name: " + name + "; age: " + age
  }
}
```

这里有点和Java不同，在Java中类中的代码都必须是在方法中或者静态代码块中的，，但是**Scala中的class里面，不在方法中的代码统统归为主构造器的执行代码！**例如：

```scala
class Person (inName:String, inAge:Int) {
  var name:String = inName
  var age:Int = inAge
  
  println("调用了主构造器")
  
  override def toString: String = {
    "name: " + name + "; age: " + age
  }
  
  println("构造完成！")
}
```

这两句输出代码都是直接写在类中的，也就是说被归到了主构造器的执行代码中，每次调用主构造器都会执行！我们来看看反编译的Java代码=>

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200921152756401.png" alt="image-20200921152756401" style="zoom: 80%;" />

==注意：当主构造器为空参的时候，括号可以省略，调用的时候也可以省略！===





> 辅助构造器

Scala允许一个类有多个辅助构造器，所有的构造器都是重载的关系。Java中重载构造器保证参数列表不同即可。但是Scala中辅助构造器有些需要注意的地方：

1. 命名使用`this` 
2. 必须在第一行直接或者间接调用到主构造器！
3. 调用主构造器需要显式调用！（主要是为了和父类建立联系，而Java的构造器基本都会默认隐式调用super!）



Java的类构造器中一般都会隐式调用父类的空参的构造，即`super()`，或者你也可以添加参数来指定调用父类的具体某个构造器，例如`super(?,?,?)` 这其实就是和父类建立起联系的方式！ 但是当你在构造器的第一行调用了同类的其他重载构造即`this(?,?,..)`，最终去执行这个重载的构造的时候第一行执行的还是父类构造的调用！

```java
public class Person {
    private int age;
    private String name;

    public Person (){

    }

    public Person (String  inName){
        // super(); 默认调用，可省略
        this.name = inName;
        this.age = 20;
    }

    public Person (String inName, int inAge) {
        // 这里调用了同类的重载构造，最终还是会调用super();
        this(inName);
        this.age = inAge;
    }

}
```

可是当调用了重载的构造，又紧接着调用super()，这样是不允许的：

```java
public Person (String inName, int inAge) {
        // 这里调用了同类的重载构造，最终还是会调用super();
        this(inName);
    	super(); // 这里直接报错！！
        this.age = inAge;
    }
```

**因为对父类的构造调用必须在第一行执行！！**所以你这样写，无论是写在前面还是后面，总有一个`super()`不是第一行执行！





我们再说回Scala，在Scala中是没有这种默认隐式调用父类构造的，只有主构造器才能和父类联系！所以所有的辅助构造器就需要最终调到主构造器，否则无法和父类建立联系。

所以在Java中这种写法，在Scala中行不通=》

```scala
class Person (inName:String, inAge:Int) {
  var name:String = inName
  var age:Int = inAge

  println("调用了主构造器")

  override def toString: String = {
    "name: " + name + "; age: " + age
  }

  println("构造完成！")
  
  // 错误写法！！但是Java可以！对比上面Java案例代码的第二个构造器
  def this(inName:String) {
    name = inName
    age = 18
  }

}
```

究其原因就是没有调用主构造器和父类产生联系，所以正确写法应该是=》

```scala
class Person (inName:String, inAge:Int) {
  var name:String = inName
  var age:Int = inAge

  println("调用了主构造器")

  override def toString: String = {
    "name: " + name + "; age: " + age
  }

  println("构造完成！")
  
  // 正确写法，先调用主构造！
  def this(inName:String) {
    this(inName, 18)
  }
}
```

或者这样：

```scala
class Person (inName:String, inAge:Int) {
  var name:String = inName
  var age:Int = inAge

  println("调用了主构造器")

  override def toString: String = {
    "name: " + name + "; age: " + age
  }

  println("构造完成！")

  def this() {
    this("sakura",20)
    println("空参构造")
  }

  def this(inName:String) {
    // 调用了空参的辅助构造，间接调用主构造
    this
    name = inName
  }
}
```

这种间接调用到主构造也是可以的！！**反正目的只有一个：调到主构造，并且在第一行执行！！**



> 其他注意点

**构造器私有化**

主构造器私有化：在参数列表之前加上`private`

```scala
class Person private(inName:String, inAge:Int){
    // ...
}
```

辅助构造器私有化：def之前加上private

```scala
private def this() {
    this("sakura",20)
    println("空参构造")
}
```

**辅助构造器不要出现与主构造器同参定义！！**





### 6.4、属性高级部分

**在Scala的主构造方法中，使用var（val）定义形参，可以将形参转化为类的成员变量**（var定义的为可读可写 val定义的为只读）可以通过反编译验证。

```scala
// 将形参inName变为了Student的一个可读可写成员变量,inAge是只读的！
class Student (var inName:String,val inAge:Int) {
  var name:String = inName
  var age:Int = inAge
}

object TestConstructor {
  def main(args: Array[String]): Unit = {
    
    val s1:Student = new Student("sakura",20);
    println(s1.inName)
    s1.inName = "xiaohua"
//    s1.inAge = 19  错误用法，用val定义的inAge为只读的
    println(s1.inAge)
  }
}
```

反编译的代码：

![image-20200921180725676](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200921180725676.png)



> Getter和Setter

和Java的类成员变量一样，Scala的成员变量也可以写get和set方法，和默认生成的`xxx()`、`xxx_$eq(..)`互不影响。

Scala提供的是更便捷的方式——注解标注。`@BeanProperty`

```scala
class Student (var inName:String,val inAge:Int) {
  @BeanProperty 
  var name:String = inName
  @BeanProperty
  var age:Int = inAge
}
```

`@BeanProperty`注解直接使用在成员变量上。就会自动生成get、set方法，在后续创建的对象中可以直接调用！我们通过反编译来验证：

![image-20200925142009220](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200925142009220.png)



其实发现最终还是调用默认生成的那两个方法！





## Chap07. Scala包

### 7.1、Scala包基本介绍

Scala和Java具有相同的包机制,但是Scala中包机制要较为复杂，并且功能也更强大！！

> package的主要功能

说到底，包的作用大部分是为了区分同名的类，包名的存在就解决了项目中类同名的问题！就好像文件夹的出现，解决了文件系统的文件同名的问题。（同名文件在不同的文件夹中不会产生冲突！）



### 7.2、Scala包的特点及注意事项

> 特点一：包结构可以与文件结构不同

==在Java中项目包的结构是对应文件系统的结构的！==（例如：com.sakura.java包 对应到文件系统就是 …./com/sakura/java/文件夹）
而在Scala中就没有这么严格哦！！来看看例子吧

代码是这样的：

```scala
package com.sakura.chapter07.demo

/**
 * @author sakura
 * @date 2020/9/25 下午4:39
 */
object TestPackage {
  def main(args: Array[String]): Unit = {
    println("Hello World")
  }
}
```

注意package是 `com.sakura.chapter07.demo`但是项目结构中根本就不存在这个包！！

![image-20200925164259306](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200925164259306.png)



你敢在Java中这么写，编译器就直接报错给你看！但是Scala允许了，但是我们来看看文件系统的结构：

![image-20200925164454561](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20200925164454561.png)

文件系统中还是老老实实创建了！！那我们改动一下代码上的package为`com.sakura.chapter.demo02`看看会发生什么？！

项目包结构还是那个样，但是文件系统中demo文件夹被删除，随之创建了demo2文件夹。





> 特点二：包的写法有多种

在Java中，包名都是写在文件的第一行的！

```java
package xx.yy.zz
...
```

这种写法在Scala中也被保留了！但是扩展了另一种等价的写法：

```scala
package xx.yy
package zz
....
```

还有一种更特殊的写法，这种写法完全颠覆了传统Java包的书写规范！

==居然可以在一个类文件中，同时存在多个包，并且分别编写各个包的类！！==

```scala
package xx.yy {
    class Car {
        // ...
    }
    
    package zz {
        class Car {
            
        }
    }
    
   	// ...
}

package aa.bb {
    // ...
    class Car {
        
    }
}
```

包名后使用大括号包裹内容，划定包的内容范围。并且可以在同一个项目类中同时存在多个包（非嵌套式）！！但是通过编译之后，编译器会自动处理在文件系统中创建对应的包文件夹，每个类单独生成一个class文件！



### 7.3、包对象package object

刚才看完Scala包和Java包的区别，现在要学习一个Scala中特有的一个东西——**包对象（package object）**。这个包对象，和我们创建的包之间又有什么关系呢？！



> 为什么有包对象？有什么作用？

在Java中，所有的变量、方法都必须写在class中，而package里面只能有class的定义，package中是不能定义变量和方法的。当然scala中的package也是。于是Scala引出了包对象的概念。==目的就是可以在package中定义变量和方法，并且这些变量和方法，是当前包下随处可用的！！==



> 创建包对象

使用package object声明,  包对象的名字和与之作用的包名保持一致即可，例如

```scala
package sakura{

}

package object sakura {

}
```

当然在package下还是不能写变量和方法的。定义变量和方法都在package object下进行！



> 测试使用package object

```scala
package object sakura {
  val name = "sakura"
  var age = 20

  def sayHi () = {
    println("Hello World")
  }
}

package sakura{

  object TestSakura {
    def main(args: Array[String]): Unit = {
      // 直接使用package object中的方法和变量
      println("info-> {" + s"name : $name" + s", age : $age}")
      sayHi()

      println("-----------")

      val root:User = new User
      println(s"username: ${root.username}" + s", age: ${root.user_age}")
      print("User.sendMessage() => ")
      root.sendMessage
    }
  }

  // 包内随处可用
  class User {
    val username = name
    var user_age = age

    def sendMessage: Unit = {
      sayHi()
    }
  }

}
```

上面这个例子中，在package object中定义的变量和方法，在其作用的package中随处可用，emmm，有点像java中class下定义的变量和方法，在class中也是随处可用！



> 反编译，查看实现过程

查看反编译的Java代码，看看是如何实现的：

![image-20201003220858341](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20201003220858341.png)



首先是创建了名为`package$` 和 `package`的类

方法和变量定义都是在`package$`类中的，并且其给出了一个实例`MODULE$`便于其他位置调用类中的属性和方法。



![image-20201003221439980](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20201003221439980.png)

可以看出，其底层的实现只是利用了一个类似于定义工具类的方式，并不高深。。



> 注意事项

1.  **每个package至多有一个package object**
2.  package object的**命名必须与包名一致！**
3. package和package object**必须在同级包下定义！！（同一个父包）**





### 7.4、访问权限

先回顾一下Java的访问权限修饰符：

|  访问权限  |  修饰符   | 同类 | 同包 | 子类 | 不同包 |
| :--------: | :-------: | :--: | :--: | :--: | :----: |
|    公共    |  public   |  ✅   |  ✅   |  ✅   |   ✅    |
|  受保护的  | protected |  ✅   |  ✅   |  ✅   |  :x:   |
| 不写，默认 |    无     |  ✅   |  ✅   | :x:  |  :x:   |
|    似有    |  private  |  ✅   | :x:  | :x:  |  :x:   |



注意一下：这里的受保护的（protected）对子类是开放的！**就算子类在不同包下也是可以的！**



> 伴生对象和伴生类

在了解Scala的访问权限之前，有必要知道这两个东西。`companion object`和`companion class`。

例如：

```scala
object Test {
    
}

class Test {
    
}
```

这种情况，我们称`object Test`是`class Test`的伴生对象，编译后成为`Test$.class`文件。
`class Test`为`object Test`的伴生类，编译后为`Test.class`文件。



**这俩东西出现的意义是什么呢？**

在Scala设计过程中，抛弃了`static`关键字，但是静态成员、静态代码块又是常用的。于是规定将静态成员、方法、代码块都写在`伴生对象`中，实例成员写在`伴生类`中。



现在我们给出一段案例的Java代码：

```java
class Student {
    static {
        System.out.println("创建了一个学生");
    }
    
    public static int count = 56;
    
    private String name;
    private String id;
    
    public Student(String name,String id) {
        this.name = name;
        this.id = id;
    }
    
    public static void sayHi() {
        System.out.println("你好，我是一名学生！");
    }
    
    public void introduce() {
        System.out.println("My Name Is " + name + ", And My Id Is " + id);
    }
}
```

像这样一个简单的Java类，其中包含了静态变量、静态代码块和静态方法，这些都隶属于这个class，而不属于某一个实例。同时还有两个成员变量以及一个成员方法，这些是与实例对象紧密关系的，只能通过实例化的对象调用或者修改。



现在我们要改写成scala代码，由于没有static关键字，所以静态部分要放到伴生对象中…  开干开干！！

```scala
object Student {
  println("创建了一个学生")

  var count:Int = 56

  def sayHi: Unit = {
    println("你好，我是一名学生！")
  }
}

class Student(inName:String, inId:String) {
  var name:String = inName
  var id:String = inId

  def introduce: Unit = {
    println("My Name Is " + name + ", And My Id Is " + id)
  }
}
```

> 注意！！这样改写，存在一个小小的点，就是在编译后会生成两个.class文件。`Student$.class`和`Studnet.class`
>
> 而我们在伴生对象（object Studnet）中写的那句输出，变成了Student$.class的静态代码块！
>
>  ![image-20201004232454954](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/image-20201004232454954.png)
>
>  
>
> 但是我们使用new创建Student对象的时候，并不涉及到静态部分的创建，所以理所当然也就摸不到`Student$.class`，所以这句输出也就不会输出。但是在Java中，实例化类的时候第一件事就是执行静态代码块。所以你要是想看到这句输出，必须是在访问/修改静态部分的时候！例如:
>
> ```scala
> object Test {
>   def main(args: Array[String]): Unit = {
>     
>     val s1:Student = new Student("Sakura","18130311") // 不输出
>     
>     println(Student.count) // 输出，因为访问了静态变量count
>   }
> }
> ```



**伴生类和伴生对象内容访问：**

- 伴生类访问伴生对象中的内容，使用`类名.变量`  ` 类名.方法`。
- 伴生对象访问伴生类中的内容，必须通过实例化对象。



仔细体会一番，可以看出Scala中将类的静态部分和实例成员部分完完整整分离开来。但是使用起来还是和Java类似，当然也就牺牲了部分代码的易读性。我想可能这就是Scala作为纯面向对象语言做出的妥协和牺牲吧。

-----



现在我们继续来学习Scala中的访问权限控制。。

学习了这么久的Scala，是不是发现我们已经很少写public、private这种访问权限关键字了？！这是因为Scala中变量、方法都是有默认的访问权限的！！

- `变量`默认为`private`

  > 这里随说变量默认是private，但是默认生成了一套类似的getter/setter的方法，在外部依然可以通过这两个public方法访问/修改。
  >
  > 但是！！**如果你显式使用private修饰，这一套getter/setter就变成了private的，也就保证了外部也调不到！**

- `方法`默认为`public`

还有几个特别重要的注意点！

- **Scala中没有public关键字！！！**
- Scala中的`protected`相较于Java要更为严格！**只允许同类、子类访问**



在Scala中还有一个非常灵活的控制访问权限的方式，看代码！

```scala

```







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
   
   