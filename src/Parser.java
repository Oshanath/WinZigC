
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

// binary tree node to represent nodes in AST
class BinaryTreeNode{

    private String node_label;

    private BinaryTreeNode left;

    private BinaryTreeNode right;

    private int childCount;

    BinaryTreeNode(String node_label){
        this.node_label = node_label;
    }

    public void setLeftChild(BinaryTreeNode node) {
        left = node;
    }

    public void setRightChild(BinaryTreeNode node) {
        right = node;
    }

    public void setChildCount(int c) {
        childCount = c;
    }

    // Pre Order Traverse with indented printing
    public void PreOrderTraverse(int indentSize){
        for(int i = 0 ; i < indentSize; i++ ) System.out.print(". ");
        System.out.print(this.node_label);
        System.out.println("("+ childCount +")");

        if(this.left != null){
            left.PreOrderTraverse(indentSize + 1);
        }

        if(this.right != null){
            right.PreOrderTraverse(indentSize);
        }
    }

    // Pre Order Traverse with indented printing and writing to an output file
    public void PreOrderTraverse(int indentSize, FileWriter writer) throws IOException {
        for(int i = 0 ; i < indentSize; i++ ) {
//            System.out.print(". ");
            writer.write(". ");
        }
//        System.out.print(this.node_label);
//        System.out.println("("+ childCount +")");
        writer.write(this.node_label + "(" + this.childCount + ")\n");

        if(this.left != null){
            left.PreOrderTraverse(indentSize + 1,writer);
        }

        if(this.right != null){
            right.PreOrderTraverse(indentSize,writer);
        }
    }
}

public class Parser{

    private Stack<BinaryTreeNode> binaryTreeStack;

    private List<Token> tokenStream;

    private int tokenIndex;

    private final FileWriter fileWriter;

    Token nextToken;

    boolean hasNext(){
        return tokenIndex <= tokenStream.size()-2;
    }

    String peek(){
        if(tokenIndex <= tokenStream.size()-1){
            return tokenStream.get(tokenIndex).type.type;
        }
        System.out.println("TOKENS ARE OVER");
        throw new Error();
    }

    // set the next token and increment the index
    void getNextToken(){
        nextToken = tokenStream.get(tokenIndex);
        tokenIndex++;
    }

    Parser(List<Token> tokenStream) throws IOException {
        this.tokenStream = tokenStream;
        binaryTreeStack = new Stack<>();
        this.fileWriter = null;
        getNextToken();
        winZigAST();
    }

    Parser(List<Token> tokenStream, String outputFilePath) throws IOException {
        this.tokenStream = tokenStream;
        binaryTreeStack = new Stack<>();
        this.fileWriter = new FileWriter(outputFilePath);
        getNextToken();
        winZigAST();
        this.fileWriter.close();
    }

    // look what's the next token without incrementing the token index
    public Token peekToken(){
        return tokenStream.get(tokenIndex);
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

//        System.out.println(tokenStream.get(1));
//        System.out.println(tokenStream.get(tokenStream.size()-2));


        // change count accordingly
        constructTree("program" ,7);

//        for(ASTNode node : treeStack){
//            node.DFTraverse(0);
//            System.out.println("---------------------");
//        }

        for(BinaryTreeNode node : binaryTreeStack){
            if(this.fileWriter == null){
                node.PreOrderTraverse(0);
            }else {
                node.PreOrderTraverse(0,this.fileWriter);
            }

        }

    }

    void Name(){
        read(TokenType.IDENTIFIER);
    }

    void Consts(){
        if(nextToken.type.type.equals("const")){
            read("const");
            int list_count = 1;
            Const();
            while(!nextToken.type.type.equals(";")){
                read(",");
                Const();
                list_count += 1;
            }
            read(";");
            constructTree("consts" ,list_count);
        }else{
            constructTree("consts" ,0);
        }
    }

    void Const(){
        Name();
        read("=");
        ConstValue();

        constructTree("const", 2);
    }

    void ConstValue(){
        // skip <char> or <integer> but create a node for <identifier>
        if(nextToken.type.type.equals("<char>") || nextToken.type.type.equals("<integer>") ){
            read(nextToken.type);
        }

        if(nextToken.type.type.equals("<identifier>")){
            Name();
        }
    }

    void Types(){
        if(nextToken.type.type.equals("type")){
            read("type");
            int count = 0;
            while(nextToken.type.type.equals("<identifier>")){
                Type();
                read(";");
                count++;
            }
            constructTree("types", count);

        }else{
            constructTree("types", 0);
        }
    }

