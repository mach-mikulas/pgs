package farmerWorkers;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeSet;

public class Worker extends Thread{

    private String name;
    private Farmer farmer;

    private int speed;

    public Worker(String name, Farmer farmer, int speed) {
        this.name = name;
        this.farmer = farmer;
        this.speed = speed;
    }

    @Override
    public void run() {

        int i;

        int jobCount = 0;
        String joke;

        TreeSet<WordRecord> results;

        System.out.println(name + " - Zacinam makat.");

        while ((joke = farmer.getJoke(this.name)).equals("$$skonci$$") == false) {
            System.out.println( name + " - Dostal jsem praci.");

            results = new TreeSet<WordRecord>(new WRComparer());

            String[] words = joke.split("[^A-Za-z0-9]+");

            long prevTime = System.currentTimeMillis();

            for (i = 0; i < words.length; i++) {

                long currTime = System.currentTimeMillis();

                while(currTime < prevTime + speed){
                    currTime = System.currentTimeMillis();
                }

                // prevedeni slova na mala pismena
                words[i] = words[i].toLowerCase();

                // zpracovani slova
                WordRecord wr = getWordRecord(words[i], results);

                if (wr == null) { // nove slovo ve vtipu
                    wr = new WordRecord();
                    wr.word = words[i];
                    wr.frequency = 1;
                }
                else
                { // slovo se jiz ve vtipu vyskytlo
                    results.remove(wr);
                    wr.frequency++;
                }
                results.add(wr);

                System.out.println(name + " - Zpracovano slovo: " + " / " + wr.word + " /");

                prevTime = currTime;
            }
            // ulozeni vysledku do globalniho seznamu u farmera
            farmer.reportResult(results, this.name);
            jobCount++;
        }
        System.out.println(name + " - Koncim, zpracoval jsem " + jobCount + " vtipu.");
    }

    public WordRecord getWordRecord (String item, TreeSet<WordRecord> results) {
        Iterator<WordRecord> it = results.iterator();

        while (it.hasNext()) {
            WordRecord listItem = it.next();

            if (listItem.word.equals(item))
                return listItem;
        }
        return null;
    }
}
