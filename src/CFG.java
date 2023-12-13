import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CFG {
    /**
     * 
     * @param fileInput Scanner object primed with the file to read
     * @return a CFG in the form of a HashMap, with key as rule name/symbol and value as list
     *         of productions.
     */
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

    /** Note: I cannot control the order which the production rules are printed out in
     *  so even though initial rule is S, it may not appear first. This is due to HashMap's 
     *  internal implementation of the iterator returned by HashMap.keySet().
     * 
     *  @param cfg the CFG to be printed.
     */
    public static void printCFG(HashMap<String, ArrayList<String>> cfg){
        for(String key : cfg.keySet()){
            System.out.println(key + " -> " + cfg.get(key));
        }
    }

    /**
     * @param cfg the CFG to be simplified.
     * @return the simplified CFG.
     */
    public static HashMap<String, ArrayList<String>> simplifyCFG(HashMap<String, ArrayList<String>> cfg){
        removeEpsilonRules(cfg);
        removeUselessRules(cfg);
        
        return cfg;
    }

    /**
     * @param cfg the CFG to remove epsilon rules from.
     * @return the given CFG minus any epsilon rules.
     */
    public static HashMap<String, ArrayList<String>> removeEpsilonRules(HashMap<String, ArrayList<String>> cfg){
       
        
        return cfg;
    }

    /**
     * @param cfg the CFG to remove useless rules from.
     * @return the given CFG minus any useless rules.
     */
    public static HashMap<String, ArrayList<String>> removeUselessRules(HashMap<String, ArrayList<String>> cfg){
        

        return cfg;
    }
}
