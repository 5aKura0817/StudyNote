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
>   public static void main(String[] paramArrayOfString) {
>     HelloScala$.MODULE$.main(paramArrayOfString);
>   }
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
>   public static final HelloScala$ MODULE$ = new HelloScala$();
>   
>   public void main(String[] args) {
>     Predef$.MODULE$.println("hello scala");
>   }
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



# Scala语言学习



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



还有一点：**Scala中完全保留了Java的隐式类型转换机制。即更小范围的类型可以转化为更大范围的类型，反之则不行。**（例如：Float转Double可以，反之不行）



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

   

- Nothing是一切类的子类，且是一个不可实例化的抽象类，并且不可以被继承！

  