    void Type(){
        if(nextToken.type.type.equals("<identifier>")){
            Name();
            read("=");
            LitList();

            constructTree("type", 2);
        }
    }

    void LitList(){
        read("(");
        Name();
        int count = 1;
        while(!nextToken.type.type.equals(")")){
            read(",");
            Name();
            count++;
        }
        read(")");
        constructTree("lit", count);
    }

    void Dclns(){
        if(nextToken.type.type.equals("var")){
            read("var");
            Dcln();
            read(";");
            int count = 1;
            while(nextToken.type.type.equals("<identifier>")){
                Dcln();
                read(";");
                count++;
            }

            constructTree("dclns", count);
        }else{
            constructTree("dclns", 0);
        }
    }

    void Dcln(){
        Name();
        int count = 1;
        while(!nextToken.type.type.equals(":")){
            read(",");
            Name();
            count++;
        }
        read(":");
        Name();
        count++;

        constructTree("var", count);
    }

    void SubProgs(){

        Fcn();
        int count = 1;
        while(nextToken.type == TokenType.FUNCTION){
            Fcn();
            count++;
        }

        constructTree("subprogs", count);
    }

    void Fcn(){
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

        constructTree("fcn",8);
    }

    void Params(){
        Dcln();
        int count = 1;
        while(nextToken.type.type.equals(";")){
            read(";");
            Dcln();
            count++;
        }

        constructTree("params", count);
    }

    void Body(){
        read("begin");
        Statement();
        int count = 1;
        while(nextToken.type.type.equals(";")){
            read(";");
            Statement();
            count++;
        }
        read("end");

        constructTree("block", count);
    }

