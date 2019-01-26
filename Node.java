/**
 * Node class holds character entry along with character's frequency, both of which can be changed
 * Implemented for the purposes of HuffmanEncoder class.
 * @author Aadil Islam, Spring 2018
 */

public class Node {
	
	private char ch;
	private int freq;
	
	public Node(char ch, int freq) {
		this.ch = ch;
		this.freq = freq;
	}
	
	// getter
	public char getChar() {
		return ch;
	}
	
	// getter
	public int getFreq() {
		return freq;
	}
	
	// setter
	public void setChar(char ch) {
		this.ch = ch;
	}

	// setter
	public void setFreq(int freq) {
		this.freq = freq;
	}
	
	/**
	 * Override toString to display character and its associated frequency
	 */
	@Override
	public String toString() {
		return(ch + " -> " + freq);
	}
	
}
