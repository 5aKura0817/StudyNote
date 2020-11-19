package com.sakura;

/**
 * @author sakura
 * @date 2020/9/21 下午3:43
 */
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
