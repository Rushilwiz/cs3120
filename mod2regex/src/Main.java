import java.util.*;

public class Main{

	/* A quicker way to print things out */
	public static void p(String toPrint){
		System.out.println(toPrint);
	}

	public static void main(String[] args){

		/* Reads in the input and calls appropriate methods. */
		/* You DO NOT need to change anything here, but should read it over. */

		p("Enter Regular Expression:");

		Scanner in = new Scanner(System.in);
		String regEx = in.next();

		p("The expression you entered is: " + regEx);

		/* Build the NFA from the regular expression */
		NFA nfa = buildNFA(regEx);

		/* You can uncomment this line if you want to see the */
		/* machine your buildNFA method produced */
		//p("Machine: " + nfa);

		/* Read in the number of strings */
		int n = in.nextInt();

		for(int i=0; i<n; i++){
			String input = in.next();

			/* See if the NFA accepts it! */
			if(nfa.acceptsString(input)) p("YES");
			else p("NO");
		}
	}


	/*
	 * buildNFA: Given a regular expression as a string, build the NFA object that
	 * represents a machine that would accept that regular expression.
	 * Psuedo-code is provided for your convenience
	 */
	public static NFA buildNFA(String exp){

		/* TODO: IMPLEMENT THIS METHOD */
		/* --------------------------------------------- */

		/* Case 1 - Base Case: exp is empty string, nothing to do */

		if (exp.length() == 0) {
			NFA nfa = new NFA();
			nfa.addFinalState(nfa.getStartState());
			return nfa;
		}

		/* Case 2 - Look for U operator (will never be inside parens so don't need to worry about that) */
		
		/*
		If exp contains "U" operators
			Split exp into all the segments between the Us (e.g., aaUddUda => [aa,dd,da])
			
			Recursively call buildNFA on each individual segment (e.g., aa)
			
			Call the union() method on the NFA objects returns to patch them together.
			
			return the unioned NFA
		*/
		
		if (exp.contains("U")) {
			// split the expression by U
			String[] parts = exp.split("U");

			// build NFA for the first part
			NFA result = buildNFA(parts[0]);

			// union additional parts with result
			for (int i = 1; i < parts.length; i++) {
				NFA nextNFA = buildNFA(parts[i]);
				result.union(nextNFA);
			}

			return result;
		}
		
		/* Case 3 - First character of exp is 'a' or 'd' */

		/*
		If first character is 'a' or 'd'
			Create an NFA object that has start state and single 'a' / 'd'
			transition to a final state
		
			If the character after the 'a' or 'd' is the * operator
				call star() on the nfa you just built
				concatenate() with the NFA for rest of the expression (after the star)
			Else if the character after 'a' or 'd' is not the * operator
				just concatenate() with the NFA for rest of the expression
		
			Return the NFA that was built
		*/
		
		if (exp.charAt(0) == 'a' || exp.charAt(0) == 'd') {
			NFA nfa = new NFA();
			
			int startState = nfa.getStartState();
			int finalState = nfa.addState();
			nfa.addFinalState(finalState);
			nfa.addTransition(startState, exp.charAt(0), finalState);

			if (exp.length() > 1) {
				if (exp.charAt(1) == '*') {
					nfa.star();
					if (exp.length() > 2) {
						nfa.concatenate(buildNFA(exp.substring(2)));
					}
				} else {
					nfa.concatenate(buildNFA(exp.substring(1)));
				}
			}

			return nfa;
		}

		

		/* Case 4 - First character is an open paren */

		/*
		If first character is open paren
			Work your way down the exp to find index of closing paren that matches this one.

			Call buildNFA() on everything within the parentheses

			Call star() on this NFA (because right paren must have * after it)

			Concatenate with the NFA for the rest of the expression after the *
		*/

		/* --------------------------------------------- */

		if (exp.charAt(0) == '(') {
			int parenCount = 1;
			int closeIndex = 1;
			while (parenCount > 0 && closeIndex < exp.length()) {
				if (exp.charAt(closeIndex) == '(') {
					parenCount++;
				} else if (exp.charAt(closeIndex) == ')') {
					parenCount--;
				}

				closeIndex++;
			}
				
			NFA nfa = buildNFA(exp.substring(1, closeIndex - 1));

			nfa.star();

			if (closeIndex < exp.length() - 1) {
				nfa.concatenate(buildNFA(exp.substring(closeIndex + 1)));
			}

			return nfa;
		}

		/* Should never happen...but here so code compiles */
		return null;

	}
}