import arg.ArgParser;
import java.net.Socket;

public class Main {

    private static String getDefaultCaptureDir() {

        var dir = "MMWL_Capture_"; // Default capture directory name

        var date = java.time.LocalDate.now(); // Get the current date
        dir += date.getYear();
        dir += (date.getMonthValue() < 10 ? "0" + date.getMonthValue() : date.getMonthValue());
        dir += date.getDayOfMonth() + "_";

        var time = java.time.LocalTime.now(); // Get the current time
        dir += time.getHour() + "h";
        dir += time.getMinute() + "m";
        dir += time.getSecond() + "s";
        return dir;
    }

    private final static String DEFAULT_CAPTURE_DIR = Main.getDefaultCaptureDir();
    private final static String DEFAULT_IP_ADDR = "192.168.33.180";
    private final static int DEFAULT_PORT = 5001;
    private final static int DEFAULT_RECORD_TIME = 30;

    private static String captureDir;
    private static String ipAddr;
    private static int port;
    private static int recordTime;
    private static Socket socket;

    private static final ArgParser argParser = new ArgParser("mmWave", "1.0", "Configuration and control tool for TI MMWave cascade Evaluation Module");

    /**
     * Set the command line arguments for the mmWave CLI.
     */
    private static void setArgs() {

        argParser.addArgument("-d", "--capture-dir", "Name of the director where to store recordings on the DSP board. Default: 'MMWL_Capture_<timestamp>'", ArgParser.Argument.Type.STRING);
        argParser.getArgument("-d").setDefaultValue(DEFAULT_CAPTURE_DIR);

        argParser.addArgument("-p", "--port", "Port number the DSP board server app is listening on. Default: 5001", ArgParser.Argument.Type.INTEGER);
        argParser.getArgument("-p").setDefaultValue(DEFAULT_PORT);

        argParser.addArgument("-i", "--ip-addr", "IP Address of the MMWCAS DSP evaluation module. Default: '192.168.33.180'", ArgParser.Argument.Type.STRING);
        argParser.getArgument("-i").setDefaultValue(DEFAULT_IP_ADDR);

        argParser.addArgument("-c", "--configure", "Configure the MMWCAS-RF-EVM board", ArgParser.Argument.Type.BOOLEAN);
        argParser.addArgument("-r", "--record", "Trigger data recording. This assumes that configuration is completed.", ArgParser.Argument.Type.BOOLEAN);
        argParser.addArgument("-t", "--time", "Indicate how long the recording should last in seconds. Default: 30 seconds", ArgParser.Argument.Type.FLOAT);
        argParser.getArgument("-t").setDefaultValue(DEFAULT_RECORD_TIME);

        argParser.addArgument("-f", "--cfg", "TOML config file (see 'TOML config files' for details). Overwrite the default config when provided", ArgParser.Argument.Type.STRING);
        argParser.addArgument("-h", "--help", "Print CLI option help and exit.", ArgParser.Argument.Type.BOOLEAN);
        argParser.addArgument("-v", "--version", "Print program version and exit.", ArgParser.Argument.Type.BOOLEAN);
    }

    /**
     * Print the help message for the command line arguments.
     */
    private static void printHelp() {

        System.out.println(argParser.getName() + " - Version " + argParser.getVersion()); // Print the name and version of the application
        System.out.println(argParser.getDescription() + "\n"); // Print the description of the application
        System.out.println("Options:");
        for (ArgParser.Argument argument : argParser.getHead()) { // Iterate through the list of arguments

            System.out.print("  " + argument.getArgs() + " | " + argument.getArgl()); // Print the argument names
            for (int i = argument.getArgs().length() + argument.getArgl().length() ; i < 20; i++) System.out.print(" "); // Print spaces for alignment
            System.out.println(argument.getHelp()); // Print the help message for the argument
        }
        System.exit(0); // Exit the program after printing help
    }

    /**
     * Print the version of the application.
     */
    private static void printVersion() {

        System.out.println(argParser.getName() + " - Version " + argParser.getVersion()); // Print the name and version of the application
        System.exit(0); // Exit the program after printing version
    }

    private static void initDevice(int deviceIndex) {

        try { 

            
        } catch (Exception e) { // Catch any exceptions that occur during connection

            System.err.println("ERROR: Failed to connect to the MMWCAS-RF-EVM board: " + e.getMessage()); // Print error message
            System.exit(1); // Exit with error code
        }
    }

    private static void initMaster() {

        var devicesIndex = 15; // Index of the devices (15 as an example - all devices activated)
        initDevice(devicesIndex);
    }

    /**
     * Application entry point.
     * @param args Command line arguments
     * @throws Exception If an error occurs during execution
     */
    public static void main(String... args) {

        if (args.length == 0) { // If no arguments are provided
            System.err.println("ERROR: No command line arguments provided. Use -h or --help for usage information."); // Print error message
            System.exit(1); // Exit with error code
        }

        Main.setArgs(); // Set the command line arguments
        argParser.parse(args); // Parse the command line arguments
        if (argParser.getArgument("-h").isSet()) Main.printHelp(); // Print help message
        if (argParser.getArgument("-v").isSet()) Main.printVersion(); // Print version message

        if (argParser.getArgument("-d").isSet()) // If the capture directory argument is set
            captureDir = (String)argParser.getArgument("-d").getValue(); // Get the value of the capture directory argument
        else captureDir = (String)argParser.getArgument("-d").getDefaultValue(); // Get the default value of the capture directory argument

        if (argParser.getArgument("-i").isSet()) // If the IP address argument is set
            ipAddr = (String)argParser.getArgument("-i").getValue(); // Get the value of the IP address argument
        else ipAddr = (String)argParser.getArgument("-i").getDefaultValue(); // Get the default value of the IP address argument

        if (argParser.getArgument("-p").isSet()) // If the port argument is set
            port = (int)argParser.getArgument("-p").getValue(); // Get the value of the port argument
        else port = (int)argParser.getArgument("-p").getDefaultValue(); // Get the default value of the port argument

        if (argParser.getArgument("-t").isSet()) // If the record time argument is set
            recordTime = (int)argParser.getArgument("-t").getValue() * 1000; // Get the value of the record time argument
        else recordTime = (int)argParser.getArgument("-t").getDefaultValue() * 1000; // Get the default value of the record time argument

        System.out.println("Capture Directory: " + captureDir); // Print the capture directory
        System.out.println("IP Address: " + ipAddr); // Print the IP address
        System.out.println("Port: " + port); // Print the port number
        System.out.println("Record Time: " + recordTime); // Print the record time

        if (argParser.getArgument("-c").isSet()) try { // If the configure argument is set

            System.out.println("Configuring the MMWCAS-RF-EVM board..."); // Print configuration message
            socket = new Socket(ipAddr, port); // Connect to the MMWCAS-RF-EVM board
            System.out.println("Connected to the MMWCAS-RF-EVM board at " + ipAddr + ":" + port); // Print connection message
            
            Main.initMaster();
        } catch (Exception e) { // Catch any exceptions that occur during configuration

            System.err.println("ERROR: Failed to configure the MMWCAS-RF-EVM board: " + e.getMessage()); // Print error message
            System.exit(1); // Exit with error code
        }
    }
}