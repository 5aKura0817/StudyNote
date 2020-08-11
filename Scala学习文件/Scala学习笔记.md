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

