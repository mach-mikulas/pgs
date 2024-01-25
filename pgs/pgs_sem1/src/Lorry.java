import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Lorry implements Runnable{

    /**Cargo capacity of the lorry*/
    private final int capLorry;
    /**Maximal traveling time of the lorry*/
    private final int tLorry;
    /**Number of resources loaded in the truck*/
    private int cargo;
    private final Lock lock;

    private final Foreman foreman;
    /**Identification of the lorry*/
    private final int id;
    private final long startOfMine;

    private final Random ran;
    private final DataWriter dataWriter;

    /**
     * @param capLorry Capacity of lorry
     * @param tLorry maximum travel time of lorry
     * @param id identification of the lorry
     * @param foreman foreman
     * */
    public Lorry(int capLorry, int tLorry, int id, Foreman foreman, long startOfMine ,DataWriter dataWriter) {
        this.capLorry = capLorry;
        this.tLorry = tLorry;
        this.cargo = 0;

        this.lock = new ReentrantLock(true);

        this.foreman = foreman;
        this.id = id;
        this.startOfMine = startOfMine;

        this.ran = new Random();
        this.dataWriter = dataWriter;
    }

    @Override
    public void run() {

        long startTravelTime = System.currentTimeMillis();

        Ferry ferry = foreman.getFerry();
        System.out.println("lorry["+ this.id + "] started");

        //Travel to ferry
        int travelTime = ran.nextInt(1,tLorry);
        try {
            Thread.sleep(travelTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Writing to the output file
        writeLorryWaitingForFerry(System.currentTimeMillis() - startTravelTime);

        //Waits for ferry being full
        ferry.lorryWaiting();

        startTravelTime = System.currentTimeMillis();

        //Traveling to the final destination
        travelTime = ran.nextInt(1,tLorry + 1);
        try {
            Thread.sleep(travelTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        foreman.unloadLorry(this);
        //Writing to the output file
        writeLorryFinished(System.currentTimeMillis() - startTravelTime);
    }

    /**Adds one resource to the cargo*/
    public void load(){
        cargo++;
    }

    /**If the capacity of the lorrys cargo is reached returns true*/
    public boolean isLorryFull(){
        if(cargo < capLorry){
            return false;
        }
        return true;
    }

    public boolean lorryHasCargo(){
        if(cargo > 0){
            return true;
        }
        return false;
    }

    /**Returns lock*/
    public Lock getLock() {
        return lock;
    }

    /**Returns number of resources that lorry is carrying*/
    public int getCargo() {
        return cargo;
    }

    /**Returns identification of the lorry*/
    public int getId() {
        return id;
    }

    private void writeLorryWaitingForFerry(long time){

        long timeStamp = System.currentTimeMillis() - startOfMine;

        dataWriter.writeText(timeStamp + ";lorry;" + this.id + ";TimeSpendTravelingToFerry," + time + "\n");
    }

    private void writeLorryFinished(long time){

        long timeStamp = System.currentTimeMillis() - startOfMine;

        dataWriter.writeText(timeStamp + ";lorry;" + this.id + ";TimeSpendTravelingToFinish," + time + "\n");
    }
}
