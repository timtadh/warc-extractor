package cwru.mjq;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by majunqi on 7/10/16.
 */
public class CommandLineUtil {

    private static final String number = "n";
    private static final String url = "url";
    private static final String file = "file";
    private static final String outputDir = "outputDir";

    public static String getNumber() {
        return number;
    }

    public static String getUrl() {
        return url;
    }

    public static String getFile() {
        return file;
    }

    public static String getOutputDir() {
        return outputDir;
    }

    public static void commandLineInput(String[] argv, HashMap<String, String> argsMap) throws IOException {

        final Option helpOpt = new Option("h", "help", false, "Print this message");
        final Option numberOpt = new Option("n", "number", true, "Input the number of pages");
        final Option urlOpt = new Option("u", "url", true, "Input the url of WARC");
        final Option fileOpt = new Option("f", "file", true, "Input the file of WARC");
        final Option outputDirOpt = new Option("o", "output-dir", true, "Input the output file destination");
        final Options options = new Options();

        options.addOption(helpOpt);
        options.addOption(numberOpt);
        options.addOption(urlOpt);
        options.addOption(fileOpt);
        options.addOption(outputDirOpt);

        try {
            GnuParser parser = new GnuParser();
            CommandLine line = parser.parse(options, argv);

            if (line.hasOption(helpOpt.getLongOpt())) {
                Usage(options);
            }

            if (line.hasOption(numberOpt.getLongOpt()) && line.hasOption(outputDirOpt.getLongOpt())) {
                String nValue = line.getOptionValue(numberOpt.getLongOpt());
                String outputDirValue = line.getOptionValue(outputDirOpt.getLongOpt());
                String fileValue;
                String urlValue;

                argsMap.put(number, nValue);
                argsMap.put(outputDir, outputDirValue);

                if ((line.hasOption(urlOpt.getLongOpt())) == true && (line.hasOption(fileOpt.getLongOpt())) == true) {
                    throw new RuntimeException();
                } else if (line.hasOption(fileOpt.getLongOpt())) {
                    fileValue = line.getOptionValue(fileOpt.getLongOpt());
                    argsMap.put(file, fileValue);
                } else if (line.hasOption(urlOpt.getLongOpt())) {
                    urlValue = line.getOptionValue(urlOpt.getLongOpt());
                    argsMap.put(url, urlValue);
                } else if ((line.hasOption(urlOpt.getLongOpt())) == false && (line.hasOption(fileOpt.getLongOpt())) == false) {
                    throw new RuntimeException();
                }
            } else {
                throw new NullPointerException();
            }

        } catch (final MissingOptionException e) {
            System.err.println("1" + e.getMessage());
            Usage(options);
        } catch (final UnrecognizedOptionException e) {
            System.err.println("2" + e.getMessage());
            Usage(options);
        } catch (final ParseException e) {
            System.err.println("3" + e.getMessage());
            System.exit(1);
        } catch (final NullPointerException e) {
            System.err.println("You must supply some tokens");
            Usage(options);
        } catch (final RuntimeException e) {
            System.err.println("You must supply at least file or url or You can supply both file and url");
            Usage(options);
        }

    }

    private static void Usage(Options options) {
        new HelpFormatter().printHelp("Warc Extractor", options, true);
        System.exit(1);
    }


    private static String readInput() throws IOException {
        InputStreamReader stdin = new InputStreamReader(System.in);
        StringBuilder sb = new StringBuilder();
        char[] chars = new char[4096];
        int read = readChars(stdin, sb, chars);
        while (read > -1) {
            read = readChars(stdin, sb, chars);
        }
        return sb.toString();
    }

    private static int readChars(InputStreamReader stdin, StringBuilder sb, char[] chars) throws IOException {
        int read = stdin.read(chars);
        if (read > 0) {
            sb.append(chars, 0, read);
        }
        return read;
    }
}
