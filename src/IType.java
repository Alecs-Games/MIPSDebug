import java.util.HashMap;
import java.util.Objects;

public class IType extends Instruction{
    HashMap<String, Integer> labels;
    String rs = "0";
    String rt = "0";
    int imm = 0;

    public IType(String args, String opcode, HashMap<String, Integer> labels){
        this.split = args.replaceAll("\t|\\s|\\$", "").split(",");
        this.opcode = Opcode.valueOf(opcode);
        this.labels = labels;
    }

    public void parse(int currLine){
        boolean rtfirst = true;
        switch (opcode) {
            case addi, lw, sw -> {
            }
            case beq, bne -> rtfirst = false;
            default -> {
                System.out.println("invalid instruction: " + opcode);
                System.exit(0);
            }
        }
        if(split.length >= 1){
            if(rtfirst){
                rt = split[0];
            }else{
                rs = split[0];
            }
        }
        if(split.length >= 2){
            if(rtfirst){
                if(split[1].contains("(") || split[1].contains(")")){
                    String[] parenthSplit = split[1].split("[()]");
                    rs = parenthSplit[1];
                    imm = Integer.parseInt(parenthSplit[0]);
                }else{
                    //addi
                    rs = split[1];
                }
            }else{
                rt = split[1];
            }

        }
        if(split.length >= 3){
            try{
                //handle int
                imm = Integer.parseInt(split[2]);

            }catch(NumberFormatException e){
                //handle label
                imm = labels.get(split[2]) - currLine;
            }
        }
        if(rs.equals("zero")){
            rs = "0";
        }
        if(rt.equals("zero")){
            rt = "0";
        }
    }
    public String binary(){
        String opstr = "000000";
        String brs;
        String brt;
        String bimm;


        brs = Instruction.convertRegister(rs);
        brt = Instruction.convertRegister(rt);
        bimm = Integer.toBinaryString(imm);
        bimm = Instruction.dumbExtend(bimm, 16);

        switch (opcode) {
            case addi -> opstr = "001000";
            case beq -> opstr = "000100";
            case bne -> opstr = "000101";
            case lw -> opstr = "100011";
            case sw -> opstr = "101011";
            default -> {
                System.out.println("invalid instruction: " + opcode);
                System.exit(0);
            }
        }
        return opstr + " " + brs + " " + brt + " " + bimm;
    }

    public void execute(Memory m, Registers r){
        switch(opcode){
            case addi:
                r.registers.put(rt, r.registers.get(rs) + imm);
                break;
            case beq:
                if(Objects.equals(r.registers.get(rs), r.registers.get(rt))){
                    m.programCounter = m.programCounter + imm;
                }
                break;
            case bne:
                if(!Objects.equals(r.registers.get(rs), r.registers.get(rt))){
                    m.programCounter = m.programCounter + imm;
                }
                break;
            case lw:
                r.registers.put(rt, m.dataMemory[r.registers.get(rs) + imm]);
                break;
            case sw:
                m.dataMemory[r.registers.get(rs) + imm] = r.registers.get(rt);
                break;
            default:
                System.out.println("invalid instruction: " + opcode);
                System.exit(0);
                break;
        }
    }
    public String toString(){
        return opcode.name() + " rs=" + rs + " rt=" + rt + " immediate=" + imm;
    }
}
