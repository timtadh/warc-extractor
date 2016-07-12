package cwru.mjq;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;


/**
 * Created by majunqi0102 on 7/8/16.
 */

public class WarcParser {

    public static class Error extends Exception {
        public Error(String msg) {
            super(msg);
        }
    }

    public static class NotZip extends Error {
        public NotZip(String path) {
            super(String.format("File %s was not a gzip file", path));
        }
    }

    public static class ReadError extends Error {
        public ReadError(String path, String msg) {
            super(String.format("Error reading file %s: %s", path, msg));
        }
    }

    // private static final Logger logger = LoggerFactory
    //         .getLogger(ParserWarc.class);

    private File file = null;

    public WarcParser(File file) {
        this.file = file;
    }

    static List<Integer> randomSample(int populationSize, int sampleSize) {
        if (populationSize < 1 || sampleSize < 1) {
            throw new RuntimeException(String.format(
                  "randomSample arguments out of bounds %d %d",
                  populationSize, sampleSize));
        }
        if (sampleSize > populationSize) {
            sampleSize = populationSize;
        }
        Random random = new SecureRandom();
        List<Integer> samples = new ArrayList<>(sampleSize);
        Set<Integer> seen = new HashSet<>(sampleSize);
        for (int i = 0; i < sampleSize; i++) {
            int s = random.nextInt(populationSize);
            while (seen.contains(s)) {
                s = random.nextInt(populationSize);
            }
            samples.add(s);
            seen.add(s);
        }
        return samples;
    }

    DataInputStream gzipStream(File f) throws Error {
        try {
            return new DataInputStream(new GZIPInputStream(new FileInputStream(this.file)));
        } catch (ZipException e) {
            throw new NotZip(this.file.getPath());
        } catch (IOException e) {
            throw new ReadError(this.file.getPath(), e.toString());
        }
    }

    private int numberOfHtmlRecords() throws Error {
        DataInputStream inStream = gzipStream(this.file);
        int count = 0;
        WarcRecord r;
        try {
            while ((r = WarcRecord.readNextWarcRecord(inStream)) != null) {
                if (r.getHeaderRecordType().equals("response") &&
                    r.getHeaderMetadataItem("Content-Type").indexOf("application/http") != -1) {
                    count++;
                }
            }
        } catch (IOException e) {
            throw new ReadError(this.file.getPath(), e.toString());
        }
        return count;
    }

    public HashSet<HtmlEntity> getHtmlSet(int number) throws IOException {
      throw new RuntimeException("unimplemented");
      /*
        HashSet<HtmlEntity> htmlEntitySet = new HashSet<HtmlEntity>();
        getNumberOfHtml();
        if (number > indexOfHtmlList.size())
            throw new RuntimeException();

        Random random = new Random();
        IntStream intStream = random.ints(0, indexOfHtmlList.size());
        List<Integer> randomIndexOfHtml = intStream.limit(number).boxed().collect(Collectors.toList());
        Collections.sort(randomIndexOfHtml);


        int i = 0;
        int j = 0;
        try {
            while (((thisWarcRecord2 = WarcRecord.readNextWarcRecord(inStream2)) != null) && j < number) {
                try {
                    // see if it's a response record
                    if (i == indexOfHtmlList.get(randomIndexOfHtml.get(j)).intValue()) {
                        if (thisWarcRecord2.getHeaderRecordType().equals("response") && thisWarcRecord2.getHeaderMetadataItem("Content-Type").indexOf("application/http") != -1) {
                            // it is - create a WarcHTML record
                            WarcHTMLResponseRecord htmlRecord = new WarcHTMLResponseRecord(thisWarcRecord2);
                            // get our TREC ID and target URI
                            String thisTargetURI = htmlRecord.getTargetURI();

                            InputStreamReader in = new InputStreamReader(new ByteArrayInputStream(thisWarcRecord2.getContent()));

                            BufferedReader br = new BufferedReader(in);

                            String line = br.readLine();
                            Map<String, String> httpHeaderMap = new HashMap<String, String>();
                            while (!(line = br.readLine()).isEmpty()) {
                                int temp = line.indexOf(":");
                                httpHeaderMap.put(line.substring(0, temp).trim(),
                                        line.substring(temp + 1).trim());
                            }

                            StringBuffer sb = new StringBuffer();
                            while ((line = br.readLine()) != null)
                                sb.append(line);

                            // System.out.println(thisWarcRecord.getContentUTF8());
                            if (httpHeaderMap.get("Content-Type").indexOf("text/html") == -1)
                                continue;
/

                            HtmlEntity htmlEntity = new HtmlEntity();

                            htmlEntity.setHeaderMap(httpHeaderMap);
                            htmlEntity.setContent(sb.toString());
                            htmlEntity.setUrl(thisTargetURI);
                            br.close();
                            in.close();

                            htmlEntitySet.add(htmlEntity);
                            j++;
                        }
                    }
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());

                }
            }

            try {
                inStream.close();
                inStream2.close();
                gzInputStream1.close();
                gzInputStream2.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
            return htmlEntitySet;
        } catch (Exception e) {
            //if the warc file doesn't have html it will show the errors
            System.err.println(e.getMessage());
//            e.printStackTrace();
            return null;
        }
    */
    }
}
