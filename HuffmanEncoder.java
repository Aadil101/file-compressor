/**
 * WarAndPeace.txt was originally a 3.2 MB sized text doc.
 * Compression led to it being only 1.8 MB, more than 40% size reduction!
 * @author Aadil Islam, Spring 2018
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class HuffmanEncoder {

	// stores file name
	private String pathName;
	// assists in storing non-leaf nodes
	private static HashMap<Integer, String> innerNodes;
	// stores codeTree as Binary Tree 
	private BinaryTree<Node> codeTree;
	

	public HuffmanEncoder(String pathName) {
		this.pathName = pathName;
		innerNodes = new HashMap<Integer, String>();
		codeTree  = new BinaryTree<Node>(null);
	}
	
	/**
	 * Reads file and compresses characters
	 * @throws IOException
	 */
	public void compress() throws IOException {
		
		// create buffered reader object to read file later
		BufferedReader input = new BufferedReader(new FileReader(pathName));
		// create hash map to store relationship between characters and their frequencies
		HashMap<Character, Integer> freqMap = new HashMap<Character, Integer>();
		
		// loop through every existing character in file
		int i;
		while((i = input.read()) != -1){
			// convert integer to character form
			char c = (char) i;
			// either create brand new entry in freqMap, or add to existing entry's frequency
			if(freqMap.containsKey(c)) 
				freqMap.put(c,freqMap.get(c)+1);
			else 
				freqMap.put(c,1);
		}
		
		input.close();
		
		// display freqMap
		//System.out.println("freqMap:\n"+freqMap);
		
		// create object to establish comparison of node entries in priority queue
		// see TreeComparator class
		Comparator<BinaryTree<Node>> nodeCompare = new TreeComparator();
		PriorityQueue<BinaryTree<Node>> priQueue = new PriorityQueue<BinaryTree<Node>>(nodeCompare);
		
		// add frequency-mapped characters to priority queue,
		// which organizes them by priority using frequencies
		for(char c : freqMap.keySet())
			priQueue.add(new BinaryTree<Node>(new Node(c,freqMap.get(c))));
		
		// check if priQueue (or, effectively, the file) is non-empty
		if(priQueue.size() > 1) {
			// if so, repeatedly remove lowest priority elements and combine into BinaryTree
			// until there is a single BinaryTree in the priority queue
			while(priQueue.size() != 1) {
				BinaryTree<Node> t1 = priQueue.remove();
				BinaryTree<Node> t2 = priQueue.remove();
				priQueue.add(new BinaryTree<Node>(new Node('r',t1.getData().getFreq()
						+t2.getData().getFreq()),t1,t2));
			}
		}
		// check if priQueue is handling a file with a single frequency-mapped character
		else if (priQueue.size() == 1) {
			BinaryTree<Node> t = priQueue.remove();
			// make sure one of the children in new BinaryTree is null
			priQueue.add(new BinaryTree<Node>(new Node('r',t.getData().getFreq()),t,null));
		}
		// check if priQueue is handling empty, if so, no compression necessary.
		else {
			return;
		}
	
		// code tree is the remaining BinaryTree in priority queue
		codeTree = priQueue.remove();
		// display code tree
		//System.out.println("codeTree:\n" + codeTree);
		// create map of each character to its new code using helper function
		HashMap<Character, String> codeMap = getLeafPaths(codeTree);
		// display code map
		//System.out.println("codeMap:\n" + codeMap);
		
		// re-initialize input to read file again
		input = new BufferedReader(new FileReader(pathName));
		// create bit writer object to create compressed file
		BufferedBitWriter bitOutput = new BufferedBitWriter(pathName.substring(0,pathName.indexOf('.'))
				+"_compressed.txt");
		
		// read file 
		while((i = input.read()) != -1){
			char c = (char) i;
			// add each character's mapped code to compressed file
			String code = codeMap.get(c);
			if(code != null) {
				// each digit in code has to be translated to boolean values
				for(char digit : code.toCharArray()) {
					if(digit == '0')
						bitOutput.writeBit(false);
					else
						bitOutput.writeBit(true);
				}
			}
		}
		
		input.close();
		bitOutput.close();
		
	}
	
	/**
	 * Tries to read compressed file and translate its code into characters from original file
	 * @throws IOException
	 */
	public void decompress() throws IOException {
		
		// as long as there exists a compressed file to decompress
		try {
			// create bit reader object to translate bits
			BufferedBitReader bitInput = new BufferedBitReader(pathName.substring(0,pathName.indexOf('.'))
					+"_compressed.txt");
			// create buffered writer object to write translated characters
			BufferedWriter output = new BufferedWriter(new FileWriter(pathName.substring(0,pathName.indexOf('.')) 
					+"_decompressed.txt")); 
			
			// begin holding root node of codeTree
			BinaryTree<Node> current = codeTree;
			while(bitInput.hasNext()) {
				boolean bit = bitInput.readBit();
				// true bit translates to 1, or right movement in codeTree 
				if(bit) 
					current = current.getRight();
				// false bit means left movement in codeTree
				else 
					current = current.getLeft();
				// if arrived at a character in codeTree, write character in output
				// head back to beginning of codeTree to start translation of next character
				// if this statement does not run, it means more bits are needed to translate current character
				if(current.isLeaf()) {
					output.write(current.getData().getChar());
					current = codeTree;
				}
			}
			
			bitInput.close();
			output.close();
		}
		// in case there is no compressed file to decompress (empty original file case)
		catch (FileNotFoundException e) {
			
		}
		
	}
	

	/**
	 * Returns map of all characters in binary code tree r using recursion 
	 * @throws IOException
	 */
	private static HashMap<Character, String> getLeafPaths (BinaryTree<Node> r){
		
		// Hash map relating character to string form of code
		HashMap<Character, String> charPaths = new HashMap<Character, String>();

		// check for left child of root node
		if(r.hasLeft()) {
			// have reached character (not inner-node) if it has no children
			if(r.getLeft().isLeaf()) {
				// check inner-nodes map for an existing code to build on for current character 
				if(innerNodes.containsKey(r.getData().getFreq())) 
					charPaths.put(r.getLeft().getData().getChar(), innerNodes.get(r.getData().getFreq())+"0");
				// otherwise, create brand new code for this character
				else 
					charPaths.put(r.getLeft().getData().getChar(), "0");
				// end of the line for this path, character's code has been established
			}
			// have not reached character, but have reached an inner node
			else {
				// check inner-nodes map for an existing code to build on for current inner-node
				if(innerNodes.containsKey(r.getData().getFreq())) 
					innerNodes.put(r.getLeft().getData().getFreq(), innerNodes.get(r.getData().getFreq())+"0");
				// otherwise, create brand new code for this inner-node
				else 
					innerNodes.put(r.getLeft().getData().getFreq(), "0");
				// since we have not reached a character yet, must recurse in this direction of the tree 
				// add newly found codes to original charPaths map
				charPaths.putAll(getLeafPaths(r.getLeft()));
			}
		}
		// similar logic for right child as demonstrated above!
		if(r.hasRight()) {
			if(r.getRight().isLeaf()) {
				if(innerNodes.containsKey(r.getData().getFreq()))
					charPaths.put(r.getRight().getData().getChar(), innerNodes.get(r.getData().getFreq())+"1");
				else
					charPaths.put(r.getRight().getData().getChar(), "1");
			}
			else {
				if(innerNodes.containsKey(r.getData().getFreq()))
					innerNodes.put(r.getRight().getData().getFreq(), innerNodes.get(r.getData().getFreq())+"1");
				else
					innerNodes.put(r.getRight().getData().getFreq(), "1");
				charPaths.putAll(getLeafPaths(r.getRight()));
			}
		}
		
		// once all recursion has ended, charPaths holds every single character mapped to code
		return charPaths;
	
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HuffmanEncoder c;
		String[] files = {"inputs/BoundaryCaseEmptyFile.txt", "inputs/BoundaryCaseSingleChar.txt",
							"inputs/BoundaryCaseRepSingleChar.txt", "inputs/Test1.txt",
							"inputs/Test2.txt","inputs/Test3.txt","inputs/USConstitution.txt"};
		for(String f : files) {
			c = new HuffmanEncoder(f);
			c.compress();
			c.decompress();
		}

	}

}
