import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Instance of Ferry represent barrier.
 */
public class Ferry {

    private final CyclicBarrier barrier;
    private long startOfMine;
    /**Time when ferry started waiting*/
    private long startOfFerryWaiting;
    private final DataWriter dataWriter;


    public Ferry(int capFerry, long startOfMine, DataWriter dataWriter) {
        this.barrier = new CyclicBarrier(capFerry, barrierAction);
        this.startOfMine = startOfMine;
        this.startOfFerryWaiting = startOfMine;
        this.dataWriter = dataWriter;
    }

    /**Waits till ferry is full*/
    public void lorryWaiting(){
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    /**When every thread reached barrier, writes time of ferry departure to output*/
    Runnable barrierAction = () -> {
        writeFerrySailed(System.currentTimeMillis() - startOfFerryWaiting);
        startOfFerryWaiting = System.currentTimeMillis();
        System.out.println("ferry| sailed across the river");
    };

    private void writeFerrySailed(long time){

        long timeStamp = System.currentTimeMillis() - startOfMine;

        dataWriter.writeText(timeStamp + ";ferry;" + 1 + ";TimeSpendWaiting," + time + "\n");
    }
}
