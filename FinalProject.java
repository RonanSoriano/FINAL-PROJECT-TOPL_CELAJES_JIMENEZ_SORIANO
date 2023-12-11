import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

   /*
        CELAJES, AERON RED R.
        JIMENEZ, CESAR JULIUS D.
        SORIANO, RONAN D.
        
        3 BSCS - 1
        TOPL FINAL PROJECT
     */

public class FinalProject extends JFrame {
    public JPanel MainPanel;
    public JButton openFileButton, lexicalAnalysisButton, clearButton, semanticAnalysisButton, syntaxAnalysisButton;
    public JTextArea codeTextArea, resultTextArea;
    private JScrollPane resultAreaScroll, codeAreaScroll;
    private File selectedFile;

    public FinalProject() {
        setContentPane(MainPanel);
        setTitle("FINAL PROJECT");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000,450);
        setLocationRelativeTo(null);
        setVisible(true);
        resultTextArea.setEditable(false);
        codeTextArea.setAutoscrolls(true);
        codeTextArea.setEditable(false);

        lexicalAnalysisButton.setEnabled(false);
        syntaxAnalysisButton.setEnabled(false);
        semanticAnalysisButton.setEnabled(false);
        clearButton.setEnabled(false);

        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        lexicalAnalysisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLexicalAnalysis();
            }
        });

        syntaxAnalysisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultTextArea.setText("");
                performSyntaxAnalysis();
            }
        });

        semanticAnalysisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultTextArea.setText("");
                performSemanticAnalysis();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        
    }

    // For openFile.
    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(java.io.File f) {
                return f.getName().toLowerCase().endsWith(".java") || f.isDirectory();
            }

            public String getDescription() {
                return "Java Files (*.java)";
            }
        });

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {

            try {
                String filePath = fileChooser.getSelectedFile().getPath();

                if (AnalysisChecker.isJavaFile(filePath)) {
                    openFileButton.setEnabled(false);

                    selectedFile = fileChooser.getSelectedFile();
                    BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                    StringBuilder code = new StringBuilder();

                    String line;

                    while ((line = reader.readLine()) != null) {
                        code.append(line).append("\n");
                    }

                    reader.close();
                    codeTextArea.setText(code.toString());
                    lexicalAnalysisButton.setEnabled(true);
                }else {
                    codeTextArea.setText("Error: Only Java files are accepted. Please choose a file with a '.java' extension.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            codeTextArea.setText("No file selected! Please try again.");
        }
    }

    // For lexicalAnalysis.
    private void performLexicalAnalysis() {
        String code = codeTextArea.getText();
        if (AnalysisChecker.lexicalChecker(code)) {
            syntaxAnalysisButton.setEnabled(true);
            resultTextArea.setText("Lexical Analysis Passed!\n");
            resultTextArea.append(AnalysisChecker.outputChecker);
        } else {
            resultTextArea.setText("Lexical Analysis Error!\n");
            clearButton.setEnabled(true);
        }
        lexicalAnalysisButton.setEnabled(false);
    }

    // For syntaxAnalysis.
    private void performSyntaxAnalysis() {
        if (AnalysisChecker.syntaxChecker(AnalysisChecker.outputChecker)) {
            semanticAnalysisButton.setEnabled(true);
            resultTextArea.append("Syntax Analysis Passed!\n");
        } else {
            resultTextArea.append("Syntax Error!\n");
            clearButton.setEnabled(true);
        }
        syntaxAnalysisButton.setEnabled(false);
    }

    // For semanticAnalysis.
    private void performSemanticAnalysis() {
        String code = codeTextArea.getText();
        if (AnalysisChecker.semanticAnalyzer(code)){
            clearButton.setEnabled(true);
            resultTextArea.append("Semantic Analysis Passed!\n");
        }else {
            resultTextArea.append("Semantic Analysis Error!\n");
            clearButton.setEnabled(true);
        }
        semanticAnalysisButton.setEnabled(false);
    }

    // For clear.
    private void clear() {
        codeTextArea.setText("");
        resultTextArea.setText("");
        selectedFile = null;
        lexicalAnalysisButton.setEnabled(false);
        syntaxAnalysisButton.setEnabled(false);
        semanticAnalysisButton.setEnabled(false);
        openFileButton.setEnabled(true);
        clearButton.setEnabled(false);
    }

    // Analysis Code.
    public class AnalysisChecker {

        public static String outputChecker;

        public static boolean isJavaFile(String filePath) {
            // Check if the file has a '.java' extension
            return filePath.toLowerCase().endsWith(".java");
        }

        public static List<String> readFileLines(String filePath) {

            List<String> lines = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new StringReader(filePath))) {
                reader.lines().forEach(lines::add);
            } catch (IOException e) {
                System.err.println("Error reading the file: " + e.getMessage());
            }

            return lines;
        }


        public static boolean lexicalChecker(String filePath) {

            String lexicalToken = new String();
            String outputPrint = new String();

            List<String> input = readFileLines(filePath);
            for (String line : input) {
                // splits the input
                String[] tokens = line.split("\\s|(?<=[=;+])|(?=[=;+]\\s*)");
                int valueCtr = 0;

                // Identifying the lexemes with their respective tokens
                for (String token : tokens) {

                    // for string values only
                    // it will only print one value if more spaces were added in the string value
                    if (token.startsWith("\"")) {
                        outputPrint += "<value> ";
                        valueCtr++;
                    }
                    if (token.endsWith(("\""))) {
                        valueCtr = 0;
                        continue;
                    }

                    // if no more than 0 values in the given input
                    if (valueCtr == 0) {
                        if (token.equals("int") || token.equals("double") || token.equals("char") || token.equals("String") || token.equals("boolean")) {
                            outputPrint += "<data_type> ";
                        } else if (token.equals("=")) {
                            outputPrint += "<assignment_operator> ";
                        } else if (token.equals(";")) {
                            outputPrint += "<delimiter> ";
                        } else if (token.equals("true") || token.equals("false")) {
                            outputPrint += "<value> ";
                        } else if (token.matches("[0-9]+|\"[^\"]*\"|'[^']*'|\\d+\\.\\d+")) {
                            outputPrint += "<value> ";
                        } else if (!token.isEmpty()) {
                            outputPrint += "<identifier> ";
                        }
                    }

                }

                lexicalToken += outputPrint + "\n";

                outputChecker = lexicalToken;

                String[] numTokens = outputPrint.split(" ");
                int count = numTokens.length;
                outputPrint = new String();

                if (count <= 2){
                    return false;
                }

            }
            return true;
        }

        public static boolean syntaxChecker(String outputPrint) {
            List<String> input = readFileLines(outputPrint);
            for (String line : input) {

                String checkerPrint = line.trim();

                String chkr1 = "<data_type> <identifier> <assignment_operator> <value> <delimiter>";
                String chkr2 = "<data_type> <identifier> <delimiter>";

                if (!checkerPrint.contains(chkr1) && !checkerPrint.contains(chkr2)) {
                    return false;
                }
            }
            return true;
        }

        public static boolean semanticAnalyzer(String outputPrint) {

            List<String> lineChecker = readFileLines(outputPrint);

            for (String input : lineChecker) {

            String[] inputContains = input.split("="); // store user input.

            if (inputContains.length != 2) {
                return false; // if the assignment statement is invalid.
            }

            String declaration = inputContains[0]; // store dataType and identifier.
            String container = inputContains[1]; // store value with delimiter.
            String value = container.substring(0, container.length() - 1); // to get value only.

            String[] declarationContains = declaration.split(" ");

            if (declarationContains.length != 2) {
                return false; // if the declaration is invalid.
            }

            String dataType = declarationContains[0]; // store dataType.


            if (dataType.equals("int")) { // for int dataType.
                if (value.trim().contains(".")) {
                    return false; // int dataType has no decimal.
                }
                else if (value.trim().contains("\"") || value.trim().contains("'")) {
                    return false; // int dataType has no double and single quotation marks.
                }
                else if (value.trim().matches("[a-zA-Z]*")) {
                    return false; // int dataType does not accept words value.
                }
            }
            else if (dataType.equals("String")) { // for String dataType.
                if (!(value.startsWith("\"") || value.endsWith("\""))) {
                    return false; // String dataType does not accept value without double quotations.
                }
            }
            else if (dataType.equals("double")) { // for double dataType.
                if (!(value.trim().contains(".")))  {
                    return false; // double dataType need decimal.
                }
                else if (value.trim().contains("\"") || value.trim().contains("'")) {
                    return false; // double dataType has no double and single quotation marks.
                }
                else if (value.trim().matches("[a-zA-Z]*")) {
                    return false; // double dataType does not accept words value.
                }
            }
            else if (dataType.equals("char")) { // for char dataType.
                if (!(value.trim().contains("'"))) {
                    System.out.println(value);
                    return false; // char dataType need single quotations if character value.
                }
            }
            else if (dataType.equals("boolean")) {
                if (!value.trim().contains("true") && !value.trim().contains("false")) {
                    return false;
                }
            }
            else { // for other dataType that are not included.
                return false;
            }
        }
            return true;
        }
    }

    public static void main(String[] args) {
        new FinalProject();
    }
}
