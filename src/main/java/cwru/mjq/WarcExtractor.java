package cwru.mjq;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class WarcExtractor {

    public static int number;
    public static String url = null;
    public static String file = null;
    public static String outputDir = null;

    public static void main(String[] args) throws IOException {

        HashMap<String, String> argsMap = new HashMap<>();
        CommandLineUtil.commandLineInput(args, argsMap);
        setAllArgs(argsMap);
        if (file != null){
            System.out.println("It's running, it may take some time.");
            findHtmlBodyInWarchRecord(number, file, outputDir, true);
        }
        else if (url != null) {
            System.out.println("It's running, it may take some time.");
//            findHtmlBodyInWarchRecord(number, url, outputDir, false);
        }
        /*
        String file = "/home/majunqi/IdeaProjects/WarcExtractor/CC-MAIN-20151124205404-00008-ip-10-71-132-137.ec2.internal.warc.gz";
        String outputDir ="/home/majunqi/IdeaProjects/WarcExtractor/Result";
        findHtmlBodyInWarchRecord(20, file, outputDir, true);
        */
    }

    private static void findHtmlBodyInWarchRecord(int number, String file, String outputDir, Boolean isFile) throws IOException {
        File f;
        WarcParser pw;

        if (isFile) {
            f = new File(file);
            if (f == null)
                throw new NullPointerException();
            pw = new WarcParser(f);

        } else {
            f = new File("CC-MAIN-20151124205404-00008-ip-10-71-132-137.ec2.internal.warc.gz");
            pw = new WarcParser(f);
        }

        HashSet<HtmlEntity> htmlEntityHashSet = pw.getHtmlSet(number);
        MJQFileWriter fw = new MJQFileWriter();
        Iterator it = htmlEntityHashSet.iterator();
        int i = 1;
        while (it.hasNext()) {
            HtmlEntity html = (HtmlEntity) it.next();
            fw.openFile(outputDir + "/" + i + ".html");
            try {
                fw.writeFile(html.getContent());
                i++;
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

        }

        if (i != 0)
            System.out.println(i-1 + " files has been created in the directory : " + outputDir);
    }

    private static void setAllArgs(HashMap<String, String> argsMap) {
        number = Integer.parseInt(argsMap.get(CommandLineUtil.getNumber()));
        url = argsMap.get(CommandLineUtil.getUrl());
        file = argsMap.get(CommandLineUtil.getFile());
        outputDir = argsMap.get(CommandLineUtil.getOutputDir());
    }


}

