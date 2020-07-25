# 一、入门安装配置

> 什么是Git?

分布式|==版本控制==|软件

发展历程：

- 文件管理
- 本地版本控制
- 集中式版本控制（SVN）
- 分布式版本控制



> 本地Git配置

配置全局的用户名和邮箱，命令分别为

`git config --global user.name "username"`

`git config --global user.email "email"`

查看全局的用户名和邮箱，命令分别为

`git config --global user.name`

`git config --global user.email `

----



# 二、使用Git做本地版本控制

> 初始化

1. 进入需要进行版本管理控制的文件夹

2. 右键Git Bash Here，使用命令`git init`行对文件夹进行初始化。

3. 执行初始化以后，会创建一个`.git`文件包含着当前文件本地版本控制的一些信息，此时被初始化的文件夹中所有文件纳入到Git的管理范围之中，但是此时还没有被Git接管，还要使用命令将文件加入Git的管理列表中。

4. 并且你会发现此时命令行的工作目录上多了一个`(master)`

   

> 文件状态

1. 执行`git status`查看当前文件夹下的所有文件状态

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724100052.png" alt="image-20200724100052561" style="zoom:67%;" />

2. Untracked files（红色表示的文件夹）表示还未被GIt跟踪管理（即没有纳入管理清单中）

3. 使用`git add file_name/dir_name`将文件加入到Git的管理列表（==暂存区，Stage==）中，让GIt进行管理。

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724100746.png" alt="image-20200724100746452" style="zoom:67%;" />

   为了简便：可以使用`git add .`表示将当前文件夹中所有内容加入管理。

4. 此时我们可以使用`git commit`管理列表中的文件提交

   使用`-m "commit_text"`加上提交注释信息

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724101906.png" alt="image-20200724101906448" style="zoom:67%;" />

   命令行会输出多少文件被修改，以及内容修改的情况（增/删）



> 撤销Add

如果某些文件是你不想进行版本控制的，而你又误将其add到了暂存区（Stage）中，可以使用

- `git rm –cached file_name`：删除分支上，或者暂存区的文件，下次commit时分支上文件会被移除但是本地依旧保留，==适用于需要进行临时的版本控制，后续不再进行版本控制的文件==
- `git restore --staged file_name`：将文件从暂存区移出

将add操作撤销，那么commit时，就不会为这些没有加入到暂存区中的文件进行版本创建，也不会跟踪文件的修改。



> 版本更新

假如现在两个文件都加入暂存区并`commit`，然后目前版本的v1.0，我们对两个文件进行修改，然后查看文件状态：

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724104254.png" alt="image-20200724104254411" style="zoom:67%;" />

此时会告诉你，有两个文件被修改了但是还没有到暂存区中：你现在就有两个选择

- 使用`git add`将修改加入暂存区中,等待commit
- 使用`git restore`撤销你对文件的修改，退回到上一次commit时，所有的此前修改直接清除。

add所有修改的文件到暂存区后，然后我们再次执行commit，进行版本更迭。



**当提交（commit）多次后，想要查看之前的提交记录时使用**`git log`来查看历史记录：

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724105244.png" alt="image-20200724105244634" style="zoom:67%;" />

==每个commit都有一个唯一的编号，方便我们后续进行处理。==



> 版本回滚

之前我们做过一次修改撤销使用`git restore`，这个只是撤销还没有进入暂存区的文件修改。
这里我们针对的问题是对**已经commit的版本，进行回滚。**

示例：假如我们先将内容更迭到了v3.0，但是我又想退回到上一个版本！

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724114510.png" alt="image-20200724114510692" style="zoom:67%;" />

这里就要用到一个命令`git reset`命令，以及你要撤销的commit的编号

`git reset`有三种执行方式

- `--hard`
- `--soft`
- `--mixed`(default)

来了解一下三种的方式的区别。推荐阅读博客：<https://www.jianshu.com/p/c2ec5f06cf1a>



> 简单小结，解释reset的三种方式

首先我们要熟悉三个区之间的协调工作是如何进行的，先来看一张图：

![img](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724132056.webp)

