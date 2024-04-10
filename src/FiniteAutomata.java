import java.util.HashMap;

public class FiniteAutomata {

    enum State{
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

    enum Input{
        LETTER,
        DIGIT,
        HASH,
        BLOCK_BEGIN,
        BLOCK_END,
        EOL,
        WHITESPACE,
        SINGLE_QUOTE,
        DOUBLE_QUOTE,
        SPECIAL,
        EPSILON

    }

    private HashMap<State, HashMap<Input, State>> table;

    public FiniteAutomata(){
        table = new HashMap<>();
    }

    public void ConvertToDFA(){

    }

    public void Minimize(){

    }

}
