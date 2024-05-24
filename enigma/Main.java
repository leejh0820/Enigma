package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import java.util.List;


import ucb.util.CommandArgs;


import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Jeonghyun Lee
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine machine = readConfig();
        String n = _input.nextLine();
        if (!n.contains("*")) {
            throw new EnigmaException("Have to contain *");
        }
        while (n != null) {
            if (n.contains("*")) {
                setUp(machine, n);
            } else if (n.equals("")) {
                _output.println();
            } else {
                n = n.replaceAll("\\s", "");
                printMessageLine(machine.convert(n));
            }
            if (_input.hasNextLine()) {
                n = _input.nextLine();
            } else {
                n = null;
            }
        }

    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.next());
            int a = _config.nextInt();
            int b = _config.nextInt();
            ArrayList<Rotor> rotor = new ArrayList<>();
            if (!_config.hasNext()) {
                throw new EnigmaException("Does noe Exist");
            }
            _ringStr = _config.next();
            while (_config.hasNext(".*")) {
                rotor.add(readRotor());
            }
            return new Machine(_alphabet, a, b, rotor);
        } catch (NoSuchElementException excp) {
            throw error("Configuration file Error");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _ringStr;
            String type = _config.next();
            String cycles = "";
            String n = _config.next();
            if (type == null || type.startsWith("(")) {
                throw new EnigmaException("Type Error");
            }
            while (n.startsWith("(") && _config.hasNext()) {
                cycles += n + " ";
                n = _config.next();
                _ringStr = n;
            }
            if (!_config.hasNext()) {
                cycles += n;
            }
            Permutation perm = new Permutation(cycles, _alphabet);
            if (type.charAt(0) == 'N') {
                return new FixedRotor(name, perm);
            } else if (type.charAt(0) == 'M') {
                return new MovingRotor(name, perm, type.substring(1));
            } else {
                return new Reflector(name, perm);
            }
        } catch (NoSuchElementException excp) {
            throw error("Bad Rotor");
        }
    }
    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] r = new String[M.numRotors()];
        Scanner setting = new Scanner(settings);
        setting.next();
        for (int i = 0; i < M.numRotors(); i++) {
            String ar = setting.next();
            r[i] = ar;
        }
        M.insertRotors(r);
        if (!setting.hasNext()) {
            throw new EnigmaException("rotor setting unable");
        }
        String sr = setting.next();
        if (sr.equals("") || sr.startsWith("(")) {
            throw new EnigmaException("rotor setting unable");
        }
        String quote = "";
        String quote2 = "";
        if (setting.hasNext()) {
            quote = setting.next();
        }
        if (!quote.equals("") && !quote.startsWith("(")) {
            sr = sr.concat(" " + quote);
        } else if (!quote.equals("")) {
            quote2 = quote2.concat(quote + " ");
        }
        while (setting.hasNext()) {
            quote2 = quote2.concat(setting.next() + " ");
        }
        M.setRotors(sr);
        if (quote2.length() != 0) {
            M.setPlugboard(new Permutation(quote2, _alphabet));
        } else {
            M.setPlugboard(new Permutation("", _alphabet));
        }
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i++) {
            _output.print(msg.charAt(i));
            if ((i != msg.length() - 1) && (i % 5 == 4)) {
                _output.print(" ");
            }
        }
        _output.print("\r\n");
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;
    /** */
    private String _ringStr = "";
}
