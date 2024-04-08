package com.stk.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testSocketServerConnection() throws IOException {
		Socket socket = new Socket("localhost", 8888);
		System.out.println(socket.getInetAddress().getHostAddress() + "에 연결됨");
		PrintWriter out = new PrintWriter(socket.getOutputStream());
		out.println("Hello");
		out.flush();

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String message = in.readLine();
		System.out.println("Received response: " + message);
		
		assert message.equals("연결 성공");
		in.close();
		out.close();
		socket.close();
	}
}
