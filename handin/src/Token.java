public class Token {
    public TokenType type;
    public String text;
    public int position;
    public int line_number;
    public int column_number;

    public Token(TokenType type, String text, int position, int line, int column) {
        this.type = type;
        this.position = position;
        this.text = text;
        this.line_number = line;
        this.column_number = column;
    }
}
