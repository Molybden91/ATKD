package Pahan.util;

import java.io.PrintStream;

/**
 * Created by Lab22 on 08.09.2016.
 */
public class Error {
    public static void error(Object obj){
        System.err.println(obj);
    }
    public static void error(){
        System.err.println();
    }
    public static void errornb(Object obj){System.err.print(obj);}
    public static PrintStream errorf(String format, Object...args) {
        return System.err.printf(format, args);
    }
    public static void warning(String warningtext){
        System.err.println(warningtext);
    }
}
