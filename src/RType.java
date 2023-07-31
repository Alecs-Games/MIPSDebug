public class RType extends Instruction{
    String rs = "0";
    String rt = "0";
    String rd = "0";
    int shamt = 0;

    public RType(String args, String opcode){
        this.split = args.replaceAll("\t|\\s|\\$", "").split(",");
        this.opcode = Opcode.valueOf(opcode);
    }

    public void parse(int currLine){
        boolean useshamt = false;
        boolean swapjr = false;
        switch (opcode) {
            case add, or, and, sub, slt -> {
            }
            case sll -> useshamt = true;
            case jr -> swapjr = true;
            default -> {
                System.out.println("invalid instruction: " + opcode);
                System.exit(0);
            }
        }
        if(split.length >= 1){
            if(swapjr){
                rs = split[0];
            }else {
                rd = split[0];
            }
        }
        if(split.length >= 2){
            if(useshamt){
                rt = split[1];
            }else{
                rs = split[1];
            }
        }
        if(split.length >= 3){
            if(useshamt){
                shamt = Integer.parseInt(split[2]);
            }else {
                rt = split[2];
            }

        }
        if(rs.equals("zero")){
            rs = "0";
        }
        if(rt.equals("zero")){
            rt = "0";
        }
        if(rd.equals("zero")){
            rd = "0";
        }
    }

    public String binary(){
        String opstr = "000000";
        String funct = "000000";
        String brs;
        String brt;
        String brd;
        String bshamt;


        switch (opcode) {
            case add -> {
                opstr = "000000";
                funct = "100000";
            }
            case or -> {
                opstr = "000000";
                funct = "100101";
            }
            case and -> {
                opstr = "000000";
                funct = "100100";
            }
            case sll -> {
                opstr = "000000";
                funct = "000000";
            }
            case sub -> {
                opstr = "000000";
                funct = "100010";
            }
            case slt -> {
                opstr = "000000";
                funct = "101010";
            }
            case jr -> {
                opstr = "000000";
                funct = "001000";
            }
            default -> {
                System.out.println("invalid instruction: " + opcode);
                System.exit(0);
            }
        }
        brs = Instruction.convertRegister(rs);
        brt = Instruction.convertRegister(rt);
        brd = Instruction.convertRegister(rd);
        bshamt = Instruction.dumbExtend(Integer.toBinaryString(shamt), 5);


        return opstr + " " + brs + " " + brt + " " + brd + " " + bshamt + " " + funct;
    }
    public void execute(Memory m, Registers r){
        switch (opcode) {
            case add -> r.registers.put(rd, r.registers.get(rs) + r.registers.get(rt));
            case or -> r.registers.put(rd, r.registers.get(rs) | r.registers.get(rt));
            case and -> r.registers.put(rd, r.registers.get(rs) & r.registers.get(rt));
            case sll -> r.registers.put(rd, r.registers.get(rt) << shamt);
            case sub -> r.registers.put(rd, r.registers.get(rs) - r.registers.get(rt));
            case slt -> r.registers.put(rd, (r.registers.get(rs) < r.registers.get(rt) ? 1 : 0));
            case jr -> m.programCounter = r.registers.get(rs) - 1;
            default -> {
                System.out.println("invalid instruction: " + opcode);
                System.exit(0);
            }
        }
    }
    public String toString(){
        return opcode.name() + " rs=" + rs + " rt=" + rt + " rd=" + rd;
    }

}
