
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

// binary tree node to represent nodes in AST
class BinaryTreeNode {

    private BinaryTreeNode leftTree;

    private BinaryTreeNode rightTree;

    private String label;

    private int children;

    BinaryTreeNode(String node_label) {
        this.label = node_label;
    }

    public void setLeftChild(BinaryTreeNode node) {
        leftTree = node;
    }

    public void setRightChild(BinaryTreeNode node) {
        rightTree = node;
    }

    public void setChildren(int c) {
        children = c;
    }

    // Traversing the tree in pre-order
    public void PreOrderTraverse(int indentation) {
        for (int i = 0; i < indentation; i++)
            System.out.print(". ");
        System.out.print(this.label);
        System.out.println("(" + children + ")");

        if (this.leftTree != null) {
            leftTree.PreOrderTraverse(indentation + 1);
        }

        if (this.rightTree != null) {
            rightTree.PreOrderTraverse(indentation);
        }
    }

    // Traversing the tree in pre-order and writing to output file
    public void PreOrderTraverse(int indentation, FileWriter writer) throws IOException {
        for (int i = 0; i < indentation; i++) {
            writer.write(". ");
        }
        writer.write(this.label + "(" + this.children + ")\n");

        if (this.leftTree != null) {
            leftTree.PreOrderTraverse(indentation + 1, writer);
        }

        if (this.rightTree != null) {
            rightTree.PreOrderTraverse(indentation, writer);
        }
    }
}

public class Parser {

    private final FileWriter fileWriter;
    private List<Token> tokenList;
    private int tokenIndex;
    Token nextToken;
    private Stack<BinaryTreeNode> treeStack;

    Parser(List<Token> tokenList) throws IOException {
        this.tokenList = tokenList;
        treeStack = new Stack<>();
        this.fileWriter = null;
        getNextToken();
        winZigAST();
    }

    Parser(List<Token> tokenList, String outputFilePath) throws IOException {
        this.tokenList = tokenList;
        treeStack = new Stack<>();

        if (outputFilePath != null) {
            this.fileWriter = new FileWriter(outputFilePath);
        } else {
            this.fileWriter = null;
        }

        getNextToken();
        winZigAST();

        if (fileWriter != null) {
            this.fileWriter.close();
        }
    }

    boolean hasNext() {
        return tokenIndex <= tokenList.size() - 2;
    }

    String peek() {
        if (tokenIndex <= tokenList.size() - 1) {
            return tokenList.get(tokenIndex).type.type;
        }
        System.out.println("End of token list");
        throw new Error();
    }

    void getNextToken() {
        nextToken = tokenList.get(tokenIndex);
        tokenIndex++;
    }

    void winZigAST() throws IOException {
        read("program");
        Name();
        read(":");
        Consts();
        Types();
        Dclns();
        SubProgs();
        Body();
        Name();
        read(".");

        constructTree("program", 7);

        for (BinaryTreeNode node : treeStack) {
            if (this.fileWriter == null) {
                node.PreOrderTraverse(0);
            } else {
                node.PreOrderTraverse(0, this.fileWriter);
            }

        }

    }

    void Name() {
        read(TokenType.IDENTIFIER);
    }

    void Consts() {
        if (nextToken.type.type.equals("const")) {
            read("const");
            int list_count = 1;
            Const();
            while (!nextToken.type.type.equals(";")) {
                read(",");
                Const();
                list_count += 1;
            }
            read(";");
            constructTree("consts", list_count);
        } else {
            constructTree("consts", 0);
        }
    }

    void Const() {
        Name();
        read("=");
        ConstValue();

        constructTree("const", 2);
    }

    void ConstValue() {
        if (nextToken.type.type.equals("<char>") || nextToken.type.type.equals("<integer>")) {
            read(nextToken.type);
        }

        if (nextToken.type.type.equals("<identifier>")) {
            Name();
        }
    }

    void Types() {
        if (nextToken.type.type.equals("type")) {
            read("type");
            int count = 0;
            while (nextToken.type.type.equals("<identifier>")) {
                Type();
                read(";");
                count++;
            }
            constructTree("types", count);

        } else {
            constructTree("types", 0);
        }
    }

    void Type() {
        if (nextToken.type.type.equals("<identifier>")) {
            Name();
            read("=");
            LitList();

            constructTree("type", 2);
        }
    }

    void LitList() {
        read("(");
        Name();
        int count = 1;
        while (!nextToken.type.type.equals(")")) {
            read(",");
            Name();
            count++;
        }
        read(")");
        constructTree("lit", count);
    }

    void Dclns() {
        if (nextToken.type.type.equals("var")) {
            read("var");
            Dcln();
            read(";");
            int count = 1;
            while (nextToken.type.type.equals("<identifier>")) {
                Dcln();
                read(";");
                count++;
            }

            constructTree("dclns", count);
        } else {
            constructTree("dclns", 0);
        }
    }

    void Dcln() {
        Name();
        int count = 1;
        while (!nextToken.type.type.equals(":")) {
            read(",");
            Name();
            count++;
        }
        read(":");
        Name();
        count++;

        constructTree("var", count);
    }

