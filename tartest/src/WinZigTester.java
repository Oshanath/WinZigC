import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class WinZigTester {
    WinZigProgramReader winZigProgramReader = new WinZigProgramReader();
    Screener screener = new Screener();

    private void executeTestProgram(String testID){
        String program_string = null;
        try {
            program_string = winZigProgramReader.readProgram("winzig_test_programs\\winzig_"+testID);

            Lexer lexer = new Lexer(program_string);
            List<Token> scannedTokenList = lexer.getTokenList();
            List<Token> screenedTokenList = screener.getScreenedTokenList(scannedTokenList);
            String outputPath = "TestOutputs\\winzig_"+testID+".tree";
            Parser parser = new Parser(screenedTokenList, outputPath);


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean compareOutput(String outputPath, String expectedOutputPath) throws IOException {
        BufferedReader outputReader = new BufferedReader(new FileReader(outputPath));
        BufferedReader expectedOutputReader = new BufferedReader(new FileReader(expectedOutputPath));
        String outputLine, expectedOutputLine = null;
        while ((outputLine = outputReader.readLine()) != null && (expectedOutputLine = expectedOutputReader.readLine()) != null) {
            if (!outputLine.equals(expectedOutputLine)) {
                System.out.println("There's a mismatch in line "+outputLine+" from the test output and line "+expectedOutputLine +" from the expected output.");
                return false;
            }
        }
        if (outputLine == null) {
            expectedOutputLine = expectedOutputReader.readLine();
        }
        return outputLine == null && expectedOutputLine == null;
    }

    public boolean assertTestProgram(String testID) throws IOException {
        executeTestProgram(testID);
        return compareOutput("TestOutputs\\winzig_"+testID+".tree","winzig_test_programs\\winzig_"+testID+".tree");
    }

    public void runAllTests(){
        try {
            for (int i = 1; i <= 15; i++) {
                String testID = i < 10 ? "0" + String.valueOf(i) : String.valueOf(i);
                boolean output = this.assertTestProgram(testID);
                if(output){
                    System.out.println("Test "+testID+" passed.\n");
                }else{
                    System.out.println("Test "+testID+" failed.\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