假设初始状态下，工作区、暂存区为空、版本仓库中最新版本(repository(head))和本地工作区的内容一致。

![img](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724133328.webp)

1. 首先我们在本地进行文件的修改，内容修改在工作区中进行的，此时**工作区发生了变化，暂存区为空，版本仓库中是修改前版本。**

   ![img](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724133345.webp)

   但是Git已经知道了那些文件(tracked file)发生了修改，并将这些文件置为modified(Unstaged File)，即未加入暂存区的已修改的文件。

2. 此时我们使用`git add .`将修改的文件加入到暂存区，此时暂存区和工作区同步了，仓库中仍然没有修改依旧留在修改前的最新版本。

   ![img](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724134134.webp)

3. 最后我们使用`git commit`将暂存区的内容推到本地版本仓库中，此时三者中内容又重新同步，完成一次完整的本地版本迭代：

   ![img](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724134434.webp)



> 实战三种reset

首先我们要建立一个认识，我们所有的提交记录相当一个链表串在一起的，有一个HEAD指针就是指向我们的当前所在的分支。

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724135013.png" alt="image-20200724135013859" style="zoom:67%;" />

- `reset--hard`重置stage区和工作目录

  会在重置 **HEAD** 和**branch**的同时，重置stage区和工作目录里的内容。当你在 **reset** 后面加了 **--hard** 参数时，你的**stage区和工作目录里的内容会被完全重置为和HEAD的新位置相同的内容**。换句话说，**就是你的没有commit的修改会被全部擦掉。**

  假如我们现在处于v3.0，并且我新增了一个文件（testA.txt）并加入到了Stage区，还新增了一个文件(testB.txt)但是没有加入Stage区，我们尝试一下回退到v2.0：

  `git reset HEAD^`：表示回退到上一个版本，执行命令之后发生以下几个变化：

  ```shell
  Vintege@Vintage_5akura MINGW64 /f/GitLocalRepository/LocalFile (master)
  $ git reset --hard HEAD^
  HEAD is now at 494e230 index v2.0 commit
  
  # 提示我们HEAD指针回退到了v2.0
  ```

  1. 本地文件所有v3.0的修改删除，回退到了v2.0的状态，并且**工作区的代码也被一并删除**
  2. Stage区的未commit的文件testA.txt被删除，工作区中未加入Stage区的testB.txt文件还在（未commit的Stage中的修改没了）

   ==hard：工作区、仓库一步回退到上一个版本的状态，Stage清空。==慎用:red_circle:

  ​	

- `reset --soft`保留工作目录，并把重置 HEAD 所带来的新的差异放进暂存区

  **重置HEAD带来的差异**：当你从v3.0回退到v2.0中时候，由于工作区中内容被保留（工作区中依然是v3.0的版本内容），那么重置后的HEAD在v2.0上，那么工作区中包含的v3.0的修改就被认为是重置HEAD带来的差异。

  ![image-20200724143612546](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724143612.png)

   看得出来是一种非常"温柔"的撤回，效果上只是撤销了一次commit的操作。

  ==soft：工作目录保留，回退版本后，修改依然存在，添加到Stage区，原Stage区的内容依旧存在==

   

- `reset--mixed`保留工作目录，并清空暂存区

  相当对是hard 和 soft 的一种折中处理办法。**reset** 如果不加参数，那么默认使用 **--mixed** 参数。它的行为是：**保留工作目录，并且清空暂存区**。也就是说，工作目录的修改、暂存区的内容以及由 **reset** 所导致的新的文件差异，都会被放进工作目录。简而言之，就是「把所有差异都混合（mixed）放在工作目录中」。![image-20200724144823913](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724144823.png)

   这里说的清空工作区没有hard那么暴力，所有的修改直接抹去。**相当于在soft的基础上，将Stage区的内容转移到了工作区中。（在soft的基础上撤销了一次git add ）**

  ==mixed：保留工作区，重置HEAD带来的差异和未提交的Stage中内容 进入工作区(清理Stage区，hard应该叫擦除Stage区)==

   

> 三者的适用场景

