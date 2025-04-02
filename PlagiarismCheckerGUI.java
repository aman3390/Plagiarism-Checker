import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.AbstractBorder; // Add this import
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;

public class PlagiarismCheckerGUI extends JFrame {
    private JTextField file1Field, file2Field;
    private JButton browse1, browse2, checkButton, closeButton;
    private JProgressBar progressBar;

    public PlagiarismCheckerGUI() {
        setTitle("Plagiarism Checker");
        setSize(650, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(32, 32, 32)); // Dark theme

        UIManager.put("Button.font", new Font("San Francisco", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("San Francisco", Font.PLAIN, 14));
        UIManager.put("Label.font", new Font("San Francisco", Font.BOLD, 14));
        UIManager.put("ProgressBar.font", new Font("San Francisco", Font.PLAIN, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        file1Field = new JTextField(20);
        file1Field.setBorder(new EmptyBorder(5, 5, 5, 5));
        file1Field.setBackground(new Color(48, 48, 48));
        file1Field.setForeground(Color.WHITE);
        file1Field.setCaretColor(Color.WHITE);
        file1Field.setOpaque(true);

        file2Field = new JTextField(20);
        file2Field.setBorder(new EmptyBorder(5, 5, 5, 5));
        file2Field.setBackground(new Color(48, 48, 48));
        file2Field.setForeground(Color.WHITE);
        file2Field.setCaretColor(Color.WHITE);
        file2Field.setOpaque(true);

        browse1 = createAnimatedButton("Select File 1", new Color(50, 168, 82));
        browse2 = createAnimatedButton("Select File 2", new Color(50, 168, 82));
        checkButton = createAnimatedButton("Check Plagiarism", new Color(0, 122, 255));
        closeButton = createAnimatedButton("Close", new Color(255, 59, 48));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(50, 168, 82));

        browse1.addActionListener(e -> chooseFile(file1Field));
        browse2.addActionListener(e -> chooseFile(file2Field));
        checkButton.addActionListener(this::checkPlagiarism);
        closeButton.addActionListener(e -> System.exit(0));

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("File 1:"), gbc);
        gbc.gridx = 1; add(file1Field, gbc);
        gbc.gridx = 2; add(browse1, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("File 2:"), gbc);
        gbc.gridx = 1; add(file2Field, gbc);
        gbc.gridx = 2; add(browse2, gbc);

        gbc.gridx = 1; gbc.gridy = 2; add(checkButton, gbc);
        gbc.gridx = 1; gbc.gridy = 3; add(progressBar, gbc);
        gbc.gridx = 1; gbc.gridy = 4; add(closeButton, gbc);

        setVisible(true);
    }

    private JButton createAnimatedButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("San Francisco", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorder(new RoundedBorder(10)); // Rounded border

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(button.getBackground().brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(button.getBackground().darker());
                bounceButton(button);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(color);
            }
        });
        return button;
    }

    private void bounceButton(JButton button) {
        Point originalLocation = button.getLocation();
        javax.swing.Timer timer = new javax.swing.Timer(10, e -> {
            Point currentLocation = button.getLocation();
            int xOffset = (int) (Math.random() * 10 - 5);
            int yOffset = (int) (Math.random() * 10 - 5);
            button.setLocation(currentLocation.x + xOffset, currentLocation.y + yOffset);

            if (Math.abs(currentLocation.x - originalLocation.x) > 20 || Math.abs(currentLocation.y - originalLocation.y) > 20) {
                ((javax.swing.Timer) e.getSource()).stop();
                button.setLocation(originalLocation); // Return to original position after bounce
            }
        });
        timer.start();
    }

    private void chooseFile(JTextField field) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            field.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void checkPlagiarism(ActionEvent e) {
        String file1Path = file1Field.getText();
        String file2Path = file2Field.getText();

        if (file1Path.isEmpty() || file2Path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select both files!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new Thread(() -> {
            try {
                for (int i = 10; i <= 100; i += 10) {
                    progressBar.setValue(i);
                    Thread.sleep(100);
                }
                String text1 = readFile(file1Path);
                String text2 = readFile(file2Path);
                double similarity = checkSimilarity(text1, text2);
                JOptionPane.showMessageDialog(this, "Plagiarism Similarity: " + similarity + "%", "Result", JOptionPane.INFORMATION_MESSAGE);
                progressBar.setValue(0);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    private String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private double checkSimilarity(String text1, String text2) {
        String[] words1 = text1.toLowerCase().split("\\s+");
        String[] words2 = text2.toLowerCase().split("\\s+");
        int commonWords = 0;
        for (String word : words1) {
            if (Arrays.asList(words2).contains(word)) {
                commonWords++;
            }
        }
        return (double) commonWords / Math.max(words1.length, words2.length) * 100;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PlagiarismCheckerGUI::new);
    }

    // Rounded border for buttons and text fields
    static class RoundedBorder extends AbstractBorder {
        private final int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(c.getBackground());
            g.fillRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(10, 10, 10, 10);
        }
    }
}
