import java.util.ArrayList;
import java.util.List;

public class Screener {
    public Screener() {
    }

    public List<Token> getScreenedTokenList(List<Token> scannedTokenList) {
        List<Token> screenedTokenList = new ArrayList<Token>();
        for (Token token : scannedTokenList) {
            if (!(token.type == TokenType.NEWLINE || token.type == TokenType.COMMENT
                    || token.type == TokenType.WHITESPACE || token.type == TokenType.END_OF_PROGRAM)) {
                screenedTokenList.add(token);
            }
        }
        return screenedTokenList;
    }
}
