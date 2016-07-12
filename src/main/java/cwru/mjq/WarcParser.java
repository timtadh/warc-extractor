package cwru.mjq;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;


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

    public static class WriteError extends Error {
        public WriteError(String path, String msg) {
            super(String.format("Error writing file %s: %s", path, msg));
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
        Collections.sort(samples);
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
            throw new ReadError(this.file.getPath(), e.getMessage());
        }
        return count;
    }

    WarcRecord getNextRec(DataInputStream inStream) throws ReadError {
        try {
            return WarcRecord.readNextWarcRecord(inStream);
        } catch (IOException e) {
            throw new ReadError(this.file.getPath(), e.getMessage());
        }
    }

    public void sample(int sampleSize, String outputDir) throws Error {
        List<Integer> samples = randomSample(numberOfHtmlRecords(), sampleSize);
        System.out.println(samples);
        DataInputStream inf = gzipStream(this.file);
        int recordIdx = 0;
        int sampleIdx = 0;
        int toSample = samples.get(sampleIdx);
        WarcRecord r = getNextRec(inf);
        while (r != null) {
            if (r.getHeaderRecordType().equals("response") &&
                r.getHeaderMetadataItem("Content-Type").indexOf("application/http") != -1) {
                if (toSample == recordIdx) {
                    System.out.println(String.format("sampling %d %d %d", sampleIdx, toSample, recordIdx));
                    write(sampleIdx, r, outputDir);
                    sampleIdx++;
                    toSample = samples.get(sampleIdx);
                }
                recordIdx++;
            }
            r = getNextRec(inf);
        }
    }

    public void write(int i, WarcRecord r, String outputDir) throws WriteError {
        String path = outputDir + "/" + i + ".html";
        try (FileOutputStream ouf = new FileOutputStream(path)) {
            ouf.write(r.getContent());
        } catch (FileNotFoundException e) {
            throw new WriteError(path, e.getMessage());
        } catch (IOException e) {
            throw new WriteError(path, e.getMessage());
        }
    }
}
