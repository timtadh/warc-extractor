package cwru.mjq;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by majunqi0102 on 7/8/16.
 */

public class ParserWarc {
    private static final Logger logger = LoggerFactory
            .getLogger(ParserWarc.class);

    private File file = null;
    private GZIPInputStream gzInputStream1 = null;
    private GZIPInputStream gzInputStream2 = null;
    private DataInputStream inStream = null;
    private DataInputStream inStream2 = null;
    private WarcRecord thisWarcRecord = null;
    private WarcRecord thisWarcRecord2 = null;
    private List<Integer> indexOfHtmlList = null;
    private int numberOfHtml = 0;

    public ParserWarc(File file) {
        super();
        this.file = file;
        try {
            gzInputStream1 = new GZIPInputStream(new FileInputStream(this.file));
            gzInputStream2 = new GZIPInputStream(new FileInputStream(this.file));
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        inStream = new DataInputStream(gzInputStream1);
        inStream2 = new DataInputStream(gzInputStream2);
    }

    private void getNumberOfHtml() {
        indexOfHtmlList = new ArrayList<>();

        try {
            while ((thisWarcRecord = WarcRecord.readNextWarcRecord(inStream)) != null) {
                if (thisWarcRecord.getHeaderRecordType().equals("response") && thisWarcRecord.getHeaderMetadataItem("Content-Type").indexOf("application/http") != -1) {
                    indexOfHtmlList.add(numberOfHtml);
                }
                numberOfHtml++;
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public HashSet<HtmlEntity> getHtmlSet(int number) throws IOException {
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

                        /*
                         * for (Entry<String, String> per :
                         * httpHeaderMap.entrySet()) {
                         * System.out.println(per.getKey()+":"+per.getValue());
                         * }
                         */

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
    }
}
