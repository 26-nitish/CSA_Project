import java.util.*;

public class Registers {

    // Register array and register size
    private int[] val;
    private int size;

    public Registers(int size) {
        init(size);
    }

    // Getter & Setter for register values
    public int[] getRegVals() {
        return val;
    }

    public void setRegVals(int[] newVal) {
        // Ensure the register size is non-zero before setting values
        if (size == 0) {
            throw new IllegalStateException("Register size is zero. Cannot set value.");
        }
        this.val = Arrays.copyOf(newVal, newVal.length);
    }

    // Initialize register values and size
    private void init(int size) {
        this.size = size;
        this.val = new int[size]; // Allocate array for register values
    }
}
