/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kleenstheorem;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * This class represents a lambda NFA.
 *
 * @author ThienDinh
 */
public class LambdaNFA extends FiniteAutomaton {

    private TreeSet<Character> alphabet;

    /**
     * Constructor for LambdaNFA.
     *
     * @param stateSet a state set.
     * @param initialState a initial state.
     * @param finalStateSet a set of final states.
     * @param transitionList a transition list.
     * @param alphabet an alphabet.
     */
    public LambdaNFA(TreeSet<State> stateSet, State initialState,
            TreeSet<State> finalStateSet,
            TreeSet<Transition> transitionList, TreeSet<Character> alphabet) {
        super(stateSet, initialState, finalStateSet, transitionList);
        this.alphabet = (TreeSet<Character>) alphabet.clone();
    }

    /**
     * Separate lambda moves into a transitive closure.
     *
     * @return a list of lambda moves.
     */
    private TreeSet<Transition> getTransitiveClosure() {
        TreeSet<Transition> transitiveClosure = new TreeSet<>();
        int size = transitionList.size();
        // Separate lambda moves
        for (Transition trans: transitionList) {
            if (trans.getTransition().equals("")) {
                transitiveClosure.add(trans);
            }
        }
        return reconfigureTransitiveClosure(transitiveClosure);
    }

    /**
     * Get alphabet.
     *
     * @return alphabet.
     */
    public TreeSet<Character> getAlphabet() {
        return this.alphabet;
    }

    /**
     * Reconfigure the transitive closure in which new lambda moves are added.
     *
     * @param transClosure original transitive closure.
     * @return reconfigured transitive closure.
     */
    private TreeSet<Transition> reconfigureTransitiveClosure(TreeSet<Transition> transClosure) {
        TreeSet<Transition> reconfigClosure = new TreeSet<>(transClosure);
        // For each state, do breadth first seach from that state.
        // For each state after level 1, make new transition
        Queue<State> openList = new LinkedList<>();
        ArrayList<State> closedList = new ArrayList<>();
        for (State originalState: stateSet) {            
            openList.add(originalState);
            // For each iteration of the loop, one element of the open list is removed.
            while (!openList.isEmpty()) {
                //System.out.println("Open list:" + openList + ", Closed list:" + closedList);
                State consideringState = openList.remove();
                // If it's already in the closed list, ignore
                if (closedList.contains(consideringState)) {
                    continue;
                }
                // Get all state connect to this State from Transitive Closure
                for (Transition trans: transClosure) {
                    // 
                    if (trans.getFromState().equals(consideringState)) {
                        if (!closedList.contains(trans.getToState())) {
                            openList.add(trans.getToState());
                        }
                    }
                }
                closedList.add(consideringState);
            }

            for (int j = 1; j < closedList.size(); j++) {
                Transition trans = new Transition(originalState, "", closedList.get(j));
                if (!reconfigClosure.contains(trans)) {
                    reconfigClosure.add(trans);
                }
            }
            // Clear lists to work with the next state.
            closedList.clear();
            openList.clear();
        }
        return reconfigClosure;
    }

    /**
     * Get a list of letter transitions from the lambda NFA.
     *
     * @return a list of letter transitions.
     */
    private TreeSet<Transition> getLetterTransitions() {
        TreeSet<Transition> letterTransitions = new TreeSet<>();
        for (Transition trans : transitionList) {
            if (!trans.getTransition().equals("")) {
                letterTransitions.add(trans);
            }
        }
        return letterTransitions;
    }

    /**
     * Kill all lambda moves of this LambdaNFA and return the list of
     * transitions that does not contain lambda moves.
     *
     * @return a list of transitions.
     */
    private TreeSet<Transition> killLambdaMoves() {
        // Get transitive closure
        TreeSet<Transition> transitiveClosure = getTransitiveClosure();
        // Get letter moves
        TreeSet<Transition> letterMoves = getLetterTransitions();
        // A list to contain new transitions.
        TreeSet<Transition> newTransitions = new TreeSet<>();
        // For each letter move, consider the transitive closure.
        for (Transition letterTrans : letterMoves) {
            // Get the transition
            String transition = letterTrans.getTransition();
            // Get a set of going in states.
            TreeSet<State> goingInStates = new TreeSet<>();
            goingInStates.add(letterTrans.getFromState());
            for (Transition lambdaTrans : transitiveClosure) {
                if (lambdaTrans.getToState().equals(letterTrans.getFromState())) {
                    goingInStates.add(lambdaTrans.getFromState());
                }
            }
            // Get a set of going out states.
            TreeSet<State> goingOutStates = new TreeSet<>();
            goingOutStates.add(letterTrans.getToState());
            for (Transition lambdaTrans : transitiveClosure) {
                if (lambdaTrans.getFromState().equals(letterTrans.getToState())) {
                    goingOutStates.add(lambdaTrans.getToState());
                }
            }
            // Create new transition from this letter move.
            for (State inState : goingInStates) {
                for (State outState : goingOutStates) {
                    newTransitions.add(new Transition(inState, transition, outState));
                }
            }
        }
        return newTransitions;
    }

    /**
     * Convert lambdaNFA to NFA.
     *
     * @return the converted NFA.
     */
    public NFA createNFA() {
        TreeSet<State> newFinalStateSet = (TreeSet<State>) this.finalStateSet.clone();
        TreeSet<Transition> transitionList = this.killLambdaMoves();
        // Check if lambda is in the language of the Lambda NFA.
        // Perform depth first search.
        Stack<State> openList = new Stack<>();
        TreeSet<State> closedList = new TreeSet<>();
        openList.add(initialState);

        TreeSet<Transition> transClosure = getTransitiveClosure();
        boolean found = false;
        while (!openList.empty() && !found) {
            State examiningState = openList.pop();
            // Check all transitions of transitive closure that have matching beginning state.
            for (Transition trans: transClosure) {
                if (trans.getFromState().equals(examiningState)) {
                    State state = trans.getToState();
                    // If the to state is in final state set,
                    // then the initial state should be in final state set.
                    if (finalStateSet.contains(state)) {
                        newFinalStateSet.add(initialState);
                        found = true;
                        break;
                    }
                    // If the newly discovered state is already in the closed list,
                    // then we should not add it into open list to avoid recursively loop.
                    if (!closedList.contains(state)) {
                        openList.add(state);
                    }

                }
            }
            // Add the examiningState into closed list.
            closedList.add(examiningState);
        }

        NFA nfa = new NFA(this.stateSet, this.initialState,
                newFinalStateSet, transitionList, alphabet);
        return nfa;
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
