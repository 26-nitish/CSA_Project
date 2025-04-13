import javax.swing.*;
import java.io.*;
import java.util.*;

public class Processor {
    // Create objects for memory and registers
    StringFormatter sFormatter= new StringFormatter();
    DataMemory dataMem = new DataMemory();

    // Add the main registers in Processor (CPU)
    Registers PC = new Registers(12);
    Registers CC = new Registers(4);
    Registers IR = new Registers(16);
    Registers MAR = new Registers(12);
    Registers MBR = new Registers(16);
    Registers MFR = new Registers(4);
    Registers IX1 = new Registers(16);
    Registers IX2 = new Registers(16);
    Registers IX3 = new Registers(16);
    Registers GPR0 = new Registers(16);
    Registers GPR1 = new Registers(16);
    Registers GPR2 = new Registers(16);
    Registers GPR3 = new Registers(16);
    Registers HLT = new Registers(1);
    public boolean fileOpened = false; // Indicates whether the file has been successfully opened or not.
    public boolean fileOpened2 = false;


    // Function to execute a Single Step or Run
    public void execute(String type){
        if ("single".equals(type)) {
            int[] instruction_address = getRegVals("PC");
            int int_instruction_address = sFormatter.binaryToInt(instruction_address);
            setRegVals("IR", getMemVal(int_instruction_address));

            //Increment PC
            int[] current_PC = getRegVals("PC");
            int int_PC = sFormatter.binaryToInt(current_PC);
            int_PC = int_PC + 1;
            int[] new_PC = intToBinaryArrayShort(Integer.toBinaryString(int_PC));
            setRegVals("PC", new_PC);

            //Read and decode instruction
            int[] binaryInstruction = getMemVal(int_instruction_address);
            int[] OpCode = Arrays.copyOfRange(binaryInstruction, 0, 6);
            String instruction = decodeOPCode(OpCode);

            //Execute opcode instruction
            if ("LDR".equals(instruction)) {     //Load Opcode LDR

                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int I = result[1];
                int R = result[2];
                int IX = result[3];

                // Setting MAR to the location in memory to fetch
                int Addr = result[4];
                setRegVals("MAR", intToBinaryArrayShort(Integer.toBinaryString(EA)));

                // Set MBR to the value to be stored in register
                switch (R) {
                    case 0:
                        setRegVals("MBR", getMemVal(EA));
                        GPR0.setRegVals(MBR.getRegVals());
                        break;
                    case 1:
                        setRegVals("MBR", getMemVal(EA));
                        GPR1.setRegVals(MBR.getRegVals());
                        break;
                    case 2:
                        setRegVals("MBR", getMemVal(EA));
                        GPR2.setRegVals(MBR.getRegVals());
                        break;
                    default:
                        setRegVals("MBR", getMemVal(EA));
                        GPR3.setRegVals(MBR.getRegVals());
                }
                setRegVals("MBR", getMemVal(EA));

            } else if ("STR".equals(instruction)) {    //Load Opcode STR

                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int I = result[1];
                int R = result[2];
                int IX = result[3];
                int Addr = result[4];

                // Set MBR to the value to be stored in memory
                setRegVals("MAR", intToBinaryArrayShort(Integer.toBinaryString(EA)));
                switch (R) {
                    case 0:
                        setRegVals("MBR", GPR0.getRegVals());
                        setMemVal(EA, MBR.getRegVals());
                        break;
                    case 1:
                        setRegVals("MBR", GPR1.getRegVals());
                        setMemVal(EA, MBR.getRegVals());
                        break;
                    case 2:
                        setRegVals("MBR", GPR2.getRegVals());
                        setMemVal(EA, MBR.getRegVals());
                        break;
                    default:
                        setRegVals("MBR", GPR3.getRegVals());
                        setMemVal(EA, MBR.getRegVals());
                }
            } else if ("LDA".equals(instruction)) {    //Load Opcode LDA
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int I = result[1];
                int R = result[2];
                int IX = result[3];
                int Addr = result[4];

                setRegVals("MAR", intToBinaryArrayShort(Integer.toBinaryString(EA)));
                int[] converted_value = intToBinaryArray(Integer.toBinaryString(EA));
                switch (R) {
                    case 0:
                        GPR0.setRegVals(converted_value);
                        break;
                    case 1:
                        GPR1.setRegVals(converted_value);
                        break;
                    case 2:
                        GPR2.setRegVals(converted_value);
                        break;
                    default:
                        GPR3.setRegVals(converted_value);
                }
            } else if ("LDX".equals(instruction)) {    //Load Opcode LDX
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int I = result[1];
                int R = result[2];
                int IX = result[3];
                int Addr = result[4];

                setRegVals("MAR", intToBinaryArrayShort(Integer.toBinaryString(EA)));
                switch (IX) {
                    case 1:
                        setRegVals("MBR", getMemVal(EA));
                        IX1.setRegVals(MBR.getRegVals());
                        break;
                    case 2:
                        setRegVals("MBR", getMemVal(EA));
                        IX2.setRegVals(MBR.getRegVals());
                        break;
                    case 3:
                        setRegVals("MBR", getMemVal(EA));
                        IX2.setRegVals(MBR.getRegVals());
                        break;
                    default:
                }
            } else if ("STX".equals(instruction)) {    //Load Opcode STX
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int I = result[1];
                int R = result[2];
                int IX = result[3];
                int Addr = result[4];

                setRegVals("MAR", intToBinaryArrayShort(Integer.toBinaryString(EA)));
                switch (IX) {
                    case 1:
                        setRegVals("MBR", IX1.getRegVals());
                        setMemVal(EA, MBR.getRegVals());
                        break;
                    case 2:
                        setRegVals("MBR", IX2.getRegVals());
                        setMemVal(EA, MBR.getRegVals());
                        break;
                    case 3:
                        setRegVals("MBR", IX3.getRegVals());
                        setMemVal(EA, MBR.getRegVals());
                        break;
                    default:
                }
            } else if ("HLT".equals(instruction)) {    //Load Opcode HLT
                int[] msg = new int[]{1};
                HLT.setRegVals(msg);
            } else if("IN".equals(instruction)){    // Input character from keyboard
                int[] result = computeEA(binaryInstruction);
                int R = result[2];  // Get register number

                // Read character from keyboard and convert to binary
                String input = JOptionPane.showInputDialog("Enter a character:");
                if(input != null && input.length() > 0) {
                    int value = (int)input.charAt(0);
                    int[] binary = intToBinaryArray(Integer.toBinaryString(value));
                    // Store in specified register
                    switch(R) {
                        case 0:
                            GPR0.setRegVals(binary);
                            break;
                        case 1:
                            GPR1.setRegVals(binary);
                            break;
                        case 2:
                            GPR2.setRegVals(binary);
                            break;
                        default:
                            GPR3.setRegVals(binary);
                    }
                }

            } else if ("OUT".equals(instruction)) {    // Output character to console
                int[] result = computeEA(binaryInstruction);
                int R = result[2]; 

                // Get value from register and convert to character
                int[] binary;
                switch (R) {
                    case 0:
                        binary = GPR0.getRegVals();
                        break;
                    case 1:
                        binary = GPR1.getRegVals();
                        break;
                    case 2:
                        binary = GPR2.getRegVals();
                        break;
                    default:
                        binary = GPR3.getRegVals();
                }
                int value = sFormatter.binaryToInt(binary);
                System.out.print((char) value);

            } else if ("ADD".equals(instruction)) {    // Add
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int R = result[2];

                int[] memoryValue = getMemVal(EA);
                int memVal = sFormatter.binaryToInt(memoryValue);

                // Add memory value to register
                switch (R) {
                    case 0:
                        int regVal0 = sFormatter.binaryToInt(GPR0.getRegVals());
                        GPR0.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal0 + memVal)));
                        break;
                    case 1:
                        int regVal1 = sFormatter.binaryToInt(GPR1.getRegVals());
                        GPR1.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal1 + memVal)));
                        break;
                    case 2:
                        int regVal2 = sFormatter.binaryToInt(GPR2.getRegVals());
                        GPR2.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal2 + memVal)));
                        break;
                    default:
                        int regVal3 = sFormatter.binaryToInt(GPR3.getRegVals());
                        GPR3.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal3 + memVal)));
                }

            } else if ("SUB".equals(instruction)) {    // Subtract
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int R = result[2];

                int[] memoryValue = getMemVal(EA);
                int memVal = sFormatter.binaryToInt(memoryValue);

                // Subtract memory value from register
                switch (R) {
                    case 0:
                        int regVal0 = sFormatter.binaryToInt(GPR0.getRegVals());
                        GPR0.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal0 - memVal)));
                        break;
                    case 1:
                        int regVal1 = sFormatter.binaryToInt(GPR1.getRegVals());
                        GPR1.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal1 - memVal)));
                        break;
                    case 2:
                        int regVal2 = sFormatter.binaryToInt(GPR2.getRegVals());
                        GPR2.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal2 - memVal)));
                        break;
                    default:
                        int regVal3 = sFormatter.binaryToInt(GPR3.getRegVals());
                        GPR3.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal3 - memVal)));
                }

            } else if ("MUL".equals(instruction)) {    // Multiply
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int R = result[2];

                int[] memoryValue = getMemVal(EA);
                int memVal = sFormatter.binaryToInt(memoryValue);

                // Multiply register by memory value
                switch (R) {
                    case 0:
                        int regVal0 = sFormatter.binaryToInt(GPR0.getRegVals());
                        GPR0.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal0 * memVal)));
                        break;
                    case 1:
                        int regVal1 = sFormatter.binaryToInt(GPR1.getRegVals());
                        GPR1.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal1 * memVal)));
                        break;
                    case 2:
                        int regVal2 = sFormatter.binaryToInt(GPR2.getRegVals());
                        GPR2.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal2 * memVal)));
                        break;
                    default:
                        int regVal3 = sFormatter.binaryToInt(GPR3.getRegVals());
                        GPR3.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal3 * memVal)));
                }

            } else if ("DIV".equals(instruction)) {    // Divide
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int R = result[2];

                int[] memoryValue = getMemVal(EA);
                int memVal = sFormatter.binaryToInt(memoryValue);

                if (memVal == 0) {
                    // Handle divide by zero
                    int[] fault_code = {0, 0, 1, 0};
                    MFR.setRegVals(fault_code);
                    int[] msg = {1};
                    HLT.setRegVals(msg);
                    return;
                }

                // Divide register by memory value
                switch (R) {
                    case 0:
                        int regVal0 = sFormatter.binaryToInt(GPR0.getRegVals());
                        GPR0.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal0 / memVal)));
                        break;
                    case 1:
                        int regVal1 = sFormatter.binaryToInt(GPR1.getRegVals());
                        GPR1.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal1 / memVal)));
                        break;
                    case 2:
                        int regVal2 = sFormatter.binaryToInt(GPR2.getRegVals());
                        GPR2.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal2 / memVal)));
                        break;
                    default:
                        int regVal3 = sFormatter.binaryToInt(GPR3.getRegVals());
                        GPR3.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal3 / memVal)));
                }

            } else if ("AND".equals(instruction)) {    // Logical AND
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int R = result[2];
                int IX = result[3];

                int[] register2 = new int[16];
                switch (IX){
                    case 1: register2 = IX1.getRegVals();
                        break;
                    case 2: register2 = IX2.getRegVals();
                        break;
                    default:
                        register2 = IX3.getRegVals();
                }

                // Perform bitwise AND
                switch (R) {
                    case 0:
                        int[] regVal0 = GPR0.getRegVals();
                        int[] newVal0 = new int[16];
                        for (int i = 0; i < 16; i++) {
                            newVal0[i] = regVal0[i] & register2[i];
                        }
                        GPR0.setRegVals(newVal0);
                        break;
                    case 1:
                        int[] regVal1 = GPR1.getRegVals();
                        int[] newVal1 = new int[16];
                        for (int i = 0; i < 16; i++) {
                            newVal1[i] = regVal1[i] & register2[i];
                        }
                        GPR1.setRegVals(newVal1);
                        break;
                    case 2:
                        int[] regVal2 = GPR2.getRegVals();
                        int[] newVal2 = new int[16];
                        for (int i = 0; i < 16; i++) {
                            newVal2[i] = regVal2[i] & register2[i];
                        }
                        GPR2.setRegVals(newVal2);
                        break;
                    default:
                        int[] regVal3 = GPR3.getRegVals();
                        int[] newVal3 = new int[16];
                        for (int i = 0; i < 16; i++) {
                            newVal3[i] = regVal3[i] & register2[i];
                        }
                        GPR3.setRegVals(newVal3);
                }

            } else if ("ORR".equals(instruction)) {    // Logical OR
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int R = result[2];
                int IX = result[3];
                int[] register2 = new int[16];
                switch (IX){
                    case 0: register2 = IX1.getRegVals();
                    break;
                    case 1: register2 = IX2.getRegVals();
                        break;
                    default:
                        register2 = IX3.getRegVals();
                }

                // Perform bitwise OR
                switch (R) {
                    case 0:
                        int[] regVal0 = GPR0.getRegVals();
                        int[] newVal0 = new int[16];
                        for (int i = 0; i < 16; i++) {
                            newVal0[i] = regVal0[i] | register2[i];
                        }
                        GPR0.setRegVals(newVal0);
                        break;
                    case 1:
                        int[] regVal1 = GPR1.getRegVals();
                        int[] newVal1 = new int[16];
                        for (int i = 0; i < 16; i++) {
                            newVal1[i] = regVal1[i] | register2[i];
                        }
                        GPR1.setRegVals(newVal1);
                        break;
                    case 2:
                        int[] regVal2 = GPR2.getRegVals();
                        int[] newVal2 = new int[16];
                        for (int i = 0; i < 16; i++) {
                            newVal2[i] = regVal2[i] | register2[i];
                        }
                        GPR2.setRegVals(newVal2);
                        break;
                    default:
                        int[] regVal3 = GPR3.getRegVals();
                        int[] newVal3 = new int[16];
                        for (int i = 0; i < 16; i++) {
                            newVal3[i] = regVal3[i] | register2[i];
                        }
                        GPR3.setRegVals(newVal3);
                }

            } else if ("NOT".equals(instruction)) {    // Logical NOT
                int[] result = computeEA(binaryInstruction);
                int R = result[2];

                // Perform bitwise NOT on register
                switch (R) {
                    case 0:
                        int[] regVal0 = GPR0.getRegVals();
                        int[] newVal0 = new int[16];
                        for (int i = 0; i < 16; i++) {
                            newVal0[i] = regVal0[i] == 0 ? 1 : 0;
                        }
                        GPR0.setRegVals(newVal0);
                        break;
                    case 1:
                        int[] regVal1 = GPR1.getRegVals();
                        int[] newVal1 = new int[16];
                        for (int i = 0; i < 16; i++) {
                            newVal1[i] = regVal1[i] == 0 ? 1 : 0;
                        }
                        GPR1.setRegVals(newVal1);
                        break;
                    case 2:
                        int[] regVal2 = GPR2.getRegVals();
                        int[] newVal2 = new int[16];
                        for (int i = 0; i < 16; i++) {
                            newVal2[i] = regVal2[i] == 0 ? 1 : 0;
                        }
                        GPR2.setRegVals(newVal2);
                        break;
                    default:
                        int[] regVal3 = GPR3.getRegVals();
                        int[] newVal3 = new int[16];
                        for (int i = 0; i < 16; i++) {
                            newVal3[i] = regVal3[i] == 0 ? 1 : 0;
                        }
                        GPR3.setRegVals(newVal3);
                }

            } else if ("JZ".equals(instruction)) {    // Jump if Zero
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int R = result[2];

                // Check if register is zero
                int[] regValue;
                switch (R) {
                    case 0:
                        regValue = GPR0.getRegVals();
                        break;
                    case 1:
                        regValue = GPR1.getRegVals();
                        break;
                    case 2:
                        regValue = GPR2.getRegVals();
                        break;
                    default:
                        regValue = GPR3.getRegVals();
                }

                if (sFormatter.binaryToInt(regValue) == 0) {
                    setRegVals("PC", intToBinaryArrayShort(Integer.toBinaryString(EA)));
                }

            } else if ("JNE".equals(instruction)) {    // Jump if Not Equal
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int R = result[2];

                // Check if register is not zero
                int[] regValue;
                switch (R) {
                    case 0:
                        regValue = GPR0.getRegVals();
                        break;
                    case 1:
                        regValue = GPR1.getRegVals();
                        break;
                    case 2:
                        regValue = GPR2.getRegVals();
                        break;
                    default:
                        regValue = GPR3.getRegVals();
                }

                if (sFormatter.binaryToInt(regValue) != 0) {
                    setRegVals("PC", intToBinaryArrayShort(Integer.toBinaryString(EA)));
                }

            } else if ("JCC".equals(instruction)) {    // Jump if Condition Code
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int R = result[2];  // Using R as condition code

                // Get condition code
                int[] cc = CC.getRegVals();
                if (cc[R] == 1) {
                    setRegVals("PC", intToBinaryArrayShort(Integer.toBinaryString(EA)));
                }
            }
            else if ("AMR".equals(instruction)) {    // Add Memory To Register
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int R = result[2];

                int[] memoryValue = getMemVal(EA);
                int memVal = sFormatter.binaryToInt(memoryValue);

                // Add memory value to register
                switch (R) {
                    case 0:
                        int regVal0 = sFormatter.binaryToInt(GPR0.getRegVals());
                        GPR0.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal0 + memVal)));
                        break;
                    case 1:
                        int regVal1 = sFormatter.binaryToInt(GPR1.getRegVals());
                        GPR1.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal1 + memVal)));
                        break;
                    case 2:
                        int regVal2 = sFormatter.binaryToInt(GPR2.getRegVals());
                        GPR2.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal2 + memVal)));
                        break;
                    default:
                        int regVal3 = sFormatter.binaryToInt(GPR3.getRegVals());
                        GPR3.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal3 + memVal)));
                }
            }

            else if ("SMR".equals(instruction)) {    // Subtract Memory From Register
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int R = result[2];

                int[] memoryValue = getMemVal(EA);
                int memVal = sFormatter.binaryToInt(memoryValue);

                // Subtract memory value from register
                switch (R) {
                    case 0:
                        int regVal0 = sFormatter.binaryToInt(GPR0.getRegVals());
                        GPR0.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal0 - memVal)));
                        break;
                    case 1:
                        int regVal1 = sFormatter.binaryToInt(GPR1.getRegVals());
                        GPR1.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal1 - memVal)));
                        break;
                    case 2:
                        int regVal2 = sFormatter.binaryToInt(GPR2.getRegVals());
                        GPR2.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal2 - memVal)));
                        break;
                    default:
                        int regVal3 = sFormatter.binaryToInt(GPR3.getRegVals());
                        GPR3.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal3 - memVal)));
                }
            }

            else if ("AIR".equals(instruction)) {    // Add Immediate to Register
                int[] result = computeEA(binaryInstruction);
                int immed = result[4];  // Using Address field as immediate value
                int R = result[2];

                // Add immediate value to register
                switch (R) {
                    case 0:
                        int regVal0 = sFormatter.binaryToInt(GPR0.getRegVals());
                        GPR0.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal0 + immed)));
                        break;
                    case 1:
                        int regVal1 = sFormatter.binaryToInt(GPR1.getRegVals());
                        GPR1.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal1 + immed)));
                        break;
                    case 2:
                        int regVal2 = sFormatter.binaryToInt(GPR2.getRegVals());
                        GPR2.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal2 + immed)));
                        break;
                    default:
                        int regVal3 = sFormatter.binaryToInt(GPR3.getRegVals());
                        GPR3.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal3 + immed)));
                }
            }

            else if ("SIR".equals(instruction)) {    // Subtract Immediate from Register
                int[] result = computeEA(binaryInstruction);
                int immed = result[4];  // Using Address field as immediate value
                int R = result[2];

                // Subtract immediate value from register
                switch (R) {
                    case 0:
                        int regVal0 = sFormatter.binaryToInt(GPR0.getRegVals());
                        GPR0.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal0 - immed)));
                        break;
                    case 1:
                        int regVal1 = sFormatter.binaryToInt(GPR1.getRegVals());
                        GPR1.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal1 - immed)));
                        break;
                    case 2:
                        int regVal2 = sFormatter.binaryToInt(GPR2.getRegVals());
                        GPR2.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal2 - immed)));
                        break;
                    default:
                        int regVal3 = sFormatter.binaryToInt(GPR3.getRegVals());
                        GPR3.setRegVals(intToBinaryArray(Integer.toBinaryString(regVal3 - immed)));
                }
            }

            else if ("JMA".equals(instruction)) {    // Unconditional Jump To Address
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                setRegVals("PC", intToBinaryArrayShort(Integer.toBinaryString(EA)));
            }

            else if ("JSR".equals(instruction)) {    // Jump and Save Return Address
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int R = result[2];

                // Save return address (current PC + 1) in R3
                int[] currentPC = getRegVals("PC");
                int nextInstr = sFormatter.binaryToInt(currentPC);
                GPR3.setRegVals(intToBinaryArray(Integer.toBinaryString(nextInstr)));
                setRegVals("PC", intToBinaryArrayShort(Integer.toBinaryString(EA)));
            }

            else if ("RFS".equals(instruction)) {    // Return From Subroutine
                int[] result = computeEA(binaryInstruction);
                int R = result[2];
                int[] returnAddr = GPR3.getRegVals();
                setRegVals("PC", intToBinaryArrayShort(Integer.toBinaryString(sFormatter.binaryToInt(returnAddr))));
            }

            else if ("SOB".equals(instruction)) {    // Subtract One and Branch
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int R = result[2];

                // Subtract one from register
                int[] regValue;
                switch (R) {
                    case 0:
                        regValue = GPR0.getRegVals();
                        int val0 = sFormatter.binaryToInt(regValue) - 1;
                        GPR0.setRegVals(intToBinaryArray(Integer.toBinaryString(val0)));
                        if (val0 > 0) {
                            setRegVals("PC", intToBinaryArrayShort(Integer.toBinaryString(EA)));
                        }
                        break;
                    case 1:
                        regValue = GPR1.getRegVals();
                        int val1 = sFormatter.binaryToInt(regValue) - 1;
                        GPR1.setRegVals(intToBinaryArray(Integer.toBinaryString(val1)));
                        if (val1 > 0) {
                            setRegVals("PC", intToBinaryArrayShort(Integer.toBinaryString(EA)));
                        }
                        break;
                    case 2:
                        regValue = GPR2.getRegVals();
                        int val2 = sFormatter.binaryToInt(regValue) - 1;
                        GPR2.setRegVals(intToBinaryArray(Integer.toBinaryString(val2)));
                        if (val2 > 0) {
                            setRegVals("PC", intToBinaryArrayShort(Integer.toBinaryString(EA)));
                        }
                        break;
                    default:
                        regValue = GPR3.getRegVals();
                        int val3 = sFormatter.binaryToInt(regValue) - 1;
                        GPR3.setRegVals(intToBinaryArray(Integer.toBinaryString(val3)));
                        if (val3 > 0) {
                            setRegVals("PC", intToBinaryArrayShort(Integer.toBinaryString(EA)));
                        }
                }
            }

            else if ("JGE".equals(instruction)) {    // Jump Greater Than or Equal To
                int[] result = computeEA(binaryInstruction);
                int EA = result[0];
                int R = result[2];

                // Check if register value is >= 0
                int[] regValue;
                switch (R) {
                    case 0:
                        regValue = GPR0.getRegVals();
                        break;
                    case 1:
                        regValue = GPR1.getRegVals();
                        break;
                    case 2:
                        regValue = GPR2.getRegVals();
                        break;
                    default:
                        regValue = GPR3.getRegVals();
                }

                if (sFormatter.binaryToInt(regValue) >= 0) {
                    setRegVals("PC", intToBinaryArrayShort(Integer.toBinaryString(EA)));
                }
            }

            else if ("MLT".equals(instruction)) {    // Multiply Register by Register
                int[] result = computeEA(binaryInstruction);
                int Rx = result[2];  // First register
                int IX = result[3]; // second register
                int[] register2 = new int[16];
                switch (IX){
                    case 1: register2 = IX1.getRegVals();
                        break;
                    case 2: register2 = IX2.getRegVals();
                        break;
                    default:
                        register2 = IX3.getRegVals();
                }

                // Get values from both registers
                int val1, val2;
                val2 = sFormatter.binaryToInt(register2);
                switch (Rx) {
                    case 0: val1 = sFormatter.binaryToInt(GPR0.getRegVals()); break;
                    case 1: val1 = sFormatter.binaryToInt(GPR1.getRegVals()); break;
                    case 2: val1 = sFormatter.binaryToInt(GPR2.getRegVals()); break;
                    default: val1 = sFormatter.binaryToInt(GPR3.getRegVals());
                }
                int multResult = val1 * val2;
                // Store high order bits in Rx, low order bits in Rx+1
                if (Rx < 3) {
                    switch (Rx) {
                        case 0:
                            GPR0.setRegVals(intToBinaryArray(Integer.toBinaryString(multResult >> 16)));
                            GPR1.setRegVals(intToBinaryArray(Integer.toBinaryString(multResult & 0xFFFF)));
                            break;
                        case 1:
                            GPR1.setRegVals(intToBinaryArray(Integer.toBinaryString(multResult >> 16)));
                            GPR2.setRegVals(intToBinaryArray(Integer.toBinaryString(multResult & 0xFFFF)));
                            break;
                        case 2:
                            GPR2.setRegVals(intToBinaryArray(Integer.toBinaryString(multResult >> 16)));
                            GPR3.setRegVals(intToBinaryArray(Integer.toBinaryString(multResult & 0xFFFF)));
                            break;
                    }
                } else {
                    // Set MFR for illegal operation if Rx is 3
                    int[] fault_code = {0, 1, 0, 0};
                    MFR.setRegVals(fault_code);
                }
            }
            else if ("DVD".equals(instruction)) {    // Multiply Register by Register
                int[] result = computeEA(binaryInstruction);
                int Rx = result[2];  // First register
                int IX = result[3]; // second register
                int[] register2 = new int[16];
                switch (IX){
                    case 1: register2 = IX1.getRegVals();
                        break;
                    case 2: register2 = IX2.getRegVals();
                        break;
                    default:
                        register2 = IX3.getRegVals();
                }

                // Get values from both registers
                int val1, val2;
                val2 = sFormatter.binaryToInt(register2);
                switch (Rx) {
                    case 0: val1 = sFormatter.binaryToInt(GPR0.getRegVals()); break;
                    case 1: val1 = sFormatter.binaryToInt(GPR1.getRegVals()); break;
                    case 2: val1 = sFormatter.binaryToInt(GPR2.getRegVals()); break;
                    default: val1 = sFormatter.binaryToInt(GPR3.getRegVals());
                }
                int multResult = val1 / val2;
                // Store high order bits in Rx, low order bits in Rx+1
                if (Rx < 3) {
                    switch (Rx) {
                        case 0:
                            GPR0.setRegVals(intToBinaryArray(Integer.toBinaryString(multResult >> 16)));
                            GPR1.setRegVals(intToBinaryArray(Integer.toBinaryString(multResult & 0xFFFF)));
                            break;
                        case 1:
                            GPR1.setRegVals(intToBinaryArray(Integer.toBinaryString(multResult >> 16)));
                            GPR2.setRegVals(intToBinaryArray(Integer.toBinaryString(multResult & 0xFFFF)));
                            break;
                        case 2:
                            GPR2.setRegVals(intToBinaryArray(Integer.toBinaryString(multResult >> 16)));
                            GPR3.setRegVals(intToBinaryArray(Integer.toBinaryString(multResult & 0xFFFF)));
                            break;
                    }
                } else {
                    // Set MFR for illegal operation if Rx is 3
                    int[] fault_code = {0, 1, 0, 0};
                    MFR.setRegVals(fault_code);
                }
            }
        }
    }

    //Function to decode a operation instruction
    public String decodeOPCode(int[] binary_OPCode){
        String returnVal;
        // Convert int array to string
        String opCode = Arrays.toString(binary_OPCode);
        opCode = opCode.replace("[", "");
        opCode = opCode.replace("]", "");
        opCode = opCode.replace(",", "");
        opCode = opCode.replace(" ", "");

        returnVal = switch (opCode) {
            case "000001" -> "LDR";   // Load Register From Memory
            case "000010" -> "STR";   // Store Register To Memory
            case "000011" -> "LDA";   // Load Register with Address
            case "000100" -> "AMR";   // Add Memory To Register
            case "000101" -> "SMR";   // Subtract Memory From Register
            case "000110" -> "AIR";   // Add Immediate to Register
            case "000111" -> "SIR";   // Subtract Immediate from Register
            case "001000" -> "JZ";    // Jump If Zero
            case "001001" -> "JNE";   // Jump If Not Equal
            case "001010" -> "JCC";   // Jump If Condition Code
            case "001011" -> "JMA";   // Unconditional Jump To Address
            case "001100" -> "JSR";   // Jump and Save Return Address
            case "001101" -> "RFS";   // Return From Subroutine
            case "001110" -> "SOB";   // Subtract One and Branch
            case "001111" -> "JGE";   // Jump Greater Than or Equal To
            case "010000" -> "MLT";   // Multiply Register by Register
            case "010001" -> "DVD";   // Divide Register by Register
            case "010011" -> "AND";   // Logical And of Register and Register
            case "010100" -> "ORR";   // Logical Or of Register and Register
            case "010101" -> "NOT";   // Logical Not of Register
            case "011000" -> "IN";    // Input Character To Register
            case "011001" -> "OUT";   // Output Character To Console
            case "100001" -> "LDX";   // Load Index Register from Memory
            case "100010" -> "STX";   // Store Index Register to Memory
            default -> "HLT";
        };
        return returnVal;
    }

    //Function to get values from the registers
    public int[] getRegVals(String register){
        return switch (register) {
            case "PC" -> PC.getRegVals();
            case "CC" -> CC.getRegVals();
            case "IR" -> IR.getRegVals();
            case "MAR" -> MAR.getRegVals();
            case "MBR" -> MBR.getRegVals();
            case "MFR" -> MFR.getRegVals();
            case "IX1" -> IX1.getRegVals();
            case "IX2" -> IX2.getRegVals();
            case "IX3" -> IX3.getRegVals();
            case "GPR0" -> GPR0.getRegVals();
            case "GPR1" -> GPR1.getRegVals();
            case "GPR2" -> GPR2.getRegVals();
            case "GPR3" -> GPR3.getRegVals();
            case null, default -> HLT.getRegVals();
        };
    }

    //Function to set value to the registers
    public void setRegVals(String register, int[] value){
        switch (register) {
            case "PC" -> {
                if (sFormatter.binaryToInt(value) < 10) {
                    //If PC can't be less than 10!!
                    int[] tmp_val = {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0};
                    PC.setRegVals(tmp_val);
                } else {
                    PC.setRegVals(value);
                }
            }
            case "CC" -> CC.setRegVals(value);
            case "IR" -> IR.setRegVals(value);
            case "MAR" -> MAR.setRegVals(value);
            case "MBR" -> MBR.setRegVals(value);
            case "MFR" -> MFR.setRegVals(value);
            case "IX1" -> IX1.setRegVals(value);
            case "IX2" -> IX2.setRegVals(value);
            case "IX3" -> IX3.setRegVals(value);
            case "GPR0" -> GPR0.setRegVals(value);
            case "GPR1" -> GPR1.setRegVals(value);
            case "GPR2" -> GPR2.setRegVals(value);
            case "GPR3" -> GPR3.setRegVals(value);
            case null, default -> HLT.setRegVals(value);
        }

    }

    // Function to get a value from memory
    public int[] getMemVal(int row){
        if (row < 6){
            return new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        }else{
            return dataMem.getMemVal(row);
        }
    }

    // Function to set a value to memory
    public void setMemVal(int row, int[] value){
        if (row < 6){
            int[] fault_code = new int[]{0,0,0,1};
            MFR.setRegVals(fault_code);
            int [] msg = new int[]{1};
            HLT.setRegVals(msg);
        }else{
            dataMem.setMemVal(row, value);
        }
    }


    public int[] computeEA(int[] instruction) {
        // Formatting data from the Array
        String strInstruction = Arrays.toString(instruction)
                .replace("[", "")
                .replace("]", "")
                .replace(",", "")
                .replace(" ", "");

        // Calculate I (Indirect Addressing Mode)
        int I = (strInstruction.charAt(10) == '0') ? 0 : 1;
        // Calculate R (General Register)
        int R = calculateRegister(strInstruction.substring(6, 8));
        // Calculate IX (Index Register)
        int IX = calculateRegister(strInstruction.substring(8, 10));
        // Calculate Address Field
        int[] Addr_Field = Arrays.copyOfRange(instruction, 11, 16);
        // Calculate Effective Address (EA)
        int EA;
        if (I == 0) {
            EA = (IX == 0) ? sFormatter.binaryToInt(Addr_Field) : sFormatter.binaryToInt(Addr_Field) + getIXValue(IX);
        } else {
            EA = calculateIndirectEA(Addr_Field, IX);
        }
        return new int[]{EA, I, R, IX, sFormatter.binaryToInt(Addr_Field)};
    }

    private int calculateRegister(String binaryString) {
        return switch (binaryString) {
            case "00" -> 0;
            case "01" -> 1;
            case "10" -> 2;
            case "11" -> 3;
            default -> 0;  // Fallback in case of unexpected input
        };
    }

    private int getIXValue(int IX) {
        String IXRegister = (IX == 1) ? "IX1" : (IX == 2) ? "IX2" : "IX3";
        return sFormatter.binaryToInt(getRegVals(IXRegister));
    }

    private int calculateIndirectEA(int[] Addr_Field, int IX) {
        int tempVariable = sFormatter.binaryToInt(Addr_Field);
        if (IX != 0) {
            int IX_value = getIXValue(IX);
            tempVariable += IX_value;
        }
        // Get memory value at the calculated address
        int[] tmp_var = getMemVal(tempVariable);
        tempVariable = sFormatter.binaryToInt(tmp_var);
        // Get the final EA from memory
        return sFormatter.binaryToInt(getMemVal(tempVariable));
    }


    // Function to load the file into memory
    public void loadIPLFile(String path) throws FileNotFoundException, IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(path))))) {
            String strLine;
            while ((strLine = br.readLine()) != null) {
                String[] tokens = strLine.split(" ");
                // ** If loading.txt file is in hexadecimal format, use sFormatter.hexToInt() instead of sFormatter.octToInt() ** //   
                int row = sFormatter.octToInt(tokens[0]);
                // ** If loading.txt file is hexadecimal, use hexToBinaryArrayShort(tokens[0]) instead of sFormatter.octToBinaryArr(tokens[0],12); ** //
                int[] rowBinary = sFormatter.octToBinaryArr(tokens[0],12);
                setRegVals("MAR", rowBinary);
                // ** If loading.txt file is hexadecimal, use sFormatter.hexToBinaryArray(tokens[1]) instead of sFormatter.octToBinaryArr(tokens[1],16) ** //
                int [] value = sFormatter.octToBinaryArr(tokens[1],16);
                setRegVals("MBR", value);

                // Set memory value
                System.out.println("Setting Memory for Row "+ row + " \n");
                setMemVal(row, value);
                int[] fault_code = {0, 0, 0, 0};
                MFR.setRegVals(fault_code);
            }
        }
    }

    public void openFileChooser() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);  // No parent frame

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                loadIPLFile(selectedFile.getAbsolutePath());
                if(selectedFile.getAbsolutePath().contains("Program1")){
                    fileOpened = true;
                }
                if(selectedFile.getAbsolutePath().contains("Program2")){
                    fileOpened2 = true;
                }
                } catch (IOException ex) {
                    System.out.println("Something went wrong while loading the file" + ex);
            }
                int[] default_PC_loc = new int[]{0,0,0,0,0,0,0,0,1,1,1,0};
                setRegVals("PC",default_PC_loc);
        } else {
            System.out.println("Something went wrong while opening the dialog box");
        }
    }

    // Function to convert a binary int value to binary array value.
    public int[] intToBinaryArray(String int_value){
        int[] returnVal = new int[16];
        char[] arr = int_value.toCharArray();
        for (int i = 0; i < 16; i++) {
            if (i < 16 - arr.length){
                returnVal[i] = 0;
            }else{
                returnVal[i] = Character.getNumericValue(int_value.charAt(i-(16 - arr.length)));
            }
        }
        return returnVal;
    }

    // Function to convert a binary int value to binary array value specifically for the PC
    public int[] intToBinaryArrayShort(String int_value){
        int[] returnVal = new int[12];
        char[] arr = int_value.toCharArray();
        for (int i = 0; i < 12; i++) {
            if (i < 12 - arr.length){
                returnVal[i] = 0;
            }else{
                returnVal[i] = Character.getNumericValue(int_value.charAt(i-(12 - arr.length)));
            }

        }
        return returnVal;
    }
}
