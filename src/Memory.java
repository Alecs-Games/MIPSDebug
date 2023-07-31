import java.util.ArrayList;
import java.util.Arrays;

public class Memory {
    public Integer[] dataMemory = new Integer[8192];
    public ArrayList<Instruction> instructions = new ArrayList<Instruction>();

    public int programCounter;
    public Memory(){
        init();
    }

    public void init(){
        programCounter = 0;
        Arrays.fill(dataMemory, 0);
    }

    public Instruction nextInstruction(){
        return instructions.get(programCounter);
    }

    public void incrementPC(){
        programCounter++;
    }

    public boolean hasNextInstruction(){
        return programCounter < instructions.size();
    }
}
