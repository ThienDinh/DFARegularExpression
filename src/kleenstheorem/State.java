/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kleenstheorem;

/**
 * This class represents a state.
 * @author ThienDinh
 */
public class State implements Comparable {

    private String description;

    /**
     * Constructor for a state.
     *
     * @param description a string.
     */
    public State(String description) {
        this.description = description;
    }

    /**
     * Set the state's description.
     *
     * @param descr a string.
     */
    public void setDescription(String descr) {
        this.description = descr;
    }

    /**
     * Get the state's description.
     *
     * @return a string description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get a string representation.
     *
     * @return a string.
     */
    public String toString() {
        return description;
    }

    /**
     * Check if two states are equal.
     *
     * @param t other state.
     * @return true if they are; otherwise, false.
     */
    public boolean equals(Object t) {
        State otherState = (State) t;
        return description.equals(otherState.description);
    }

    /**
     * Compare two states according to its description.
     *
     * @param t other state.
     * @return an integer.
     */
    @Override
    public int compareTo(Object t) {
        State otherState = (State) t;
        return description.compareTo(otherState.description);
    }

}
