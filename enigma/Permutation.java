package enigma;



import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Jeonghyun Lee
 */
class Permutation {

    /**
     * Set this Permutation to that specified by CYCLES, a string in the
     * form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     * is interpreted as a permutation in cycle notation.  Characters in the
     * alphabet that are not included in any cycle map to themselves.
     * Whitespace is ignored.
     */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;


    }


    public String[] split(String str) {
        return str.replaceAll("[()]", "").split("\\s");
    }

        /**
         * Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
         * c0c1...cm.
         */
    private void addCycle(String cycle) {
        _cycles += cycle;

    }

    /**
     * Return the value of P modulo the size of this permutation.
     */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /**
     * Returns the size of the alphabet I permute.
     */
    int size() {
        return _alphabet.size();
    }

    /**
     * Return the result of applying this permutation to P modulo the
     * alphabet size.
     */
    int permute(int p) {
        String[] split = cycleSplitter(_cycles);
        char ch = _alphabet.toChar(wrap(p));
        for (int i = 0; i < split.length; i++) {
            for (int j = 0; j < split[i].length(); j++) {
                if (split[i].charAt(j) == ch && (j + 1)
                        == split[i].length()) {
                    ch = split[i].charAt(0);
                    return _alphabet.toInt(ch);
                } else if (split[i].charAt(j) == ch) {
                    ch = split[i].charAt(j + 1);
                    return _alphabet.toInt(ch);
                }
            }
        }
        return _alphabet.toInt(ch);
    }

    public static String[] cycleSplitter(String cycles) {
        String[] result;
        String space = cycles.replaceAll("[()]", "");
        result = space.split(" ");
        return result;
    }

    /**
     * Return the result of applying the inverse of this permutation
     * to  C modulo the alphabet size.
     */
    int invert(int c) {
        String[] split = cycleSplitter(_cycles);
        char ch = _alphabet.toChar(wrap(c));
        for (int i = 0; i < split.length; i++) {
            for (int j = 0; j < split[i].length(); j++) {
                if (split[i].charAt(j) == ch && j == 0) {
                    ch = split[i].charAt
                            (split[i].length() - 1);
                    return _alphabet.toInt(ch);
                } else if (split[i].charAt(j) == ch) {
                    ch = split[i].charAt(j - 1);
                    return _alphabet.toInt(ch);
                }
            }
        }
        return _alphabet.toInt(ch);
    }




    /**
     * Return the result of applying this permutation to the index of P
     * in ALPHABET, and converting the result to a character of ALPHABET.
     */
    char permute(char p) {
        int a = permute(_alphabet.toInt(p));
        return _alphabet.toChar(a);
    }

    /**
     * Return the result of applying the inverse of this permutation to C.
     */
    char invert(char c) {
        int a = invert(_alphabet.toInt(c));
        return _alphabet.toChar(a);
    }

    /**
     * Return the alphabet used to initialize this Permutation.
     */
    Alphabet alphabet() {
        return _alphabet;
    }

    /**
     * Return true iff this permutation is a derangement (i.e., a
     * permutation for which no value maps to itself).
     */
    boolean derangement() {
        String[] split = cycleSplitter(_cycles);
        int count = 0;
        for (int i = 0; i < split.length; i++) {
            if (split[i].length() == 1) {
                return false;
            }
            for (int j = 0; j < split[i].length(); j++) {
                count += 1;
            }
        }
        return count == size();
    }



    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** */
    private String _cycles;
}
