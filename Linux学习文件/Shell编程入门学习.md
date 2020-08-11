# ：shell编程(入门版)

> 为什么学习Shell？

- 看懂运维所写的Shell脚本
- 使用简单的Shell查询来管理集群、提高开发效率

## 概述

> Shell是一个**命令解释器**，位于==Linux内核层之上，外层应用程序之下。==，通过shell接受处理程序/用户的命令，然后调用操作系统内核。
>
> 一个强大的对手：Python。

## 入门二、Hello World

1. 格式！

   脚本以`#!/bin/bash`开头(指定解析器)

2. 创建脚本文件helloworld.sh

   ```shell
   #!/bin/bash
   echo "hello world"
   ```

3. 运行脚本(三种方式)

   - sh helloworld.sh
   - bash helloworld.sh

   > 以上两者本质都没有执行脚本文件，而只是读取内容然后作出动作

   - ./helloworld

   > 这种方式需要脚本文件执行，所以通常会有权限问题，和文件是否可执行的问题。



## 入门二、多命令

创建一个目录，并在此目录下创建一个文件，并将消息输出到文件中。

```shell
#!/bin/bash
mkdir -p /home/sakura/doc
cd /home/sakura/doc
touch message.txt
echo "I am 5akura" >> message.txt
```



## Shell中的变量

### 系统变量

常用系统变量

- $HOME:家目录
- $PWD:当前所在目录
- $SHELL:目前使用的解释器
- $USER:当前用户的用户名



### 自定义变量

格式：变量=值

`a=1`，注意**千万不要在等号两边带空格**！

> 若变量值中包含空格：使用单引号/双引号包裹。

- 取值：`$变量`

- 撤销变量：`unset 变量`

声明只读(静态)变量：`readonly 变量=值`，不能被unset！



> ==shell中变量的默认类型都是字符串无法就行数值运算==
>
> ```shell
> C=1+1
> echo $C
> 1+1
> ```



将变量提升为全局环境变量，可以供其他Shell程序使用！

语法：`export 变量名`



### 特殊变量——$n

> n是数字！
>
> 其中`$0`是脚本名、`$1..$n`代表第1...第n个参数。
>
> 当n>9的时候使用`${10}`来表示第10个参数。

test-n.sh

```shell
#!/bin/bash
echo $0
echo $1
echo $2
echo $3
```

```powershell
[root@localhost shell-study]# sh test-n.sh paramA paramB paramC
test-n.sh
paramA
paramB
paramC
```



### 特殊变量——$#

> $#代表获取的输入参数个数。

在test-n.sh加上了`echo $#`

```powershell
[root@localhost shell-study]# sh test-n.sh paramA paramB
test-n.sh
paramA
paramB

2
```



### 特殊变量——$*、\$@

>$* ：
>
>$@：
>
>两者都是代表所有参数，但是仍然有细微的差别(后面再说)

```powershell
[root@localhost shell-study]# sh test-n.sh paramA paramB
test-n.sh
paramA
paramB

2
*: paramA paramB
@: paramA paramB
```



### 特殊变量——$?

> 用于查看上一条命令的执行情况，若为0，表示上一条命令执行成功，非0则表示执行过程存在问题

```shell
[root@localhost shell-study]# ls
helloworld.sh  multi-instruct.sh  test-n.sh
[root@localhost shell-study]# echo $?
0
[root@localhost shell-study]# 1/0
-bash: 1/0: 没有那个文件或目录
[root@localhost shell-study]# echo $?
127
```



## 运算符

基本语法：

`$((运算式))`

或者`$[运算式]`

或者`expr 运算式` 运算符：`+`加	`-`减	`\*`乘	`/`除	`%`求余

> 运算符和数据直接一定要有空格！！

```shell
[root@localhost shell-study]# expr 3+2
3+2
[root@localhost shell-study]# expr 3+ 2
expr: 语法错误
[root@localhost shell-study]# expr 3 + 2
5
```



> 通过    ==`== 来表示层级

```shell
[root@localhost shell-study]# expr 2 + 3 \* 4
14
[root@localhost shell-study]# expr `expr 2 + 3 \* 4
> `
14
[root@localhost shell-study]# expr `expr 2 + 3` \* 4
20
```



> 便捷易读的写法就是使用$[运算式]

```shell
[root@localhost shell-study]# s=$[(2+3)*4]
[root@localhost shell-study]# echo $s
20
```



## 条件判断

基本语法

`[ 条件表达式 ]`，注意表达式**两边一定有空格**！



常用判断条件：

