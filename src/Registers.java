import java.util.HashMap;

public class Registers {
    public HashMap<String, Integer> registers;

    public Registers(){
        init();
    }

    void init(){
        registers = new HashMap<>();
        for(String s : Instruction.registers){
            registers.put(s, 0);
        }
    }
}