    void SubProgs() {

        Fcn();
        int count = 1;
        while (nextToken.type == TokenType.FUNCTION) {
            Fcn();
            count++;
        }

        constructTree("subprogs", count);
    }

    void Fcn() {
        read("function");
        Name();
        read("(");
        Params();
        read(")");
        read(":");
        Name();
        read(";");
        Consts();
        Types();
        Dclns();
        Body();
        Name();
        read(";");

        constructTree("fcn", 8);
    }

    void Params() {
        Dcln();
        int count = 1;
        while (nextToken.type.type.equals(";")) {
            read(";");
            Dcln();
            count++;
        }

        constructTree("params", count);
    }

    void Body() {
        read("begin");
        Statement();
        int count = 1;
        while (nextToken.type.type.equals(";")) {
            read(";");
            Statement();
            count++;
        }
        read("end");

        constructTree("block", count);
    }

    void Statement() {
        int count = 0;
        switch (nextToken.type.type) {
            case "if":
                read("if");
                Expression();
                read("then");
                Statement();
                count = 2;
                if (nextToken.type.type.equals("else")) {
                    read("else");
                    Statement();
                    count++;
                }

                constructTree("if", count);
                break;
            case "for":
                read("for");
                read("(");
                ForStat();
                read(";");
                ForExp();
                read(";");
                ForStat();
                read(")");
                Statement();
                constructTree("for", 4);
                break;
            case "while":
                read("while");
                Expression();
                read("do");
                Statement();
                constructTree("while", 2);
                break;
            case "repeat":
                read("repeat");
                Statement();
                count = 1;
                while (nextToken.type.type.equals(";")) {
                    read(";");
                    Statement();
                    count++;
                }
                read("until");
                Expression();
                count++;
                constructTree("repeat", count);
                break;
            case "loop":
                read("loop");
                Statement();
                count = 1;
                while (nextToken.type.type.equals(";")) {
                    read(";");
                    Statement();
                    count++;
                }
                read("pool");
                constructTree("loop", count);
                break;
            case "output":
                read("output");
                read("(");
                OutEXp();
                count = 1;
                while (nextToken.type.type.equals(",")) {
                    read(",");
                    OutEXp();
                    count++;
                }
                read(")");
                constructTree("output", count);
                break;
            case "exit":
                read("exit");
                constructTree("exit", 0);
                break;
            case "return":
                read("return");
                Expression();
                constructTree("return", 1);
                break;
            case "read":
                read("read");
                read("(");
                Name();
                count = 1;
                while (nextToken.type.type.equals(",")) {
                    read(",");
                    Name();
                    count++;
                }
                read(")");
                constructTree("read", count);
                break;
            case "case":
                read("case");
                Expression();
                read("of");
                count = 1;
                count += Caseclauses();
                count += OtherwiseClause();
                read("end");
                constructTree("case", count);
                break;
            case "<identifier>":
                Assignment();
                break;
            case "begin":
                Body();
                break;
            default:
                constructTree("<null>", 0);
                break;
        }
    }

    int Caseclauses() {
        Caseclause();
        read(";");
        int count = 1;
        while (nextToken.type.type.equals("<integer>") || nextToken.type.type.equals("<char>")
                || nextToken.type.type.equals("<identifier>")) {
            Caseclause();
            read(";");
            count++;
        }

        return count;
    }

    void Caseclause() {
        CaseExpression();
        int count = 1;
        while (nextToken.type.type.equals(",")) {
            read(",");
            CaseExpression();
            count++;
        }
        read(":");
        Statement();
        count++;
        constructTree("case_clause", count);
    }

    void CaseExpression() {
        ConstValue();
        if (nextToken.type.type.equals("..")) {
            read("..");
            ConstValue();
            constructTree("..", 2);
        }
    }

    int OtherwiseClause() {
        if (nextToken.type.type.equals("otherwise")) {
            read("otherwise");
            Statement();
            constructTree("otherwise", 1);
            return 1;
        } else {
            return 0;
        }
    }

    void OutEXp() {
        if (nextToken.type.type.equals("<string>")) {
            StringNode();
        } else {
            Expression();
            constructTree("integer", 1);
        }
    }

    void StringNode() {
        read(TokenType.STRING);
    }

    void ForStat() {
        if (nextToken.type.type.equals(";")) {
            constructTree("<null>", 0);
        } else {
            Assignment();
        }
    }

    void ForExp() {
        if (nextToken.type.type.equals(";")) {
            constructTree("true", 0);
        } else {
            Expression();
        }
    }

    void Assignment() {
        switch (peek()) {
            case ":=":
                Name();
                read(":=");
                Expression();
                constructTree("assign", 2);
                break;
            case ":=:":
                Name();
                read(":=:");
                Name();
                constructTree("swap", 2);
                break;
            default:
                throw new Error("Error: " + peek() + ". Error Next: " + nextToken.type + ". Line: "
                        + nextToken.line_number + " .Column: " + nextToken.column_number);
        }
    }