1. 整数比较：`[ num1 -gt num2 ]`

   - = (也可用于字符串)

   - -lt (less than)
   - -le (less equal)
   - -gt (greater than)
   - -ge
   - -eq 等于
   - -ne 不等于

2. 文件权限判断`[ -x filename ]`

   - -r (是否有读的权限)
   - -w ..
   - -x ..

3. 文件类型判断：`[ -f filename ]`

   - -f (常规文件)
   - -e (文件是否存在)
   - -d (目录)

> 以上条件表达式的结果执行完成之后，使用`$?`查看结果！
>
> 0:为真，非0(一般是1)为假

```shell
[root@localhost shell-study]# [ 23 -gt 32 ]
[root@localhost shell-study]# echo $?
1

[root@localhost shell-study]# [ -r multi-instruct.sh ]
[root@localhost shell-study]# echo $?
0

[root@localhost shell-study]# [ -f multi-instruct.sh ]
[root@localhost shell-study]# echo $?
0
```



**多条件组合判断**

`&&`：前一个条件执行成功，则判断下一条

`||`：前一个条件判断失败，则判断下一条

```shell
[root@localhost shell-study]# [ 23 -lt 45 ]&&[ -f multi-instruct.sh ]&&echo hello
hello

[root@localhost shell-study]# [ 49 -lt 45 ]&&[ -f multi-instruct.sh ]&&echo hello
[root@localhost shell-study]# echo $?
1

[root@localhost shell-study]# [ 23 -lt 45 ]||[ -f multi-instruct.sh ]||echo hello
[root@localhost shell-study]# echo $?
0
```



## 流程控制

### if判断

基本语法：

```shell
if [ condition ];then
	
	#code
	#...
	
fi

# 或者

if [ condition ]
	then
	
	#code
	
fi
```

> 两个地方一定要有空格：
>
> - if后要带空格
> - 条件表达式两侧要带空格！

```shell
#!/bin/bash
if [ $1 -eq 1 ]
then
    echo "hello this is if"
elif [ $1 -eq 2 ]
then
    echo "this is else if"
else
    echo "this is else"
fi
```

> if和elif都需要加条件判断和then
>
> else则不用

```shell
[root@localhost shell-study]# sh test-if.sh 1
hello this is if
[root@localhost shell-study]# sh test-if.sh 2
this is else if
[root@localhost shell-study]# sh test-if.sh 4
this is else
```



### case语句

基本语法：

```shell
case $变量名 in
value1)
	#code
;;
value2)
	#code
;;
...

*)
	#code
;;

esac
```

> 注意点：
>
> 每个case用`;;`结束

```shell
#!/bin/bash

case $1 in
1)
    echo "this is case1"
;;
2)
    echo "this is case2"
;;
*)
    echo "this is case*"
;;
esac
```



```powershell
[root@localhost shell-study]# sh case.sh
this is case*
[root@localhost shell-study]# sh case.sh 2
this is case2
[root@localhost shell-study]# sh case.sh 1
this is case1
```



### for循环

基本语法(1)：

```shell
for((初始值;循环控制条件;变量变化))
do
	#code
done
```

> 注意使用的是双括号！

```shell
#!/bin/bash

s=0
for((i=1;i<=100;i++))
do
    s=$[s+i]
done
echo $s
```

> 注意在进行数值运算的时候要带`$[]`
>
> 必要时取变量时候也加上`$`



基本语法(2)

```shell
for 变量 in value1 value2 value3
do
	#code
done
```

**value1~valuen：可以使用我们之前的$*、\$@代替，现在就能体现两者的区别了**

```shell
#!/bin/bash

for i in $*
do
    echo "i am "$i
done

echo ==========================

for i in $@
do
    echo "i am "$i
done
```

两种方式都是可以正常输出的：

```powershell
[root@localhost shell-study]# sh for2.sh 1 2 3 4
i am 1
i am 2
i am 3
i am 4
==========================
i am 1
i am 2
i am 3
i am 4
```



当我们使用`“$*”`和`“$@”`时候，==前者将所有的参数视为一个整体，而后者没什么变化==：

```shell
#!/bin/bash

for i in "$*"
do
    echo "i am "$i
done

echo ==========================

for i in "$@"
do
    echo "i am "$i
done
```

结果：

```shell
[root@localhost shell-study]# sh for2.sh 1 2 3 4
i am 1 2 3 4
==========================
i am 1
i am 2
i am 3
i am 4
```



### while循环

基本语法：

```shell
while [ 条件判断 ]
do
    #code
done
```

> 注意点：while后面有空格！！

```shell
#!/bin/bash

i=1
s=0
while [ $i -le 100 ]
do
    s=$[s+i]
    i=$[i+1]
done
echo $s
```



## 读取控制台输入(read)

