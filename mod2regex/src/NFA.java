import java.util.*;

/*
 * This class simulates a non-deterministic finite automata
 * @author Floryan
 */
public class NFA{

	/* This global counter is used to issue numbers to each state one at a time as they are added */
	/* Helps us guarantee that all state numbers are unique, which is convenient */
	private static Integer ID_Counter = 0;

	/* Variables that define the machine itself. */
	/* States are labeled by increasing integers */
	private HashSet<Integer> states;
	private HashMap<QSig, HashSet<Integer>> transitions;
	/* Sigma is just 'a', 'd', 'e' (epsilon). No need to represent explicitly */
	private Integer startState;
	private HashSet<Integer> finalStates;


	/* Constructor. Creates a machine with a single start state only! */
	public NFA(){
		states = new HashSet<Integer>();

		transitions = new HashMap<QSig, HashSet<Integer>>();
		startState = addState(); //add one start state and remember it
		finalStates = new HashSet<Integer>();
	}

	/* Adding state with next available unique ID number */
	public int addState(){
		states.add(ID_Counter);
		ID_Counter++;
		return ID_Counter-1;
	}

	/* You can use this internally to manually add a specific state */
	/* Should only be used when you are merging states with another machine */
	private int addState(int stateId){
		states.add(stateId);
		return stateId;
	}

	/* Translate input to a or d */
	/* Be careful of 'e' the letter versus 'e' as epsilon transition */
	/* You do not want to call this on an 'e' epsilon transition accidentally */
	private char translateInput(char sig){
		if(sig >= 'a' && sig <= 'z') return 'a';
		else if(sig >= '0' && sig <= '9') return 'd';
		else return sig;
	}


	/* Creating and removing transitions */
	public boolean addTransition(int state, char sig, int newState){
		/* Make sure the inputs are all valid */
		if(!states.contains(state)) return false;
		if(!states.contains(newState)) return false;

		/* Create the transition and add it if it does not already exist */
		QSig qs = new QSig(state, sig);

		/* If first time seeing this, create the vector of destination states */
		if(!transitions.containsKey(qs)) transitions.put(qs, new HashSet<Integer>());

		/* add or overwrite it */
		transitions.get(qs).add(newState);
		return true;
	}

	/* Easier way to grab transitions */
	public boolean hasTransition(int state, char inputChar){
		QSig qs = new QSig(state, inputChar);
		return transitions.containsKey(qs);
	}

	public HashSet<Integer> getTransitions(int state, char inputChar){
		QSig qs = new QSig(state, inputChar);
		return transitions.get(qs); //might be null FYI
	}


	/* Setting / changing the start state */
	public boolean setStartState(int startState){
		/* Make sure state is one of the valid states */
		if(!states.contains(startState)) return false;

		this.startState = startState;
		return true;
	}

	public int getStartState(){
		return this.startState;
	}


	/* Setting / changing the final state(s) */
	public boolean clearFinalStates(){
		finalStates.clear();
		return true;
	}

	public boolean addFinalState(int state){
		if(!states.contains(state)) return false;
		if(finalStates.contains(state)) return false;

		finalStates.add(state);
		return true;
	}

	/* Performs all transitions for one character of input. */
	/* Removes the old state from the state list */
	/* Adds new states to the state list */
	/* Returns the new set of states the machine is in */
	public HashSet<Integer> transition(HashSet<Integer> curStates, char input){

		/* These states will be removed */
		HashSet<Integer> newStates = new HashSet<Integer>();

		for(int state : curStates){
			if(hasTransition(state, input))
				newStates.addAll(getTransitions(state, input));
		}

		return newStates;
	}

	/* Handles all epsilon transitions one time */
	/* Returns new set of states the machine is in */
	public HashSet<Integer> epsilonTransition(HashSet<Integer> curStates){

		while(true){
			HashSet<Integer> toAdd = new HashSet<Integer>();
			int initSize = curStates.size();
			for(int state: curStates){
				if(hasTransition(state, 'e'))
					toAdd.addAll(getTransitions(state, 'e'));
			}
			/* Break once no new states added */
			curStates.addAll(toAdd);
			if(initSize == curStates.size()) break;
			
		}
		return curStates;
	}

