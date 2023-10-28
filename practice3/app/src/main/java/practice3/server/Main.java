package practice3.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static final int PORT = 8070;

  public static void main(String[] args) throws IOException {
    try (var server = new ServerSocket(PORT)) {
      System.out.println("Server Started");

      while (true) {
        Socket socket = server.accept();
        try {
          Server.serverList.add(new Server(socket));
        } catch (IOException e) {
          socket.close();
        }
      }
    }
  }
}
