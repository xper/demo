package com.stk.demo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class DemoApplication {

  private static final int DTO_PORT_NUMBER = 8000;
  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
    // 새 쓰레드로 소켓서버를 시작
    try(ServerSocket server = new ServerSocket(DTO_PORT_NUMBER)){
      while(true){
        Socket connection = server.accept();
        Thread task = new BytesThreadServer(connection);
        task.start();
      }
    }catch(IOException e){
      log.error("error", e);
    }finally{
      log.info("main exit");
    }
  }

}
    