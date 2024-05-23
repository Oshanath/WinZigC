import java.io.*;
import java.nio.charset.Charset;

public class Scanner {

    enum TOKEN{
        NEWLINE,
        PROGRAM,
        VAR,
        CONST,
        TYPE,
        FUNCTION,
        RETURN,
        BEGIN,
        END,
        SWAP,
        ASSIGN,
        OUTPUT,
        IF,
        THEN,
        ELSE,
        WHILE,
        DO,
        CASE,
        OF,
        DDOT,
        OTHERWISE,
        REPEAT,
        FOR,
        UNTIl,
        LOOP,
        POOL,
        EXIT,
        LEQ,
        NEQ,
        LT,
        GEQ,
        GT,
        EQ,
        MOD,
        AND,
        OR,
        NOT,
        READ,
        SUCC,
        PRED,
        CHR,
        ORD,
        EOF,
        BLOCK_BEGIN,
        COLON,
        SEMI_COLON,
        DOT,
        COMMA,
        BRACKET_BEGIN,
        BRACKET_END,
        PLUS,
        MINUS,
        MULT,
        DIV,
        WHITESPACE,
        INTEGER,
        CHAR,
        STRING,
        COMMENT
    }

    Reader reader;

    public Scanner(String filename){

        Charset encoding = Charset.defaultCharset();
        File file = new File(filename);
        InputStream in;

        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Reader temp = new InputStreamReader(in, encoding);
        reader = new BufferedReader(temp);

    }

    private class CharResult{
        public final boolean valid;
        public final char value;

        public CharResult(boolean valid, char value){
            this.valid = valid;
            this.value = value;
        }
    }

    CharResult getChar(){
        int r;

        try {
            r = reader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new CharResult(r != -1, (char)r);
    }

    public TOKEN scan(){

        while(true){
            CharResult charResult = getChar();

            if(!charResult.valid){
                System.out.println("End of string");
                break;
            }
            else{
                System.out.println(charResult.value);
            }
        }

        return TOKEN.ASSIGN;
    }

}
