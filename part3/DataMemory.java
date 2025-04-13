import java.util.*;

// Manages a 2D array of integer values.
public class DataMemory {
   private final int[][] values = new int[2048][16];
   public DataMemory() {} // Constructor

   public int[] getMemVal(int index) {
    return Arrays.copyOf(values[index], 16);
   }

   public void setMemVal(int index, int[] data) {
    System.arraycopy(data, 0, values[index], 0, 16);
   }
}