- hard，当commit的内容有问题，想要直接抛弃的commit。但是一定要对Stage区的内容检查避免误删
- soft，适合对commit进行内容追加的时候，将commit撤销并将追加的修改也加入到Stage区，连同撤销的commit整合为一个commit提交（比如一个功能草草做完就commit了，结果后来又想加一些东西，再次commit的话一个功能就会有多个commit的点，还不如撤销commit然后合并为一个commit点再提交）
- mixed
  1. 合并commit（与soft差别不大）
  2. 撤销Stage中内容：`git reset HEAD`，也可以实现将Stage中的内容移回工作区，解决add错误文件的情况
  3. commit中有错误，但是不想修改再次commit,因为会存在一个错误的commit点，可以撤销commit然后修改正确后再次commit。





> 版本回退

上面看完版本的回滚从v3.0回退到了v2.0，**那如何从v2.0回退到v3.0呢？这种操作允许吗？**

答案是**可以的！**

首先无论你采用何种方式回退到了v2.0（HEAD指向v2.0），只要你中途要回到你之前回退的版本（v3.0）执行以下操作：

- 使用git log是无法再看到v3.0的commit记录的，使用`git reflog`

  ![image-20200724152703106](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724152703.png)

- 找到最近一次v3.0的提交记录，拿到编号后使用`git reset xxxx`即可回到v3.0

  ![image-20200724152955393](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724152955.png)





# 三、Git的三大区域

- 工作区（working directory）
- 暂存区（stage index）
- 历史版本区（history）

![image-20200724155719773](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724155719.png)





# 四、分支

> 概念了解

学习分支之前我们先了解Git的mater也就是我们的开发主干是怎么进行版本迭代的：

假如现在初始化仓库中就有一个成型的项目文件版本是v1.0。
第一次修改后版本迭代到v2.0，那么git再保存v2.0的内容的时候是将1.0的部分全拷贝一份然后加上修改的内容形成2.0存储的吗？很显然这种做法在多次版本更迭后会造成大量的存储空间浪费。
Git所采用的做法是 **每次提交的内容只有修改的内容增量**，然后用**一个类似指针的东西，指向父版本**（即这些修改是在哪个版本上进行的）。这样做我们只需要某个版本的提交内容然后通过指针找到其父版本就能还原出此版本的内容！

![image-20200724162053314](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724162053.png)

这里理解以后，那么就可以引出分支的内容，开始我们讲的都是主干上的发展，一旦主干的某一个版本成为了两个提交内容的父版本：

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724162354.png" alt="image-20200724162354258" style="zoom:67%;" />

这就形成了从主干分化出两个分支。

> 案例引入

假如现在功能研发到了v3.0，此时准备开发4.0的新功能，不巧的是刚开发了1个多月的新功能，线上v3.0的代码出现了bug需要我们紧急修复！！此时就遇到了问题：
我们本地是已经开发了一半的新功能的代码，此时又要对此前v3.0部分代码做bug修复，最终commit的时候未开发完的新功能发现还不能上线，就直接阻碍了bug修复代码的重新上线。

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724164949.png" alt="image-20200724164949554" style="zoom:67%;" />

用我们已经学习的知识，只有两种办法：

- 顶着bug不修，等新功能开发完成，然后修拖了几个月的bug然后一起上线（等着卷铺盖回家把）
- 一个月的新功能开发全白费，修完bug又从零开始。（最终身心俱疲，走上辞职不归路）

看似两个方法都不太可行，那么利用分支就能完美解决问题:

从分支的概念来说，每个分支虽然是基于同一个父类进行修改/开发的，但是==分支之间是没有任何干扰的，各自的修改也是相互独立的。==问题迎面而解：

**<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724170505.png" alt="image-20200724170504965" style="zoom:67%;" />**

使用分支后，dev分支和hotfix分支、master分支互相不干扰，你开发你的新功能，我修我的bug，工作做完就合并到主分支上。



> 命令实现

`git branch`：查看已有的分支，`*`代表当前所在分支

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724172250.png" alt="image-20200724172250227" style="zoom:67%;" />



`git branch branch_name`创建名为branch_name的分支

`git checkout branch_name`切换到branch_name分支

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724172529.png" alt="image-20200724172529748" style="zoom:67%;" />

