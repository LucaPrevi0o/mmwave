package arg;
import java.util.Iterator;

/**
 * ArgParser.java
 * Argument parser for command line applications.
 * This class provides a way to define command line arguments and options,
 * parse them from the command line, and retrieve their values.
 * It supports different types of arguments, including strings, integers,
 * floats, and booleans.
 */
public class ArgParser {
    
    /**
     * Argument class represents a command line argument or option.
     * It contains information about the argument, such as its name,
     * type, default value, and whether it has been set.
     */
    public class Argument implements Iterable<Argument> {

        /**
         * Type enum represents the different types of command line arguments.
         * It includes STRING, INTEGER, FLOAT, and BOOLEAN types.
         */
        public enum Type {

            STRING, INTEGER, FLOAT, BOOLEAN;

            public String toString() {

                switch (this) { // Return the string representation of the type

                    case STRING: return "STRING";
                    case INTEGER: return "INTEGER";
                    case FLOAT: return "FLOAT";
                    case BOOLEAN: return "BOOLEAN";
                    default: return "UNKNOWN";
                }
            }
        }

        /**
         * CliOption class represents a command line option.
         * It contains information about the option, such as its arguments,
         * long name, help description, type, and default value.
         */
        public class CliOption {

            private String args; // Short name of the option
            private String argl; // Long name of the option
            private String help; // Help description of the option
            private Type type;   // Type of the option (STRING, INTEGER, FLOAT, BOOLEAN)

            private Object value; // Value of the option
            private Object defaultValue; // Default value of the option

            /**
             * Constructor for CliOption.
             * @param args Short name of the option
             * @param argl Long name of the option
             * @param help Help description of the option
             * @param type Type of the option (STRING, INTEGER, FLOAT, BOOLEAN)
             */
            public CliOption(String args, String argl, String help, Type type) {

                this.args = args;
                this.argl = argl;
                this.help = help;
                this.type = type;
            }
        }
        
        private CliOption cliOption; // Command line option
        private boolean isSet; // Flag indicating if the option has been set
        private Argument next; // Pointer to the next argument in the linked list

        /**
         * Constructor for Argument.
         */
        public Argument() {

            this.cliOption = null; // Initialize cliOption to null
            this.isSet = false; // Initialize isSet flag to false
            this.next = null; // Initialize next pointer to null
        }

        public void set() { this.isSet = true; } // Set the isSet flag to true
        public boolean isSet() { return this.isSet; } // Get the value of the isSet flag
        public CliOption getCliOption() { return this.cliOption; } // Get the CliOption object
        public String getArgs() { return this.cliOption.args; } // Get the short name of the option
        public String getArgl() { return this.cliOption.argl; } // Get the long name of the option
        public String getHelp() { return this.cliOption.help; } // Get the help description of the option
        public Type getType() { return this.cliOption.type; } // Get the type of the option
        public Object getValue() { return this.cliOption.value; } // Get the value of the option
        public Object getDefaultValue() { return this.cliOption.defaultValue; } // Get the default value of the option

        public void setValue(Object value) { this.cliOption.value = value; } // Set the value of the option
        public void setDefaultValue(Object defaultValue) { this.cliOption.defaultValue = defaultValue; } // Set the default value of the option

        @Override
        public Iterator<ArgParser.Argument> iterator() {
            
            return new Iterator<Argument>() { // Create an iterator for the Argument class

                private Argument current = head; // Start from the head of the list

                @Override
                public boolean hasNext() { return current != null; } // Check if there is a next argument

                @Override
                public Argument next() { // Get the next argument
                    Argument temp = current;
                    current = current.next;
                    return temp;
                }
            };
        }
    }

    private String name;
    private String version;
    private String description;
    private Argument head;

    /**
     * Adds a new command line argument to the parser.
     * @param args Short name of the argument
     * @param argl Long name of the argument
     * @param help Help description of the argument
     * @param type Type of the argument (STRING, INTEGER, FLOAT, BOOLEAN)
     */
    public void addArgument(String args, String argl, String help, Argument.Type type) {

        Argument argument = new Argument(); // Create a new Argument object
        argument.isSet = false; // Initialize the isSet flag to false
        argument.cliOption = argument.new CliOption(args, argl, help, type); // Create a new CliOption object
        if (head == null) head = argument; // If head is null, set the new argument as the head of the list
        else {

            Argument current = head; // Start from the head of the list
            while (current.next != null) current = current.next; // Traverse to the end of the list
            current.next = argument; // Set the new argument as the next argument in the list
        }
    }

    /**
     * Retrieves the command line argument with the specified short name.
     * @param args Short name of the argument
     * @return The Argument object if found, <code>null</code> otherwise
     */
    public Argument getArgument(String args) {

        Argument current = head; // Start from the head of the list
        while (current != null) { // Traverse the list

            if (current.cliOption.args.equals(args)) return current; // Return the argument if found
            current = current.next; // Move to the next argument
        }
        return null; // Return null if not found
    }

    public String getName() { return name; }
    public String getVersion() { return version; }
    public String getDescription() { return description; }
    public Argument getHead() { return head; }

    public void parse(String[] args) {

        for (int i = 0; i < args.length; i++) { // Iterate through the command line arguments

            Argument argument = getArgument(args[i]); // Get the argument object
            if (argument != null) { // If the argument is found

                argument.set(); // Set the isSet flag to true
                if (argument.getType() == Argument.Type.BOOLEAN) continue; // If the type is BOOLEAN, continue to the next argument
                if (i + 1 < args.length) { // If there is a next argument

                    switch (argument.getType()) { // Set the value based on the type
                        case STRING:

                            argument.setValue(args[i + 1]); // Set the default value to the next argument
                            break;
                        case INTEGER:

                            try {
                                argument.setValue(Integer.parseInt(args[i + 1])); // Set the default value to the next argument as an integer
                            } catch (NumberFormatException e) {
                                throw new IllegalArgumentException("Invalid integer value: " + args[i + 1]); // Print error message if parsing fails
                                //System.err.println("Invalid integer value: " + args[i + 1]); // Print error message if parsing fails
                            }
                            break;
                        case FLOAT:

                            try {
                                argument.setValue(Float.parseFloat(args[i + 1])); // Set the default value to the next argument as a float
                            } catch (NumberFormatException e) {
                                throw new IllegalArgumentException("Invalid float value: " + args[i + 1]); // Print error message if parsing fails
                                //System.err.println("Invalid float value: " + args[i + 1]); // Print error message if parsing fails
                            }
                            break;
                        default:

                            throw new IllegalArgumentException("Unknown argument type: " + argument.getType()); // Print error message for unknown type
                            //System.err.println("ERROR: Unknown argument type: " + argument.getType()); // Print error message for unknown type
                            //break;
                    }
                    i++; // Move to the next argument
                } else {
                    throw new IllegalArgumentException("Missing value for argument: " + args[i]); // Throw exception for missing value
                }
            } else System.err.println("WARNING: Unknown argument: " + args[i]); // Print error message if the argument is not found
        }
    }

    /**
     * Constructor for ArgParser.
     * @param name Name of the application
     * @param version Version of the application
     * @param description Description of the application
     */
    public ArgParser(String name, String version, String description) {

        this.name = name;
        this.version = version;
        this.description = description;
    }
}
