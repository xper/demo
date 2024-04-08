package com.stk.demo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketThreadServer extends Thread {

  private Socket socket;
  private BufferedReader br = null;
  private PrintWriter pw = null;
  private Scanner scanner = null;

  public SocketThreadServer(Socket socket) {
    this.socket = socket;
  }

  public static <T> T toObject(byte[] bytes, Class<T> type) throws IOException, ClassNotFoundException {
    Object obj = null;
    try {
      ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
      ObjectInputStream ois = new ObjectInputStream(bis);
      obj = ois.readObject();
    } catch (IOException e) {
      System.out.println(e.getMessage());
      throw e;
    } catch (ClassNotFoundException e) {
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
      int nReadSize = is.read(recvBuffer);
      // 받아온 값이 0보다 클때
      log.info("\n * nReadSize: {}", nReadSize);
      if (nReadSize > 128) {
        // 받아온 byte를 Object로 변환
        HeaderDTO headerDTO = toObject(recvBuffer, HeaderDTO.class);

        // 확인을 위해 출력
        log.info("\n * HeaderDTO: {}", headerDTO.toString());
      
      }
      pw = new PrintWriter(socket.getOutputStream());
      // 클라이언트에 문자열 전송
      pw.println("서버: 수신 성공");
      pw.flush();
      is.close();
    } catch (IOException e) {
      log.error("error", e);
    } catch (ClassNotFoundException e) {
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
      if (scanner != null) {
        scanner.close();
      }
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