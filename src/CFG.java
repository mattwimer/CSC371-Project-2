import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CFG {
    public static HashMap<String, ArrayList<String>> buildCFG(Scanner fileInput){
        HashMap<String,ArrayList<String>> rules = new HashMap<String,ArrayList<String>>();
        while(fileInput.hasNextLine()){
            String rule = "";
            String activeProduction = "";
            for (char c : fileInput.nextLine().toCharArray()) {
                if(c == '\n')
                    break;
                else if(rule.length() == 0){
                    rule += c;
                    rules.put(rule, new ArrayList<String>());
                }
                else if(c == '|'){
                    rules.get(rule).add(activeProduction);
                    activeProduction = "";
                }
                else if(c != '-')
                    activeProduction += c;
            }
            rules.get(rule).add(activeProduction);

            // System.out.println(rule + " -> " + rules.get(rule));
        }



        return rules;
    }
}
