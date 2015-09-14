/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kleenstheorem;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 *
 * @author ThienDinh
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String[] splits = "a..b".split("\\.");
        System.out.println(splits.length + " == " + 3);
        
        // TODO code application logic here
        // All examples are from Barrington's book Volume II
        // Example page 14-50 
         String[] expressions = {
           "a", "(ab+a)(bb+ba)*(ab+aa)", "ba+ab", "(ab+ba)*+bb"
        };
        RegularExpression reg = new RegularExpression(expressions[1]);
        System.out.println("Regular expression:\n" + reg);

        System.out.println("==============================");
        LambdaNFA lambNFA = reg.createLambdaNFA();
        System.out.println("Lambda NFA from the regular expression:\n" + lambNFA);

        System.out.println("==============================");
        NFA nfa = lambNFA.createNFA();
        System.out.println("NFA from the lambda NFA:\n" + nfa);

        System.out.println("==============================");
        DFA dfa = nfa.createDFA();
        System.out.println("DFA from the NFA:\n" + dfa);

        System.out.println("==============================");
        // Example page 14-59. Result is in the last sentence of page 14.60
        State[] set = new State[]{
            new State("00"), new State("01"), new State("10"), new State("11")
        };
        TreeSet<State> stateSet = new TreeSet<>();
        stateSet.add(set[0]);
        stateSet.add(set[1]);
        stateSet.add(set[2]);
        stateSet.add(set[3]);
        State initialState = set[0];
        TreeSet<State> finalStateSet = new TreeSet();
        finalStateSet.add(set[0]);
        TreeSet<Transition> transList = new TreeSet<>();
        transList.add(new Transition(set[0], "a", set[2]));
        transList.add(new Transition(set[0], "b", set[1]));
        transList.add(new Transition(set[1], "a", set[3]));
        transList.add(new Transition(set[1], "b", set[0]));
        transList.add(new Transition(set[2], "a", set[0]));
        transList.add(new Transition(set[2], "b", set[3]));
        transList.add(new Transition(set[3], "a", set[1]));
        transList.add(new Transition(set[3], "b", set[2]));
        RegNFA regNFA = new RegNFA(stateSet, initialState, finalStateSet, transList);
        System.out.println("RegNFA:\n" + regNFA);
        System.out.println("==============================");
        ArrayList<State> removingOrder = new ArrayList<>();
        removingOrder.add(set[1]);
        removingOrder.add(set[2]);
        removingOrder.add(set[3]);
        removingOrder.add(set[0]);
        System.out.println("Regular expression from the regNFA:\n" + regNFA.createRegularExpression(removingOrder));
    }

}
