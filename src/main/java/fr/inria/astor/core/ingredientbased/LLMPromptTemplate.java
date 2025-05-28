package fr.inria.astor.core.ingredientbased;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to manage prompt templates for LLM-based repair.
 * This allows referencing templates by name and provides predefined templates.
 */
public class LLMPromptTemplate {
    
    // Static map of predefined templates
    private static final Map<String, String> predefinedTemplates = new HashMap<>();
    
    // Initialize predefined templates
    static {
        // More detailed repair prompt with context
        
        
        // New template for multiple solutions
        predefinedTemplates.put(
            "MULTIPLE_SOLUTIONS",
                "There's a bug in the following Java code:\n\n{buggycode}\n" +
                "The failing test case is:\n\n{testcode}\n" +
                "Please provide {nsolutions} different possible fixes that might make the test pass.\n" +
                "Format your response as follows:\n" +
                "SOLUTION 1:\n[first solution code]\n" +
                "SOLUTION 2:\n[second solution code]\n" +
                "SOLUTION 3:\n[third solution code]\n" +
                "Each solution should be a single line of code that can replace the buggy line, do not write code in different lines." +
                "Do not include extra explanations, just the code solutions."
        );
    }
    
    /**
     * Get a template by name
     * 
     * @param templateName The name of the predefined template
     * @return The template string or null if not found
     */
    public static String getTemplate(String templateName) {
        return predefinedTemplates.get(templateName);
    }
    
    /**
     * Check if a template with the given name exists
     * 
     * @param templateName The name to check
     * @return true if the template exists, false otherwise
     */
    public static boolean hasTemplate(String templateName) {
        return predefinedTemplates.containsKey(templateName);
    }
    
    /**
     * Fill a template with the provided values
     * 
     * @param template The template string with placeholders
     * @param buggyCode The buggy code to insert
     * @param testCode The test code to insert
     * @param maxP The maximum number of solutions to provide (default is 3)
     * @return The filled template
     */
    public static String fillTemplate(String template, String buggyCode, String testCode, int maxP) {
        return template
                .replace("{buggycode}", buggyCode)
                .replace("{testcode}", testCode)
                .replace("{nsolutions}", String.valueOf(maxP));
    }
    
    /**
     * Get a filled template by name
     * 
     * @param templateName The name of the template
     * @param buggyCode The buggy code to insert
     * @param testCode The test code to insert
     * @return The filled template or null if template not found
     */
    public static String getFilledTemplate(String templateName, String buggyCode, String testCode, int maxP) {
        String template = getTemplate(templateName);
        if (template == null) {
            return null;
        }
        return fillTemplate(template, buggyCode, testCode, maxP);
    }
}