import java.util.HashMap;

public class FiniteAutomata {

    enum ScannerState{
        START,
        IDENTIFIER,
        INTEGER,
        COMMENT_SINGLE,
        COMMENT_MULTI,
        WHITESPACE,
        CHAR,
        CHAR2,
        STRING,
        PUNCTUATION
    }

    enum CharType{
        LETTER,
        DIGIT,
        HASH,
        BLOCK_BEGIN,
        BLOCK_END,
        EOL,
        WHITESPACE,
        SINGLE_QUOTE,
        DOUBLE_QUOTE,
        SPECIAL

    }

    private HashMap<ScannerState, HashMap<CharType, ScannerState>> table;

    public FiniteAutomata(){
        table = new HashMap<>();
    }

    public void ConvertToDFA(){

    }

    public void Minimize(){

    }

}
