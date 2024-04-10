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
        PUNCTUATION,
        NONE
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

        HashMap<HashSet<Integer>, HashMap<Input, HashSet<Integer>>> midDFA = new HashMap<>();
        Input[] inputs = Input.values();
        Stack<HashSet<Integer>> stack = new Stack<>();
        stack.push(findEpsilonClosure(State.START.ordinal()));
        HashSet<HashSet<Integer>> processedStates = new HashSet<>();

        while(!stack.empty()){
            HashSet<Integer> currentState = stack.pop();
            processedStates.add(currentState);

            HashMap<Input, HashSet<Integer>> row = new HashMap<>();

            for(int i = 0; i < inputs.length - 1; i++){
                HashSet<Integer> nextStates = findNextStates(currentState, inputs[i]);
                HashSet<Integer> nextStatesClosure = findEpsilonClosure(nextStates);
                if(!processedStates.contains(nextStatesClosure) && !nextStatesClosure.isEmpty())
                    stack.add(nextStatesClosure);
                row.put(inputs[i], nextStatesClosure);
            }

            midDFA.put(currentState, row);

        }

        int counter = 0;
        HashMap<HashSet<Integer>, Integer> stateMapping = new HashMap<>();

        for(HashSet<Integer> state : midDFA.keySet()){
            stateMapping.put(state, counter);
            counter++;
        }

        for(HashSet<Integer> state : midDFA.keySet()){
            HashMap<Input, Integer> row = new HashMap<>();
            HashMap<Input, HashSet<Integer>> entry = midDFA.get(state);

            for(Input i : entry.keySet()){
                row.put(i, stateMapping.get(entry.get(i)));
            }
            DFA.put(stateMapping.get(state), row);
        }

    }

    public void printDFA(){

        for(Integer i : DFA.keySet()){
            System.out.print(i);
            System.out.print(": ");
            System.out.println(DFA.get(i));
        }

    }

    private void printMidDFA(HashMap<HashSet<Integer>, HashMap<Input, HashSet<Integer>>> midDFA){

        for(HashSet<Integer> key : midDFA.keySet()){
            for(Integer v : key){
                System.out.print(Integer.toString(v) + " ");
            }
            System.out.print(": ");

            for(Input i : midDFA.get(key).keySet()){
                System.out.print(Integer.toString(i.ordinal()) + "(");
                for(Integer c : midDFA.get(key).get(i)){
                    System.out.print(Integer.toString(c) + " ");
                }
                System.out.print(")  ");
            }

            System.out.println();
        }

    }

    private HashSet<Integer> findNextStates(HashSet<Integer> state, Input input){

        HashSet<Integer> result = new HashSet<>();

        for(Integer s : state){
            if(NFA.get(s).containsKey(input)){
                result.addAll(NFA.get(s).get(input));
            }
        }

        return result;

    }

    private HashSet<Integer> findEpsilonClosure(HashSet<Integer> states){
        HashSet<Integer> result = new HashSet<>();

        for(Integer state : states){
            HashSet<Integer> r = findEpsilonClosure(state);
            result.addAll(r);
        }

        return result;
    }

    private HashSet<Integer> findEpsilonClosure(int startState){
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