基本语法：

`read (option) (参数)`

选项：

- -p :指定读取时的指示符
- -t :指定读取时等待的时间(秒)

参数：

​	用于输入值赋值的变量



例如：

```shell
#!/bin/bash

read -t 5 -p "Enter your name in 5 seconds" NAME
echo $NAME
```

程序执行后

- 先打印提示信息：Enter your name in 5 seconds
- 然后五秒等待控制台输入，若没有输入程序结束
- 读入到输入后 将输入值赋值给`NAME`变量
- 然后输出，程序结束

```powershell
[root@localhost shell-study]# sh read.sh
Enter your name in 10 seconds: sakura
sakura
[root@localhost shell-study]# sh read.sh
Enter your name in 10 seconds:
[root@localhost shell-study]#
```



## 函数

### 系统函数

1. basename

   > 用于获取全路径字符串中文件名，也可以指定后缀不显示。

   示例：

   ```powershell
   [root@localhost shell-study]# basename /home/sakura/shell-study/multi-instruct.sh .sh
   multi-instruct
   [root@localhost shell-study]# basename /home/sakura/shell-study/multi-instruct.sh
   multi-instruct.sh
   [root@localhost shell-study]# basename ../doc/message.txt
   message.txt
   [root@localhost shell-study]# basename ../doc/message.txt .txt
   message
   ```

2. dirname

   > 与basename的功能是互补的，获取路径中文件所在的目录位置

   示例

   ```powershell
   [root@localhost shell-study]# dirname /home/sakura/shell-study/multi-instruct.sh
   /home/sakura/shell-study
   ```

   

### 自定义函数

基本语法

```shell
function funcName()
{
	#code
	[return int;]
}
funcName
```

> 注意点：
>
> 1. 由于shell解释性语言的特性，逐行解释运行，所以==调用函数的之前必须有函数的声明定义==
> 2. 返回值：只能通过`$?`来获得，可以用return显式声明n返回值(0~255)，不加则默认使用最后一条命令的结果作为返回值。

```shell
#!/bin/bash

function sum()
{
    s=0
    s=$[$1+$2]
    echo $s
}

read -p "input number1:" num1
read -p "input number2:" num2
sum $num1 $num2

```

结果：

```powershell
[root@localhost shell-study]# bash func.sh
input number1:2
input number2:3
5
```





## shell工具

### cut

> 从文件中剪切数据，从文件中剪切内容并合并输出

基本语法：

`cut [选项] filename`

选项：

- -f :提取文件的第几**列**==(从1开始计数！！)==
- -d :按照分隔符分割每一行的数据==(默认是制表符)==

初始数据：

```txt
zhang san
li si
wang wu
song liu
zhao qi
```

```shell
[root@localhost shell-study]# cut -d " " -f 1 ../doc/cut.txt
zhang
li
wang
song
zhao

[root@localhost shell-study]# cut -d " " -f 2 ../doc/cut.txt
san
si
wu
liu
qi

[root@localhost shell-study]# cut -d " " -f 1,2 ../doc/cut.txt
zhang san
li si
wang wu
song liu
zhao qi
```

配合管道符使用：

```shell
[root@localhost shell-study]# cat ../doc/cut.txt | grep "song" | cut -d " " -f 1
song
```



练习：

查看部分环境变量：

```shell
[root@localhost shell-study]# echo $PATH | cut -d : -f 3-
```

获取本机的ip：

```shell
[root@localhost shell-study]# ifconfig ens33 | grep "inet "| tr -s [:space:]|cut -d " " -f 3
192.168.52.134
```

> 中间使用了tr去除前后空格

---



### sed

> 一种流编辑器。每次处理一行内容！
>
> 处理时，将处理的行存储在临时缓冲区中(又称：**模式空间**)，然后使用sed处理缓冲区中的内容，处理完成将缓冲区的内容送到屏幕，然后处理下一行。==不改变文件内容==，可以进行输出重定向然后存储获取新文件。

基本语法：

`sed [选项参数] command filename`

选项：

- -e 直接在指令列模式上进行sed的动作编辑

命令command：

- “a”：新增，新增字符串在下一行出现
- “d”：删除
- “s”：查找并替换



原始数据：

```txt
zhang san
li si
wang wu
song liu

zhao qi
chen ba
```

练习使用：

**在第二行下插入数据：sakura**

```shell
sed "2a sakura" ../doc/sed.txt 
```

结果：

```powershell
[root@localhost shell-study]# sed "2a sakura" ../doc/sed.txt
zhang san
li si
sakura
wang wu
song liu

zhao qi
chen ba
```

