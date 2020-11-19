package com.sakura;

/**
 * @author sakura
 * @date 2020/11/16 下午1:18
 */
public class InnerClassTest {
    public static void main(String[] args) {
        OuterClass outer = new OuterClass();
        OuterClass outer2 = new OuterClass();
        outer.showNumber();

        outer.number = 30;

        OuterClass.InnerClass inner = outer.new InnerClass();
        inner.showNumber();

        outer2.fixInnerNumber(inner);
        inner.showNumber();

        OuterClass.StaticInnerClass staticInnerClass = new OuterClass.StaticInnerClass();
        staticInnerClass.showStaticNumber();


    }
}


class OuterClass {
    public int number = 20;

    public void showNumber() {
        System.out.println(this.number);
    }

    public void fixInnerNumber(InnerClass inner) {
        inner.number += 2000;
    }

    class InnerClass {
        private int number = OuterClass.this.number + 1;

        public void showNumber(){

            System.out.println(this.number);
        }
    }

    static class StaticInnerClass{
        public int staticNumber = 2020;
        public void showStaticNumber(){
            System.out.println(staticNumber);
        }
    }

}