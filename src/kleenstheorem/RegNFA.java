/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kleenstheorem;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Iterator;

/**
 * This class represents a regular expression NFA.
 *
 * @author ThienDinh
 */
public class RegNFA extends FiniteAutomaton {

    /**
     * Constructor for a Regular Expression NFA.
     *
     * @param stateSet a state set.
     * @param initialState an initial state.
     * @param finalStateSet a final state set.
     * @param transitionList a transition list.
     */
    public RegNFA(TreeSet<State> stateSet,
            State initialState, TreeSet<State> finalStateSet,
            TreeSet<Transition> transitionList) {
        super(stateSet, initialState, finalStateSet, transitionList);
        reconfigureNFA();
    }

    /**
     * Copy constructor for Regular Expression NFA.
     *
     * @param otherRegNFA other Regular Expression NFA.
     */
    public RegNFA(RegNFA otherRegNFA) {
        super(otherRegNFA.stateSet, otherRegNFA.initialState,
                otherRegNFA.finalStateSet, otherRegNFA.transitionList);
        reconfigureNFA();
    }

    /**
     * Reconfigure NFA to ensure it has proper initial state and final state.
     */
    private void reconfigureNFA() {
        // If there is a state go to the initial state.
        TreeSet<State> initInStates = getStatesGoIn(initialState);
        // If there are states that go out from the final state.
        TreeSet<State> finalOutStates = new TreeSet<>();
        Iterator<State> finSetIterator = finalStateSet.iterator();
        while (finSetIterator.hasNext()) {
            finalOutStates.addAll(getStatesGoOut(finSetIterator.next()));
        }
        // Create new initial state and lambda move if necessary.
        if (!initInStates.isEmpty()) {
            State newInitialState = new State(" Init");
            transitionList.add(new Transition(newInitialState, "", initialState));
            initialState = newInitialState;
            this.stateSet.add(newInitialState);
        }
        // Create new final state and lambda move if necessary.
        if (!finalOutStates.isEmpty()) {
            finSetIterator = finalStateSet.iterator();
            State newFinalState = new State("~Fin");
            while (finSetIterator.hasNext()) {
                transitionList.add(new Transition(finSetIterator.next(), "", newFinalState));
            }
            finalStateSet.clear();
            finalStateSet.add(newFinalState);
            this.stateSet.add(newFinalState);
        }
    }

    /**
     * Perform state elimination.
     *
     * @param eliminatedState a state to be eliminated.
     * @return a new NFA with a state eliminated or null if trying to eliminate
     * initial or final state.
     */
    private RegNFA eliminateState(State eliminatedState) {
        if (this.finalStateSet.contains(eliminatedState)
                || this.initialState.equals(eliminatedState)) {
            return null;
        }
        // Make a copy of the current NFA.
        RegNFA eliminatedRegNFA = new RegNFA(this);
        TreeSet<State> inStates = getStatesGoIn(eliminatedState);
        TreeSet<State> outStates = getStatesGoOut(eliminatedState);

        String loop = "";

        // Check for loop.
        // We just need to check for one list because the loop is in both list. 
        // Notice that there is only one loop.
        for (State inState: inStates) {
            // If eliminated state equals to going in state
            // then it's a loop.
            if (eliminatedState.equals(inState)) {
                loop += "(" + lookUpTransition(inState, inState).getTransition() + ")*";
            }
        }
        
        
        int inStates_size = inStates.size();
        int outStates_size = outStates.size();

        // Create a list of new transitions
        TreeSet<Transition> newTransitions = new TreeSet<>();
        for (State newInState: inStates) {
            for (State newOutState: outStates) {
                // Make new transitions excluding loop at eliminated state.
                if (!newInState.equals(eliminatedState)
                        && !newOutState.equals(eliminatedState)) {
                    boolean hasEmptyString = false;
                    String leftExpr = lookUpTransition(newInState, eliminatedState).getTransition();
                    if (leftExpr.contains("+")) {
                        leftExpr = "(" + leftExpr + ")";
                    }
                    String rightExpr = lookUpTransition(eliminatedState, newOutState).getTransition();
                    if (rightExpr.contains("+")) {
                        rightExpr = "(" + rightExpr + ")";
                    }
                    String newRegExpr
                            = leftExpr
                            + loop
                            + rightExpr;
                    Transition existingTrans = lookUpTransition(newInState, newOutState);
                    // If there exists a transition from newInState and newOutState,
                    // and that transition is not lambda.
                    if (existingTrans != null && !existingTrans.getTransition().equals("")) {
                        newRegExpr = existingTrans.getTransition() + " + " + newRegExpr;
                    }
                    //newRegExpr = "(" + newRegExpr + ")";
                    newTransitions.add(new Transition(newInState,
                            newRegExpr, newOutState));
                }
            }
        }
        //System.out.println("New transitions: " + newTransitions);

        // Remove old transitions from the new NFA.
        Iterator<Transition> iterator = eliminatedRegNFA.transitionList.iterator();
        while(iterator.hasNext()){
            Transition relTrans = iterator.next();
            if (relTrans.getFromState().equals(eliminatedState)
                    || relTrans.getToState().equals(eliminatedState)) {
                iterator.remove();
            }
        }

        // Add and merge new transitions to the new NFA.
        for (Transition newTrans: newTransitions) {
            // Replace the old transition by the new one.
            Iterator<Transition> TransIterator = eliminatedRegNFA.transitionList.iterator();
            while(TransIterator.hasNext()){
                Transition oldTrans = TransIterator.next();
                if (oldTrans.getFromState().equals(newTrans.getFromState())
                        && oldTrans.getToState().equals(newTrans.getToState())) {
                    TransIterator.remove();
                    // There is only one transition from a state to another state.
                    break;
                }
            }
            eliminatedRegNFA.transitionList.add(newTrans);
        }

        // Update state set
        eliminatedRegNFA.stateSet.remove(eliminatedState);
        return eliminatedRegNFA;
    }

    /**
     * Get the expression by eliminating the states in the NFA. The order of
     * eliminating states are arranged according to the array list.
     *
     * @return a regular expression.
     */
    public RegularExpression createRegularExpression() {
        RegNFA originalRegNFA = new RegNFA(this);
        // Get the states that should be removed.
        TreeSet<State> removingStates = originalRegNFA.stateSet;
        removingStates.remove(originalRegNFA.initialState);
        removingStates.removeAll(originalRegNFA.finalStateSet);
        // Remove them all        
        Iterator<State> iterator = removingStates.iterator();
        while(iterator.hasNext()) {
            originalRegNFA = originalRegNFA.eliminateState(iterator.next());
        }
        return new RegularExpression(originalRegNFA.lookUpTransition(initialState, finalStateSet.first()).getTransition());
    }

    /**
     * Get the expression by eliminating the states in the NFA. The order of
     * eliminating states are arranged according to the array list.
     *
     * @param removingOrder the order of removing states.
     * @return a regular expression.
     */
    public RegularExpression createRegularExpression(ArrayList<State> removingOrder) {
        RegNFA originalRegNFA = new RegNFA(this);
        // Get the states that should be removed.
        ArrayList<State> removingStates = removingOrder;
        removingStates.remove(originalRegNFA.initialState);
        removingStates.removeAll(originalRegNFA.finalStateSet);
        // Remove them all
        int size = removingStates.size();
        Iterator<State> iterator = removingStates.iterator();
        while(iterator.hasNext()) {
            originalRegNFA = originalRegNFA.eliminateState(iterator.next());
        }
        return new RegularExpression(originalRegNFA.lookUpTransition(initialState, finalStateSet.first()).getTransition());
    }

}
