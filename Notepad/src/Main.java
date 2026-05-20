import java.awt.*;
import javax.swing.*;
import javax.swing.undo.UndoManager;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Notepad");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane);
        frame.setLocationRelativeTo(null); //set the window to middle

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu formatMenu = new JMenu("Format");
        JMenu viewMenu = new JMenu("View");

        JMenuItem newFile = new JMenuItem("New");
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");
        JMenuItem exitFile = new JMenuItem("Exit");

        JMenuItem copyText = new JMenuItem("Copy");
        JMenuItem cutText = new JMenuItem("Cut");
        JMenuItem pasteText = new JMenuItem("Paste");
        JMenuItem undoText = new JMenuItem("Undo");
        JMenuItem redoText = new JMenuItem("Redo");

        JMenuItem textColor = new JMenuItem("Text Color");
        JMenuItem fontSettings = new JMenuItem("Font Settings");
        JCheckBoxMenuItem wrapText = new JCheckBoxMenuItem("Word Wrap");
        JToggleButton darkMode = new JToggleButton("Dark Mode");

        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(exitFile);

        editMenu.add(copyText);
        editMenu.add(cutText);
        editMenu.add(pasteText);
        editMenu.add(undoText);
        editMenu.add(redoText);

        formatMenu.add(textColor);
        formatMenu.add(fontSettings);
        formatMenu.add(wrapText);

        JMenuItem zoomIn = new JMenuItem("Zoom In");
        JMenuItem zoomOut = new JMenuItem("Zoom Out");

        viewMenu.add(zoomIn);
        viewMenu.add(zoomOut);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        menuBar.add(viewMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(darkMode);

        frame.setJMenuBar(menuBar);

        UndoManager undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
        frame.setVisible(true);

//        functionality
        newFile.addActionListener(e -> textArea.setText(""));
        JFileChooser fileChooser = new JFileChooser();
        exitFile.addActionListener(e -> {
            if (!textArea.getText().isEmpty()) {
                int choice = JOptionPane.showConfirmDialog(
                        frame,
                        "Do you want to save before exiting?",
                        "Exit",
                        JOptionPane.YES_NO_CANCEL_OPTION
                );

                if (choice == JOptionPane.CANCEL_OPTION) {
                    return;
                }

                if (choice == JOptionPane.YES_OPTION) {
                    int option = fileChooser.showSaveDialog(frame);

                    if (option == JFileChooser.APPROVE_OPTION) {
                        try {
                            java.io.File file = fileChooser.getSelectedFile();
                            java.io.FileWriter writer = new java.io.FileWriter(file);
                            writer.write(textArea.getText());
                            writer.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }

            System.exit(0);
        });
        saveFile.addActionListener(e -> {
            int option = fileChooser.showSaveDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                try{
                    java.io.File file = fileChooser.getSelectedFile();
                    java.io.FileWriter writer = new java.io.FileWriter(file);
                    writer.write(textArea.getText());
                    writer.close();
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        openFile.addActionListener(e -> {
            int option = fileChooser.showOpenDialog(frame);

            if (option == JFileChooser.APPROVE_OPTION) {
                try {
                    java.io.File file = fileChooser.getSelectedFile();
                    java.util.Scanner scanner = new java.util.Scanner(file);

                    textArea.setText("");
                    while (scanner.hasNextLine()) {
                        textArea.append(scanner.nextLine() + "\n");
                    }
                    scanner.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        copyText.addActionListener(e-> textArea.copy());
        cutText.addActionListener(e-> textArea.cut());
        pasteText.addActionListener(e-> textArea.paste());

        undoText.addActionListener(e -> {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        });

        redoText.addActionListener(e -> {
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        });

        textColor.addActionListener(e -> {
            java.awt.Color selectedColor = JColorChooser.showDialog(frame, "Choose Text Color", java.awt.Color.BLACK);

            if (selectedColor != null) {
                textArea.setForeground(selectedColor);
            }
        });
        wrapText.addActionListener(e -> {
            textArea.setLineWrap(wrapText.isSelected());
            textArea.setWrapStyleWord(true);
        });

        darkMode.addActionListener(e -> {
            if (darkMode.isSelected()) {
                darkMode.setText("Light Mode");
                textArea.setBackground(Color.BLACK);
                textArea.setForeground(Color.WHITE);
                textArea.setCaretColor(Color.WHITE);
            } else {
                darkMode.setText("Dark Mode");
                textArea.setBackground(Color.WHITE);
                textArea.setForeground(Color.BLACK);
                textArea.setCaretColor(Color.BLACK);
            }
        });

        fontSettings.addActionListener(e -> {
            String fontName = JOptionPane.showInputDialog(frame, "Enter font name:");
            String fontSize = JOptionPane.showInputDialog(frame, "Enter font size:");

            try {
                int size = Integer.parseInt(fontSize);
                textArea.setFont(new Font(fontName, Font.PLAIN, size));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid font size.");
            }
        });

        java.awt.Font currentFont = textArea.getFont();

        zoomIn.addActionListener(e -> {
            java.awt.Font font = textArea.getFont();
            textArea.setFont(new java.awt.Font(
                    font.getName(),
                    font.getStyle(),
                    font.getSize() + 2
            ));
        });

        zoomOut.addActionListener(e -> {
            java.awt.Font font = textArea.getFont();
            int newSize = Math.max(8, font.getSize() - 2);

            textArea.setFont(new java.awt.Font(
                    font.getName(),
                    font.getStyle(),
                    newSize
            ));
        });
    }
}
