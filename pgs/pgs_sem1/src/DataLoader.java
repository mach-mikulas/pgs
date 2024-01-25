import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Loads resources from input file
 */
public class DataLoader {

    /**Original args*/
    private final String[] args;
    /**Number of loaded resources from input file*/
    private int numOfLoadedResources = 0;

    public DataLoader(String[] args) {
        this.args = args;
    }

    /**
     * Loads data from input file and creates WorkBlocks
     * @return Queue of loaded WorkBlocks
     */
    public Queue<WorkBlock> loadData(){

        String inputFile = args[0];
        Queue<WorkBlock> loadedBlocks = new LinkedList<>();

        try {
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            while ((line = bufferedReader.readLine())!= null) {

                line = line.trim();
                String[] blocks = line.split("\\s+");

                for (String block: blocks){
                    numOfLoadedResources += block.length();
                    loadedBlocks.add(new WorkBlock(block.length()));
                }

            }
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Wrong input file");;
        }


        return loadedBlocks;
    }

    /**Returns number of resources loaded from input file*/
    public int getNumOfLoadedResources(){
        return this.numOfLoadedResources;
    }



}
