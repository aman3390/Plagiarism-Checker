import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class PlagiarismCheckerGUI extends JFrame {
    private JTextField file1Field, file2Field;
    private JButton browse1, browse2, checkButton, closeButton;
    private JProgressBar progressBar;
    private Font poppinsRegular, poppinsBold;

    public PlagiarismCheckerGUI() {
        setTitle("Plagiarism Checker - KMP Algorithm");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(15, 15, 15));

        loadCustomFonts();

        UIManager.put("Button.font", poppinsBold);
        UIManager.put("TextField.font", poppinsRegular);
        UIManager.put("Label.font", poppinsBold);
        UIManager.put("ProgressBar.font", poppinsRegular);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        file1Field = createStyledField();
        file2Field = createStyledField();

        browse1 = createAnimatedButton("\uD83D\uDCC2 File 1", new Color(50, 168, 82));
        browse2 = createAnimatedButton("\uD83D\uDCC2 File 2", new Color(50, 168, 82));
        checkButton = createAnimatedButton("\uD83D\uDD0D Check", new Color(0, 122, 255));
        closeButton = createAnimatedButton("\u274C Close", new Color(255, 59, 48));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(212, 175, 55));
        progressBar.setBackground(new Color(40, 40, 40));

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

    private void loadCustomFonts() {
        try {
            poppinsRegular = Font.createFont(Font.TRUETYPE_FONT, new File("Poppins-Regular.ttf")).deriveFont(14f);
            poppinsBold = Font.createFont(Font.TRUETYPE_FONT, new File("Poppins-Bold.ttf")).deriveFont(16f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(poppinsRegular);
            ge.registerFont(poppinsBold);
        } catch (Exception e) {
            poppinsRegular = new Font("SansSerif", Font.PLAIN, 14);
            poppinsBold = new Font("SansSerif", Font.BOLD, 16);
        }
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField(20);
        field.setBorder(new RoundedBorder(10));
        field.setBackground(new Color(30, 30, 30));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        return field;
    }

    private JButton createAnimatedButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(poppinsBold);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorder(new RoundedBorder(12));

        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                button.setBackground(button.getBackground().brighter());
            }
            @Override public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
            @Override public void mousePressed(MouseEvent e) {
                button.setBackground(button.getBackground().darker());
            }
            @Override public void mouseReleased(MouseEvent e) {
                button.setBackground(color);
            }
        });
        return button;
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

        File file1 = new File(file1Path);
        File file2 = new File(file2Path);
        if (file1.length() == 0 || file2.length() == 0) {
            JOptionPane.showMessageDialog(this, "One of the files is empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new Thread(() -> {
            try {
                for (int i = 10; i <= 100; i += 20) {
                    progressBar.setValue(i);
                    Thread.sleep(100);
                }
                String text1 = readFile(file1Path);
                String text2 = readFile(file2Path);
                double similarity = calculateKMPPlagiarism(text1, text2);
                showGlassResult(similarity);
                progressBar.setValue(0);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    private void showGlassResult(double similarity) {
        // Create result panel with premium style
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(28, 28, 30, 220)); // Matte dark background
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(350, 120));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Result label
        JLabel label = new JLabel("🔍 Similarity: " + String.format("%.2f", similarity) + "%");
        label.setFont(new Font("Poppins", Font.BOLD, 22));
        label.setForeground(new Color(212, 175, 55)); // Premium gold
        label.setOpaque(false);

        // Add label to panel
        panel.add(label);

        // Show premium-styled dialog
        JOptionPane.showMessageDialog(
                this,
                panel,
                "Plagiarism Result",
                JOptionPane.PLAIN_MESSAGE
        );
    }



    private String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line.toLowerCase()).append(" ");
            }
        }
        return content.toString();
    }

    // ✅ KMP Algorithm for Substring Matching
    private double calculateKMPPlagiarism(String text1, String text2) {
        String[] sentences1 = text1.split("[\\.!?]");
        int matchedSentences = 0;

        for (String sentence : sentences1) {
            sentence = sentence.trim();
            if (sentence.length() > 10 && KMPSearch(sentence, text2)) {
                matchedSentences++;
            }
        }

        return (double) matchedSentences / sentences1.length * 100;
    }

    private boolean KMPSearch(String pattern, String text) {
        int[] lps = computeLPSArray(pattern);
        int i = 0, j = 0;

        while (i < text.length()) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++; j++;
            }

            if (j == pattern.length()) return true;
            else if (i < text.length() && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) j = lps[j - 1];
                else i++;
            }
        }
        return false;
    }

    private int[] computeLPSArray(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0, i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i++] = ++len;
            } else {
                if (len != 0) len = lps[len - 1];
                else lps[i++] = 0;
            }
        }
        return lps;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PlagiarismCheckerGUI::new);
    }

    static class RoundedBorder extends AbstractBorder {
        private final int radius;
        public RoundedBorder(int radius) {
            this.radius = radius;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(212, 175, 55));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x, y, width - 2, height - 2, radius, radius);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(10, 10, 10, 10);
        }
    }
}
