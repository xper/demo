package com.stk.demo.lib;

import java.io.UnsupportedEncodingException;

public abstract class MsgUtil {
    public static Object parseTypeFromHexStr(String hexValue, String type) {
        switch (type.toLowerCase()) {
            case "int":
                return Integer.parseInt(hexValue, 16);
            case "short":
                return Short.parseShort(hexValue, 16);
            case "char":
                return (char) Integer.parseInt(hexValue, 16);
            case "long":
                return Long.parseLong(hexValue, 16);
            case "byte":
                return Byte.parseByte(hexValue, 16);
            case "string":
                try {
                    // NOTE: PBS_A_V1.9 문서의 2.2.1 Data Type의 String 정의에 따라 UTF-8로 인코딩
                    return new String(hexValue.getBytes("UTF-8"), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    return hexValue;
                }
            default:
                return hexValue;
        }
    }
}
