import java.util.ArrayList;
import java.util.Arrays;
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
        // if a rule's only production is epsilon, remove it entirely.
        ArrayList<String> flagged = new ArrayList<String>();
        for (String rule : cfg.keySet())
            if(cfg.get(rule).size() == 1 && cfg.get(rule).get(0).compareTo("0") == 0)
                flagged.add(rule);
                // cfg.remove(rule); throws ConcurrentModificationException :/
        for (String rule : flagged)
            removeRule(cfg, rule);

        
        
        flagged = new ArrayList<String>();
        // for all rules,
        for (String rule : cfg.keySet())
            // for each production
            for (int i = cfg.get(rule).size() - 1; i > 0 ; i--)
            // if one of a rules productions is epsilon, remove that production and flag it. 
                if(cfg.get(rule).get(i).compareTo("0") == 0){
                    flagged.add(rule);
                    cfg.get(rule).remove(i); // changes index of elements right of it, so we begin at last index
                }


        // for all rules,
        for (String rule : cfg.keySet()){
            // for each production
            for (int i = cfg.get(rule).size() - 1; i > 0 ; i--){
                String production = cfg.get(rule).get(i);
                // for each flagged rule,
                int count = 0;
                for (String flagRule : flagged)
                    // count occurrences of rule in production
                    for (int j = 0 ; j < production.length() ; j++)
                        if(production.charAt(i) == flagRule.charAt(0))
                            count++;
                
                // create an inclusion matrix ie for each flagged rule found in production, create combinations of
                //      the string either including the flagged rule or not
                // generateInclusionMatrix()

            }


        }
        
        return cfg;
    }

    private static ArrayList<ArrayList<Boolean>> generateInclusionMatrix(int numCombinations){
        int times = (int)Math.pow(2, numCombinations);
        ArrayList<ArrayList<Boolean>> bools = new ArrayList<ArrayList<Boolean>>(times);
        ArrayList<Boolean> currentCombo = new ArrayList<Boolean>(numCombinations);
        int[] countTilSwap = new int[numCombinations];
        for (int i = 0 ; i < numCombinations ; i++){
            currentCombo.add(false);
            countTilSwap[i] = ((int)Math.pow(2, i));
        }
        for (int i = 0 ; i < times ; i++){
            bools.add((ArrayList<Boolean>)currentCombo.clone());
            for(int j = 0 ; j < numCombinations ; j++){
                countTilSwap[j] -= 1;
                if (countTilSwap[j] == 0){
                    currentCombo.set(j, !currentCombo.get(j)); // swap it
                    countTilSwap[j] = ((int)Math.pow(2, j)); // count down to next swap
                }
            }
        }


        return bools;
    }

    /**
     * @param cfg the CFG to remove useless rules from.
     * @return the given CFG minus any useless rules.
     */
    public static HashMap<String, ArrayList<String>> removeUselessRules(HashMap<String, ArrayList<String>> cfg){
        

        return cfg;
    }

    /**
     * @param cfg the CFG to remove a rule from.
     * @param rule the rule to remove from the CFG.
     * @return the given CFG minus any useless rules.
     */
    private static HashMap<String, ArrayList<String>> removeRule(HashMap<String, ArrayList<String>> cfg, String rule){
        // System.out.println("Removing \"" + rule + "\" from CFG...");
        cfg.remove(rule);
        // for each rule
        for (String key : cfg.keySet()) 
            // for each production
            for (int i = 0 ; i < cfg.get(key).size(); i++)
                if(cfg.get(key).get(i).indexOf(rule) != -1) /* one of the productions contains the rule as a character */
                    //replace the production with the same production excluding removed rule
                    cfg.get(key).set(i, cfg.get(key).get(i).replaceAll(rule, ""));
                
            
        

        return cfg;
    }
}
