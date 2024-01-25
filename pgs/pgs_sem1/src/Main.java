import javax.xml.crypto.Data;

public class Main {

    /** Takes args as input and returns string of args in correct order.
     * Can be used only before starting other threads.
     * @param startArgs args from main
     * @return array of args in correct order, wrong input will result in null
     */
    public static String[] parseArgs(String[] startArgs){

        if(startArgs.length < 7){
            return null;
        }

        String[] parsedArgs = new String[7];


        for(int i = 0; i < 14; i++){

            switch (startArgs[i]) {
                case ("-i") -> {
                    i++;
                    parsedArgs[0] = startArgs[i];
                }
                case ("-o") -> {
                    i++;
                    parsedArgs[1] = startArgs[i];
                }
                case ("-cWorker") -> {
                    i++;
                    parsedArgs[2] = startArgs[i];
                }
                case ("-tWorker") -> {
                    i++;
                    parsedArgs[3] = startArgs[i];
                }
                case ("-capLorry") -> {
                    i++;
                    parsedArgs[4] = startArgs[i];
                }
                case ("-tLorry") -> {
                    i++;
                    parsedArgs[5] = startArgs[i];
                }
                case ("-capFerry") -> {
                    i++;
                    parsedArgs[6] = startArgs[i];
                }
            }

        }

        return parsedArgs;

    }

    /**
     * Prints starting parameters to the console.
     * Can be used only before starting other threads.
     */
    public static void argPrint(String[] startArgs){

        System.out.println(
                "Input file: " + startArgs[0] + "\n" +
                "Output file: " + startArgs[1] + "\n" +
                "cWorker: " + startArgs[2] + "\n" +
                "tWorker: " + startArgs[3] + "ms\n" +
                "capLorry: " + startArgs[4] + "\n" +
                "tLorry: " + startArgs[5] + "ms\n" +
                "capFerry: " + startArgs[6] + "\n"

        );

    }

    public static void main(String[] args) {

        //Parsing input from args
        String[] startArgs = parseArgs(args);

        if (startArgs == null){
            return;
        }
        //Printing args
        argPrint(startArgs);

        //Creates and starts foreman thread
        Foreman foreman = new Foreman(startArgs);
        Thread foremanTH = new Thread(foreman);
        foremanTH.start();

        //Waiting till foreman ends
        try {
            foremanTH.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("END");

    }
}