    void Statement(){
        int count = 0;
        switch(nextToken.type.type){
            case "if":
                read("if");
                Expression();
                read("then");
                Statement();
                count = 2;
                if(nextToken.type.type.equals("else")){
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
                constructTree("while",2);
                break;
            case "repeat":
                read("repeat");
                Statement();
                count = 1;
                while(nextToken.type.type.equals(";")){
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
                while(nextToken.type.type.equals(";")){
                    read(";");
                    Statement();
                    count++;
                }
                read("pool");
                constructTree("loop",count);
                break;
            case "output":
                read("output");
                read("(");
                OutEXp();
                count = 1;
                // out exp list
                while(nextToken.type.type.equals(",")){
                    read(",");
                    OutEXp();
                    count++;
                }
                read(")");
                constructTree("output",count);
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
                while(nextToken.type.type.equals(",")){
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
                constructTree("<null>",0);
                break;
        }
    }

    int Caseclauses(){
        Caseclause();
        read(";");
        int count = 1;
        while(nextToken.type.type.equals("<integer>")  || nextToken.type.type.equals("<char>")  || nextToken.type.type.equals("<identifier>")){
            Caseclause();
            read(";");
            count++;
        }

        return count;
    }

    void Caseclause(){
        CaseExpression();
        int count = 1;
        while(nextToken.type.type.equals(",")){
            read(",");
            CaseExpression();
            count++;
        }
        read(":");
        Statement();
        count++;
        constructTree("case_clause", count);
    }

    void CaseExpression(){
        ConstValue();
        if(nextToken.type.type.equals("..")){
            read("..");
            ConstValue();
            constructTree("..",2);
        }
    }

    int  OtherwiseClause(){
        if(nextToken.type.type.equals("otherwise")){
            read("otherwise");
            Statement();
            constructTree("otherwise",1);
            return 1;
        }else{
            return 0;
        }
    }

    void OutEXp(){
        if(nextToken.type.type.equals("<string>")){
            StringNode();
        }else{
            Expression();
            constructTree("integer", 1);
        }
    }

    void StringNode(){
        read(TokenType.STRING);
    }

    void ForStat(){
        if(nextToken.type.type.equals(";")){
            constructTree("<null>",0);
        }else{
            Assignment();
        }
    }

    void ForExp(){
        if(nextToken.type.type.equals(";")){
            constructTree("true",0);
        }else{
            Expression();
        }
    }

    void Assignment(){
        switch(peek()){
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
                System.out.println("ERROR PEEK: "+peek());
                System.out.println("ERROR NEXT: "+nextToken.type);
                System.out.println("LINE NO: " + nextToken.line_number);
                System.out.println("CLMN NO: " + nextToken.column_number);
                throw new Error();
        }
    }

    void Expression(){
        Term();
        if(nextToken.type.type.equals("<=") || nextToken.type.type.equals("<") || nextToken.type.type.equals(">=") || nextToken.type.type.equals(">") || nextToken.type.type.equals("=") || nextToken.type.type.equals("<>")){
            switch(nextToken.type.type){
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
                    System.out.println("ERROR in Expression");
                    System.out.println("TOKEN WAS: "+nextToken.type);
                    System.out.println("LINE NO: " + nextToken.line_number);
                    System.out.println("CLMN NO: " + nextToken.column_number);
                    throw new Error();
            }
        }
    }

    void Term() {
        Factor();
        while (nextToken.type.type.equals("+") || nextToken.type.type.equals("-") || nextToken.type.type.equals("or") ) {
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
                    System.out.println("ERROR in Term");
                    System.out.println("LINE NO: " + nextToken.line_number);
                    System.out.println("CLMN NO: " + nextToken.column_number);
                    throw new Error();
            }
        }
    }

    void Factor(){
        Primary();
        while(nextToken.type.type.equals("*") || nextToken.type.type.equals("/") ||nextToken.type.type.equals("and") ||nextToken.type.type.equals("mod") ){
            switch(nextToken.type.type){
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

    void Primary(){

        switch(nextToken.type.type){
            case "<char>":
                read(TokenType.CHAR); break;
            case "<integer>":
//                System.out.println(nextToken.type + " "+nextToken.text);
                read(TokenType.INTEGER); break;
            case "eof":
                read("eof");
                constructTree("eof",0);
                break;
            case "-":
                read("-");
                Primary();
                constructTree("-",1);
                break;
            case "+":
                read("+");
                Primary();
                constructTree("+",1);
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
                if(peek().equals("(")){
                    Name();
                    read("(");
                    Expression();
                    int count = 2;
                    while(nextToken.type.type.equals(",")){
                        read(",");
                        Expression();
                        count++;
                    }
                    read(")");

                    constructTree("call", count);
                }else{
                    Name();
                }
                break;
            default:
                System.out.println("ERROR WHILE PARSING: " + nextToken.type);
                System.out.println("LINE NO: " + nextToken.line_number);
                System.out.println("CLMN NO: " + nextToken.column_number);
                throw new Error();
        }

    }

    void read(String type){
        if(!nextToken.type.type.equals(type)){
            System.out.println("EXPECTED: ->|"+type+"|<-");
            System.out.println("FOUND: " + nextToken.type + " " + nextToken.text);
            System.out.println("LINE NO: " + nextToken.line_number);
            System.out.println("CLMN NO: " + nextToken.column_number);
            throw new Error();
        }

        if(hasNext()){
            getNextToken();
        }
    }

    void read(TokenType kind){

        if(nextToken.type != kind){
            System.out.println("EXPECTED: "+kind);
            System.out.println("FOUND: "+nextToken.type+" "+nextToken.text);
            System.out.println("LINE NO: " + nextToken.line_number);
            System.out.println("CLMN NO: " + nextToken.column_number);
            throw new Error();
        }

//        ASTNode node_1 = new ASTNode(nextToken.type);
//
//        ASTNode node_2 = new ASTNode(nextToken.text);
//        node_1.addChildNode(node_2);
//
//        treeStack.push(node_1);

        BinaryTreeNode node_1 = new BinaryTreeNode(nextToken.type.type);
        BinaryTreeNode node_2 = new BinaryTreeNode(nextToken.text);

        node_1.setLeftChild(node_2);

        node_1.setChildCount(1);
        binaryTreeStack.push(node_1);

        getNextToken();

    }


//    void constructTree(String node_label, int count){
//        ASTNode node = new ASTNode(node_label);
//        for(int i = 0; i < count ;i++){
//            node.addChildAtIndex(0,treeStack.pop());
//        }
//        treeStack.push(node);
//    }

    void constructTree(String node_label, int count){
        BinaryTreeNode node = new BinaryTreeNode(node_label);
        BinaryTreeNode p = null;

        for(int j = 0; j < count; j++){
            BinaryTreeNode c = binaryTreeStack.pop();
            if(p != null){
                c.setRightChild(p);
            }
            p = c;
        }
        node.setLeftChild(p);
        node.setChildCount(count);
        binaryTreeStack.push(node);
    }
}