现在我们处于dev分支，我们来"开发新功能”

![image-20200724172848920](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724172848.png)

在master视角下，出现了一个dev分支正在开发新功能，此时dev分支上的修改只有dev分支上能看到，主分支的文件并没有修改。

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724173105.png" alt="image-20200724173105321" style="zoom:67%;" /><img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724173858.png" alt="image-20200724173141555" style="zoom: 67%;" />

然后现在bug来了，我们又创建一个hotfix分支进行bug修改：==注意这里创建分支要回到master上创建！==
同样此时的修改也不影响master上的文件。

![image-20200724174217069](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724174217.png)

可是我的bug修复终究是要到线上去的（合并到主(master)分支上），此时我们**切换回到master分支上**，
使用`git merge branch_name`将branch_name分支的修改合并到主分支上。

![image-20200724174436592](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724174436.png)

然后我们便可删除无用的hotfix分支了：`git branch -d hotfix`，此命令必须要在创建分支的branch上才能删除。

接着我们继续研发新功能，研发完成同样合并到主分支上：

![image-20200724175035679](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724175035.png)

回到主分支准备合并dev分支：

![image-20200724175343605](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724175343.png)

文件中查看冲突的内容：

![image-20200724175550901](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724175550.png)

==遇到冲突就要解决冲突！！==我们必须选择每个冲突中谁去谁留：

解决后：
![image-20200724175917259](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724175917.png)

然后commit即可合并到主分支上：
![image-20200724180248719](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200724180248.png)



> 为什么产生冲突？为什么第一次Merge没有产生冲突？

首先我们要知道分支合并的简单原理，

我们创建分支后，在**分支上进行修改的时候，有一个“基石”即我们创建分支时候的版本内容，所有分支上的修改都是基于这个父版本的。**
**一旦修改完成进行分支合并的时候，是要比对当前要合并上的分支的文件的**，如果中间没有变动，直接对比前后两个文件就能查找到修改的地方和内容，修改的地方直接覆盖，其他保留就能完成Merge。这也就是为什么第一次Merge顺利完成：hotfix分支创建后，在bug修复期间，master分支安然不动，bug修改完毕直接merge。

而第二次Merge的时候，父版本已经进行了一次更迭，但是dev分支并不知道，其dev分支对当前要合并的分支上的目前版本“不认识”，而恰好dev分支修改的行，已经被其他分支变动过了，这下master分支也不知道要保留哪一个修改，于是冲突就产生了，需要开发人员手动去解决冲突行的内容，然后提交。
==并不是所有的父版本更迭都会发生冲突，而是对父版本中同一个文件的同一行，同时被多个分支修改就会产生冲突。==



# 五、工作流（规范）

master分支作为主分支，不应该直接在master分支上进行代码修改，master分支的更迭应该对应着线上稳定版的更新。
所有的功能业务开发由dev分支进行管理，dev分支上测试通过后，合并到主分支进行稳定版发布，同时master分支进行版本更新。
紧急bug修复，在由master分支引出的hotfix分支上进行紧急修复，修复完毕合并到主分支上。

具体各个功能的开发测试在dev分支上还要不断细分成多个分支，具体规范按照公司规范。

