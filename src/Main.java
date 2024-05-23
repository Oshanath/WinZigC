import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        WinZigTester winZigTester = new WinZigTester();
//        WinZigProgramReader winZigProgramReader = new WinZigProgramReader();
//        Screener screener = new Screener();
//        String program_string = null;
//        try {
//            program_string = winZigProgramReader.readProgram("winzig_test_programs\\winzig_02");
//
//            Lexer lexer = new Lexer(program_string);
//            List<Token> scannedTokenList = lexer.getTokenList();
//            List<Token> screenedTokenList = screener.getScreenedTokenList(scannedTokenList);
//            Parser parser = new Parser(screenedTokenList);
//
//
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }

        //running tests
        winZigTester.runAllTests();
    }
}