package com.stk.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScannerThreadServer extends Thread {

  private Socket socket;
  private BufferedReader br = null;
  private PrintWriter pw = null;
  private Scanner scanner = null;

  public ScannerThreadServer(Socket socket) {
    this.socket = socket;
  }

  // 단순 문자열 Thread server
  public void run() {
    try {
      String connIp = socket.getInetAddress().getHostAddress();
      System.out.println(connIp + "에서 연결 시도.");

      // 소켓에서 받은 Byte[]을 읽어들여 Scanner 를 통해 각형식에 맞는 데이터로 변환
      scanner = new Scanner(socket.getInputStream());

      // long lLMagicNumber = scanner.nextLong();
      // log.info("\n * lLMagicNumber: {}", lLMagicNumber);
      // char ucCrypType = scanner.next().charAt(0);
      // log.info("\n * ucCrypType: {}", ucCrypType);
      // char ucTermType = scanner.next().charAt(0);
      // log.info("\n * ucTermType: {}", ucTermType);
      // char ucMessageID = scanner.next().charAt(0);
      // log.info("\n * ucMessageID: {}", ucMessageID);
      // char ucServiceID = scanner.next().charAt(0);
      // log.info("\n * ucCrypType: {}", ucServiceID);

      // 다음 토큰이 long 형식인지 확인합니다.
      if (scanner.hasNextLong()) {
        // 다음 토큰을 long 형식으로 가져옵니다.
        long ucCrypType = scanner.nextLong();

        // 값을 출력합니다.
        System.out.println("Received long value: " + ucCrypType);
      } else {
        System.out.println("No long value received.");
      }

      // 다음 4개의 문자를 확인합니다.
      for (int i = 0; i < 4; i++) {
        if (scanner.hasNextByte()) {
          // 다음 토큰을 문자로 가져옵니다.
          byte character = scanner.nextByte();

          // 문자를 출력합니다.
          System.out.println("Received character: " + character);
        } else {
          System.out.println("No more characters received.");
          break;
        }
      }
      pw = new PrintWriter(socket.getOutputStream());
      // 클라이언트에 문자열 전송
      pw.println("서버: 수신 성공");
      pw.flush();
    } catch (IOException e) {
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