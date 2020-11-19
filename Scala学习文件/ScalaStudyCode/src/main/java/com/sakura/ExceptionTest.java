package com.sakura;

/**
 * @author sakura
 * @date 2020/9/10 上午10:08
 */
public class ExceptionTest {
    public static void main(String[] args) {

        try {
            // ...
        } catch (ArithmeticException | NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // ...
        }

    }
}
