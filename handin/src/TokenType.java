public enum TokenType {
    NEWLINE(" "),
    PROGRAM("program"),
    VAR("var"),
    CONST("const"),
    TYPE("type"),
    FUNCTION("function"),
    RETURN("return"),
    BEGIN("begin"),
    END("end"),
    SWAP(":=:"), // :=:
    ASSIGN(":="), // :=
    OUTPUT("output"),
    IF("if"),
    THEN("then"),
    ELSE("else"),
    WHILE("while"),
    DO("do"),
    CASE("case"),
    OF("of"),
    DDOT(".."), // ..
    OTHERWISE("otherwise"),
    REPEAT("repeat"),
    FOR("for"),
    UNTIL("until"),
    LOOP("loop"),
    POOL("pool"),
    EXIT("exit"),
    LEQ("<="), // <=
    NEQ("<>"), // <>
    LT("<"), // <
    GEQ(">="), // >=
    GT(">"), // >
    EQ("="), // =
    MOD("mod"),
    AND("and"),
    OR("or"),
    NOT("not"),
    READ("read"),
    SUCC("succ"),
    PRED("pred"),
    CHR("chr"),
    ORD("ord"),
    EOF("eof"),
    BLOCK_BEGIN("{"), // {
    COLON(":"), // :
    SEMI_COLON(";"), // ;
    DOT("."), // .
    COMMA(","), // ,
    BRACKET_BEGIN("("), // (
    BRACKET_END(")"), // )
    PLUS("+"), // +
    MINUS("-"), // -
    MULT("*"), // *
    DIV("/"), // /
    WHITESPACE(" "),
    INTEGER("<integer>"),
    CHAR("<char>"),
    STRING("<string>"),
    COMMENT("comment"),
    END_OF_PROGRAM("endProgramme"),
    IDENTIFIER("<identifier>"),
    ERROR("error");

    public final String type;

    TokenType(String type) {
        this.type = type;
    }
}
