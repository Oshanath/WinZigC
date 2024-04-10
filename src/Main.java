import java.util.HashSet;

public class Main {
    public static void main(String[] args) {

        FiniteAutomata dfa = new FiniteAutomata();

        HashSet<Integer> closure;

        for(int i = 0; i < 12; i++){
            closure = dfa.findEpsilonClosure(i);
            System.out.print("State " + Integer.toString(i) + ": ");

            for (Integer integer : closure) {
                System.out.print(integer + " ");
            }
            System.out.println();
        }

    }
}