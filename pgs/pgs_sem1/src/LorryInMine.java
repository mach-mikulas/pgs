import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LorryInMine {


    /**Parsed args[]*/
    private final String[] parsedArgs;
    private Lorry lorryInMine;
    /**Time when forman was started*/
    private final long startOfMine;
    private final DataWriter dataWriter;
    private final Foreman foreman;
    private Thread lorryInMineTH;
    /**Time when lorry started to fill*/
    private long lorryStartFilling;
    private final Lock lock;

    /**
     * @param startOfMine time of when simulation was started
     * @param dataWriter datawriter reference
     * @param foreman foreman
     * @param parsedArgs parsed input arguments
     * */
    public LorryInMine(long startOfMine, DataWriter dataWriter, Foreman foreman, String[] parsedArgs) {
        this.parsedArgs = parsedArgs;
        this.lorryInMine = null;
        this.startOfMine = startOfMine;
        this.dataWriter = dataWriter;
        this.foreman = foreman;
        this.lock = new ReentrantLock(true);
    }

    /**Returns lorry object. When lorry is full, it starts it thread. If no lorry is present in the mine, created new lorry*/
    public synchronized Lorry getLorry(){

        //If lorry is full, send it on the way
        if(lorryInMine != null && lorryInMine.isLorryFull()){
            writeLorryFilled(System.currentTimeMillis() - lorryStartFilling, lorryInMine.getId());
            lorryInMine = null;
            lorryInMineTH.start();
        }

        //Create new lorry
        if (lorryInMine == null) {

            lorryStartFilling = System.currentTimeMillis();

            foreman.lorryCreated();

            lorryInMine = new Lorry(Integer.parseInt(parsedArgs[4]), Integer.parseInt(parsedArgs[5]), foreman.getlorriesCreated(), foreman, startOfMine, dataWriter);
            lorryInMineTH = new Thread(lorryInMine);
            foreman.addLorryTH(lorryInMineTH);
        }

        return lorryInMine;
    }

    /**Checks if lorry is not full, if it is, it starts lorry thread*/
    public synchronized void checkLorry(){
        if(lorryInMine != null && lorryInMine.isLorryFull()){
            writeLorryFilled(System.currentTimeMillis() - lorryStartFilling, lorryInMine.getId());
            lorryInMine = null;
            lorryInMineTH.start();

        }
    }

    /** If last lorry is not fully loaded, this method will send him on the way*/
    public void sendLastLorry(){

        if(lorryInMine != null){
            writeLorryFilled(System.currentTimeMillis() - lorryStartFilling, lorryInMine.getId());
            lorryInMineTH.start();
        }

    }

    public Lock getLock() {
        return lock;
    }

    private void writeLorryFilled(long time, int id){

        long timeStamp = System.currentTimeMillis() - startOfMine;

        dataWriter.writeText(timeStamp + ";lorry;" + id + ";TimeSpendFillingLorry," + time + "\n");
    }

}
