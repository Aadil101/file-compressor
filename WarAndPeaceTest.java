import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * @author Aadil Islam, Spring 2018
 */

public class WarAndPeaceTest {
	
	public static final String inputPath = "inputs/WarAndPeace.txt";
	
	private static HashMap<Integer, String> innerNodes = new HashMap<Integer, String>();

	public static HashMap<Character, String> getLeafPaths (BinaryTree<Node> r){
		
		HashMap<Character, String> charPaths = new HashMap<Character, String>();

		if(r.hasLeft()) {
			if(r.getLeft().isLeaf()) {
				if(innerNodes.containsKey(r.getData().getFreq())) 
					charPaths.put(r.getLeft().getData().getChar(), innerNodes.get(r.getData().getFreq())+"0");
				else 
					charPaths.put(r.getLeft().getData().getChar(), "0");
			}
			else {
				if(innerNodes.containsKey(r.getData().getFreq())) 
					innerNodes.put(r.getLeft().getData().getFreq(), innerNodes.get(r.getData().getFreq())+"0");
				else 
					innerNodes.put(r.getLeft().getData().getFreq(), "0");
				charPaths.putAll(getLeafPaths(r.getLeft()));
			}
		}
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
		
		return charPaths;
	
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader input = new BufferedReader(new FileReader(inputPath));
		HashMap<Character, Integer> freqMap = new HashMap<Character, Integer>();
		
		int i;
		while((i = input.read()) != -1){
			char c = (char) i;
			if(freqMap.containsKey(c)) 
				freqMap.put(c,freqMap.get(c)+1);
			else 
				freqMap.put(c,1);
		}
		
		input.close();
		
		Comparator<BinaryTree<Node>> nodeCompare = new TreeComparator();
		PriorityQueue<BinaryTree<Node>> priQueue = new PriorityQueue<BinaryTree<Node>>(nodeCompare);
		
		for(char c : freqMap.keySet()) {
			priQueue.add(new BinaryTree<Node>(new Node(c,freqMap.get(c))));
		}
		
		if(priQueue.size() > 1) {
			while(priQueue.size() != 1) {
				BinaryTree<Node> t1 = priQueue.remove();
				BinaryTree<Node> t2 = priQueue.remove();
				priQueue.add(new BinaryTree<Node>(new Node('r',t1.getData().getFreq()+t2.getData().getFreq()),t1,t2));
			}
		}
		else if (priQueue.size() == 1) {
			BinaryTree<Node> t = priQueue.remove();
			priQueue.add(new BinaryTree<Node>(new Node('r',t.getData().getFreq()),t,null));
		}
		else {
			BufferedBitWriter bitOutput = new BufferedBitWriter(inputPath.substring(0,inputPath.indexOf('.'))+"_compressed.txt");
			BufferedWriter output = new BufferedWriter(new FileWriter(inputPath.substring(0,inputPath.indexOf('.'))+"_decompressed.txt"));
			bitOutput.close();
			output.close();
			return;
		}
		
		BinaryTree<Node> codeTree = priQueue.remove();
		HashMap<Character, String> codeRetrieval = getLeafPaths(codeTree);
		
		input = new BufferedReader(new FileReader(inputPath));
		BufferedBitWriter bitOutput = new BufferedBitWriter(inputPath.substring(0,inputPath.indexOf('.'))+"_compressed.txt");
		
		while((i = input.read()) != -1){
			char c = (char) i;
			String code = codeRetrieval.get(c);
			if(code != null) {
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
		
		BufferedBitReader bitInput = new BufferedBitReader(inputPath.substring(0,inputPath.indexOf('.'))+"_compressed.txt");
		BufferedWriter output = new BufferedWriter(new FileWriter(inputPath.substring(0,inputPath.indexOf('.'))+"_decompressed.txt")); 
		
		BinaryTree<Node> current = codeTree;
		while(bitInput.hasNext()) {
			boolean bit = bitInput.readBit();
			if(bit) 
				current = current.getRight();
			else 
				current = current.getLeft();
			if(current.isLeaf()) {
				output.write(current.getData().getChar());
				current = codeTree;
			}
		}
		
		bitInput.close();
		output.close();
		
		
	}
	
}
