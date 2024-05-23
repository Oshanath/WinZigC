import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {
        WinZigProgramReader winZigProgramReader = new WinZigProgramReader();

        String program_string = null;
        try {
            program_string = winZigProgramReader.readProgram("E:\\Aca\\8th Semester\\CS4542 - Compiler Design\\Project\\WinZigC\\winzig_test_programs\\winzig_01");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(program_string);

        //Testing
//        char[] white_space_chars = {'\t', '\r', '\f', ' '};
//        Lexer lexer = new Lexer("#f5g67");
//        System.out.println(lexer.getIdentifier());
    }
}