import java.util.Comparator;

/**
 * TreeComparator class describes how to compare two different BinaryTree node objects.
 * Implemented for use of the HuffmanEncoder class.
 * @author Aadil Islam, Spring 2018
 */

public class TreeComparator implements Comparator {
	
	public TreeComparator() {
		super();
	}
	
	/**
	 * Returns relationship between what are assume to be two BinaryTree node objects
	 * 
	 * @param o1	first object
	 * @param o2	second object
	 */
	@Override
	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub

		// extract frequencies from BinaryTree node objects
		int freq1 = ((BinaryTree<Node>) o1).getData().getFreq();
		int freq2 = ((BinaryTree<Node>) o2).getData().getFreq();
		
		// if first node has higher frequency, it gets higher priority thus method returns 1, otherwise...
		if(freq1 > freq2)
			return 1;
		else if(freq1 < freq2)
			return -1;
		else
			return 0;
	}
	
}
