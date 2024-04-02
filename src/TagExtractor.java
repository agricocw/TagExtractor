import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class TagExtractor extends JFrame {
    private JLabel fileLabel;
    private JTextArea tagsTextArea;
    private JButton selectFileButton;
    private JButton selectStopWordsButton;
    private JButton extractTagsButton;
    private JButton saveTagsButton;

    private File selectedFile;
    private Set<String> stopWords;
    private Map<String, Integer> tagsFrequency;

    public TagExtractor() {
        setTitle("Tag/Keyword Extractor");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        fileLabel = new JLabel("No file selected.");
        tagsTextArea = new JTextArea();
        tagsTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(tagsTextArea);
        selectFileButton = new JButton("Select File");
        selectStopWordsButton = new JButton("Select Stop Words");
        extractTagsButton = new JButton("Extract Tags");
        saveTagsButton = new JButton("Save Tags");

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(fileLabel);
        topPanel.add(selectFileButton);
        topPanel.add(selectStopWordsButton);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(extractTagsButton);
        bottomPanel.add(saveTagsButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    fileLabel.setText("Selected File: " + selectedFile.getName());
                }
            }
        });

        selectStopWordsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File stopWordsFile = fileChooser.getSelectedFile();
                    stopWords = loadStopWords(stopWordsFile);
                }
            }
        });

        extractTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null && stopWords != null) {
                    tagsFrequency = extractTags(selectedFile, stopWords);
                    displayTags(tagsFrequency);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a file and stop words first.");
                }
            }
        });

        saveTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tagsFrequency != null) {
                    JFileChooser fileChooser = new JFileChooser();
                    int result = fileChooser.showSaveDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File outputFile = fileChooser.getSelectedFile();
                        saveTags(outputFile, tagsFrequency);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No tags to save. Please extract tags first.");
                }
            }
        });
    }

    private Set<String> loadStopWords(File stopWordsFile) {
        Set<String> stopWords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(stopWordsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopWords.add(line.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWords;
    }

    private Map<String, Integer> extractTags(File file, Set<String> stopWords) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            while (scanner.hasNext()) {
                String word = scanner.next().toLowerCase().replaceAll("[^a-zA-Z]", "");
                if (!word.isEmpty() && !stopWords.contains(word)) {
                    frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return frequencyMap;
    }


    private void displayTags(Map<String, Integer> tagsFrequency) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : tagsFrequency.entrySet()) {
            result.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        tagsTextArea.setText(result.toString());
    }

    private void saveTags(File outputFile, Map<String, Integer> tagsFrequency) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (Map.Entry<String, Integer> entry : tagsFrequency.entrySet()) {
                writer.println(entry.getKey() + ": " + entry.getValue());
            }
            JOptionPane.showMessageDialog(null, "Tags saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TagExtractor().setVisible(true);
            }
        });
    }
}
