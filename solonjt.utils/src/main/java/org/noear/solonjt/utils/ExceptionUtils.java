package org.noear.solonjt.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常格式化工具
 * */
public class ExceptionUtils {
    public static String getString(Exception ex){
        StringWriter sw = new StringWriter();
        PrintWriter ps = new PrintWriter(sw);
        ex.printStackTrace(ps);

        return sw.toString();
    }
}
