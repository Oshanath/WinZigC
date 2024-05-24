import java.io.IOException;
import java.util.List;

public class winzigc {
    public static void main(String[] args) {

        String flag = args[0];

        if(flag.equals("-ast")){

            WinZigProgramReader winZigProgramReader = new WinZigProgramReader();

            String programPath = args[1];
            String program;

            try {
                program = winZigProgramReader.readProgram(programPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Lexer lexer = new Lexer(program);
            Screener screener = new Screener();

            List<Token> scannedTokenList = lexer.getTokenList();
            List<Token> screenedTokenList = screener.getScreenedTokenList(scannedTokenList);
            Parser parser;

            try {
                parser = new Parser(screenedTokenList, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if(flag.equals("-test")){
            WinZigTester winZigTester = new WinZigTester();
            winZigTester.runAllTests();
        }
    }
}