    void Expression() {
        Term();
        if (nextToken.type.type.equals("<=") || nextToken.type.type.equals("<") || nextToken.type.type.equals(">=")
                || nextToken.type.type.equals(">") || nextToken.type.type.equals("=")
                || nextToken.type.type.equals("<>")) {
            switch (nextToken.type.type) {
                case "<=":
                    read("<=");
                    Term();
                    constructTree("<=", 2);
                    break;
                case "<":
                    read("<");
                    Term();
                    constructTree("<", 2);
                    break;
                case ">=":
                    read(">=");
                    Term();
                    constructTree(">=", 2);
                    break;
                case ">":
                    read(">");
                    Term();
                    constructTree(">", 2);
                    break;
                case "=":
                    read("=");
                    Term();
                    constructTree("=", 2);
                    break;
                case "<>":
                    read("<>");
                    Term();
                    constructTree("<>", 2);
                    break;
                default:
                    throw new Error("Error in Expression");
            }
        }
    }

    void Term() {
        Factor();
        while (nextToken.type.type.equals("+") || nextToken.type.type.equals("-") || nextToken.type.type.equals("or")) {
            switch (nextToken.type.type) {
                case "+":
                    read("+");
                    Factor();
                    constructTree("+", 2);
                    break;
                case "-":
                    read("-");
                    Factor();
                    constructTree("-", 2);
                    break;
                case "or":
                    read("or");
                    Factor();
                    constructTree("or", 2);
                    break;
                default:
                    System.out.println("Error in Term");
                    throw new Error();
            }
        }
    }

    void Factor() {
        Primary();
        while (nextToken.type.type.equals("*") || nextToken.type.type.equals("/") || nextToken.type.type.equals("and")
                || nextToken.type.type.equals("mod")) {
            switch (nextToken.type.type) {
                case "*":
                    read("*");
                    Factor();
                    constructTree("*", 2);
                    break;
                case "/":
                    read("/");
                    Factor();
                    constructTree("/", 2);
                    break;
                case "and":
                    read("and");
                    Factor();
                    constructTree("and", 2);
                    break;
                case "mod":
                    read("mod");
                    Factor();
                    constructTree("mod", 2);
                    break;
            }
        }
    }

    void Primary() {

        switch (nextToken.type.type) {
            case "<char>":
                read(TokenType.CHAR);
                break;
            case "<integer>":
                read(TokenType.INTEGER);
                break;
            case "eof":
                read("eof");
                constructTree("eof", 0);
                break;
            case "-":
                read("-");
                Primary();
                constructTree("-", 1);
                break;
            case "+":
                read("+");
                Primary();
                constructTree("+", 1);
                break;
            case "not":
                read("not");
                Primary();
                constructTree("not", 1);
                break;
            case "(":
                read("(");
                Expression();
                read(")");
                break;
            case "succ":
                read("succ");
                read("(");
                Expression();
                read(")");
                constructTree("succ", 1);
                break;
            case "pred":
                read("pred");
                read("(");
                Expression();
                read(")");
                constructTree("pred", 1);
                break;
            case "chr":
                read("chr");
                read("(");
                Expression();
                read(")");
                constructTree("chr", 1);
                break;
            case "ord":
                read("ord");
                read("(");
                Expression();
                read(")");
                constructTree("ord", 1);
                break;
            case "<identifier>":
                if (peek().equals("(")) {
                    Name();
                    read("(");
                    Expression();
                    int count = 2;
                    while (nextToken.type.type.equals(",")) {
                        read(",");
                        Expression();
                        count++;
                    }
                    read(")");

                    constructTree("call", count);
                } else {
                    Name();
                }
                break;
            default:
                throw new Error("Error while parsing: " + nextToken.type + ". Line: " + nextToken.line_number
                        + "Column: " + nextToken.column_number);
        }

    }

    void read(String type) {
        if (!nextToken.type.type.equals(type)) {
            throw new Error("Expected: ->|" + type + "|<-. " + "Found: " + nextToken.type + " " + nextToken.text
                    + ". Line: " + nextToken.line_number + ". Column: " + nextToken.column_number);
        }

        if (hasNext()) {
            getNextToken();
        }
    }

    void read(TokenType kind) {

        if (nextToken.type != kind) {
            throw new Error("Expected: " + kind + ". Found: " + nextToken.type + " " + nextToken.text + ". Line: "
                    + nextToken.line_number + ". Column: " + nextToken.column_number);
        }

        BinaryTreeNode node_1 = new BinaryTreeNode(nextToken.type.type);
        BinaryTreeNode node_2 = new BinaryTreeNode(nextToken.text);

        node_1.setLeftChild(node_2);

        node_1.setChildren(1);
        treeStack.push(node_1);

        getNextToken();

    }

    void constructTree(String node_label, int count) {
        BinaryTreeNode node = new BinaryTreeNode(node_label);
        BinaryTreeNode p = null;

        for (int j = 0; j < count; j++) {
            BinaryTreeNode c = treeStack.pop();
            if (p != null) {
                c.setRightChild(p);
            }
            p = c;
        }
        node.setLeftChild(p);
        node.setChildren(count);
        treeStack.push(node);
    }
}