	/*
	 * Most important method. Given a string, does the NFA accept it?
	 */
	public boolean acceptsString(String input){
		
		/* Keep track of states and add start state */
		HashSet<Integer> currentStates = new HashSet<Integer>();
		currentStates.add(startState);

		/* --------------------------------- */
		/* TODO: IMPLEMENT THIS METHOD */
		/* --------------------------------- */




		/* --------------------------------- */

		return false;
	}


	/* Applies the * operator to this machine. */
	public void star(){
		
		/* --------------------------------- */
		/* TODO: IMPLEMENT THIS METHOD */
		/* --------------------------------- */

		// basically, wrap everything in epsilons

		int newStart = addState();
		int newFinal = addState();

		// add epsilon transitions from new start to both start and final
		addTransition(newStart, 'e', startState);
		addTransition(newStart, 'e', newFinal);

		// add epsilon transitions from old final to new final and start
		for (int finalState : finalStates) {
			addTransition(finalState, 'e', newFinal);
			addTransition(finalState, 'e', startState);
		}

		setStartState(newStart);
		clearFinalStates();
		addFinalState(newFinal);

		/* --------------------------------- */
	}

	/* Applies the union operator. Changes this machine but not the parameter */
	public void union(NFA other){
		if(other == null) return;

		/* --------------------------------- */
		/* TODO: IMPLEMENT THIS METHOD */
		/* --------------------------------- */

		int newStart = addState();
		int newFinal = addState();
		
		// map the states of the other machine to new states in this machine
		HashMap<Integer, Integer> otherMappings = new HashMap<>();
		for (int state : other.states) {
			int newState = addState();
			otherMappings.put(state, newState);
		}

		// merge transitions from other
		for (QSig transition : other.transitions.keySet()) {
			int otherSource = otherMappings.get(transition.q);
			char otherSig = transition.sig;

			for (int dest : other.transitions.get(transition)) {
				int otherDest = otherMappings.get(dest);
				addTransition(otherSource, otherSig, otherDest);
			}
		}

		// add epsilon transitions from new start to both start states
		addTransition(newStart, 'e', startState);
		addTransition(newStart, 'e', otherMappings.get(other.startState));

		// add epsilon transitions from both final states to new final
		for (int finalState : finalStates) {
			addTransition(finalState, 'e', newFinal);
		}

		for (int finalState : other.finalStates) {
			addTransition(otherMappings.get(finalState), 'e', newFinal);
		}

		setStartState(newStart);
		clearFinalStates();
		addFinalState(newFinal);

		/* --------------------------------- */

	}

	/* Apply the concatenation operator. Concatenates other to THIS machine */
	/* other is NOT altered */
	public void concatenate(NFA other){
		if(other == null) return;

		/* --------------------------------- */
		/* TODO: IMPLEMENT THIS METHOD */
		/* --------------------------------- */

		// map the states of the other machine to new states in this machine
		HashMap<Integer, Integer> otherMappings = new HashMap<>();
		for (int state : other.states) {
			int newState = addState();
			otherMappings.put(state, newState);
		}

		// get other's start and add epsilon transitions from our final
		int otherStart = otherMappings.get(other.startState);
		for (int state : finalStates) {
			addTransition(state, 'e', otherStart);
		}

		// merge transitions from other
		for (QSig transition : other.transitions.keySet()) {
			int otherSource = otherMappings.get(transition.q);
			char otherSig = transition.sig;

			for (int dest : other.transitions.get(transition)) {
				int otherDest = otherMappings.get(dest);
				addTransition(otherSource, otherSig, otherDest);
			}
		}

		// update final states to be from other
		clearFinalStates();
		for (int finalState : other.finalStates) {
			addFinalState(otherMappings.get(finalState));
		}

		/* --------------------------------- */

	}


	/*
	 * Use this to print out the machine if you want
	 * Pretty useful for debugging problems
	 */
	public String toString(){
		String out = "States: (";
		for(int state : states){
			out += state + ", ";
		}
		out += ")\n";

		out += "Start: " + startState + "\n";
		out += "Final States:\n";
		for(int state : finalStates){
			out += state + ", ";
		}
		out += "\n";

		for(QSig transition : transitions.keySet()){
			out += transition + " --> " + transitions.get(transition) + "\n";
		}

		return out;
	}


}