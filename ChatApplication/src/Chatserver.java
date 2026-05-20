import java.io.*;
import java.net.*;
import java.util.*;

public class Chatserver {

    static final int PORT = 12345;
    static List<PrintWriter> clients = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Server started");

        while (true) {
            Socket socket = server.accept();
            new Thread(() -> handle(socket)).start();
        }
    }

    static void handle(Socket socket) {

        PrintWriter out = null;

        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            out = new PrintWriter(socket.getOutputStream(), true);

            synchronized (clients) {
                clients.add(out);
            }

            String msg;

            while ((msg = in.readLine()) != null) {
                broadcast(msg);
            }

        } catch (Exception ignored) {

        } finally {

            if (out != null) {
                synchronized (clients) {
                    clients.remove(out);
                }
            }

            try {
                socket.close();
            } catch (Exception ignored) {}
        }
    }

    static void broadcast(String msg) {

        synchronized (clients) {
            for (PrintWriter pw : clients) {
                pw.println(msg);
            }
        }
    }
}