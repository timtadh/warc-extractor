package cwru.mjq;

import org.apache.commons.cli.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by majunqi on 7/10/16.
 */
public class CommandLineUtil {

    public static final String number = "n";
    public static final String file = "file";
    public static final String outputDir = "outputDir";

    public static Map<String,String> parseOpts(String[] argv) {

        Map<String,String> args = new HashMap<>();

        final Option helpOpt = new Option("h", "help", false, "Print this message");
        final Option numberOpt = new Option("n", "number", true, "Input the number of pages");
        final Option fileOpt = new Option("f", "file", true, "Input the file of WARC");
        final Option outputDirOpt = new Option("o", "output-dir", true, "Input the output file destination");
        final Options options = new Options();

        options.addOption(helpOpt);
        options.addOption(numberOpt);
        options.addOption(fileOpt);
        options.addOption(outputDirOpt);

        try {
            GnuParser parser = new GnuParser();
            CommandLine line = parser.parse(options, argv);

            if (line.hasOption(helpOpt.getLongOpt())) {
                Usage(options);
            }
            if (!line.hasOption(numberOpt.getLongOpt())) {
                System.err.println("You must supply -n");
                Usage(options);
            }
            if (!line.hasOption(outputDirOpt.getLongOpt())) {
                System.err.println("You must supply -o");
                Usage(options);
            }
            if (!line.hasOption(fileOpt.getLongOpt())) {
                System.err.println("You must supply -f");
                Usage(options);
            }

            args.put(number, line.getOptionValue(numberOpt.getLongOpt()));
            args.put(outputDir, line.getOptionValue(outputDirOpt.getLongOpt()));
            args.put(file, line.getOptionValue(fileOpt.getLongOpt()));
        } catch (MissingOptionException e) {
            System.err.println(e.getMessage());
            Usage(options);
        } catch (UnrecognizedOptionException e) {
            System.err.println(e.getMessage());
            Usage(options);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return args;
    }

    private static void Usage(Options options) {
        new HelpFormatter().printHelp("Warc Extractor", options, true);
        System.exit(1);
    }
}
