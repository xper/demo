package com.stk.demo.lib;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.stk.demo.FieldInfo;
import com.stk.demo.dto.HeaderDTO;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public abstract class MsgUtil {

    public static ArrayList<FieldInfo> getFieldInfos(HeaderDTO headerDTO) {
        ArrayList<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();
        String serviceId = String.format("IN_%02X_%02X_V1.%03d", headerDTO.getUcServiceID(), headerDTO.getUcMessageID(),
                headerDTO.getUsVersion());

        switch (serviceId) {
            case "IN_02_01_V1.001":

                fieldInfos.add(new FieldInfo("ullTkSerNum", "long", 16));
                fieldInfos.add(new FieldInfo("szTkSerNumEx", "String", 32));
                fieldInfos.add(new FieldInfo("ucGameID", "int", 2));
                break;

            default:
                fieldInfos.add(new FieldInfo("usResult", "String", 12));
                break;
        }

        log.info("* serviceId: " + serviceId);
        return fieldInfos;

    }

    public static ArrayList<FieldInfo> getHeaderInfos() {
        ArrayList<FieldInfo> headerInfos = new ArrayList<FieldInfo>();

        headerInfos.add(new FieldInfo("lLMagicNumber", "long", 16));
        headerInfos.add(new FieldInfo("ucCrypType", "int", 2));
        headerInfos.add(new FieldInfo("ucTermType", "int", 2));
        headerInfos.add(new FieldInfo("ucMessageID", "int", 2));
        headerInfos.add(new FieldInfo("ucServiceID", "int", 2));
        headerInfos.add(new FieldInfo("usVersion", "short", 4));

        return headerInfos;

    }

    public static int getHeaderHexLength(ArrayList<FieldInfo> headerInfos) {
        int headerHexLength = 0;
        for (FieldInfo info : headerInfos) {
            headerHexLength += info.getHexLength();
        }
        return headerHexLength;
    }

    public static <T> T bytesToObject(String hexString, ArrayList<FieldInfo> fieldInfos, Class<T> type)
            throws IllegalAccessException {
        Object obj = null;
        try {
            obj = type.getDeclaredConstructor().newInstance();

            int idx = 0;
            for (FieldInfo info : fieldInfos) {
                if (idx + info.getHexLength() > hexString.length()) {
                    log.warn(String.format("* hexString length is not enough. hexString.length() %d",
                            hexString.length()));
                    break;
                }
                String hexValue = hexString.substring(idx, idx + info.getHexLength());
                Object value = MsgUtil.parseTypeFromHexStr(hexValue, info.getFieldType());

                Field field = type.getDeclaredField(info.getFieldName()); // 해당 필드를 가져옴

                field.setAccessible(true);// private field에 접근하기 위해
                field.set(obj, value); // obj에 value를 set
                idx += info.getHexLength();
            }
            if (idx < hexString.length()) {
                log.warn(String.format("* hexString length is too long. hexString.length() %d", hexString.length()));
            }
        } catch (InstantiationException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (NoSuchFieldException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return type.cast(obj);
    }

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
