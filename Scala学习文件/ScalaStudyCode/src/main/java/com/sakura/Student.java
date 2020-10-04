package com.sakura;

/**
 * @author sakura
 * @date 2020/10/4 下午10:52
 */
public class Student {
    static {
        System.out.println("创建了一个学生");
    }

    public static int count = 56;

    private String name;
    private String id;

    public Student(String name, String id) {
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
