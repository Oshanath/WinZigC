import java.util.*;

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

    private HashMap<Integer, HashMap<Input, Integer>> DFA;
    private HashMap<Integer, HashMap<Input, HashSet<Integer>>> NFA;

    private void generateNFAexample1(){
        HashMap<Input, HashSet<Integer>> row1 = new HashMap<>();
        row1.put(Input.EPSILON, new HashSet<Integer>(Arrays.asList(2)));
        row1.put(Input.DIGIT, new HashSet<Integer>(Arrays.asList(4)));
        NFA.put(1, row1);

        HashMap<Input, HashSet<Integer>> row2 = new HashMap<>();
        row2.put(Input.LETTER, new HashSet<Integer>(Arrays.asList(2)));
        row2.put(Input.EPSILON, new HashSet<Integer>(Arrays.asList(3)));
        NFA.put(2, row2);

        HashMap<Input, HashSet<Integer>> row3 = new HashMap<>();
        row3.put(Input.DIGIT, new HashSet<Integer>(Arrays.asList(6)));
        NFA.put(3, row3);

        HashMap<Input, HashSet<Integer>> row4 = new HashMap<>();
        row4.put(Input.EPSILON, new HashSet<Integer>(Arrays.asList(5)));
        NFA.put(4, row4);

        HashMap<Input, HashSet<Integer>> row5 = new HashMap<>();
        row5.put(Input.LETTER, new HashSet<Integer>(Arrays.asList(5)));
        row5.put(Input.EPSILON, new HashSet<Integer>(Arrays.asList(6)));
        NFA.put(5, row5);

        HashMap<Input, HashSet<Integer>> row6 = new HashMap<>();
        NFA.put(6, row6);
    }

    private void generateNFAexample2(){

        HashMap<Input, HashSet<Integer>> row0 = new HashMap<>();
        row0.put(Input.DIGIT, new HashSet<Integer>(Arrays.asList(1)));
        NFA.put(0, row0);

        HashMap<Input, HashSet<Integer>> row1 = new HashMap<>();
        row1.put(Input.LETTER, new HashSet<Integer>(Arrays.asList(2)));
        NFA.put(1, row1);

        HashMap<Input, HashSet<Integer>> row2 = new HashMap<>();
        row2.put(Input.EPSILON, new HashSet<Integer>(Arrays.asList(3)));
        NFA.put(2, row2);

        HashMap<Input, HashSet<Integer>> row3 = new HashMap<>();
        row3.put(Input.EPSILON, new HashSet<Integer>(Arrays.asList(8, 4, 6)));
        NFA.put(3, row3);

        HashMap<Input, HashSet<Integer>> row4 = new HashMap<>();
        row4.put(Input.LETTER, new HashSet<Integer>(Arrays.asList(5)));
        NFA.put(4, row4);

        HashMap<Input, HashSet<Integer>> row5 = new HashMap<>();
        row5.put(Input.EPSILON, new HashSet<Integer>(Arrays.asList(8)));
        NFA.put(5, row5);

        HashMap<Input, HashSet<Integer>> row6 = new HashMap<>();
        row6.put(Input.DIGIT, new HashSet<Integer>(Arrays.asList(7)));
        NFA.put(6, row6);

        HashMap<Input, HashSet<Integer>> row7 = new HashMap<>();
        row7.put(Input.EPSILON, new HashSet<Integer>(Arrays.asList(8)));
        NFA.put(7, row7);

        HashMap<Input, HashSet<Integer>> row8 = new HashMap<>();
        row8.put(Input.EPSILON, new HashSet<Integer>(Arrays.asList(9, 3)));
        NFA.put(8, row8);

        HashMap<Input, HashSet<Integer>> row9 = new HashMap<>();
        row9.put(Input.LETTER, new HashSet<Integer>(Arrays.asList(10)));
        NFA.put(9, row9);

        HashMap<Input, HashSet<Integer>> row10 = new HashMap<>();
        row10.put(Input.DIGIT, new HashSet<Integer>(Arrays.asList(11)));
        NFA.put(10, row10);

        HashMap<Input, HashSet<Integer>> row11 = new HashMap<>();
        NFA.put(11, row11);

    }

    public FiniteAutomata(){
        DFA = new HashMap<>();
        NFA = new HashMap<>();

        generateNFAexample2();

    }

    public void ConvertToDFA(){

    }

    public HashSet<Integer> findEpsilonClosure(int startState){
        HashSet<Integer> result = new HashSet<>();

        Stack<Integer> stack = new Stack<>();
        stack.push(startState);

        while(!stack.empty()){
            int currentState = stack.pop();
            result.add(currentState);

            for(Map.Entry<Input, HashSet<Integer>> entry : NFA.get(currentState).entrySet()){
                if(entry.getKey() == Input.EPSILON){
                    for(Integer value : entry.getValue()){
                        if (!result.contains(value)){
                            stack.push(value);
                        }
                    }
                }
            }
        }

        return result;
    }

    public void Minimize(){

    }

}
