/**
 * Instace of WorkBlock defines block of resources from input file
 */
public class WorkBlock {
    private int size;

    public WorkBlock(int size) {
        this.size = size;
    }

    /**
     * @return size (number of resources in one block)
     */
    public int getSize() {
        return size;
    }
}
