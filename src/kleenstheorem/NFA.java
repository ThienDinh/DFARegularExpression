/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kleenstheorem;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

/**
 * This class represents an NFA.
 *
 * @author ThienDinh
 */
public class NFA extends FiniteAutomaton {

    private TreeSet<Character> alphabet;

    /**
     * Constructor for NFA.
     *
     * @param stateSet a state set.
     * @param initialState an initial state.
     * @param finalStateSet a final state set.
     * @param transitionList a transition list.
     * @param alphabet an alphabet.
     */
    public NFA(TreeSet<State> stateSet, State initialState,
            TreeSet<State> finalStateSet,
            TreeSet<Transition> transitionList, TreeSet<Character> alphabet) {
        super(stateSet, initialState, finalStateSet, transitionList);
        this.alphabet = (TreeSet<Character>) alphabet.clone();
    }

    /**
     * Create a DFA from this NFA.
     *
     * @return a DFA.
     */
    public DFA createDFA() {
        // What state of the machine after it takes a empty string.
        // More than likely, it is the initial state.
        // Start from that state.
        State initialState = new State("(" + this.initialState.getDescription() + ")");
        TreeSet<State> finalStateSet = new TreeSet<>();
        // Every state has a fixed number of moves to other state.
        // There will be an empty set state.
        TreeSet<State> stateSet = new TreeSet<>();
        TreeSet<Transition> transList = new TreeSet<>();

        // Prepare open list and closed list for breadth first search
        Queue<TreeSet<State>> openList = new LinkedList<>();
        ArrayList<TreeSet<State>> closedList = new ArrayList<>();
        TreeSet<State> startSet = new TreeSet<>();
        startSet.add(new State(this.initialState.getDescription()));
        openList.add(startSet);

        // While the open list is not empty.
        while (!openList.isEmpty()) {
            TreeSet<State> consideringSet = openList.remove();
            State oldState = new State(consideringSet.toString());
            // Check if it's already in closed list. If so, skip.
            if (closedList.contains(consideringSet)) {
                continue;
            }
            // Flag of checking if the set has any final state.
            boolean hasFinal = false;

            // Begin the examination. Explore its neighbors.
            // The number of sets will be produced equals to |alphabet|.
            for (Character ch : this.alphabet) {
                // newSet is to store discovered set.
                TreeSet<State> newSet = new TreeSet<>();
                for (State state: consideringSet) {
                    // If this set has a state that is in the final state of the NFA.
                    if (this.finalStateSet.contains(state)) {
                        hasFinal = true;
                    }
                    // Get end state that has the matching begin state and transition letter.
                    for (Transition trans : transitionList) {
                        if (trans.getFromState().equals(state)
                                && trans.getTransition().equals(String.valueOf(ch))) {
                            newSet.add(new State(trans.getToState().getDescription()));
                        }
                    }
                }
                // After gathering all appropriate states into new set.
                // Check this set before add it into openlist
                if (!closedList.contains(newSet)) {
                    openList.add(newSet);
                }
                // Make a new state for DFA. The description for this state
                // is a set of states.
                // Add new transition into DFA.
                State newState = new State(newSet.toString());
                Transition trans = new Transition(oldState, String.valueOf(ch), newState);
                transList.add(trans);
            }
            closedList.add(consideringSet);
            // Every set that is put in closed list correspond to each state
            // in the machine.
            stateSet.add(new State(consideringSet.toString()));
            // If this set has final state, then it should be in final set.
            if (hasFinal) {
                finalStateSet.add(new State(consideringSet.toString()));
            }
        }
        // Open list is empty.
        return new DFA(stateSet, initialState, finalStateSet, transList, alphabet);
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
    public String toString() {
        String str = super.toString();
        str += "\nAlphabet: " + alphabet.toString();;
        return str;
    }
}
