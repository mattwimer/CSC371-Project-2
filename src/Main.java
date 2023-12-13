
// Purpose of this program is to check your Java environment.

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner userInput = new Scanner(System.in);
        System.out.println("Which file should I test? ");
        // String path = userInput.nextLine();

        // ****Professor, change this file path to test different files****
        String path = ".\\Test_2.txt";
        File file = new File(path);
        System.out.println("Using: \"" + path + "\" as path.");

        while(!file.exists()){
            System.out.println("Oops, that file didn't exist. Try entering a file name: ");
            file = new File(userInput.nextLine());
        }
        try{
            Scanner fileInput = new Scanner(file);
            CFG.buildCFG(fileInput);
        }
        catch(FileNotFoundException f){
            System.out.println("Oops! This should be impossible.");
        }


	}

}
