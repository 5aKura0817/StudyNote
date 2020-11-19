package com.sakura;

/**
 * @author sakura
 * @date 2020/10/26 下午4:31
 */
public class FieldOverrideTest {
    public static void main(String[] args) {
        Super obj1 = new Super();
        Super obj2 = new Sub();
        Sub obj3 = new Sub();

        System.out.println(obj1.str);
        System.out.println(obj2.str);
        System.out.println(obj3.str);
        System.out.println(((Super) obj3).str);
    }
}

class Super {
    public String str = "父类的str";
}

class Sub extends Super {
    public String str = "子类的str";
}