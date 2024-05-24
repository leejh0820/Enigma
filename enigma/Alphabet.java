package enigma;

import static enigma.EnigmaException.*;


/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Jeonghyun Lee
 */
class Alphabet {
    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _alphabet = chars;
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _alphabet.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return _alphabet.contains(Character.toString(ch));
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index > size() || index < 0) {
            throw new EnigmaException("character number index is out of range");
        }
        return _alphabet.charAt(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        if (!_alphabet.contains(Character.toString(ch))) {
            System.out.println(Character.toString(ch));
            throw new EnigmaException("No character in Alphabet");
        }
        return _alphabet.indexOf(ch);
    }
    /** */
    private String _alphabet;
}
