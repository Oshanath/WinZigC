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

    private char getCurrentChar() {
        if (this.position >= this.program_string.length())
            return '\0';
        return this.program_string.charAt(position);
    }

    private Token getNewLine() {
        int start_position = this.position;
        if (getCurrentChar() == '\n') {
            this.position++;
            this.line_number++;
            this.column_number = 0;
            return new Token(TokenType.NEWLINE, "\n", start_position, this.line_number, this.column_number);
        }
        return null;
    }

    private Token getWhiteSpace() {
        char[] white_space_chars = { '\t', '\r', '\f', ' ' };
        int start_position = this.position;
        if (isMember(getCurrentChar(), white_space_chars)) {
            do {
                this.position++;
                this.column_number++;
            } while (isMember(getCurrentChar(), white_space_chars));
            return new Token(TokenType.WHITESPACE, this.program_string.substring(start_position, this.position),
                    start_position, this.line_number, this.column_number);
        }
        return null;
    }

    private boolean isMember(char value, char[] charList) {
        for (char ch : charList) {
            if (ch == value) {
                return true;
            }
        }
        return false;
    }

    private String getIdentifier() {
        String alphabeticalAndSpecialChar = "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        if (alphabeticalAndSpecialChar.indexOf(getCurrentChar()) >= 0) {
            int start_position = this.position;
            do {
                this.position++;
                this.column_number++;
            } while (alphabeticalAndSpecialChar.indexOf(getCurrentChar()) + digits.indexOf(getCurrentChar()) >= -1);
            return this.program_string.substring(start_position, this.position);
        }
        return null;
    }

    private Token getInteger() {
        String digits = "0123456789";
        if (digits.indexOf(getCurrentChar()) >= 0) {
            int start_position = this.position;
            do {
                this.position++;
                this.column_number++;
            } while (digits.indexOf(getCurrentChar()) >= 0);
            return new Token(TokenType.INTEGER, this.program_string.substring(start_position, this.position),
                    start_position, this.line_number, this.column_number);
        }
        return null;
    }

    private Token getChar() {
        int start_position = this.position;
        if (getCurrentChar() == '\'' & this.program_string.charAt(this.position + 2) == '\''
                & this.program_string.charAt(this.position + 1) != '\'') {
            this.position += 3;
            return new Token(TokenType.CHAR, this.program_string.substring(start_position, this.position),
                    start_position, this.line_number, this.column_number);
        }
        return null;
    }

    private Token getString() {
        int start_position = this.position;
        if (getCurrentChar() == '"') {
            do {
                position++;
                this.column_number++;
            } while (getCurrentChar() != '"');
            position++;
            this.column_number++;
            return new Token(TokenType.STRING, this.program_string.substring(start_position, this.position),
                    start_position, this.line_number, this.column_number);
        }
        return null;
    }

    private Token getCommentType1() {
        int start_position = this.position;
        if (getCurrentChar() == '#') {
            do {
                this.position++;
                this.column_number++;
            } while (getCurrentChar() != '\n');
            this.position++;
            this.column_number++;
            return new Token(TokenType.COMMENT, this.program_string.substring(start_position, this.position),
                    start_position, this.line_number, this.column_number);
        }
        return null;
    }

    private Token getCommentType2() {
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
            return new Token(TokenType.COMMENT, this.program_string.substring(start_position, this.position),
                    start_position, this.line_number, this.column_number);
        }
        return null;
    }

    private Token getNextToken() {
        if (this.position >= this.program_string.length()) {
            return new Token(TokenType.END_OF_PROGRAM, "\0", this.position, this.line_number, this.column_number);
        }
        String token = getIdentifier();
        if (token != null) {
            int start_position = this.position;
            switch (token) {
                case "program":
                    return new Token(TokenType.PROGRAM, token, start_position, this.line_number, this.column_number);
                case "var":
                    return new Token(TokenType.VAR, token, start_position, this.line_number, this.column_number);
                case "const":
                    return new Token(TokenType.CONST, token, start_position, this.line_number, this.column_number);
                case "type":
                    return new Token(TokenType.TYPE, token, start_position, this.line_number, this.column_number);
                case "function":
                    return new Token(TokenType.FUNCTION, token, start_position, this.line_number, this.column_number);
                case "return":
                    return new Token(TokenType.RETURN, token, start_position, this.line_number, this.column_number);
                case "begin":
                    return new Token(TokenType.BEGIN, token, start_position, this.line_number, this.column_number);
                case "end":
                    return new Token(TokenType.END, token, start_position, this.line_number, this.column_number);
                case "output":
                    return new Token(TokenType.OUTPUT, token, start_position, this.line_number, this.column_number);
                case "if":
                    return new Token(TokenType.IF, token, start_position, this.line_number, this.column_number);
                case "then":
                    return new Token(TokenType.THEN, token, start_position, this.line_number, this.column_number);
                case "else":
                    return new Token(TokenType.ELSE, token, start_position, this.line_number, this.column_number);
                case "while":
                    return new Token(TokenType.WHILE, token, start_position, this.line_number, this.column_number);
                case "do":
                    return new Token(TokenType.DO, token, start_position, this.line_number, this.column_number);
                case "case":
                    return new Token(TokenType.CASE, token, start_position, this.line_number, this.column_number);
                case "of":
                    return new Token(TokenType.OF, token, start_position, this.line_number, this.column_number);
                case "otherwise":
                    return new Token(TokenType.OTHERWISE, token, start_position, this.line_number, this.column_number);
                case "repeat":
                    return new Token(TokenType.REPEAT, token, start_position, this.line_number, this.column_number);
                case "for":
                    return new Token(TokenType.FOR, token, start_position, this.line_number, this.column_number);
                case "until":
                    return new Token(TokenType.UNTIL, token, start_position, this.line_number, this.column_number);
                case "loop":
                    return new Token(TokenType.LOOP, token, start_position, this.line_number, this.column_number);
                case "pool":
                    return new Token(TokenType.POOL, token, start_position, this.line_number, this.column_number);
                case "exit":
                    return new Token(TokenType.EXIT, token, start_position, this.line_number, this.column_number);
                case "mod":
                    return new Token(TokenType.MOD, token, start_position, this.line_number, this.column_number);
                case "and":
                    return new Token(TokenType.AND, token, start_position, this.line_number, this.column_number);
                case "or":
                    return new Token(TokenType.OR, token, start_position, this.line_number, this.column_number);
                case "not":
                    return new Token(TokenType.NOT, token, start_position, this.line_number, this.column_number);
                case "read":
                    return new Token(TokenType.READ, token, start_position, this.line_number, this.column_number);
                case "succ":
                    return new Token(TokenType.SUCC, token, start_position, this.line_number, this.column_number);
                case "pred":
                    return new Token(TokenType.PRED, token, start_position, this.line_number, this.column_number);
                case "chr":
                    return new Token(TokenType.CHR, token, start_position, this.line_number, this.column_number);
                case "ord":
                    return new Token(TokenType.ORD, token, start_position, this.line_number, this.column_number);
                case "eof":
                    return new Token(TokenType.EOF, token, start_position, this.line_number, this.column_number);
                default:
                    return new Token(TokenType.IDENTIFIER, token, start_position, this.line_number,
                            this.column_number - token.length());
            }
        }

        // new line
        Token newLineToken = getNewLine();
        if (newLineToken != null) {
            return newLineToken;
        }
        // white space
        Token whiteSpaceToken = getWhiteSpace();
        if (whiteSpaceToken != null) {
            return whiteSpaceToken;
        }
        // integer
        Token integerToken = getInteger();
        if (integerToken != null) {
            return integerToken;
        }
        // char
        Token charToken = getChar();
        if (charToken != null) {
            return charToken;
        }
        // string
        Token stringToken = getString();
        if (stringToken != null) {
            return stringToken;
        }
        // comment type 1
        Token commentType1Token = getCommentType1();
        if (commentType1Token != null) {
            return commentType1Token;
        }
        // comment type 2
        Token commentType2Token = getCommentType2();
        if (commentType2Token != null) {
            return commentType2Token;
        }
        // swap
        String swapString = this.program_string.substring(this.position, this.position + 3);
        if (swapString.equals(":=:")) {
            return new Token(TokenType.SWAP, swapString, this.position += 3, this.line_number, this.column_number);
        }
        // tokens of length 2
        String tokenOfLen2 = this.program_string.substring(this.position, this.position + 2);
        switch (tokenOfLen2) {
            case ":=":
                return new Token(TokenType.ASSIGN, tokenOfLen2, this.position += 2, this.line_number,
                        this.column_number);
            case "..":
                return new Token(TokenType.DDOT, tokenOfLen2, this.position += 2, this.line_number, this.column_number);
            case "<=":
                return new Token(TokenType.LEQ, tokenOfLen2, this.position += 2, this.line_number, this.column_number);
            case "<>":
                return new Token(TokenType.NEQ, tokenOfLen2, this.position += 2, this.line_number, this.column_number);
            case ">=":
                return new Token(TokenType.GEQ, tokenOfLen2, this.position += 2, this.line_number, this.column_number);
        }
        // tokens of length 1
        char tokenOfLen1 = getCurrentChar();
        switch (tokenOfLen1) {
            case '<':
                return new Token(TokenType.LT, String.valueOf(tokenOfLen1), ++this.position, this.line_number,
                        this.column_number);
            case '>':
                return new Token(TokenType.GT, String.valueOf(tokenOfLen1), ++this.position, this.line_number,
                        this.column_number);
            case '=':
                return new Token(TokenType.EQ, String.valueOf(tokenOfLen1), ++this.position, this.line_number,
                        this.column_number);
            case ':':
                return new Token(TokenType.COLON, String.valueOf(tokenOfLen1), ++this.position, this.line_number,
                        this.column_number);
            case ';':
                return new Token(TokenType.SEMI_COLON, String.valueOf(tokenOfLen1), ++this.position, this.line_number,
                        this.column_number);
            case '.':
                return new Token(TokenType.DOT, String.valueOf(tokenOfLen1), ++this.position, this.line_number,
                        this.column_number);
            case ',':
                return new Token(TokenType.COMMA, String.valueOf(tokenOfLen1), ++this.position, this.line_number,
                        this.column_number);
            case '(':
                return new Token(TokenType.BRACKET_BEGIN, String.valueOf(tokenOfLen1), ++this.position,
                        this.line_number, this.column_number);
            case ')':
                return new Token(TokenType.BRACKET_END, String.valueOf(tokenOfLen1), ++this.position, this.line_number,
                        this.column_number);
            case '+':
                return new Token(TokenType.PLUS, String.valueOf(tokenOfLen1), ++this.position, this.line_number,
                        this.column_number);
            case '-':
                return new Token(TokenType.MINUS, String.valueOf(tokenOfLen1), ++this.position, this.line_number,
                        this.column_number);
            case '*':
                return new Token(TokenType.MULT, String.valueOf(tokenOfLen1), ++this.position, this.line_number,
                        this.column_number);
            case '/':
                return new Token(TokenType.DIV, String.valueOf(tokenOfLen1), ++this.position, this.line_number,
                        this.column_number);
        }
        return new Token(TokenType.ERROR, String.valueOf(tokenOfLen1), this.position, this.line_number,
                this.column_number);
    }

    public List<Token> getTokenList() {
        List<Token> tokenList = new ArrayList<>();
        Token token;
        do {
            token = this.getNextToken();
            tokenList.add(token);
            if (token.type.equals(TokenType.ERROR)) {
                break;
            }
        } while (!token.type.equals(TokenType.END_OF_PROGRAM));
        return tokenList;
    }
}
