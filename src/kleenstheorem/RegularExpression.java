/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kleenstheorem;

import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;

/**
 * This class represents a regular expression.
 *
 * @author ThienDinh
 */
public class RegularExpression {

    public static final int RE_KLEENSTAR = 0;
    public static final int RE_UNION = 1;
    public static final int RE_CONCATENATION = 2;
    public static final int RE_ATOM = 3;

    private int expr_type;
    private String strRepresentation;

    /**
     * Constructor for a RegularExpression.
     *
     * @param str a string representation.
     */
    public RegularExpression(String str) {
        strRepresentation = str;
        this.strRepresentation = strRepresentation.replaceAll(" ", "");
        Stack<Character> parenthRecord = new Stack<>();
        int strLength = strRepresentation.length();
        if (strLength == 0) {
            return;
        }

        // Remove unnecessary parenthese
        boolean uselessParenth = false;
        if (strRepresentation.charAt(0) == '(' && strRepresentation.charAt(strLength - 1) == ')') {
            for (int i = 0; i < strLength; i++) {
                Character ch = this.strRepresentation.charAt(i);
                // If first parentheses, then add to stack until the matching
                // parentheses found.
                if (ch.equals('(')) {
                    parenthRecord.push(ch);
                }
                if (ch.equals(')')) {
                    parenthRecord.pop();
                }
                // Parentheses stack is empty and is emptied by the closing
                // parentheses at the end of the string.
                if (parenthRecord.isEmpty()) {
                    if (i == strLength - 1) {
                        uselessParenth = true;
                    }
                    break;
                }
            }
        }
        if (uselessParenth) {
            this.strRepresentation = strRepresentation.substring(1, strLength - 1);
        }
        strLength = strRepresentation.length();

        // The length 1 string must be the atom.
        if (strLength == 1) {
            expr_type = RegularExpression.RE_ATOM;
            return;
        }
        // Length 2 with first is letter and 2nd is star is Kleen Star.
        if ((strLength == 2 && strRepresentation.charAt(1) == '*')) {
            expr_type = RegularExpression.RE_KLEENSTAR;
            return;
        }

        // Check if this expression is under Kleen Star.
        // First character must be (
        // Character before the last character must be )
        // The last character must be *
        // The stack must not be empty before it reaches the matching closing parentheses.
        boolean isKleenStar = false;
        if (strRepresentation.charAt(0) == '('
                && strRepresentation.charAt(strLength - 1) == '*') {
            // Check if the expression are connected by union, concatenation, respectively.
            for (int i = 0; i < strLength - 1; i++) {
                Character ch = this.strRepresentation.charAt(i);
                // If first parentheses, then add to stack until the matching
                // parentheses found.
                if (ch.equals('(')) {
                    parenthRecord.push(ch);
                }
                if (ch.equals(')')) {
                    parenthRecord.pop();
                }
                // If the first open parentheses is not eliminated by the 
                // closing parenthese before the last character, then it's not
                // a Kleen star expression.
                if (parenthRecord.isEmpty()) {
                    if (i == strLength - 2) {
                        isKleenStar = true;
                    }
                    break;
                }
            }
        }
        // If it's Kleen Star, then set code is 0.
        if (isKleenStar) {
            this.expr_type = RegularExpression.RE_KLEENSTAR;
            return;
        }
        // Check if the expression are connected by union.
        for (int i = 0; i < strLength; i++) {
            Character ch = this.strRepresentation.charAt(i);
            // If first parentheses, then add to stack until the matching
            // parentheses found.
            if (ch.equals('(')) {
                parenthRecord.push(ch);
            }
            if (ch.equals(')')) {
                parenthRecord.pop();
            }
            if (ch.equals('+') && parenthRecord.isEmpty()) {
                this.expr_type = RegularExpression.RE_UNION;
                return;
            }
        }
        this.expr_type = RegularExpression.RE_CONCATENATION;
    }

    /**
     * Get the integer code of this expression.
     *
     * @return Check with RegularExpression.RE_*
     */
    public int getTypeOfExpression() {
        return expr_type;
    }

    /**
     * Get the expression under the kleen star operator.
     *
     * @return null if the expression is not a kleen star expression.
     */
    public RegularExpression getExpressionUnderKleenStar() {
        if (expr_type != RegularExpression.RE_KLEENSTAR) {
            return null;
        }
        String subRepresentation = "";
        if (strRepresentation.length() > 1 && strRepresentation.charAt(1) == '*') {
            subRepresentation = String.valueOf(strRepresentation.charAt(0));
        } else {
            subRepresentation = strRepresentation.substring(1, strRepresentation.length() - 2);
        }
        return new RegularExpression(subRepresentation);
    }

