import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class CFG {
    /**
     * @param fileInput Scanner object primed with the file to read
     * @return a CFG in the form of a HashMap, with key as rule name/symbol and value as list
     *         of productions.
     */
    public static HashMap<String, ArrayList<String>> buildCFG(Scanner fileInput){
        HashMap<String,ArrayList<String>> rules = new HashMap<String,ArrayList<String>>();
        boolean initialRuleExists = false;
        while(fileInput.hasNextLine()){
            String rule = "";
            String activeProduction = "";
            for (char c : fileInput.nextLine().toCharArray()) {
                if(c == '\n')
                    break;
                else if(rule.length() == 0){
                    rule += c;
                    rules.put(rule, new ArrayList<String>());
                    if(!initialRuleExists){
                        ArrayList<String> temp = new ArrayList<String>(1);
                        temp.add(rule);
                        rules.put("INITIAL", temp);
                        initialRuleExists = true;
                    }
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
     *  so even though initial rule is for example S, it may not appear first. This is due to HashMap's 
     *  internal implementation of the iterator returned by HashMap.keySet().
     * 
     *  @param cfg the CFG to be printed.
     */
    public static void printCFG(HashMap<String, ArrayList<String>> cfg){
        for(String key : cfg.keySet()){
            if(key.compareTo("INITIAL") != 0)
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
            if(rule.compareTo("INITIAL") != 0 && cfg.get(rule).size() == 1 && cfg.get(rule).get(0).compareTo("0") == 0)
                flagged.add(rule);
                // cfg.remove(rule); throws ConcurrentModificationException :/
        for (String rule : flagged)
            removeRule(cfg, rule);

        
        
        flagged = new ArrayList<String>();
        // for all rules,
        for (String rule : cfg.keySet())
            if(rule.compareTo("INITIAL") != 0)
            // for each production
                for (int i = cfg.get(rule).size() - 1; i >= 0 ; i--)
                // if one of a rules productions is epsilon, remove that production and flag it. 
                    if(cfg.get(rule).get(i).compareTo("0") == 0){
                        flagged.add(rule);
                        cfg.get(rule).remove(i); // changes index of elements right of it, so we begin at last index
                    }

        // System.out.println("Flagged rules: " + flagged);
        // for all rules,
        for (String rule : cfg.keySet()){
            if(rule.compareTo("INITIAL") != 0){
            // for each production
                for (int i = cfg.get(rule).size() - 1; i >= 0 ; i--){
                    String production = cfg.get(rule).get(i);
                    // for each flagged rule,
                    int count = 0;
                    for (String flagRule : flagged)
                        // count occurrences of rule in production
                        for (int j = 0 ; j < production.length() ; j++)
                            if(production.charAt(j) == flagRule.charAt(0))
                                count++;

                    // only simplify if a flagged rule appeared in the production
                    if(count >= 1){
                        // create an inclusion matrix i.e. for each flagged rule found in production, create combinations of
                        //      the string either including the flagged rule or not
                        ArrayList<ArrayList<Boolean>> decisions = generateInclusionMatrix(count);
                        // for each new possible production we need to add
                        for (ArrayList<Boolean> decision : decisions){
                            int k = 0;
                            String stringPermutation = "";
                            for (int j = 0 ; j < production.length() ; j++){
                                if(flagged.contains(""+production.charAt(j))) // if character is a flagged rule
                                    stringPermutation += decision.get(k++) ? production.charAt(j):""; // use decision[k] to either include or not in the new string
                                else
                                    stringPermutation += production.charAt(j); // otherwise, place the symbol back in the string
                                // stringPermutation += flagged.contains(""+production.charAt(j)) && !decision.get(k++) ? "" : production.charAt(j);
                                // above comment would suffice for above if/else. ternary operators are cool
                            }
                            if(stringPermutation.length() > 0)
                                cfg.get(rule).add(stringPermutation); // add all the permutations back to the rule's list of productions
                        }
                        cfg.get(rule).remove(i); //remove the original production ; it will be duplicated by the above loop
                    }
                }
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
        // non-generating rules
        // for all rules,
        HashSet<String> flagged = new HashSet<String>();
        for (String rule : cfg.keySet())
        if(rule.compareTo("INITIAL") != 0)
        // if rule can not reach a terminal production, remove it
        if(!ruleCanBecomeTerminal(cfg, rule))
        // removeRule(cfg, rule);
        flagged.add(rule);
        for (String rule : flagged)
        removeRule(cfg, rule);
        
        // non-reachable rules
        // printCFG(cfg);
        
        HashSet<String> reachable = new HashSet<String>();
        flagged = new HashSet<String>();
        reachable.add(cfg.get("INITIAL").get(0));
        determineReachableFrom(cfg, reachable, cfg.get("INITIAL").get(0));
        for (String rule : cfg.keySet()){
            if(rule.compareTo("INITIAL") != 0){
                if(!reachable.contains(rule)){
                    // System.out.println("Removing \"" + rule + "\" from CFG...");
                    // removeRule(cfg, rule);
                    flagged.add(rule);
                }
            }
        }


        for (String rule : flagged)
            removeRule(cfg, rule);

        return cfg;
    }

    /**
     * @param cfg the CFG to check in
     * @param rule the rule we will read a production from
     * @return true if a terminal production can be reached, false otherwise
     */
    private static boolean ruleCanBecomeTerminal(HashMap<String, ArrayList<String>> cfg, String rule){
        if(cfg.get(rule).isEmpty()) // if we run out of productions to check, rule is non-generating
            return false;

        String production = cfg.get(rule).get(0);
        for(int i = 0 ; i < production.length() ; i++){
            // if a symbol is not terminal, check if it can become terminal
            if(Character.isUpperCase(production.charAt(i))){
                cfg.get(rule).remove(0); // remove current production
                if(!ruleCanBecomeTerminal(cfg, "" + production.charAt(i))) // check if non-terminal symbol can become terminal
                    return ruleCanBecomeTerminal(cfg, rule);
                // otherwise, continue checking the production
                cfg.get(rule).add(production);
            }    
        }
        return true;
            
            
    }
    
    /**
     * @param cfg the CFG to traverse
     * @param reachable the set to add rules that are reachable from rule's productions
     * @param rule the rule to check productions from
     */
    private static void determineReachableFrom(HashMap<String, ArrayList<String>> cfg, HashSet<String> reachable, String rule){
        if(cfg.get(rule).isEmpty()) // if we run out of productions to check, finish
            return;

        // for each production
        for (int i = 0 ; i < cfg.get(rule).size() ; i++){
            String production = cfg.get(rule).get(i);
            for (int j = 0 ; j < production.length() ; j++){
                if(Character.isUpperCase(production.charAt(j))){
                    reachable.add("" + production.charAt(j));
                    cfg.get(rule).remove(i); // remove current production
                    determineReachableFrom(cfg, reachable, ""+production.charAt(j));
                    cfg.get(rule).add(production); // place back the production
                }
            }
        }
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
