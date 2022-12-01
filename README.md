# File-Comrpessiong-Huffman-Encoding

## In this project, I will use Huffman encoding to compress and decompress files. This brings together trees, maps, priority queues, and file i/o, all to help save the bits!

### A problem with variable-length encodings is figuring out where a code word ends. For instance, if we used '1' for 'E', '10' for 'F' and '00' for 'X' we would be in trouble. When decoding the file we would not know whether to interpret '1' as 'E', or as the start of the code for 'F'. Huffman Encoding removes this ambiguity by producing prefix-free codes. This means that for any given code word, adding bits to the end cannot produce another code word. Hence no code word is a prefix of another. When the computer observes a series of bits that corresponds to a code word, it knows that it cannot be part of a larger code word, and can safely interpret the series of bits as its corresponding character.

### At this point the purpose and value of Huffman Encoding should be fairly clear. So how do we do it? The task is to generate a set of prefix-free codes whose lengths are inversely correlated with the frequency of the encoded character. There are two clever parts of the algorithm, the use of a binary tree to generate the codes, and the construction of the binary tree using a priority queue. Specifically we will construct a tree such that each character is a leaf and the path from the root to that character gives that character's code word, where a left child is interpreted as a 0 and a right child as a 1.

### For example, in this tree, the codeword for e is 1101
<img width="253" alt="Screenshot 2022-11-30 at 11 00 37 PM" src="https://user-images.githubusercontent.com/40500380/204962811-d66dbe6e-e657-4ffa-9c6f-299f26efe352.png">
