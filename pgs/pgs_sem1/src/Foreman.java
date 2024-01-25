import java.util.ArrayList;
import java.util.Queue;

/**
 * Foreman assigns available work to Miners
 */
public class Foreman implements Runnable{

    /**count of miners*/
    private final int cWorker;
    /**maximum time of mining one resource*/
    private final int tWorker;
    /**how many lorries fits in one ferry*/
    private final int capFerry;

    private final DataLoader dataLoader;
    private final Ferry ferry;
    private Queue<WorkBlock> workBlocks;

    /**Arraylist of all created miners*/
    private ArrayList<Miner> minersNorm;

    /**Parsed args[]*/
    private final String[] parsedArgs;

    /**Arraylist of all lorries threads*/
    private ArrayList<Thread> lorriesTH;
    /**Number of created lorries*/
    private int numOfLorries;

    /**Number of resources that were delivered to the final destination*/
    private int deliveredResources;
    private final DataWriter dataWriter;
    /**Time when forman was started*/
    private long startOfMine;

    private final LorryInMine lorryInMine;

    /**@param parsedArgs args[] from main*/
    public Foreman(String[] parsedArgs) {

        this.startOfMine = System.currentTimeMillis();

        this.cWorker = Integer.parseInt(parsedArgs[2]);
        this.tWorker = Integer.parseInt(parsedArgs[3]);
        this.capFerry = Integer.parseInt(parsedArgs[6]);

        this.dataWriter = new DataWriter(parsedArgs[1]);

        this.dataLoader = new DataLoader(parsedArgs);
        this.ferry = new Ferry(capFerry, startOfMine, dataWriter);

        this.workBlocks = dataLoader.loadData();
        writeAnalysis();
        this.parsedArgs = parsedArgs;

        this.lorriesTH = new ArrayList<>();
        this.numOfLorries = 0;
        this.deliveredResources = 0;

        this.lorryInMine = new LorryInMine(startOfMine,dataWriter,this, parsedArgs);

    }

    @Override
    public void run() {

        System.out.println("Number of loaded resources: " + dataLoader.getNumOfLoadedResources());
        System.out.println("Number of WorkBlocks: " + workBlocks.size() + "\n");

        minersNorm = new ArrayList<>();
        Thread[] miners = new Thread[cWorker];

        //Creates and starts all miners
        for(int i = 0; i < miners.length; i++){
            Miner miner = new Miner(i+1, tWorker, this, lorryInMine, dataWriter, startOfMine);
            minersNorm.add(miner);
            miners[i] = new Thread(miner);
            miners[i].start();
        }

        //Waiting till all miners finish their work
        for(int i = 0; i < miners.length; i++){
            try {
                miners[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        lorryInMine.sendLastLorry();

        //Waiting till all lorries gets to the final destination
        for(int i = 0; i < lorriesTH.size(); i++) {
            try {
                lorriesTH.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        printMiners();
        printDelivered();

        dataWriter.closeFile();

        }

    /**
     * If queue of WorkBlock is not empty, then returns WorkBlock
     * @return workBlock
     */
    public synchronized WorkBlock getWork(){
        if(!workBlocks.isEmpty()){
            return workBlocks.poll();
        }
        return null;
    }

    /**Add lorry thread to arraylist*/
    public synchronized void addLorryTH(Thread lorryTH) {
        lorriesTH.add(lorryTH);
    }

    /**Increase counter of created lorries*/
    public synchronized void lorryCreated(){
        numOfLorries++;
    }

    /**Return number of created lorries*/
    public synchronized int getlorriesCreated(){
        return numOfLorries;
    }

    /** Prints what every Miner mined*/
    private void printMiners(){

        System.out.println("---------------------------------");
        for(int i = 0; i < minersNorm.size(); i++){
            System.out.println("miner[" + minersNorm.get(i).getId() + "] mined: " + minersNorm.get(i).getMined() + " resources");
        }
        System.out.println("---------------------------------");
    }

    /**Return ferry*/
    public Ferry getFerry() {
        return ferry;
    }

    /**This method unloads lorry at the final destination*/
    public synchronized void unloadLorry(Lorry lorry){

        deliveredResources += lorry.getCargo();
        System.out.println("lorry[" + lorry.getId() + "]| unloaded its cargo");
    }

    /**This method prints how many resources were delivered to the final destination*/
    private void printDelivered(){

        System.out.println("foreman| " + deliveredResources + " delivered resources.");

    }

    /**Writes to the output file how many resources were loaded*/
    private void writeAnalysis(){

        long timeStamp = System.currentTimeMillis() - startOfMine;

        dataWriter.writeText(timeStamp + ";foreman;"  + 1 + ";NumberOfLoadedResources," + dataLoader.getNumOfLoadedResources() + ",NumberOfWorkBlocks," + workBlocks.size() + "\n");
    }

}