> “2a”:就表示在第二行下 新增数据
>
> “a”：表示在每一行下面都新增数据！(因为是逐行处理！)



**删除带有字母 i 的行**

```shell
sed "/i/ d" ../doc/sed.txt
```

结果：

```powershell
[root@localhost shell-study]# sed "/i/ d" ../doc/sed.txt
zhang san
wang wu

chen ba
```



**将所有的空格替换为-**

```shell
sed "s/ /-/" ../doc/sed.txt
```

**将所有的i替换为k**

```shell
sed "s/i/k/" ../doc/sed.txt
```

结果：

```powershell
[root@localhost shell-study]# sed "s/ /-/" ../doc/sed.txt
zhang-san
li-si
wang-wu
song-liu

zhao-qi
chen-ba
```

> 注意：“s”命令中最后还可以加上g，表示全局替换(单行)。
>
> sed “s/i/k/g”：就可以将每一行的所有i替换为k，否则只替换第一个。



**多sed命令组合**

删除第二行，并替换空格为-

> 由于是多个sed命令需要使用 -e 来区别分开

```shell
[root@localhost shell-study]# sed -e "2d" -e "s/ /-/g" ../doc/sed.txt
zhang-san
wang-wu
song-liu

zhao-qi
chen-ba
```

----

### awk

> 先从文件中逐行读取数据，然后默认以空格作为分隔符对数据进行切片，然后对切开部分进行分析处理

基本语法：

`awk [选项] ’pattern1{action1} pattern2{action2}...‘ filename`

pattern是匹配模式(正则表达式)，action是对相应模式匹配出的内容进行的操作

选项：

- -F 指定分隔符
- -v 赋值一个用户定义的变量



练习使用：

拷贝一份/etc/passwd文件作为初始数据

![image-20200529153641465](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200529153641.png)

输出以root开头行的 第一、六列数据

```shell
[root@localhost shell-study]# awk -F : '/^root/{print $1","$6}' ../doc/passwd
/root
```

> 注意：正则表达式是使用`/regex/`包裹的
>
> 正则表达式中：
>
> - `^`表示开头
>
> - `$`表示结尾
> - `^$`表示空行！
>
> action中 print是将内容输出到屏幕，使用`$n`来表示第n列数据(从1开始)！！
>
> `$0`：代表此条完整记录



最开始输出 “user，homedir”，输出最后“sakura，/home/sakura”，中间的数据只输出第一列和第六列。

```shell
[root@localhost shell-study]# awk -F : 'BEGIN{print "user,homedir"} {print $1","$6} END{print "sakura,/home/sakura"}' ../doc/passwd
```

> BEGIN：表示开始输出，END：表示输出结束

![image-20200529155246317](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200529155246.png)

![image-20200529155304960](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200529155305.png)



将每行的第三列数据加上一个指定值：

```shell
[root@localhost shell-study]# awk -F : -v i=2 '{print $3+i}' ../doc/passwd
2
3
4
5
6
7
```

> 里面使用了-v选项定义了一个用户变量i=2，可以在action里面直接使用！

==内置变量==

- FILENAME：当前文件名
- NR：已读的记录数
- NF：当前记录的域个数(即这一行切割后共多少列)

```shell

[root@localhost shell-study]# awk -F : -v i=2 '{print FILENAME"==>"NR"==>"$0}' ../doc/passwd
../doc/passwd==>1==>root:x:0:0:root:/root:/bin/bash
../doc/passwd==>2==>bin:x:1:1:bin:/bin:/sbin/nologin
../doc/passwd==>3==>daemon:x:2:2:daemon:/sbin:/sbin/nologin
```



获取本机的ip

```shell
ifconfig ens33|grep "inet "|awk -F " " '{print $2}'
```

对比cut来说要容易一些





### sort

> 将文件内容排序然后输出

基本语法：

`sort [选项] 参数`

选项：

- -n：按照数值大小升序输出
- -r：按数值大小降序输出

当数据内容有很多是，我们只需要其中一列的数据：

- -t：设置排序时候的数据列的分隔符
- -k：设置选择第几列作为排序的数据



练习使用

还是使用passwd作为原始数据



按uid(即第三列)递增排序

```shell
sort -t : -k 3 -n ../doc/passwd
```

先将每行记录按`:`切割(-t :)然后选择第三列排序(-k 3)，排序规则使用递增排序(-n)

![image-20200529162638121](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200529162638.png)





## 常见面试题

统计空行所在行号

```shell
awk ’/^$/{print NR}‘ filename
```



每行数据域间以‘:’分隔，计算第2(n)行的和，然后打印输出

```shell
cat filename | awk -F : '{sum+=$2} END{print sum}'
```