    /**
     * Get the first expression of the union operator.
     *
     * @return null if the expression is not a union expression.
     */
    public RegularExpression getUnion1stExpression() {
        if (expr_type != RegularExpression.RE_UNION) {
            return null;
        }
        String subRepresentation = "";
        Stack<Character> parenthRecord = new Stack<>();
        for (int i = 0; i < this.strRepresentation.length(); i++) {
            Character ch = this.strRepresentation.charAt(i);
            // If first parentheses, then add to stack until the matching
            // parentheses found.
            if (ch.equals('(')) {
                parenthRecord.push(ch);
            }
            if (ch.equals(')')) {
                parenthRecord.pop();
            }
            if (ch.equals('+') && parenthRecord.isEmpty()) {
                break;
            }
            subRepresentation += ch;
        }
        return new RegularExpression(subRepresentation);
    }

    /**
     * Get the second expression of the union operator.
     *
     * @return null if the expression is not a union expression.
     */
    public RegularExpression getUnion2ndExpression() {
        if (expr_type != RegularExpression.RE_UNION) {
            return null;
        }
        String subRepresentation = "";
        Stack<Character> parenthRecord = new Stack<>();
        int recordingIndex = 0;
        for (int i = 0; i < this.strRepresentation.length(); i++) {
            Character ch = this.strRepresentation.charAt(i);
            // If first parentheses, then add to stack until the matching
            // parentheses found.
            if (ch.equals('(')) {
                parenthRecord.push(ch);
            }
            if (ch.equals(')')) {
                parenthRecord.pop();
            }
            if (ch.equals('+') && parenthRecord.isEmpty()) {
                recordingIndex = i;
                break;
            }
        }
        subRepresentation = this.strRepresentation.substring(recordingIndex + 1);
        return new RegularExpression(subRepresentation);
    }

    /**
     * Get the first expression of the concatenation operator.
     *
     * @return null if the expression is not a concatenation expression.
     */
    public RegularExpression getConcatenation1stExpression() {
        if (expr_type != RegularExpression.RE_CONCATENATION) {
            return null;
        }
        String subRepresentation = "";
        Stack<Character> parenthRecord = new Stack<>();
        // If the first character is not parentheses.
        if (this.strRepresentation.charAt(0) != '(') {
            subRepresentation = String.valueOf(this.strRepresentation.charAt(0));
            if (this.strRepresentation.length() > 1 && this.strRepresentation.charAt(1) == '*') {
                subRepresentation += '*';
            }
            return new RegularExpression(subRepresentation);
        }
        for (int i = 0; i < this.strRepresentation.length(); i++) {
            Character ch = this.strRepresentation.charAt(i);
            // If first parentheses, then add to stack until the matching
            // parentheses found.
            if (ch.equals('(')) {
                parenthRecord.push(ch);
            }
            if (ch.equals(')')) {
                parenthRecord.pop();
            }
            subRepresentation += ch;
            if (parenthRecord.isEmpty()) {
                if (i < strRepresentation.length() - 1) {
                    if (strRepresentation.charAt(i + 1) == '*') {
                        subRepresentation += '*';
                    }
                }
                break;
            }
        }
        return new RegularExpression(subRepresentation);
    }

    /**
     * Get the second expression of the concatenation operator.
     *
     * @return null if the expression is not a concatenation expression.
     */
    public RegularExpression getConcatenation2ndExpression() {
        if (expr_type != RegularExpression.RE_CONCATENATION) {
            return null;
        }
        String subRepresentation = "";
        Stack<Character> parenthRecord = new Stack<>();
        // If first character is a letter.
        if (this.strRepresentation.charAt(0) != '(') {
            if (this.strRepresentation.length() > 1 && this.strRepresentation.charAt(1) == '*') {
                return new RegularExpression(strRepresentation.substring(2));
            }
            return new RegularExpression(strRepresentation.substring(1));
        }
        int recordingIndex = 0;
        for (int i = 0; i < this.strRepresentation.length(); i++) {
            Character ch = this.strRepresentation.charAt(i);
            // If first parentheses, then add to stack until the matching
            // parentheses found.
            if (ch.equals('(')) {
                parenthRecord.push(ch);
            }
            if (ch.equals(')')) {
                parenthRecord.pop();
            }
            if (parenthRecord.isEmpty()) {
                recordingIndex = i;
                if (i < this.strRepresentation.length() - 1) {
                    if (this.strRepresentation.charAt(i + 1) == '*') {
                        recordingIndex++;
                    }
                }
                break;
            }
        }
        subRepresentation = this.strRepresentation.substring(recordingIndex + 1);
        return new RegularExpression(subRepresentation);
    }

    /**
     * Get the atom.
     *
     * @return null if it's not atom.
     */
    public RegularExpression getAtom() {
        if (expr_type != RegularExpression.RE_ATOM) {
            return null;
        }
        return new RegularExpression(strRepresentation);
    }

