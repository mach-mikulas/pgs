import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DataWriter {

    /**Path to the output file*/
    private final String outputFile;
    private BufferedWriter bWriter;


    /**
     * @param outputFile path to the output file
     */
    public DataWriter(String outputFile) {
        this.outputFile = outputFile;
        openFile();
    }

    /**Writes text to the output file*/
    public synchronized void writeText(String text){

        try {
            bWriter.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**Open file for writing*/
    public void openFile(){

        try {
            this.bWriter = new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**Closes the output file*/
    public void closeFile(){

        try {
            bWriter.flush();
            bWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
