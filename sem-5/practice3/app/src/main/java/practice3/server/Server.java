package practice3.server;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

public class Server extends Thread {
  public static LinkedList<Server> serverList = new LinkedList<>();

  private final Socket socket;
  private final BufferedReader socketReader;
  private final BufferedWriter socketWriter;

  public Server(Socket socket) throws IOException {
    this.socket = socket;

    socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

    start();
  }

  @Override
  public void run() {
    new Thread(() -> {
      while (true) {
        try {
          sleep(5000);
          socketWriter.flush();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }).start();

    try {
      var line = socketReader.readLine();
      socketWriter.write(line + "\n");

      try {
        while (true) {
          line = socketReader.readLine();

          if (line.equals("stop")) {
            this.shutdown();
            break;
          }

          System.out.println("Sending: " + line);

          for (Server vr : Server.serverList) {
            vr.send(line);
          }
        }
      } catch (NullPointerException ignored) {
      }
    } catch (IOException e) {
      this.shutdown();
    }
  }

  private void send(String msg) {
    try {
      socketWriter.write(msg + "\n");
    } catch (IOException ignored) {
    }
  }

  private void shutdown() {
    if (socket.isClosed()) {
      return;
    }

    try {
      socketReader.close();
      socketWriter.close();
      socket.close();
    } catch (IOException ignored) {
    }

    for (Server server : serverList) {
      if (server.equals(this)) {
        server.interrupt();
      }

      serverList.remove(this);
    }
  }
}
