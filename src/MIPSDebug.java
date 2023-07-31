import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MIPSDebug {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Insufficient Arguments.");
            System.exit(1);
        }

        File infile;
        Scanner scanner;
        ArrayList<Instruction> instructions = new ArrayList<>();
        Emulator emulator = new Emulator(new Memory(), new Registers());
        emulator.parseFile(args[0]);
        if(args.length > 1){
            emulator.startScriptMode(args[1]);
            }else{
                emulator.startInteractiveMode();
            }
        System.out.println("done");
    }
}