推荐阅读：
[Git版本管理规范](https://blog.csdn.net/weixin_38809962/article/details/79814308)
[Git详解及版本控制规范](https://blog.csdn.net/su1573/article/details/91988523)
[git commit 提交规范 & 规范校验](https://blog.csdn.net/y491887095/article/details/80594043)



# 六、为本地文件创建远程仓库（Gitee）

当我们想要多地工作时，却又不想随身带着电脑或者u盘，并且这种方式并不利于我们做版本控制。此时我们==需要将我们本地版本仓库的代码推送到云端进行管理，方便我们随时随地下载，并且修改然后重新推送到云端。==

常见的Git代码托管仓库：

- GitHub（全球最大的同性交友平台）
- GitLab (常用于企业搭建自己的托管仓库)
- Gitee (国内，速度较快)
- coding

下面以Gitee作为远程代码托管仓库进行学习

> Gitee上创建仓库

![image-20200725094155691](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725094155.png)



进入本地文件夹中打开Git终端执行以下代码：

```shell
# 链接到远程仓库
git remote add origin https://gitee.com/s5akura/GitStudy.git
# 将本地master分支(master)push到远程master分支（origin master）
git push -u origin master

# 如果有多个分支，需要切换分支然后推送到对应的远程分支上，例如
(dev) git push -u origin dev
```

**这里的origin是远程仓库的一个别名，origin master也就是指远程仓库的master分支**



初次Push可能要求输入Gitee的账号的密码，若不慎输入错误，可以参考一下方式修改：

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725095120.png" alt="image-20200723233903408" style="zoom:67%;" />

> 首次从远端仓库拉取代码

当你本地还没有远程仓库的代收的时候，需要从GItee上将代码拉取到本地（比如第一天去公司上班，公司电脑上没有项目文件）

使用`git clone 仓库URL`命令即可将远程仓库的代码全复制一份到本地：

虽然你用分支查看命令可能只看到一个master分支，但是其实所有的分支信息都被保留了，直接切换分支也是可以的。

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725103330.png" alt="image-20200725103330467" style="zoom:67%;" />



> 当两地都有了远程仓库代码后正常的工作流程

```markdown
1. 在公司dev分支上研发完成新功能
git checkout master
git merge dev
git push -u origin master
2. dev分支同步跟进
git checkout dev
git merge master
git push -u origin dev
3. 下班回到家继续写代码,先拉取远程仓库的代码
git pull origin master
git pull origin dev
4. 写完执行操作1,2再推送到Gitee
5. 上班写代码第一步拉取Gitee上的代码，同操作3
..
```

当我们在两地同时存在我们的代码版本的时候，每次开始Coding需要先使用`git pull`将代码拉取下来在最新的代码上进行修改。并且每次修改完一定要push到远程仓库中。确保下次拉取代码是最新的。



> **案例**：有一天公司加班，下班的时候忘记Push到Gitee只是存在了公司电脑本地的代码库中，回到家中发现写了半天的代码没有Push，只好做一些其他模块的开发，最后睡觉的时候吃一堑长一智，把家中的代码Push到了Gitee，第二天回到公司上班，第一件事Pull代码，正当自己将在家写的新模块的代码拉取到本地时候，发现和昨天在公司写的没有Push的代码产生了冲突，只好自己手动一个一个解决。

场景再现：

1. 我们到我们使用clone拉取的文件夹（公司电脑文件夹）下，先执行pull orgin dev保证代码的最新

2. 修改“公司文件夹”下的内容
   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725111545.png" alt="image-20200725111545717" style="zoom: 50%;" />

3. commit到本地的仓库，但忘记推送到Gitee

   ```shell
   git add .
   git commit -m '商城功能-50%'
   ```

4. 回家（之前本地的原生文件夹中），准备写代码先pull orgin dev

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725112015.png" alt="image-20200725112015121" style="zoom: 50%;" />

5. 想起来是在公司没有push，现在只好做其他的模块了：

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725112233.png" alt="image-20200725112233436" style="zoom: 50%;" />

   其实可以很明显看到14行已经存在了冲突。

6. 准备睡觉，吃一堑长一智把在家写的代码push到Gitee上

   ```shell
   git add .
   git commit -m '在家写的评论功能模块'
   git push origin dev
   ```

7. 第二天回到公司上班，先pull代码，结果遇到了冲突：远程的代码和公司本地仓库中的代码存在冲突

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725112947.png" alt="image-20200725112947434" style="zoom: 50%;" />

8. 手动解决冲突：解决后：

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725113127.png" alt="image-20200725113127156" style="zoom: 67%;" />

9. 重新commit提交，并push到Gitee

   ```shell
   git add .
   git commit -m '在家写的代码和公司代码冲突解决'
   git push origin dev
   ```

10. 下班回家，同样再次pull代码，此时公司和家中的代码重回一致！！

​	

## 知识扩展

> Git Fetch和 Git Merge实现Git pull

其实`git pull origin dev`可以看做是两个命令的结合体！

- `git fetch origin dev`
- `git mertge origin/dev`

fetch是用于将远程仓库的中的版本拉取到本地版本库,此时本地版本库就会有一个origin/dev的版本
然后使用merge origin/dev将本地的代码合并更新到使用fetch拉取下来的最新版。

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725131820.png" alt="image-20200725131820840" style="zoom:67%;" />



> 本地配置远程仓库免密登录

1. 查看用户文件夹下的`.ssh`有没有以下两个文件

   ![image-20200723223241930](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200723223242.png)

2. 若没有的话，选中`.ssh`文件夹，右键选项Git Bash here，然后在命令行中执行以下命令

   `ssh-keygen -t rsa`

3. 连续四个问题直接回车即可，然后.ssh文件夹中就会出现上面两个文件。

   默认情况下公钥的末尾会以`PC用户名@主机名`结尾

   ![image-20200723224422010](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200723224422.png)

   也可以使用`ssh-keygen -t rsa -C "xxx"`创建密钥，这样结尾就是你所指定的字符串

   例如：

   `ssh-keygen -t rsa -C "sakura_170312@163.com"`

   ![image-20200723224547916](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200723224547.png)

4. 创建完毕后，id_rsa是私钥，.pub文件是公钥，需要在gitee的用户设置中进行添加

   ![image-20200723224841080](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200723224841.png)

   ![image-20200723225103338](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200723225103.png)





# 七、rebase（变基）

用于简化我们的提交信息。

## 三大使用场景

> 合并多个commit

当在开发的过程中，可能会出现一个功能很多久，而且为了保存记录也commit了很多次（还没有push）,而当我们在结束功能开发时候，想要把中间的commit都合并到一个commit时可以使用`git rebase -i HEAD~n`，表示把从当前开始和往前n条commit合并：

![image-20200725135933064](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725135933.png)

比如这里的四条摸鱼commit记录，我想把这四条记录合并成一个commit，来看具体操作

```shell
# -i选项
-i, --interactive     let the user edit the list of commits to rebase
# 编辑前四条提交
git rebase -i HEAD~4
```

此时会弹出来一个编辑界面需要我们操作

<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725140859.png" alt="image-20200725140859675" style="zoom:67%;" />

例如我现在想做的操作是：

第一、二条记录：修改commit的注释
第三、四条记录：合并到第二条记录中

通过下面的操作码的描述我们就需要用到 **r**和**s**
<img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725141443.png" alt="image-20200725141443387" style="zoom:67%;" />

编辑后：

```
r 596c717 摸鱼第一天
r f63c823 摸鱼第二天
s 72f5d0c 摸鱼第三天
s b23fb5b 摸鱼第四天
```

保存回到终端，我就要开始陆续处理我们的操作请求了

1. 修改1的commit message

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725141832.png" alt="image-20200725141832390" style="zoom:67%;" />

2. 修改2的commit message(同上)

3. 合并3,4提交到2中：

   <img src="https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725142119.png" alt="image-20200725142119084" style="zoom:50%;" />

   我们只留下“开始持续摸鱼”然后保存关闭

----

四条记录处理完成后，会自动commit，我们再来看看commit的路线：

![image-20200725142326861](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725142326.png)

成了！



> 分支并入

这里指的合并分支并不是普普通通的merge,我们用一张图来说明：

![image-20200725143419474](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725143419.png)



具体操作看过来：

1. 首先确保dev和master处于同一个版本下

   ```
   git checkout master
   git merge dev
   
   git checkout dev
   git merge master
   ```

2. 切换到dev分支进行修改到达图中new feature的位置

3. 切回master分支进行修改到达图中v2,0LTS版本

   ![image-20200725145439007](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725145439.png)

4. 切回dev分支，执行`git rebase master`

   ![image-20200725200323700](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725200323.png)

   相当于把dev的基石更换到了master最新版本上

5. 现在切回master分支执行merge,把dev分支拉回来

   ![image-20200725200511135](https://picbed-sakura.oss-cn-shanghai.aliyuncs.com/notePic/20200725200511.png)

6. 此时已经完成对分支的“并入”



> 