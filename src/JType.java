import java.util.HashMap;

public class JType extends Instruction{
    HashMap<String, Integer> labels;
    int jamt = 0;

    public JType(String args, String opcode, HashMap<String, Integer> labels){
        this.split = args.replaceAll("\t|\\s", "").split(",");
        this.opcode = Opcode.valueOf(opcode);
        this.labels = labels;
    }

    public void parse(int currLine){
        if(!(opcode.equals(Opcode.j) || opcode.equals(Opcode.jal))){
            System.out.println("invalid instruction: " + opcode);
            System.exit(0);
        }
        if(split.length > 0){
            int immint;
            try{
                //handle int
                immint = Integer.parseInt(split[0]);

            }catch(NumberFormatException e){
                //handle label
                immint = labels.get(split[0]);
            }
            jamt = immint;
        }
    }
    public String binary(){
        String opstr = "000000";
        String bjamt;
        switch (opcode) {
            case j -> opstr = "000010";
            case jal -> opstr = "000011";
            default -> {
                System.out.println("invalid instruction: " + opcode);
                System.exit(0);
            }
        }
        bjamt = Integer.toBinaryString(jamt);
        bjamt = Instruction.dumbExtend(bjamt, 26);
        return opstr + " " + bjamt;
    }

    public void execute(Memory m, Registers r){
        switch (opcode) {
            case j -> m.programCounter = jamt;
            case jal -> {
                r.registers.put("ra", m.programCounter + 1);
                m.programCounter = jamt - 1;
            }
            default -> {
                System.out.println("invalid instruction: " + opcode);
                System.exit(0);
            }
        }
    }
    public String toString(){
        return opcode.name() + " destination=" + jamt;
    }
}
