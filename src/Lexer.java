import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Lexer {
    private int position;
    private int line_number = 1;
    private int column_number = 0;
    private final String program_string;

    public Lexer(String program_string) {
        this.program_string = program_string;
    }

    private char getCurrentChar(){
        if(this.position >= this.program_string.length())
            return '\0';
        return this.program_string.charAt(position);
    }

    private Token getNewLine(){
        int start_position = this.position;
        if(getCurrentChar() == '\n'){
            this.position++;
            this.line_number++;
            this.column_number = 0;
            return new Token(Scanner.TOKEN.NEWLINE,"\n",start_position,this.line_number,this.column_number);
        }
        return null;
    }

    private Token getWhiteSpace(){
        char[] white_space_chars = {'\t', '\r', '\f', ' '};
        int start_position = this.position;
        if (isMember(getCurrentChar(), white_space_chars)){
            do {
                this.position++;
                this.column_number++;
            } while (isMember(getCurrentChar(), white_space_chars));
            return new Token(Scanner.TOKEN.WHITESPACE,this.program_string.substring(start_position,this.position),start_position,this.line_number,this.column_number);
        }
        return null;
    }

    private boolean isMember(char value, char[] charList){
        for (char ch : charList){
            if(ch == value){
                return true;
            }
        }
        return false;
    }

    private String getIdentifier(){
        String alphabeticalAndSpecialChar = "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        if (alphabeticalAndSpecialChar.indexOf(getCurrentChar()) >= 0){
            int start_position = this.position;
            do {
                this.position++;
                this.column_number++;
            } while (alphabeticalAndSpecialChar.indexOf(getCurrentChar()) + digits.indexOf(getCurrentChar()) >= -1);
            return this.program_string.substring(start_position,this.position);
        }
        return null;
    }

    private Token getInteger(){
        String digits = "0123456789";
        if (digits.indexOf(getCurrentChar()) >= 0){
            int start_position = this.position;
            do {
                this.position++;
                this.column_number++;
            } while (digits.indexOf(getCurrentChar()) >= 0);
            return new Token(Scanner.TOKEN.INTEGER,this.program_string.substring(start_position,this.position),start_position,this.line_number,this.column_number);
        }
        return null;
    }

    private Token getChar(){
        int start_position = this.position;
        if (getCurrentChar() == '\'' & this.program_string.charAt(this.position + 2) == '\'' & this.program_string.charAt(this.position + 1) != '\'') {
            this.position += 3;
            return new Token(Scanner.TOKEN.CHAR, this.program_string.substring(start_position, this.position), start_position, this.line_number, this.column_number);
        }
        return null;
    }

    private Token getString(){
        int start_position = this.position;
        if (getCurrentChar() == '"') {
            do {
                position++;
                this.column_number++;
            } while (getCurrentChar() != '"');
            position++;
            this.column_number++;
            return new Token(Scanner.TOKEN.STRING,this.program_string.substring(start_position,this.position),start_position,this.line_number,this.column_number);
        }
        return null;
    }

    private Token getCommentType1(){
        int start_position = this.position;
        if (getCurrentChar() == '#') {
            do {
                this.position++;
                this.column_number++;
            } while (getCurrentChar() != '\n');
            this.position++;
            this.column_number++;
            return new Token(Scanner.TOKEN.COMMENT,this.program_string.substring(start_position,this.position),start_position,this.line_number,this.column_number);
        }
        return null;
    }

    private Token getCommentType2(){
        int start_position = this.position;
        Stack<Character> nestedComments = new Stack<>();
        if (getCurrentChar() == '{') {
            this.position++;
            this.column_number++;
            nestedComments.push('{');
            while (!nestedComments.empty()) {
                switch (getCurrentChar()) {
                    case '\n':
                        this.line_number++;
                        this.column_number = 0;
                        break;
                    case '{':
                        nestedComments.push('{');
                        break;
                    case '}':
                        if (nestedComments.peek().equals('{')) {
                            nestedComments.pop();
                        } else {
                            throw new Error("Missing { at Line " + this.line_number);
                        }
                        break;
                }
                this.position++;
                this.column_number++;
            }
            return new Token(Scanner.TOKEN.COMMENT,this.program_string.substring(start_position,this.position),start_position,this.line_number,this.column_number);
        }
        return null;
    }

    private Token getNextToken(){
        if(this.position >= this.program_string.length()){
            return new Token(Scanner.TOKEN.END_OF_PROGRAM,"\0",this.position,this.line_number,this.column_number);
        }
        String token = getIdentifier();
        if (token != null) {
            int start_position = this.position;
            switch (token){
                case "program":
                    return new Token(Scanner.TOKEN.PROGRAM,token,start_position,this.line_number,this.column_number);
                case "var":
                    return new Token(Scanner.TOKEN.VAR,token,start_position,this.line_number,this.column_number);
                case "const":
                    return new Token(Scanner.TOKEN.CONST,token,start_position,this.line_number,this.column_number);
                case "type":
                    return new Token(Scanner.TOKEN.TYPE,token,start_position,this.line_number,this.column_number);
                case "function":
                    return new Token(Scanner.TOKEN.FUNCTION,token,start_position,this.line_number,this.column_number);
                case "return":
                    return new Token(Scanner.TOKEN.RETURN,token,start_position,this.line_number,this.column_number);
                case "begin":
                    return new Token(Scanner.TOKEN.BEGIN,token,start_position,this.line_number,this.column_number);
                case "end":
                    return new Token(Scanner.TOKEN.END,token,start_position,this.line_number,this.column_number);
                case "output":
                    return new Token(Scanner.TOKEN.OUTPUT,token,start_position,this.line_number,this.column_number);
                case "if":
                    return new Token(Scanner.TOKEN.IF,token,start_position,this.line_number,this.column_number);
                case "then":
                    return new Token(Scanner.TOKEN.THEN,token,start_position,this.line_number,this.column_number);
                case "else":
                    return new Token(Scanner.TOKEN.ELSE,token,start_position,this.line_number,this.column_number);
                case "while":
                    return new Token(Scanner.TOKEN.WHILE,token,start_position,this.line_number,this.column_number);
                case "do":
                    return new Token(Scanner.TOKEN.DO,token,start_position,this.line_number,this.column_number);
                case "case":
                    return new Token(Scanner.TOKEN.CASE,token,start_position,this.line_number,this.column_number);
                case "of":
                    return new Token(Scanner.TOKEN.OF,token,start_position,this.line_number,this.column_number);
                case "otherwise":
                    return new Token(Scanner.TOKEN.OTHERWISE,token,start_position,this.line_number,this.column_number);
                case "repeat":
                    return new Token(Scanner.TOKEN.REPEAT,token,start_position,this.line_number,this.column_number);
                case "for":
                    return new Token(Scanner.TOKEN.FOR,token,start_position,this.line_number,this.column_number);
                case "until":
                    return new Token(Scanner.TOKEN.UNTIL,token,start_position,this.line_number,this.column_number);
                case "loop":
                    return new Token(Scanner.TOKEN.LOOP,token,start_position,this.line_number,this.column_number);
                case "pool":
                    return new Token(Scanner.TOKEN.POOL,token,start_position,this.line_number,this.column_number);
                case "exit":
                    return new Token(Scanner.TOKEN.EXIT,token,start_position,this.line_number,this.column_number);
                case "mod":
                    return new Token(Scanner.TOKEN.MOD,token,start_position,this.line_number,this.column_number);
                case "and":
                    return new Token(Scanner.TOKEN.AND,token,start_position,this.line_number,this.column_number);
                case "or":
                    return new Token(Scanner.TOKEN.OR,token,start_position,this.line_number,this.column_number);
                case "not":
                    return new Token(Scanner.TOKEN.NOT,token,start_position,this.line_number,this.column_number);
                case "read":
                    return new Token(Scanner.TOKEN.READ,token,start_position,this.line_number,this.column_number);
                case "succ":
                    return new Token(Scanner.TOKEN.SUCC,token,start_position,this.line_number,this.column_number);
                case "pred":
                    return new Token(Scanner.TOKEN.PRED,token,start_position,this.line_number,this.column_number);
                case "chr":
                    return new Token(Scanner.TOKEN.CHR,token,start_position,this.line_number,this.column_number);
                case "ord":
                    return new Token(Scanner.TOKEN.ORD,token,start_position,this.line_number,this.column_number);
                case "eof":
                    return new Token(Scanner.TOKEN.EOF,token,start_position,this.line_number,this.column_number);
                default:
                    return new Token(Scanner.TOKEN.IDENTIFIER,token,start_position,this.line_number,this.column_number - token.length());
            }
        }

        //new line
        Token newLineToken = getNewLine();
        if(newLineToken != null){
            return newLineToken;
        }
        //white space
        Token whiteSpaceToken = getWhiteSpace();
        if(whiteSpaceToken != null){
            return whiteSpaceToken;
        }
        //integer
        Token integerToken = getInteger();
        if(integerToken != null){
            return integerToken;
        }
        //char
        Token charToken = getChar();
        if(charToken != null){
            return charToken;
        }
        //string
        Token stringToken = getString();
        if(stringToken != null){
            return stringToken;
        }
        //comment type 1
        Token commentType1Token = getCommentType1();
        if(commentType1Token != null){
            return commentType1Token;
        }
        //comment type 2
        Token commentType2Token = getCommentType2();
        if(commentType2Token != null){
            return commentType2Token;
        }
        //swap
        String swapString = this.program_string.substring(this.position, this.position + 3);
        if(swapString.equals(":=:")){
            return new Token(Scanner.TOKEN.SWAP,swapString,this.position+=3,this.line_number,this.column_number);
        }
        //tokens of length 2
        String tokenOfLen2 = this.program_string.substring(this.position, this.position + 2);
        switch(tokenOfLen2){
            case ":=" :
                return new Token(Scanner.TOKEN.ASSIGN, tokenOfLen2,this.position+=2, this.line_number,this.column_number);
            case ".." :
                return new Token(Scanner.TOKEN.DDOT, tokenOfLen2,this.position+=2, this.line_number,this.column_number);
            case "<=" :
                return new Token(Scanner.TOKEN.LEQ, tokenOfLen2,this.position+=2, this.line_number,this.column_number);
            case "<>" :
                return new Token(Scanner.TOKEN.NEQ, tokenOfLen2,this.position+=2, this.line_number,this.column_number);
            case ">=" :
                return new Token(Scanner.TOKEN.GEQ, tokenOfLen2,this.position+=2, this.line_number,this.column_number);
        }
        //tokens of length 1
        char tokenOfLen1 = getCurrentChar();
        switch(tokenOfLen1){
            case '<' :
                return new Token(Scanner.TOKEN.LT, String.valueOf(tokenOfLen1),++this.position, this.line_number,this.column_number);
            case '>' :
                return new Token(Scanner.TOKEN.GT, String.valueOf(tokenOfLen1),++this.position, this.line_number,this.column_number);
            case '=' :
                return new Token(Scanner.TOKEN.EQ, String.valueOf(tokenOfLen1),++this.position, this.line_number,this.column_number);
            case ':' :
                return new Token(Scanner.TOKEN.COLON, String.valueOf(tokenOfLen1),++this.position, this.line_number,this.column_number);
            case ';' :
                return new Token(Scanner.TOKEN.SEMI_COLON, String.valueOf(tokenOfLen1),++this.position, this.line_number,this.column_number);
            case '.' :
                return new Token(Scanner.TOKEN.DOT, String.valueOf(tokenOfLen1),++this.position, this.line_number,this.column_number);
            case ',' :
                return new Token(Scanner.TOKEN.COMMA, String.valueOf(tokenOfLen1),++this.position, this.line_number,this.column_number);
            case '(' :
                return new Token(Scanner.TOKEN.BRACKET_BEGIN, String.valueOf(tokenOfLen1),++this.position, this.line_number,this.column_number);
            case ')' :
                return new Token(Scanner.TOKEN.BRACKET_END, String.valueOf(tokenOfLen1),++this.position, this.line_number,this.column_number);
            case '+' :
                return new Token(Scanner.TOKEN.PLUS, String.valueOf(tokenOfLen1),++this.position, this.line_number,this.column_number);
            case '-' :
                return new Token(Scanner.TOKEN.MINUS, String.valueOf(tokenOfLen1),++this.position, this.line_number,this.column_number);
            case '*' :
                return new Token(Scanner.TOKEN.MULT, String.valueOf(tokenOfLen1),++this.position, this.line_number,this.column_number);
            case '/' :
                return new Token(Scanner.TOKEN.DIV, String.valueOf(tokenOfLen1),++this.position, this.line_number,this.column_number);
        }
        return new Token(Scanner.TOKEN.ERROR,String.valueOf(tokenOfLen1),this.position, this.line_number,this.column_number);
    }

    public List<Token> getTokenList(){
        List<Token> tokenList = new ArrayList<>();
        Token token;
        do {
            token = this.getNextToken();
            tokenList.add(token);
            if (token.type.equals(Scanner.TOKEN.ERROR)) {
                break;
            }
        } while (!token.type.equals(Scanner.TOKEN.END_OF_PROGRAM));
        return tokenList;
    }
}
