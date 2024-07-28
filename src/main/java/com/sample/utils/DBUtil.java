package com.sample.utils;

import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DBUtil {

    public static Map<String, String> getOracleDerivedNamesForTableAndAttrs(String domainClassName, List<Map<String, String>> attrs , boolean isDBOracle){
        Map<String, String> derivedNames = new LinkedHashMap<>();
        String domainName = convertToCamelCase(domainClassName);
        if(isDBOracle)
            derivedNames.put(domainClassName, convertToOracleFriendlyName(domainName));
        else
            derivedNames.put(domainClassName, domainName);


        for (Map<String, String> attr : attrs) {
            if (isDBOracle) {
                String oracleFriendlyName = convertToOracleFriendlyName(attr.get("name"));
                System.out.println(attr.get("name") + "===>" + oracleFriendlyName);
                derivedNames.put(attr.get("name"), oracleFriendlyName);
            }
            else {
                derivedNames.put(attr.get("name"), attr.get("name"));
            }
            /*for (String key : attr.keySet()) {
                if (isDBOracle) {
                    String oracleFriendlyName = convertToOracleFriendlyName(attr.get(key));
                    derivedNames.put(attr.get(key), oracleFriendlyName);
                }
                else {
                    derivedNames.put(key, key);
                }
            }*/
        }

        return derivedNames;
    }

    private static String convertToOracleFriendlyName(String originalString){
        StringWriter result = new StringWriter();
        char[] originalCharacters = originalString.toCharArray();
        for(char charToInspect : originalCharacters){
            if(Character.isUpperCase(charToInspect)){
                result.write("_" + Character.toLowerCase(charToInspect));
            }else{
                result.write(charToInspect);
            }
        }
        return result.toString();
    }

    private static String convertToCamelCase(String className){
        return Character.toLowerCase(className.charAt(0)) +
                className.substring(1);
    }

}
