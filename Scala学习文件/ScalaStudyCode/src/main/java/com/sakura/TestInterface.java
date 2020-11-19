package com.sakura;

/**
 * @author sakura
 * @date 2020/11/4 下午10:49
 */
public class TestInterface implements MyInterface{
    @Override
    public void sayHi(String name) {
        System.out.println(name + ": Hello!");
    }

    public static void main(String[] args) {
        TestInterface obj = new TestInterface();
        obj.showInfo("Nothing");

    }

}

interface MyInterface {

    default void showInfo(String info) {
        System.out.println("info: " + info);
    }

    public void sayHi(String name);
}
