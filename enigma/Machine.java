package enigma;


import java.util.ArrayList;
import java.util.Collection;


import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Jeonghyun Lee
 */
class Machine {

    /**
     * A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     * and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     * available rotors.
     */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = new ArrayList<Rotor>(allRotors);
    }

    /**
     * Return the number of rotor slots I have.
     */
    int numRotors() {
        return _numRotors;
    }

    /**
     * Return the number pawls (and thus rotating rotors) I have.
     */
    int numPawls() {
        return _pawls;
    }

    /**
     * Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     * #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     * undefined results.
     */
    Rotor getRotor(int k) {
        return _rotors.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /**
     * Set my rotor slots to the rotors named ROTORS from my set of
     * available rotors (ROTORS[0] names the reflector).
     * Initially, all rotors are set at their 0 setting.
     */
    void insertRotors(String[] rotors) {
        _rotors = new ArrayList<Rotor>(_allRotors.size());
        int count = 0;
        for (String name : rotors) {
            int a = _rotors.size();
            for (Rotor rotor : _allRotors) {
                if (rotor.name().equals(name)) {
                    _rotors.add(rotor);
                    if (rotor.rotates()) {
                        count += 1;
                    }
                }
            }
            if (_rotors.size() != a + 1) {
                throw new EnigmaException("Rotor does not exist.");
            }
        }

        if (!_rotors.get(0).reflecting()) {
            throw new EnigmaException("Rotor must be a reflector.");
        }
        if (count > numPawls()) {
            throw new EnigmaException("Many Rotor Exist.");
        }
        if (_rotors == null) {
            throw new EnigmaException("Need at least one Rotor");
        }
    }





    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 0; i < setting.length(); i++) {
            _rotors.get(i + 1).set(setting.charAt(i));
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        ArrayList<Boolean> ab = new ArrayList<Boolean>(_rotors.size());
        for (int i = 0; i < _rotors.size() - 1; i++) {
            ab.add(false);
        }
        ab.add(true);
        Rotor rotor2 = _rotors.get(_rotors.size() - 1);
        assert rotor2.rotates();
        for (int i = _rotors.size() - 1; i > 0; i--) {
            Rotor c = _rotors.get(i);
            Rotor n = _rotors.get(i - 1);
            if (!c.rotates()) {
                continue;
            }
            if (c.atNotch() && n.rotates()) {
                ab.set(i, true);
                ab.set(i - 1, true);
            }
        }
        for (int i = 0; i < _rotors.size(); i++) {
            if (ab.get(i)) {
                _rotors.get(i).advance();
            }
        }
        if (_rotors == null) {
            throw new EnigmaException("Need at least one Rotor");
        }
    }



    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        int character = c;
        for (int i = _rotors.size() - 1; i > 0; i--) {
            character = _rotors.get(i).convertForward(character);
        }
        for (int i = 0; i < _rotors.size(); i++) {
            character = _rotors.get(i).convertBackward(character);
        }
        return character;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            int input = _alphabet.toInt(msg.charAt(i));
            int output = convert(input);
            result += _alphabet.toChar(output);
        }
        return result;
    }


    void settingRotor(String rString) {
        if (rString == "") {
            for (int i = 0; i < _rotors.size() - 1; i++) {
                rString = rString + _alphabet.toChar(0);
            }
        }
        for (int i = 0; i < _rotors.size() - 1; i++) {
            _rotors.get(i + 1).settingRing(rString.charAt(i));
        }
    }





    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /**  */
    private int _numRotors;
    /**  */
    private int _pawls;
    /**  */
    private ArrayList<Rotor> _allRotors;
    /**  */
    private ArrayList<Rotor> _rotors;
    /**  */
    private Permutation _plugboard;



}
