package cwru.mjq;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.HashSet;
import java.util.Iterator;

public class WarcExtractor {

    public static int number;
    public static String url = null;
    public static String file = null;
    public static String outputDir = null;

    public static void main(String[] argv) throws WarcParser.Error {
        parseArgs(argv);

        System.out.println("It's running, it may take some time.");
        sampleWarc(number, file, outputDir);
    }

    private static void sampleWarc(int number, String file, String outputDir) throws WarcParser.Error {
        WarcParser wp = new WarcParser(new File(file));
        wp.sample(number, outputDir);

    }

    private static void parseArgs(String[] argv) {
        Map<String,String> args = CommandLineUtil.parseOpts(argv);
        number = Integer.parseInt(args.get(CommandLineUtil.number));
        file = args.get(CommandLineUtil.file);
        outputDir = args.get(CommandLineUtil.outputDir);
        File directory = new File(outputDir);
        if (!directory.exists()){
            directory.mkdir();
        }
    }
}

