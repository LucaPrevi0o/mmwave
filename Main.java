public class Main {

    private final static String DEFAULT_CAPTURE_DIR = "MMWL_Capture";
    private final static String DEFAULT_IP_ADDR = "192.168.33.180";
    private final static int DEFAULT_PORT = 5001;
    private final static int DEFAULT_RECORD_TIME = 30;

    private static final ArgParser argParser = new ArgParser("mmWave", "1.0", "mmWave CLI");

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

    public static void main(String... args) {

        Main.setArgs(); // Set the command line arguments
    }
}