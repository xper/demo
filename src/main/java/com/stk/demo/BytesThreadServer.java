package com.stk.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;

import com.stk.demo.dto.HeaderDTO;
import com.stk.demo.dto.IN0201001DTO;
import com.stk.demo.lib.MsgUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BytesThreadServer extends Thread {

    private Socket socket;
    private BufferedReader br = null;
    private PrintWriter pw = null;
    private ArrayList<FieldInfo> headerInfos = new ArrayList<FieldInfo>();
    private int headerHexLength = 0;

    public BytesThreadServer(Socket socket) {
        this.socket = socket;
        this.initHeaderInfos();
    }

    public void initHeaderInfos() {
        this.headerInfos = MsgUtil.getHeaderInfos();
        this.headerHexLength = MsgUtil.getHeaderHexLength(this.headerInfos);
        log.info(String.format("* headerInfos: %d", this.headerInfos.size()));
        log.info(String.format("* headerHexLength: %d", this.headerHexLength));
    }


    // 단순 문자열 Thread server
    public void run() {
        try {
            String connIp = socket.getInetAddress().getHostAddress();
            log.info(String.format("* connection from %s", connIp));
            // 수신 버퍼의 최대 사이즈 지정
            int maxBufferSize = 1024;
            // 버퍼 생성
            byte[] recvBuffer = new byte[maxBufferSize];
            // 서버로부터 받기 위한 입력 스트림 뚫음
            InputStream is = socket.getInputStream();
            // 버퍼(recvBuffer) 인자로 넣어서 받음. 반환 값은 받아온 size
            int readSize = is.read(recvBuffer);
            // 받아온 값이 0보다 클때
            log.info("* nReadSize: {}", readSize);
            if (readSize > 1) {
                
                // 받아온 byte를 hexString으로 변환
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < readSize; i++) {
                    sb.append(String.format("%02X", recvBuffer[i]));
                }
                String headerHexString = sb.toString().substring(0, this.headerHexLength);
                log.info("* headerHexString: {}", headerHexString);
                
                // 받아온 byte[]를 Object로 변환
                HeaderDTO headerDTO = MsgUtil.bytesToObject(headerHexString, this.headerInfos, HeaderDTO.class);

                // 확인을 위해 출력
                log.info(String.format("* HeaderDTO: \n * %s", headerDTO.toString()));

                
                String bodyHexString = sb.toString().substring(this.headerHexLength);
                log.info("* bodyHexString: {}", bodyHexString);

                ArrayList<FieldInfo> bodyInfos = MsgUtil.getFieldInfos(headerDTO);
                log.info(String.format("* bodyInfos: %d", bodyInfos.size()));

                IN0201001DTO bodyDTO = MsgUtil.bytesToObject(bodyHexString, bodyInfos, IN0201001DTO.class);

                // Merge headerDTO and bodyDTO
                bodyDTO.setLLMagicNumber(headerDTO.getLLMagicNumber());
                bodyDTO.setUcCrypType(headerDTO.getUcCrypType());
                bodyDTO.setUcTermType(headerDTO.getUcTermType());
                bodyDTO.setUcMessageID(headerDTO.getUcMessageID());
                bodyDTO.setUcServiceID(headerDTO.getUcServiceID());
                bodyDTO.setUsVersion(headerDTO.getUsVersion());
                
                log.info(String.format("* bodyDTO: \n%s", bodyDTO.toString()));
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
        } catch (RuntimeException e) {
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
