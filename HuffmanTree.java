import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanTree {
    /**
     * Internal character and frequency tree
     */
    class Tree implements Comparable<Tree>{
        public int frequency;
        public char character;
        public Tree left, right;

        @Override
        public int compareTo(Tree t2) {
            return this.frequency - t2.frequency;
        }

        public Tree(int frequency, char character) {
            this.frequency = frequency;
            this.character = character;
        }

        public Tree(int frequency, Tree left, Tree right) {
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        // Generate an S-expression recursively to represent the Binary Tree
        @Override
        public String toString() {
            String c = "", f, l = "", r = "";
            if (this.character != '\0') c = "" + this.character;
            f = ""+this.frequency;
            if (this.left != null) l = this.left.toString();
            if (this.right != null) r = this.right.toString();


            return String.format("([%s %s] %s %s)", c, f, l, r);
        }

        public boolean isLeaf() {
            return (this.left == null && this.right == null);
        }
    }

    public Tree tree;
    Map<Character, Boolean[]> characterCodes;

    /**
     * Checks character frequency and creates single-node trees for each one.
     * @param text text to be used for the trees
     * @return
     */
    ArrayList<Tree> getInitialTrees(String text) {
        Map<Character, Integer> charFrequencies = new HashMap<Character, Integer>();

        // count each time a character appears in the text
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (charFrequencies.containsKey(c)) {
                charFrequencies.put(c, charFrequencies.get(c) + 1);
            } else {
                charFrequencies.put(c, 1);
            }
        }

        ArrayList<Tree> trees = new ArrayList<Tree>();

        // creates a tree for each character w/the respective frequency
        for (Map.Entry<Character, Integer> e : charFrequencies.entrySet()) {
            trees.add(
                    new Tree(e.getValue(), e.getKey())
            );
        }

        return trees;
    }

    /**
     * Generates the internal character and frequency tree
     * @param text text to be used for the trees
     */
    public void generateTree(String text) {
        if (text == null || text.length() == 0) return;

        ArrayList<Tree> initialTrees = getInitialTrees(text);

        // handle case for single or repeated character
        if (initialTrees.size() == 1) {
            Tree t = initialTrees.get(0);
            this.tree = new Tree(t.frequency, t, null);
            return;
        }

        // creates a priority queue for the tree generation
        PriorityQueue<Tree> pq = new PriorityQueue<Tree>();
        pq.addAll(initialTrees);

        // while >= 2 trees remain, grab the two smallest and merge them into a new tree
        while (pq.size() > 1) {
            Tree t1 = pq.remove();
            Tree t2 = pq.remove();

            Tree newTree = new Tree(t1.frequency + t2.frequency, t1, t2);

            pq.add(newTree);
        }

        // sets the tree to be the newly generated one
        this.tree = pq.remove();
    }

    /**
     * Recursively builds map from character to boolean character code to optimize encoding
     * @param t starting point, should start at tree root
     * @param path should start as empty, recursively holds the path that represents the character code
     */
    void buildCharacterCodeMap(Tree t, ArrayList<Boolean> path) {
        // at the leaf, add character code to map
        if (t.isLeaf()) characterCodes.put(t.character, path.toArray(new Boolean[]{}));

        // if it's not a leaf, recursively call on children with their respective paths
        if (t.left != null) {
            path.add(false);
            buildCharacterCodeMap(t.left, path);
            path.remove(path.size() - 1);
        }

        if (t.right != null) {
            path.add(true);
            buildCharacterCodeMap(t.right, path);
            path.remove(path.size() - 1);
        }
    }

    /**
     * Encodes the text using Huffman encoding
     * @param text text to be encoded
     * @return array of booleans representing the encoded text
     */
    Boolean[] encode(String text) {
        if (text == null || text.length() == 0) return new Boolean[]{};
        // generates tree if it doesn't exist
        if (this.tree == null) generateTree(text);

        // generates the characterCodeMap if it doesn't exist
        if (characterCodes == null) {
            // instantiates new hashmap
            characterCodes = new HashMap<>();
            buildCharacterCodeMap(this.tree, new ArrayList<>());
        }

        // instantiates empty arraylist
        // holding Booleans
        ArrayList<Boolean> encoded = new ArrayList<Boolean>();

        // adds each character's code to the encoded list
        for (int i = 0; i < text.length(); i++) {
            Boolean[] code = characterCodes.get(text.charAt(i));
            for (Boolean b : code) encoded.add(b);
        }

        // returns the list as an array
        return encoded.toArray(new Boolean[]{});
    }

    /**
     * Decodes an encoded representation of the text.
     * @param encoded boolean array representing the encoded text
     * @return decoded text. Returns an empty string if tree wasn't initialized
     */
    public String decode(Boolean[] encoded) {
        StringBuilder result = new StringBuilder();

        if (this.tree == null) return "";
        Tree t = this.tree;

        int i = 0;

        // goes through encoded representation moving left or right depending on the bit
        for (boolean b : encoded) {
            if (b) t = t.right;
            else if (!b) t = t.left;
            // if a leaf is reached, add the character and go back to the root
            if (t.isLeaf()) {
                result.append(t.character);
                t = this.tree;
            }
            i++;
        }

        // return the result as string
        return result.toString();
    }

    /**
     * reads textfile from drive
     * @param filename path to file
     * @return internal string
     * @throws Exception if file cannot be read
     */
    private static String loadFileIntoString(String filename) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String line;
        StringBuilder str = new StringBuilder();

        // adds each line as string
        while ((line = in.readLine()) != null) {
            str.append('\n');
            str.append(line);
        }
        in.close();
        return str.toString();
    }

    /**
     * Reads bit from file as boolean array
     * @param filepath path to the file
     * @return array of booleans representing the bits of the file
     * @throws Exception if file cannot be read
     */
    public static Boolean[] readBitsFromFile(String filepath) throws Exception {
        ArrayList<Boolean> readBits = new ArrayList<>();

        BufferedBitReader bitInput = new BufferedBitReader(filepath);

        while (bitInput.hasNext()) {
            boolean bit = bitInput.readBit();
            readBits.add(bit);
        }

        bitInput.close();
        return readBits.toArray(new Boolean[]{});
    }

    /**
     * writes bit array to file
     * @param bits bits to be written, as Boolean[]
     * @param filepath filepath of the target file
     * @throws Exception in case of IO exception
     */
    public static void writeBitsToFile(Boolean[] bits, String filepath) throws Exception {
        BufferedBitWriter bitOutput = new BufferedBitWriter(filepath);

        for (boolean bit : bits) {
            bitOutput.writeBit(bit);
        }

        bitOutput.close();
    }

    public static void main(String[] args) throws Exception{
        // check simple text test
        String filepath = "PS3/simpleText.txt";
        HuffmanTree ht = new HuffmanTree();
        String sampleText = loadFileIntoString(filepath);

        ht.generateTree(sampleText);

        // print s-expression of encoding tree
        System.out.println(ht.tree);

        // encode text and write to file
        Boolean[] e = ht.encode(sampleText);
        HuffmanTree.writeBitsToFile(e, String.format("%s.enc", filepath));

        System.out.printf("Encoding done, %d bits", e.length);


        // read the encoded representation from the file
        Boolean[] r = HuffmanTree.readBitsFromFile(String.format("%s.enc", filepath));

        // print the result of decoding
        System.out.println(ht.decode(r));

        System.out.println("\n---");

        // try single repeated character text
        HuffmanTree ht2 = new HuffmanTree();
        Boolean[] e2 = ht2.encode("aaaaaaaaa"); // encode generates tree if it does not exist yet
        System.out.println(ht2.decode(e2));

        System.out.println("\n---");

        // empty text test
        HuffmanTree ht3 = new HuffmanTree();
        Boolean[] e3 = ht3.encode("");
        System.out.println("text: " + ht3.decode(e3));

        System.out.println("\n---");

        // larger file test; USConstitution

        String filepathUS = "PS3/USConstitution.txt";
        HuffmanTree ht4 = new HuffmanTree();
        String constitution = loadFileIntoString(filepathUS);

        Boolean[] e4 = ht4.encode(constitution);
        HuffmanTree.writeBitsToFile(e4, String.format("%s.enc", filepathUS));

        System.out.printf("Encoding done, %d bits", e4.length);


        // read the encoded representation from the file
        Boolean[] rUS = HuffmanTree.readBitsFromFile(String.format("%s.enc", filepathUS));

        // print the result of decoding (first 200 chars)
        System.out.println(ht4.decode(rUS).substring(0, 200));


        System.out.println("\n---");


        // larger file test; WarAndPeace
        String tolstoyFilepath = "PS3/WarAndPeace.txt";
        HuffmanTree ht5 = new HuffmanTree();
        String warAndPeace = loadFileIntoString(tolstoyFilepath);

        Boolean[] e5 = ht5.encode(warAndPeace);
        HuffmanTree.writeBitsToFile(e5, String.format("%s.enc", tolstoyFilepath));

        System.out.printf("Encoding done, %d bits", e5.length);


        // read the encoded representation from the file
        Boolean[] rWaP = HuffmanTree.readBitsFromFile(String.format("%s.enc", tolstoyFilepath));

        // print the result of decoding (first 200 chars)
        System.out.println(ht5.decode(rWaP).substring(0, 200));
    }
}