    /**
     * Get string representation of the regular expression.
     *
     * @return a string.
     */
    @Override
    public String toString() {
        return this.strRepresentation;
    }

    /**
     * A static method to traverse a regular expression.
     *
     * @param expr a regular expression.
     */
    public static void traverseExpression(RegularExpression expr) {
        int type = expr.getTypeOfExpression();
        if (type == RegularExpression.RE_KLEENSTAR) {
            System.out.print("Kleen star:" + expr);
            RegularExpression expr0 = expr.getExpressionUnderKleenStar();
            System.out.println("; Expression under: " + expr0);
            traverseExpression(expr0);
            return;
        }
        if (type == RegularExpression.RE_UNION) {
            System.out.print("Union: " + expr);
            RegularExpression expr1 = expr.getUnion1stExpression();
            RegularExpression expr2 = expr.getUnion2ndExpression();
            System.out.println("; Two sub expressions: " + expr1 + ", " + expr2);
            traverseExpression(expr1);
            traverseExpression(expr2);
            return;
        }
        if (type == RegularExpression.RE_CONCATENATION) {
            System.out.print("Concatenation: " + expr);
            RegularExpression expr1 = expr.getConcatenation1stExpression();
            RegularExpression expr2 = expr.getConcatenation2ndExpression();
            System.out.println("; Two sub expressions: " + expr1 + ", " + expr2);
            traverseExpression(expr1);
            traverseExpression(expr2);
            return;
        }
        if (type == RegularExpression.RE_ATOM) {
            System.out.println("Atom: " + expr);
        }
    }
    
    /**
     * Create lambda NFA from the regular expression.
     * @return lambda NFA.
     */
    public LambdaNFA createLambdaNFA(){
        return createLambdaNFA(this);
    }

