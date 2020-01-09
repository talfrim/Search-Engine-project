package HandleSearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class FindDocData implements Runnable {
    private static Pattern splitByDotCom= Pattern.compile("[\\;]");
    private BufferedReader reader;
    private String docNo;
    private String docData;

    public FindDocData(BufferedReader reader, String docNo) {
        this.reader = reader;
        this.docNo = docNo;
        this.docData = null;
    }

    public String getDocData() {
        return docData;
    }

    @Override
    public void run() {
        String line = null;
        try {
            line = reader.readLine();
            while (line != null) {
                String [] postSplit = splitByDotCom.split(line);
                if(postSplit[0].equals(docNo)) {
                    this.docData = line;
                    return;
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
