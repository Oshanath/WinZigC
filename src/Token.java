public class Token {
    public Scanner.TOKEN type;
    public String text;
    public int position;
    public int line_number;
    public int column_number;

    public Token(Scanner.TOKEN type, String text, int position, int line, int column) {
        this.type = type;
        this.position = position;
        this.text = text;
        this.line_number = line;
        this.column_number = column;
    }
}
