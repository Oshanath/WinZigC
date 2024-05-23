import java.util.Arrays;
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

    public Token getNewLine(){
        int start_position = this.position;
        if(getCurrentChar() == '\n'){
            this.position++;
            this.line_number++;
            this.column_number = 0;
            return new Token(Scanner.TOKEN.NEWLINE,"\n",start_position,this.line_number,this.column_number);
        }
        return null;
    }

    public Token getWhiteSpace(){
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

    public boolean isMember(char value, char[] charList){
        for (char ch : charList){
            if(ch == value){
                return true;
            }
        }
        return false;
    }

    public String getIdentifier(){
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

    public Token getInteger(){
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

    public Token getChar(){
        int start_position = this.position;
        if (getCurrentChar() == '\'' & this.program_string.charAt(this.position + 2) == '\'' & this.program_string.charAt(this.position + 1) != '\'') {
            this.position += 3;
            return new Token(Scanner.TOKEN.CHAR, this.program_string.substring(start_position, this.position), start_position, this.line_number, this.column_number);
        }
        return null;
    }

    public Token getString(){
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

    public Token getCommentType1(){
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

    public Token getCommentType2(){
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

    public Token getNextToken(){
        //TODO
        return null;
    }
}
