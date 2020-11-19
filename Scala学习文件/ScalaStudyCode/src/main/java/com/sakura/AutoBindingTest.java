package com.sakura;

/**
 * @author sakura
 * @date 2020/10/26 下午5:03
 */
public class AutoBindingTest {
    public static void main(String[] args) {
        A obj = new B();
        System.out.println(obj.sum());
        System.out.println(obj.sum1());
    }
}

class A {
    public int i = 10;

    public int sum() {
        return getI() + 10;
    }

    public int sum1() {
        return i + 10;
    }

    public int getI() {
        return i;
    }
}

class B extends A {
    public int i = 20;

    @Override
    public int sum() {
        return getI() + 20;
    }

    // @Override
    // public int sum1() {
    //     return i + 10;
    // }

    @Override
    public int getI() {
        return i;
    }
}
