package com.sakura.constractor;

/**
 * @author sakura
 * @date 2020/10/23 上午10:41
 */
public class TestConstractor {
    public static void main(String[] args) {

    }
}

class Person {
    public String name;
    public int age;

    public Person (String inName, int inAge){
        name = inName;
        age = inAge;
    }

    public Person() {
        name = "sakura";
        age = 18;
    }

}

class Student extends Person {
    public String id;

    public Student(String inName, int inAge, String inId) {
        super(inName,inAge);
        id = inId;
    }

    public Student(String inId) {
        super();
        id = inId;
    }
}
