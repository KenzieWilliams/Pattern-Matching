import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Your implementations of various string searching algorithms.
 *
 * @author Mackenzie Williams
 * @version 1.0
 */
public class PatternMatching {

    /**
     * Knuth-Morris-Pratt (KMP) algorithm relies on the failure table (also
     * called failure function). Works better with small alphabets.
     *
     * Make sure to implement the buildFailureTable() method before implementing
     * this method.
     *
     * @param pattern    the pattern you are searching for in a body of text
     * @param text       the body of text where you search for pattern
     * @param comparator you MUST use this to check if characters are equal
     * @return list containing the starting index for each match found
     * @throws java.lang.IllegalArgumentException if the pattern is null or has
     *                                            length 0
     * @throws java.lang.IllegalArgumentException if text or comparator is null
     */
    public static List<Integer> kmp(CharSequence pattern, CharSequence text,
                                    CharacterComparator comparator) {
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("Invalid pattern to perform kmp with");
        }
        if (comparator == null || text == null) {
            throw new IllegalArgumentException("Cannot perform kmp with a null argument");
        }
        LinkedList<Integer> list = new LinkedList<Integer>();
        if (text.length() >= pattern.length()) {
            int[] failureTable = buildFailureTable(pattern, comparator);

            int check = 0;
            int start = 0;
            int j = 0;
            while (check < text.length() && pattern.length() <= text.length()
                    && text.length() - start >= pattern.length()) {
                if (comparator.compare(text.charAt(check), pattern.charAt(j)) == 0) {
                    check++;
                    j++;
                    if (j >= pattern.length()) {
                        list.add(start);
                        j = failureTable[j - 1];
                        start = check - j;
                    }
                } else if (j == 0) {
                    start = start + 1;
                    check = start;
                } else {
                    j = failureTable[j - 1];
                    start = check - j;
                }
            }
        }
        return list;
    }

    /**
     * Builds failure table that will be used to run the Knuth-Morris-Pratt
     * (KMP) algorithm.
     *
     * The table built should be the length of the input pattern.
     *
     * Note that a given index i will contain the length of the largest prefix
     * of the pattern indices [0..i] that is also a suffix of the pattern
     * indices [1..i]. This means that index 0 of the returned table will always
     * be equal to 0
     *
     * Ex.
     * pattern:       a  b  a  b  a  c
     * failureTable: [0, 0, 1, 2, 3, 0]
     *
     * If the pattern is empty, return an empty array.
     *
     * @param pattern    a pattern you're building a failure table for
     * @param comparator you MUST use this to check if characters are equal
     * @return integer array holding your failure table
     * @throws java.lang.IllegalArgumentException if the pattern or comparator
     *                                            is null
     */
    public static int[] buildFailureTable(CharSequence pattern,
                                          CharacterComparator comparator) {
        if (pattern == null || comparator == null) {
            throw new IllegalArgumentException("Cannot create a failure table with null argument");
        }
        if (pattern.length() == 1) {
            return new int[]{0};
        }
        int[] failureTable = new int[pattern.length()];
        failureTable[0] = 0;
        int i = 0;
        int j = 1;
        while (j < pattern.length()) {
            if (comparator.compare(pattern.charAt(i), pattern.charAt(j)) == 0) {
                failureTable[j] = i + 1;
                i++;
                j++;
            } else {
                if (i != 0) {
                    i = failureTable[i - 1];
                } else {
                    failureTable[j] = i;
                    j++;
                }
            }
        }
        return failureTable;
    }

    /**
     * Boyer Moore algorithm that relies on last occurrence table. Works better
     * with large alphabets.
     *
     * Make sure to implement the buildLastTable() method before implementing
     * this method. Do NOT implement the Galil Rule in this method.
     *
     * Note: You may find the getOrDefault() method from Java's Map class
     * useful.
     *
     * @param pattern    the pattern you are searching for in a body of text
     * @param text       the body of text where you search for the pattern
     * @param comparator you MUST use this to check if characters are equal
     * @return list containing the starting index for each match found
     * @throws java.lang.IllegalArgumentException if the pattern is null or has
     *                                            length 0
     * @throws java.lang.IllegalArgumentException if text or comparator is null
     */
    public static List<Integer> boyerMoore(CharSequence pattern,
                                           CharSequence text,
                                           CharacterComparator comparator) {
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("Invalid pattern for Boyer Moore");
        }
        if (text == null || comparator == null) {
            throw new IllegalArgumentException("Cannot perform Boyer Moore with null argument");
        }
        LinkedList<Integer> list = new LinkedList<>();
        int start = 0;
        int j = pattern.length() - 1;
        int check = j;
        if (pattern.length() <= text.length()) {
            Map<Character, Integer> lastTable = buildLastTable(pattern);
            while (start <= text.length() - pattern.length()) {
                if (comparator.compare(pattern.charAt(j), text.charAt(check)) == 0) {
                    if (check == start) {
                        list.add(start);
                        start++;
                        check = start + pattern.length() - 1;
                        j = pattern.length() - 1;
                    } else {
                        check--;
                        j--;
                    }
                } else if (lastTable.getOrDefault(text.charAt(check), -1) < j) {
                    if (lastTable.getOrDefault(text.charAt(check), -1) == -1) {
                        start = check + 1;
                        check = start + pattern.length() - 1;
                        j = pattern.length() - 1;
                    } else {
                        j = lastTable.get(text.charAt(check));
                        start = check - j;
                        check = start + pattern.length() - 1;
                        j = pattern.length() - 1;
                    }
                } else {
                    start++;
                    check = start + pattern.length() - 1;
                    j = pattern.length() - 1;
                }
            }
        }
        return list;
    }

    /**
     * Builds last occurrence table that will be used to run the Boyer Moore
     * algorithm.
     *
     * Note that each char x will have an entry at table.get(x).
     * Each entry should be the last index of x where x is a particular
     * character in your pattern.
     * If x is not in the pattern, then the table will not contain the key x,
     * and you will have to check for that in your Boyer Moore implementation.
     *
     * Ex. pattern = octocat
     *
     * table.get(o) = 3
     * table.get(c) = 4
     * table.get(t) = 6
     * table.get(a) = 5
     * table.get(everything else) = null, which you will interpret in
     * Boyer-Moore as -1
     *
     * If the pattern is empty, return an empty map.
     *
     * @param pattern a pattern you are building last table for
     * @return a Map with keys of all of the characters in the pattern mapping
     * to their last occurrence in the pattern
     * @throws java.lang.IllegalArgumentException if the pattern is null
     */
    public static Map<Character, Integer> buildLastTable(CharSequence pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Cannot build last table with null pattern");
        }
        HashMap<Character, Integer> lastTable = new HashMap<Character, Integer>();
        for (int i = 0; i < pattern.length(); i++) {
            lastTable.put(pattern.charAt(i), i);
        }
        return lastTable;
    }

    /**
     * Prime base used for Rabin-Karp hashing.
     * DO NOT EDIT!
     */
    private static final int BASE = 113;

    /**
     * Runs the Rabin-Karp algorithm. This algorithms generates hashes for the
     * pattern and compares this hash to substrings of the text before doing
     * character by character comparisons.
     *
     * When the hashes are equal and you do character comparisons, compare
     * starting from the beginning of the pattern to the end, not from the end
     * to the beginning.
     *
     * You must use the Rabin-Karp Rolling Hash for this implementation. The
     * formula for it is:
     *
     * sum of: c * BASE ^ (pattern.length - 1 - i)
     *   c is the integer value of the current character, and
     *   i is the index of the character
     *
     * We recommend building the hash for the pattern and the first m characters
     * of the text by starting at index (m - 1) to efficiently exponentiate the
     * BASE. This allows you to avoid using Math.pow().
     *
     * Note that if you were dealing with very large numbers here, your hash
     * will likely overflow; you will not need to handle this case.
     * You may assume that all powers and calculations CAN be done without
     * overflow. However, be careful with how you carry out your calculations.
     * For example, if BASE^(m - 1) is a number that fits into an int, it's
     * possible for BASE^m will overflow. So, you would not want to do
     * BASE^m / BASE to calculate BASE^(m - 1).
     *
     * Ex. Hashing "bunn" as a substring of "bunny" with base 113
     * = (b * 113 ^ 3) + (u * 113 ^ 2) + (n * 113 ^ 1) + (n * 113 ^ 0)
     * = (98 * 113 ^ 3) + (117 * 113 ^ 2) + (110 * 113 ^ 1) + (110 * 113 ^ 0)
     * = 142910419
     *
     * Another key point of this algorithm is that updating the hash from
     * one substring to the next substring must be O(1). To update the hash,
     * subtract the oldChar times BASE raised to the length - 1, multiply by
     * BASE, and add the newChar as shown by this formula:
     * (oldHash - oldChar * BASE ^ (pattern.length - 1)) * BASE + newChar
     *
     * Ex. Shifting from "bunn" to "unny" in "bunny" with base 113
     * hash("unny") = (hash("bunn") - b * 113 ^ 3) * 113 + y
     *              = (142910419 - 98 * 113 ^ 3) * 113 + 121
     *              = 170236090
     *
     * Keep in mind that calculating exponents is not O(1) in general, so you'll
     * need to keep track of what BASE^(m - 1) is for updating the hash.
     *
     * Do NOT use Math.pow() in this method.
     * Do NOT implement your own version of Math.pow().
     *
     * @param pattern    a string you're searching for in a body of text
     * @param text       the body of text where you search for pattern
     * @param comparator you MUST use this to check if characters are equal
     * @return list containing the starting index for each match found
     * @throws java.lang.IllegalArgumentException if the pattern is null or has
     *                                            length 0
     * @throws java.lang.IllegalArgumentException if text or comparator is null
     */
    public static List<Integer> rabinKarp(CharSequence pattern,
                                          CharSequence text,
                                          CharacterComparator comparator) {
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("Invalid pattern for Rabin Karp");
        }
        if (text == null || comparator == null) {
            throw new IllegalArgumentException("Cannot perform Rabin Karp with null argument");
        }
        LinkedList<Integer> list = new LinkedList<Integer>();
        if (pattern.length() <= text.length()) {
            int patternHash = 0;
            int textHash = 0;
            int start = 0;
            int maxExponent = 0;
            for (int i = 0; i < pattern.length(); i++) {
                int exponent = 1;
                for (int j = pattern.length() - 1; j > i; j--) {
                    exponent *= BASE;
                }
                patternHash += pattern.charAt(i) * exponent;
                textHash += text.charAt(i) * exponent;
                if (exponent > maxExponent) {
                    maxExponent = exponent;
                }
            }

            while (start <= (text.length() - pattern.length())) {
                if (start != 0) {
                    textHash = (textHash - text.charAt(start - 1) * maxExponent) * BASE
                            + text.charAt(start + pattern.length() - 1);
                }
                if (patternHash == textHash) {
                    boolean matches = true;
                    for (int i = start; i < (start + pattern.length()); i++) {
                        if (comparator.compare(text.charAt(i), pattern.charAt(i - start)) != 0) {
                            matches = false;
                            break;
                        }
                    }
                    if (matches) {
                        list.add(start);
                    }
                }
                start++;
            }
        }
        return list;
    }
}
