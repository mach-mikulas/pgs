import java.util.Random;

public class Miner implements Runnable{

    /**Identification of the miner*/
    private final int id;
    /**Foreman that is givin work to the miner*/
    private final Foreman foreman;
    /**Maximal time of mining one resource*/
    private final int tWorker;
    /**How many resources is miner carrying*/
    private int carrying;
    /**How many resource miner mined*/
    private int mined;
    /**How long does it take to load one resource in the lorry*/
    private final int LOADING_TIME = 10;
    private final long startOfMine;
    Random ran;
    private final LorryInMine lorryInMine;
    private final DataWriter dataWriter;

    /**
     * @param id identification of the miner
     * @param tWorker maximal time of mining one resource
     * @param foreman foreman that is givin work to the miner
     */
    public Miner(int id, int tWorker, Foreman foreman, LorryInMine lorryInMine, DataWriter dataWriter, long startOfMine) {
        this.id = id;
        this.tWorker = tWorker;
        this.foreman = foreman;
        this.startOfMine = startOfMine;
        this.ran = new Random();
        this.lorryInMine = lorryInMine;
        this.dataWriter = dataWriter;
    }

    @Override
    public void run() {

        WorkBlock work;
        Lorry lorry;

        //If there is any minable resources, miner gets it
        while((work = foreman.getWork()) != null){

            mine(work);

            //Prints how many resouces is miner carrying to the lorry
            if(carrying > 1){
                System.out.println("miner[" + this.id + "]| carries " + carrying + " resources");
            } else {
                System.out.println("miner[" + this.id + "]| carries " + carrying + " resource");
            }


            //Trying to load lorry
            while(carrying > 0){

                //Tries to load lorry
                if(lorryInMine.getLock().tryLock()){

                    lorry = lorryInMine.getLorry();

                    try {
                        Thread.sleep(LOADING_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    lorry.load();
                    carrying--;

                    System.out.println("miner[" + this.id + "]| loads lorry");

                    lorryInMine.checkLorry();
                    lorryInMine.getLock().unlock();

                }
                //Waiting if someone else is loading lorry
                else{
                    try {
                        Thread.sleep(LOADING_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

        }


    }

    /** Mines one whole WorkBlock*/
    private void mine(WorkBlock work){

        long startOfMiningBlock = System.currentTimeMillis();

        //Mines whole block of resources
        for (int i = 0; i < work.getSize(); i++){

            long startOfMiningResource = System.currentTimeMillis();
            long mineTime = ran.nextLong(1, tWorker + 1);

            //Mines one resource
            try {
                Thread.sleep(mineTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mined++;
            carrying++;

            long timeOfMiningResource = System.currentTimeMillis() - startOfMiningResource;

            writeResourceMined(timeOfMiningResource);

        }

        long timeOfMiningBlock = System.currentTimeMillis() - startOfMiningBlock;

        writeBlockMined(timeOfMiningBlock);

    }
    /**Returns id of the miner*/
    public int getId() {
        return id;
    }

    /**Returns number of mined resources by this miner*/
    public int getMined() {
        return mined;
    }

    private void writeResourceMined(long time){

        long timeStamp = System.currentTimeMillis() - startOfMine;

        dataWriter.writeText(timeStamp + ";miner;" + this.id + ";TimeSpendMiningResource," + time + "\n");
    }

    private void writeBlockMined(long time){

        long timeStamp = System.currentTimeMillis() - startOfMine;

        dataWriter.writeText(timeStamp + ";miner;" + this.id + ";TimeSpendMiningBlock," + time + "\n");
    }
}
