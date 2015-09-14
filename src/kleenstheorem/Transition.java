/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kleenstheorem;

/**
 * This class represents transition.
 *
 * @author ThienDinh
 */
public class Transition implements Comparable {

    private State fromState;
    private String transition;
    private State toState;

    /**
     * Constructor for a Transition.
     *
     * @param iState a beginning state.
     * @param expr an expression string.
     * @param fState an ending state.
     */
    public Transition(State iState, String expr, State fState) {
        this.fromState = new State(iState.getDescription());
        this.transition = expr;
        this.toState = new State(fState.getDescription());
    }

    /**
     * Get the from state.
     *
     * @return the from state.
     */
    public State getFromState() {
        return fromState;
    }

    /**
     * Get the transition.
     *
     * @return the transition.
     */
    public String getTransition() {
        return transition;
    }

    /**
     * Get the to state.
     *
     * @return the to state.
     */
    public State getToState() {
        return toState;
    }

    /**
     * Compare two transition relation according to the order of their elements.
     *
     * @param t other transition.
     * @return an integer.
     */
    public int compareTo(Object t) {
        Transition other = (Transition) t;
        if (fromState.compareTo(other.fromState) == 0) {
            if (toState.compareTo(other.toState) == 0) {
                return transition.compareTo(other.transition);
            } else {
                return toState.compareTo(other.toState);
            }
        } else {
            return fromState.compareTo(other.fromState);
        }
    }

    /**
     * Check if two Transitions are equal.
     *
     * @param t other transition.
     * @return true if they are; otherwise, false.
     */
    @Override
    public boolean equals(Object t) {
        Transition other = (Transition) t;
        return (fromState.equals(other.fromState)
                && transition.equals(other.transition)
                && toState.equals(other.toState));
    }

    /**
     * Update the transition.
     *
     * @param newTrans new transition.
     */
    public void updateTransition(Transition newTrans) {
        this.fromState = newTrans.fromState;
        this.transition = newTrans.transition;
        this.toState = newTrans.toState;
    }

    /**
     * Get a string representation.
     *
     * @return a string.
     */
    public String toString() {
        if (transition.equals("")) {
            return String.format("<%s,%s,%s>", fromState, "λ", toState);
        }
        return String.format("<%s,%s,%s>", fromState, transition, toState);
    }

}
