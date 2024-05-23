import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        WinZigProgramReader winZigProgramReader = new WinZigProgramReader();
        Screener screener = new Screener();
        String program_string = null;
        try {
            program_string = winZigProgramReader.readProgram("E:\\Aca\\8th Semester\\CS4542 - Compiler Design\\Project\\WinZigC\\winzig_test_programs\\winzig_02");
            Lexer lexer = new Lexer(program_string);
            List<Token> scannedTokenList = lexer.getTokenList();
            List<Token> screenedTokenList = screener.getScreenedTokenList(scannedTokenList);

            for (Token token : screenedTokenList) {
                System.out.println(token.type + ":" + token.text);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}