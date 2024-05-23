import java.util.ArrayList;
import java.util.List;

public class Screener {
    public Screener(){}
    public List<Token> getScreenedTokenList(List<Token> scannedTokenList){
        List<Token> screenedTokenList = new ArrayList<Token>();
        for (Token token : scannedTokenList){
            if (!(token.type == Scanner.TOKEN.NEWLINE || token.type == Scanner.TOKEN.COMMENT || token.type == Scanner.TOKEN.WHITESPACE || token.type == Scanner.TOKEN.END_OF_PROGRAM)){
                screenedTokenList.add(token);
            }
        }
        return screenedTokenList;
    }
}
