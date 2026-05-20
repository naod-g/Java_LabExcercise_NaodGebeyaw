import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class Chatclient {

    static final String HOST = "localhost";
    static final int PORT = 12345;

    static PrintWriter out;
    static JTextPane chatArea;
    static JTextField input;
    static String username;

    public static void main(String[] args) {

        username = JOptionPane.showInputDialog("Enter name:");
        if (username == null || username.isBlank()) return;

        SwingUtilities.invokeLater(Chatclient::buildUI);
        connect();
    }

    static void buildUI() {

        JFrame frame = new JFrame("Chat - " + username);
        frame.setSize(450, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        chatArea = new JTextPane();
        chatArea.setEditable(false);

        JScrollPane scroll = new JScrollPane(chatArea);

        input = new JTextField();

        JButton send = new JButton("Send");
        JButton image = new JButton("Image");

        send.addActionListener(e -> sendText());
        input.addActionListener(e -> sendText());
        image.addActionListener(e -> sendImage());

        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel buttons = new JPanel();
        buttons.add(image);
        buttons.add(send);

        bottom.add(input, BorderLayout.CENTER);
        bottom.add(buttons, BorderLayout.EAST);

        frame.add(scroll, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    static void connect() {

        new Thread(() -> {
            try {
                Socket socket = new Socket(HOST, PORT);

                BufferedReader in =
                        new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out = new PrintWriter(socket.getOutputStream(), true);

                String msg;

                while ((msg = in.readLine()) != null) {
                    String finalMsg = msg;
                    SwingUtilities.invokeLater(() -> handle(finalMsg));
                }

            } catch (Exception e) {
                appendText("Connection failed\n", Color.RED);
            }
        }).start();
    }

    static void sendText() {

        String text = input.getText().trim();
        if (text.isEmpty()) return;

        out.println(username + "|text|" + text);
        input.setText("");
    }

    static void sendImage() {

        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;

        try {
            File file = fc.getSelectedFile();

            byte[] bytes = new FileInputStream(file).readAllBytes();
            String b64 = Base64.getEncoder().encodeToString(bytes);

            out.println(username + "|image|" + b64);

        } catch (Exception e) {
            appendText("Image error\n", Color.RED);
        }
    }

    static void handle(String msg) {

        String[] parts = msg.split("\\|", 3);
        if (parts.length < 3) return;

        String sender = parts[0];
        String type = parts[1];
        String content = parts[2];

        if ("image".equals(type)) {
            appendImage(sender, content);
        } else {
            appendText(sender + ": " + content + "\n", Color.BLACK);
        }
    }

    static void appendText(String text, Color color) {

        StyledDocument doc = chatArea.getStyledDocument();
        Style style = chatArea.addStyle("s", null);
        StyleConstants.setForeground(style, color);

        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (Exception ignored) {}
    }

    static void appendImage(String sender, String base64) {

        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            ImageIcon icon = new ImageIcon(bytes);

            Image img = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);

            appendText(sender + " sent an image:\n", Color.BLUE);

            chatArea.insertIcon(icon);
            appendText("\n", Color.BLACK);

        } catch (Exception e) {
            appendText("Image error\n", Color.RED);
        }
    }
}