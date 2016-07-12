package cwru.mjq;

import java.io.*;


public class MJQFileWriter {

    File f = null;


    public void openFile(String path) {
        f = new File(path);
    }

    public void writeFile(String content) throws IOException {
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.newLine();
        bw.close();
        fw.close();
    }

    public void writeIntoSameFile(String content) {
        try {
            FileWriter fw = new FileWriter(f, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.newLine();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
