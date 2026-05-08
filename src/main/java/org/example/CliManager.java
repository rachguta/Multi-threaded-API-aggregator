package org.example;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CliManager {
    private static final Scanner in = new Scanner(System.in);

    public static API[] readApis() throws NoSuchElementException {
        Set<API> apis = new LinkedHashSet<>();
        List<String> apiNames = new ArrayList<>();
        for(API api : API.values()) {
            apiNames.add(api.name().toLowerCase());
        }
        String apiNamesString = String.join(" ", apiNames);
        System.out.println("Write a list of APIs that you want to use (" +
                apiNamesString + ").");
        while(true){
            String line = in.nextLine().trim();
            String[] enteredApis = line.split("[,;\\s]+");
            boolean allValid = true;
            for(String enteredApi : enteredApis){
                if(!apiNames.contains(enteredApi.toLowerCase())){
                    System.out.println(enteredApi + " is not in available api's, try again");
                    allValid = false;
                    apis.clear();
                    break;
                }
                else{
                    API api = API.valueOf(enteredApi.toUpperCase());
                    apis.add(api);
                }
            }
            if(allValid){
                return apis.toArray(new API[0]);
            }
        }
    }


    public static API[] parseApisFromArgs(String[] args) throws IllegalArgumentException {
        if(args == null || args.length < 2){
            throw new IllegalArgumentException("Expected at least one api and a format");
        }
        List<String> tokens = new ArrayList<>();
        for(int i = 0; i < args.length - 1; i++){
            String part = args[i];
            if(part == null) continue;
            String[] split = part.trim().split("[,;\\s]+");
            for(String s : split){
                if(!s.isBlank()) tokens.add(s.toLowerCase());
            }
        }
        if(tokens.isEmpty()){
            throw new IllegalArgumentException("No API names were provided");
        }
        LinkedHashSet<API> apis = new LinkedHashSet<>();
        for(String tok : tokens){
            try{
                apis.add(API.valueOf(tok.toUpperCase()));
            }catch(IllegalArgumentException e){
                throw new IllegalArgumentException("Unknown API name: '" + tok + "'");
            }
        }
        return apis.toArray(new API[0]);
    }

    public static String parseFormatFromArgs(String[] args) throws IllegalArgumentException {
        if(args == null || args.length == 0){
            throw new IllegalArgumentException("Expected format as last argument (json|csv)");
        }
        String raw = args[args.length - 1].trim().toLowerCase();
        if(raw.equals("json")) return "json";
        if(raw.equals("csv")) return "csv";
        throw new IllegalArgumentException("Unknown format: '" + raw + "'. Allowed: json, csv");
    }

    public static String readFormat() throws NoSuchElementException {
        System.out.println("Choose the output format:\n 1. JSON\n 2. CSV");
        while(true){
            String formatStr = in.nextLine().trim();
            if(formatStr.equals("1") || formatStr.equalsIgnoreCase("json")){
                return "json";
            }
            else if(formatStr.equals("2") || formatStr.equalsIgnoreCase("csv")){
                return "csv";
            }
            else{
                System.out.println("Invalid value of format. Try again");
            }
        }
    }

    public static String readParameter(API api) throws NoSuchElementException {
        String message = api.name().toLowerCase() + ": Write " + api.getParameter();
        if(api.getComment().isEmpty()){
            System.out.println(message);
        }
        else{
            System.out.println(message + " " + api.getComment());
        }
        String parameter = in.nextLine().trim();
        return URLEncoder.encode(parameter, StandardCharsets.UTF_8);

    }

    public static String readFileName() throws NoSuchElementException {
        System.out.println("Choose the file name:");
        while(true){
            String fileName = in.nextLine().trim();
            fileName = fileName.replaceAll("\\s+", "_");
            if(!fileName.matches(".*[\\\\/:*?\"<>|].*")){
                return fileName;
            }
            System.out.println("Invalid file name. It should not contain the following characters: \\ / : * ? \" < > |. Try again");
        }
    }

    public static API readApi() throws NoSuchElementException {
        List<String> apiNames = new ArrayList<>();
        for (API api : API.values()) {
            apiNames.add(api.name().toLowerCase());
        }
        System.out.println("Choose available api: ");
        for (API api : API.values()) {
            System.out.println((api.ordinal() + 1) + ". " + api.name().toLowerCase());
        }
        while (true) {
            String line = in.nextLine().trim();
            if (isInteger(line)) {
                int num = Integer.parseInt(line);
                if (num >= 1 && num <= API.values().length) {
                    return API.values()[num - 1];
                }
            }
            else if(apiNames.contains(line.toLowerCase())){
                return API.valueOf(line.toUpperCase());
            }
            System.out.println("Invalid value of api. Try again");
        }
    }

    public static String readFileMode() throws NoSuchElementException {
        System.out.println("""
                Select the file mode: create a new one or add \
                to an existing one:
                 1. create
                 2. add""");
        while(true){
            String line = in.nextLine().trim();
            if(line.equals("1") || line.equals("create")){
                return "create";
            }
            else if(line.equals("2") || line.equals("add")){
                return "add";
            }
            else{
                System.out.println("Invalid value of file mode. Try again");
            }
        }
    }

    public static String readOutputMode() throws NoSuchElementException {
        System.out.println("""
                Select the output mode: display the contents of the file \
                either in its entirety or using a specific API.
                 1. fully
                 2. by api""");
        while(true){
            String line = in.nextLine().trim();
            if(line.equals("1") || line.equals("fully")){
                return "fully";
            }
            else if(line.equals("2") || line.equals("by api")){
                return "by api";
            }
            else{
                System.out.println("Invalid value of output mode. Try again");
            }
        }
    }

    private static boolean isInteger(String str){
        try{
            Integer.parseInt(str);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }





}
