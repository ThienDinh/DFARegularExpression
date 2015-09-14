/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kleenstheorem;

import java.util.TreeSet;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * This class represents a finite automaton.
 *
 * @author ThienDinh
 */
public abstract class FiniteAutomaton {

    // State set
    protected TreeSet<State> stateSet;

    // Initial state
    protected State initialState;

    // Final state set
    protected TreeSet<State> finalStateSet;

    // Relation transition for NFA
    protected TreeSet<Transition> transitionList;

    /**
     * Constructor for a finite automaton.
     *
     * @param stateSet a state set.
     * @param initialState an initial state.
     * @param finalStateSet a final state set.
     * @param transitionList a transition list.
     */
    public FiniteAutomaton(TreeSet<State> stateSet,
            State initialState, TreeSet<State> finalStateSet,
            TreeSet<Transition> transitionList) {
        this.stateSet = (TreeSet<State>) stateSet.clone();
        this.initialState = initialState;
        this.finalStateSet = (TreeSet<State>) finalStateSet.clone();
        this.transitionList = (TreeSet<Transition>) transitionList.clone();
    }

    /**
     * Copy constructor for a finite automaton.
     *
     * @param otherFA other automaton.
     */
    public FiniteAutomaton(FiniteAutomaton otherFA) {
        stateSet = (TreeSet<State>) otherFA.stateSet.clone();
        initialState = otherFA.initialState;
        finalStateSet = (TreeSet<State>) otherFA.finalStateSet.clone();
        transitionList = (TreeSet<Transition>) otherFA.transitionList.clone();
    }

     /**
     * Update a state in the finite automaton. It will also update transitions.
     *
     * @param oldState an old state.
     * @param newState a new state.
     */
    public void updateState(State oldState, State newState) {
        // Make change in state set.
        for (State state: stateSet) {
            if (state.equals(oldState)) {
                state.setDescription(newState.getDescription());
            }
        }
        // Make change in initial state.
        if (initialState.equals(oldState)) {
            initialState.setDescription(newState.getDescription());
        }
        // Make change in final state set.
        for (State state : finalStateSet) {
            if (state.equals(oldState)) {
                state.setDescription(newState.getDescription());
            }
        }
        // Make change in transition list.
        for (Transition trans: transitionList) {
            if (trans.getFromState().equals(oldState)) {
                trans.getFromState().setDescription(newState.getDescription());
            }
            if (trans.getToState().equals(oldState)) {
                trans.getToState().setDescription(newState.getDescription());
            }
        }
    }

    /**
     * Add a transition into the regular expression NFA.
     *
     * @param fromState a state in NFA.
     * @param expr a regular expression move.
     * @param toState another state in NFA.
     */
    public void addTransition(State fromState, String expr, State toState) {
        Transition relTrans = new Transition(fromState, expr, toState);
        transitionList.add(relTrans);
    }

    /**
     * Get all states that go in a state.
     *
     * @param toState a state.
     * @return list of states that go to this state.
     */
    public TreeSet<State> getStatesGoIn(State toState) {
        TreeSet<State> listOfStates = new TreeSet<>();
        Iterator<Transition> iter = transitionList.iterator();
        while (iter.hasNext()) {
            Transition trans = iter.next();
            if (trans.getToState().equals(toState)) {
                listOfStates.add(trans.getFromState());
            }
        }
        return listOfStates;
    }

    /**
     * Get all states that go out a state.
     *
     * @param fromState a state.
     * @return list of states that go out from this state.
     */
    public TreeSet<State> getStatesGoOut(State fromState) {
        TreeSet<State> listOfStates = new TreeSet<>();
        Iterator<Transition> iter = transitionList.iterator();
        while (iter.hasNext()) {
            Transition trans = iter.next();
            if (trans.getFromState().equals(fromState)) {
                listOfStates.add(trans.getToState());
            }
        }
        return listOfStates;
    }

    /**
     * Look up a transition in the NFA.
     *
     * @param fromState a state.
     * @param toState a state.
     * @return a relation transition.
     */
    public Transition lookUpTransition(State fromState, State toState) {
        for (Transition relTrans: transitionList) {
            if (relTrans.getFromState().equals(fromState)
                    && relTrans.getToState().equals(toState)) {
                return relTrans;
            }
        }
        return null;
    }

    /**
     * Get a state by its description.
     *
     * @param stateDescription a description of the searching state.
     * @return a matching state or null if none is found.
     */
    public State lookUpState(String stateDescription) {
        for (State aState: stateSet) {
            if (aState.getDescription().equals(stateDescription)) {
                return aState;
            }
        }
        return null;
    }
    
    private String formatStateSet(String stateString){
        return stateString
                .replace("[]", "∅")
                .replace("[", "{")
                .replace("]", "}");
    }

    /**
     * Give a string representation of the finite automaton.
     *
     * @return a string.
     */
    public String toString() {
        return "State set: " + formatStateSet(stateSet.toString())
                + "\nInitial state: " + formatStateSet(initialState.toString())
                + "\nFinal state set: " + formatStateSet(finalStateSet.toString())
                + "\nRelation transitions: " + formatStateSet(transitionList.toString());
    }

}
