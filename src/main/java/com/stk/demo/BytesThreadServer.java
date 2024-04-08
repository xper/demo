package com.stk.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.Socket;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BytesThreadServer extends Thread {

    private Socket socket;
    private BufferedReader br = null;
    private PrintWriter pw = null;
    private FieldInfo[] headerInfos = new FieldInfo[10];

    public BytesThreadServer(Socket socket) {
        this.socket = socket;

        int idx = 0;
        this.headerInfos[idx++] = new FieldInfo("lLMagicNumber", "long", 16);
        this.headerInfos[idx++] = new FieldInfo("ucCrypType", "char", 2);
        this.headerInfos[idx++] = new FieldInfo("ucTermType", "char", 2);
        this.headerInfos[idx++] = new FieldInfo("ucMessageID", "char", 2);
        this.headerInfos[idx++] = new FieldInfo("ucServiceID", "char", 2);
        this.headerInfos[idx++] = new FieldInfo("usVersion", "short", 4);
     }

    @SuppressWarnings("deprecation")
    public static <T> T bytesToObject(byte[] recvBuffer, int readSize, FieldInfo[] headerInfos, Class<T> type)
            throws IllegalAccessException, InstantiationException {
        Object obj = null;
        try {
            obj = type.newInstance();

            // 받아온 byte를 hexString으로 변환
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < readSize; i++) {
                sb.append(String.format("%02X", recvBuffer[i]));
            }
            String hexString = sb.toString();
            log.info("\n * hexString: {}", hexString);
            int idx = 0;
            for (FieldInfo info : headerInfos) {
                String hexValue = hexString.substring(idx, idx + info.getHexLength());
                Object value = null;
                switch (info.getFieldType()) {
                    case "long":
                        value = Long.parseLong(hexValue, 16);
                        break;
                    case "char":
                        value = (char) Integer.parseInt(hexValue, 16);
                        break;
                    case "short":
                        value = Short.parseShort(hexValue, 16);
                        break;
                    // 사용될 모든 타입 추가 해야함.
                }
                Field field = type.getDeclaredField(info.getFieldName());
                field.setAccessible(true);
                field.set(obj, value);
                idx += info.getHexLength();
            }
        } catch (NoSuchFieldException e) {
            System.out.println(e.getMessage());
            throw new IllegalAccessException(e.getMessage());
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        return type.cast(obj);
    }

    // 단순 문자열 Thread server
    public void run() {
        try {
            String connIp = socket.getInetAddress().getHostAddress();
            log.info(String.format("\n * %s 에서 연결 시도.", connIp));
            // 수신 버퍼의 최대 사이즈 지정
            int maxBufferSize = 1024;
            // 버퍼 생성
            byte[] recvBuffer = new byte[maxBufferSize];
            // 서버로부터 받기 위한 입력 스트림 뚫음
            InputStream is = socket.getInputStream();
            // 버퍼(recvBuffer) 인자로 넣어서 받음. 반환 값은 받아온 size
            int readSize = is.read(recvBuffer);
            // 받아온 값이 0보다 클때
            log.info("\n * nReadSize: {}", readSize);
            if (readSize > 1) {
                
                
                // 받아온 byte[]를 Object로 변환
                HexHeaderDTO headerDTO = bytesToObject(recvBuffer, readSize, this.headerInfos, HexHeaderDTO.class);

                // 확인을 위해 출력
                log.info("\n * HexHeaderDTO: {}", headerDTO.toString());

                

            }
            pw = new PrintWriter(socket.getOutputStream());
            // 클라이언트에 문자열 전송
            pw.println("서버: 수신 성공");
            pw.flush();
            is.close();
        } catch (IOException e) {
            log.error("error", e);
        } catch (IllegalAccessException e) {
            log.error("error", e);
        } catch (InstantiationException e) {
            log.error("error", e);
        } finally {
            try {
                log.info("Thread finally");

                this.exit();
            } catch (IOException e) {
                log.error("error", e);
            }
        }
    }

    // SocketThreadServer 가 종료될 때 자원을 해제
    private void exit() throws IOException {
        try {
            log.info("Thread exit");
            if (pw != null) {
                pw.close();
            }
            if (br != null) {
                br.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            log.error("error", e);
        }
    }
}
