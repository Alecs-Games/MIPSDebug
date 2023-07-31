import java.util.ArrayList;
import java.util.Arrays;

public class Instruction {
    public enum Opcode {
        and,
        or,
        add,
        addi,
        sll,
        sub,
        slt,
        beq,
        bne,
        lw,
        sw,
        j,
        jr,
        jal
    }
    public static ArrayList<String> registers = new ArrayList<>(Arrays.asList(
            "0",
            "v0",
            "v1",
            "a0",
            "a1",
            "a2",
            "a3",
            "t0",
            "t1",
            "t2",
            "t3",
            "t4",
            "t5",
            "t6",
            "t7",
            "s0",
            "s1",
            "s2",
            "s3",
            "s4",
            "s5",
            "s6",
            "s7",
            "t8",
            "t9",
            "sp",
            "ra"));

    public Opcode opcode;
    public String[] split;
    public static String convertRegister(String reg){
        int num = registers.indexOf(reg);
        if(reg.equals("zero")){
            num = 0;
        }
        String str = Integer.toBinaryString(num);
        return dumbExtend(str, 5);
    }

    public static String signExtend(String num, int len){
        char sign = num.charAt(0);
        StringBuilder numBuilder = new StringBuilder(num);
        for(int i = numBuilder.length(); i < len; i++) {
            numBuilder.insert(0, sign);
        }
        num = numBuilder.toString();
        //truncate if too long
        return limitLen(num, len);
    }

    public static String dumbExtend(String num, int len){
        //System.out.println("Sign extending: " + num);
        StringBuilder numBuilder = new StringBuilder(num);
        for(int i = numBuilder.length(); i < len; i++) {
            numBuilder.insert(0, "0");
        }
        num = numBuilder.toString();
        //System.out.println("Result: " + num);
        return limitLen(num, len);
    }

    public static String limitLen(String num, int len){
        if(num.length() > len){
            num = num.substring(num.length() - len);
        }
        return num;
    }

    public void parse(int currLine){

    }
    public void execute(Memory m, Registers r){

    }

    public String binary(){
        return "No subclass";
    }
}