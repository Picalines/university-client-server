package practice3.client;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
  private Socket socket;
  private BufferedReader socketReader;
  private BufferedWriter socketWriter;

  private String userNickname;
  private BufferedReader stdinReader;

  public Client(String addr, int port) {
    try {
      this.socket = new Socket(addr, port);
    } catch (IOException e) {
      System.err.println("Socket failed");
    }

    try {
      stdinReader = new BufferedReader(new InputStreamReader(System.in));
      socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

      this.askForNickname();

      new MessageReader().start();
      new MessageWriter().start();
    } catch (IOException e) {
      Client.this.shutdown();
    }
  }

  private void askForNickname() throws IOException {
    System.out.print("Enter your nickname: ");

    userNickname = stdinReader.readLine();
    socketWriter.write("Hello " + userNickname + "!\n");
    socketWriter.flush();
  }

  private void shutdown() {
    if (socket.isClosed()) {
      return;
    }

    try {
      socket.close();
      socketReader.close();
      socketWriter.close();
    } catch (IOException ignored) {
    }
  }

  private class MessageReader extends Thread {
    @Override
    public void run() {
      String str;
      try {
        while (true) {
          str = socketReader.readLine();
          if (str.equals("stop")) {
            shutdown();
            break;
          }
          System.out.println(str);
        }
      } catch (IOException e) {
        Client.this.shutdown();
      }
    }
  }

  public class MessageWriter extends Thread {
    @Override
    public void run() {
      while (true) {
        try {
          var timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
          var userInput = stdinReader.readLine();

          if (userInput.equals("stop")) {
            socketWriter.write("stop\n");
            Client.this.shutdown();
            break;
          } else {
            socketWriter.write("[" + timestamp + "] " + userNickname + ": " + userInput + "\n");
          }

          socketWriter.flush();
        } catch (IOException e) {
          Client.this.shutdown();
        }
      }
    }
  }
}