    /**
     * Build lambda NFA from the regular expression.
     *
     * @param reg a
     * @return a lambda NFA.
     */
    private LambdaNFA createLambdaNFA(RegularExpression reg) {
        // Recusrively build LambdaNFA from regular expression.
        TreeSet<State> stateSet = new TreeSet<>();
        State initialState = null;
        TreeSet<State> finalStateSet = new TreeSet<>();
        TreeSet<Transition> transList = new TreeSet<>();
        TreeSet<Character> alphabet = new TreeSet<>();
        // If the regular expression is an atom.
        if (reg.expr_type == RegularExpression.RE_ATOM) {
            stateSet.add(new State("1"));
            stateSet.add(new State("2"));
            initialState = new State("1");
            finalStateSet.add(new State("2"));
            transList.add(new Transition(new State("1"), reg.strRepresentation, new State("2")));
            alphabet.add(reg.strRepresentation.charAt(0));
            LambdaNFA returningLamNFA = new LambdaNFA(stateSet, initialState, finalStateSet, transList, alphabet);
            //System.out.println("Atom: " + reg);
            //System.out.println(returningLamNFA);
            return returningLamNFA;
        }
        if (reg.expr_type == RegularExpression.RE_UNION) {
            LambdaNFA lamNFA1st = createLambdaNFA(reg.getUnion1stExpression());
            LambdaNFA lamNFA2nd = createLambdaNFA(reg.getUnion2ndExpression());
            //System.out.println("Union: " + reg);
            // Initial state of the new machine.
            State initState = new State("1");
            initialState = initState;
            // Final state set of the new machine.
            int totalState = lamNFA1st.stateSet.size() + lamNFA2nd.stateSet.size() - 2;
            State finState = new State(String.valueOf(totalState));
            finalStateSet.add(finState);
            // Update the final state in machine 1 by new final state.
            State finalStateNFA1 = new State(lamNFA1st.finalStateSet.first().getDescription());
            lamNFA1st.updateState(finalStateNFA1, finState);
            // Update the final state in machine 2 by new final state.
            State finalStateNFA2 = new State(lamNFA2nd.finalStateSet.first().getDescription());
            lamNFA2nd.updateState(finalStateNFA2, finState);

            // Remove initial state and final state from state set.
            lamNFA1st.stateSet.remove(initialState);
            lamNFA1st.stateSet.removeAll(finalStateSet);
            lamNFA2nd.stateSet.remove(initialState);
            lamNFA2nd.stateSet.removeAll(finalStateSet);

            // Modify state description of states in machine 2.
            // State set of machine 2 now does not contain initial and final states.            
            int stateNumber = lamNFA1st.stateSet.size() + 2;
            for (State state : lamNFA2nd.stateSet) {
                State newState = new State(String.valueOf(stateNumber));
                State oldState = new State(state.getDescription());
                lamNFA2nd.updateState(oldState, newState);
                stateNumber++;
            }
            // Add initial state and final state set.
            stateSet.add(initialState);
            stateSet.addAll(finalStateSet);

            // Add all states in machine 1 and machine 2.
            stateSet.addAll(lamNFA1st.stateSet);
            stateSet.addAll(lamNFA2nd.stateSet);
            // Add all transitions in machine 1 and machine 2.
            transList.addAll(lamNFA1st.transitionList);
            transList.addAll(lamNFA2nd.transitionList);
            // Union two alphabet.
            alphabet.addAll(lamNFA1st.getAlphabet());
            alphabet.addAll(lamNFA2nd.getAlphabet());
            TreeSet<Character> set = new TreeSet<>(alphabet);
            alphabet.clear();
            for (Character ch: set) {
                alphabet.add(ch);
            }
            LambdaNFA returningLamNFA = new LambdaNFA(stateSet, initialState, finalStateSet, transList, alphabet);
            //System.out.println(returningLamNFA);
            return returningLamNFA;
        }
        if (reg.expr_type == RegularExpression.RE_CONCATENATION) {
            LambdaNFA lamNFA1st = createLambdaNFA(reg.getConcatenation1stExpression());
            LambdaNFA lamNFA2nd = createLambdaNFA(reg.getConcatenation2ndExpression());
            //System.out.println("Concatenation: " + reg);
            // Initial state of the new machine.
            State initState = new State("1");
            initialState = initState;
            // Final state set of the new machine.
            int totalState = lamNFA1st.stateSet.size() + lamNFA2nd.stateSet.size() - 1;
            State finState = new State(String.valueOf(totalState));
            finalStateSet.add(finState);
            // Modify state description of states in machine 2.
            int stateNumber = totalState;
            // Loop backward to avoid duplicated description.
            int size2nd = lamNFA2nd.stateSet.size();
            Iterator<State> backwIterator = lamNFA2nd.stateSet.descendingIterator();
            while(backwIterator.hasNext()){
                State newState = new State(String.valueOf(stateNumber));
                State oldState = new State(backwIterator.next().getDescription());
                lamNFA2nd.updateState(oldState, newState);
                stateNumber--;
            }
            // Add all states in machine 1 and machine 2.
            stateSet.addAll(lamNFA1st.stateSet);
            lamNFA2nd.stateSet.remove(lamNFA2nd.initialState);
            stateSet.addAll(lamNFA2nd.stateSet);
            // Add all transitions in machine 1 and machine 2.
            transList.addAll(lamNFA1st.transitionList);
            transList.addAll(lamNFA2nd.transitionList);
            // Union two alphabet.
            alphabet.addAll(lamNFA1st.getAlphabet());
            alphabet.addAll(lamNFA2nd.getAlphabet());
            TreeSet<Character> set = new TreeSet<>(alphabet);
            alphabet.clear();
            for (Character ch: set) {
                alphabet.add(ch);
            }
            LambdaNFA returningLamNFA = new LambdaNFA(stateSet, initialState, finalStateSet, transList, alphabet);
            //System.out.println(returningLamNFA);
            return returningLamNFA;
        }
        if (reg.expr_type == RegularExpression.RE_KLEENSTAR) {
            LambdaNFA lamNFA = createLambdaNFA(reg.getExpressionUnderKleenStar());
            //System.out.println("Kleen Star: " + reg);
            initialState = new State("1");
            int size = lamNFA.stateSet.size();
            // New lamNFA has 2 extra states.
            finalStateSet.add(new State(String.valueOf(size + 2)));
            // Modify description of all states in the original NFA.
            Iterator<State> bwIterator = lamNFA.stateSet.descendingIterator();
            int i = size - 1;
            while(bwIterator.hasNext()){
                State oldState = new State(bwIterator.next().getDescription());
                State newState = new State(String.valueOf(i + 2));
                lamNFA.updateState(oldState, newState);
                i--;
            }
            // After updating all states and its relation, add all into new lambda NFA.
            stateSet.addAll(lamNFA.stateSet);
            transList.addAll(lamNFA.transitionList);
            // Add new initial state and final state set.
            stateSet.add(initialState);
            stateSet.addAll(finalStateSet);
            // Add new lambda moves.
            transList.add(new Transition(lamNFA.initialState, "", lamNFA.finalStateSet.first()));
            transList.add(new Transition(lamNFA.finalStateSet.first(), "", lamNFA.initialState));
            transList.add(new Transition(initialState, "", lamNFA.initialState));
            transList.add(new Transition(lamNFA.finalStateSet.first(), "", finalStateSet.first()));
            alphabet.addAll(lamNFA.getAlphabet());
            LambdaNFA returningLamNFA = new LambdaNFA(stateSet, initialState, finalStateSet, transList, alphabet);
            //System.out.println(returningLamNFA);
            return returningLamNFA;
        }
        return null;
    }

}
