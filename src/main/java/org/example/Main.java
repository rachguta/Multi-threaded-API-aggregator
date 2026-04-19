package org.example;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args)  {
        Scanner in = new Scanner(System.in);
        System.out.println("Choose the mode:\n1. Auto\n2. Interactive");
        String mode = "";
        while(!mode.equals("1") && !mode.equals("2")) {
            mode = in.nextLine().trim();
            if (mode.equals("1")) {
                autoMode(in);
            } else if (mode.equals("2")) {
                interactiveMode(in);
            } else {
                System.out.println("Invalid input. Please enter 1 or 2.");
            }
        }
        in.close();
    }

    public static void autoMode(Scanner in) {
        System.out.println("Auto mode");
        String[] apis;
        String outputFileFormat;
        String input;
        input = in.nextLine();
        System.out.println("Enter APIs to fetch data from");
        apis = input.split("\\s+");
        System.out.println("Enter output file format (json or csv):");
        outputFileFormat = in.nextLine().trim().toLowerCase();

    }

    private static void interactiveMode(Scanner in) {
        System.out.println("Interactive mode");

    }








}

