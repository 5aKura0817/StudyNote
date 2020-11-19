package com.sakura;

/**
 * @author sakura
 * @date 2020/11/3 下午3:27
 */
public class DynamicBindTest {
    public static void main(String[] args) {
        Father obj = new Son();
        obj.f();
        System.out.println(obj.str);
        System.out.println(((Son) obj).str);

        System.out.println(obj.name);
        System.out.println(((Son) obj).name);
    }
}

class Father {

    public String str = "Father";

    public static String name = "A";

    public static void output() {
        System.out.println("我是父类的output");
    }

    public void f() {
        System.out.println("Father -> f()");
    }
}

class Son extends Father {

    public String str = "Son";

    public static String name = "B";

    public static void output() {
        System.out.println("我是子类的Output");
    }

    @Override
    public void f() {
        System.out.println("Son -> f()");
    }
}