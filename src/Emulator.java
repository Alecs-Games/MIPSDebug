import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import static java.lang.System.exit;

public class Emulator {
    public Memory mem;
    public Registers reg;
    public HashMap<String, Integer> labels = new HashMap<>();
    public int currLine = 0;

    public static String HELP_MESSAGE = """

    "h", "help" - print out a usage message
    "d", "dump" - print program counter and status of all registers
    "s", "step" - execute a single line
    "s", "step" [n] - execute [n] number of lines
    "r", "run" - run until end of file
    "m", "mem", "memory" [n1] [n2] - print out all memory from address [n1] to address [n2]
    "c", "clear" - clear all memory and reset program counter
    "pc" - print out the program counter
    "pc add" [n] - add [n] to the program counter
    "pc sub", "pc subtract" - subtract [n] from the program counter
    "q", "quit" - exit

            """;
    public static String VALID_FLAGS = "Valid flags: -v";
    public Emulator(Memory mem, Registers reg){
        this.mem = mem;
        this.reg = reg;
    }
    public boolean hasNextInstruction(){
        return mem.hasNextInstruction();
    }
    public void step(Boolean verbose, Boolean binary){
        if(hasNextInstruction()) {
            //System.out.println("Executing " + mem.nextInstruction().opcode);
            if(verbose){
                System.out.print("\n" + mem.nextInstruction().toString());
            }
            if(binary){
                System.out.print("\n" + mem.nextInstruction().binary());
            }
            mem.nextInstruction().execute(mem, reg);
            mem.incrementPC();
            currLine++;

        }else{
            System.out.println("Error: out of instructions");
            exit(1);
        }
    }
    public void startInteractiveMode(){
        Scanner scanner = new Scanner(System.in);
        String input;
        //noinspection InfiniteLoopStatement
        while(true){
            System.out.print("\nmips> ");
            input = scanner.nextLine();
            processCommand(input);
        }
    }
    public void startScriptMode(String infile){
        try{
            File in = new File(infile);
            Scanner scanner = new Scanner(in);
            String currLine;
            while(scanner.hasNextLine()){
                currLine = scanner.nextLine();
                System.out.println("mips> " + currLine);
                processCommand(currLine);
            }
        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    public void processCommand(String command){
        String[] split = Arrays.stream(command.split("\\s")).filter(s -> s.charAt(0) != '-').toArray(String[]::new);
        String[] flags = Arrays.stream(command.split("\\s")).filter(s -> s.charAt(0) == '-').toArray(String[]::new);
        boolean verbose = false;
        boolean binary = false;
        for(String arg : flags){
            switch(arg){
                case "-v" -> {
                    verbose = true;
                }case "-b" -> {
                    binary = true;
                }
                default -> {
                    System.out.println(VALID_FLAGS);
                }
            }
        }
        switch(split[0]){
            case "h", "help" -> System.out.print(HELP_MESSAGE);
            case "d", "dump" -> {
                System.out.println("\nProgram Counter = " + mem.programCounter);
                int i = 0;
                for(String key : Instruction.registers){
                    i++;
                    String s = ("$" + key + " = " + reg.registers.get(key));
                    System.out.printf("%-16s", s);
                    if(i == 4){
                        System.out.print("\n");
                        i = 0;
                    }
                }
                System.out.print("\n\n");
            }
            case "s", "step" -> {
                int insts = 1;
                int i;
                if(split.length > 1){
                    try{
                        insts = Integer.parseInt(split[1]);
                    }catch(NumberFormatException e){
                        System.out.println(e.getMessage());
                        exit(1);
                    }
                }
                for(i = 0; i < insts; i++){
                    if(hasNextInstruction()){
                        step(verbose, binary);
                    }else{
                        break;
                    }
                }
                System.out.println(i + " instruction(s) executed");

            }
            case "r", "run" -> {
                while(hasNextInstruction()){
                    step(verbose, binary);
                }
                System.out.println("End of program reached. Program Counter = " + mem.programCounter);
            }
            case "m", "mem", "memory" -> {
                int start = 0;
                int end = 0;
                if(split.length < 3){
                    System.out.println("Usage: m num1 num2");
                    break;
                }
                try{
                    start = Integer.parseInt(split[1]);
                    end = Integer.parseInt(split[2]);
                }catch(NumberFormatException e){
                    System.out.println(e.getMessage());
                    exit(1);
                }

                for( ; start <= end; start++){
                    System.out.println("[" + start + "] = " + mem.dataMemory[start]);
                }
                System.out.println();
            }
            case "c", "clear" -> {
                System.out.println("Simulator reset.");
                reg.init();
                mem.init();
            }
            case "pc" -> {
                if(split.length > 1){
                    boolean add = false;
                    boolean sub = false;
                    switch(split[1]){
                        case "add" -> {
                            add = true;
                        }
                        case "sub", "subtract" -> {
                            sub = true;
                        }
                        default -> {
                            try{
                                mem.programCounter = Integer.parseInt(split[1]);
                            }catch(NumberFormatException e){
                                System.out.println("Incorrectly formatted number");
                            }
                        }
                    }
                    if(split.length > 2){
                        if(add){
                            mem.programCounter += Integer.parseInt(split[2]);
                        }else if(sub){
                            mem.programCounter -= Integer.parseInt(split[2]);
                        }
                    }
                }
                System.out.println("Program Counter = " + mem.programCounter);
            }
            case "q", "quit" -> exit(0);
            default -> {
                //try to execute a command if valid
                try{
                    Instruction.Opcode opcode = Instruction.Opcode.valueOf(split[0]);
                    Instruction i = parseInstruction(command);
                    i.parse(currLine);
                    i.execute(mem, reg);
                    System.out.println("Instruction executed. This instruction did not increment the program counter.");
                }catch(IllegalArgumentException e){
                    System.out.println("Unknown command.");
                }
            }
        }
    }

    public Instruction parseInstruction(String inLine){
        String line = "";
        String[] safetySplit = inLine.split("#");
        if(safetySplit.length > 0){
            line = safetySplit[0];
        }
        if(line.contains(":")){
            line = line.split(":", 2)[1];
        }
        String[] splitline = line.replaceAll("\t", "").trim().split("\\$|\\s", 2);
        if(splitline[0].matches("add|or|and|sll|sub|slt|jr")){
            return new RType(splitline[1], splitline[0]);
        }else if(splitline[0].matches("addi|beq|bne|lw|sw")){
            return new IType(splitline[1], splitline[0], labels);
        }else if(splitline[0].matches("j|jal")){
            return new JType(splitline[1], splitline[0], labels);
        }else if(splitline[0].length() > 0){
            return new RType(splitline[0], splitline[0]);
        }
        return null;
    }

    public void parseFile(String filename){
        parseLabels(filename);
        //second pass, parsing instructions
        try{
            File infile = new File(filename);
            Scanner scanner = new Scanner(infile);
            while (scanner.hasNextLine()) {
                Instruction i = parseInstruction(scanner.nextLine());
                if(i != null){
                    mem.instructions.add(i);
                }
            }
        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
            exit(1);
        }
        int parsingLine = 0;
        for(Instruction i : mem.instructions) {
            parsingLine++;
            i.parse(parsingLine);
        }
    }
    public void parseLabels(String filename){
        try{
            File infile = new File(filename);
            Scanner scanner = new Scanner(infile);
            int currLine = 0;
            while (scanner.hasNextLine()) {
                String line = "";
                String[] safetySplit = scanner.nextLine().split("#");
                if(safetySplit.length > 0){
                    line = safetySplit[0];
                }

                if(line.contains(":")){
                    String[] split = line.split(":", 2);
                    String labelName = split[0];
                    labels.put(labelName, currLine);
                }
                //check for valid instruction
                if (line.replaceAll("\\t|\\s", "").length() > 0) {
                    currLine++;
                }
            }
            scanner.close();
        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
            exit(1);
        }
    }


}
