/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kleenstheorem;

import java.util.TreeSet;

/**
 * This class represents a DFA.
 *
 * @author ThienDinh
 */
public class DFA extends FiniteAutomaton {

    private TreeSet<Character> alphabet;

    /**
     * Constructor for the DFA.
     *
     * @param stateSet a state set.
     * @param initialState an initial state.
     * @param finalStateSet a final state set.
     * @param transitionList a transition list.
     * @param alphabet an alphabet list.
     */
    public DFA(TreeSet<State> stateSet, State initialState,
            TreeSet<State> finalStateSet,
            TreeSet<Transition> transitionList, TreeSet<Character> alphabet) {
        super(stateSet, initialState, finalStateSet, transitionList);
        this.alphabet = (TreeSet<Character>) alphabet.clone();
    }

    /**
     * Create a RegNFA from the DFA.
     *
     * @return a RegNFA.
     */
    public RegNFA createRegNFA() {
        RegNFA regNFA = new RegNFA(this.stateSet, initialState,
                this.finalStateSet, this.transitionList);
        return regNFA;
    }

    /**
     * Give a string representation of the machine.
     *
     * @return a string.
     */
    @Override
    public String toString() {
        String str = super.toString();
        str += "\nAlphabet: " + alphabet.toString();
        return str;
    }
}
