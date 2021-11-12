import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Calculator {

    public static void main(String[] args) {
        if (args.length == 0) {
            consoleMethod();
        } else {
            fileMethod(args[0], args[1]);
        }
    }

    private static void fileMethod(String input, String output) {
        File inputFile = new File(input);
        File outputFile = new File(output);
        try (FileReader fr = new FileReader(inputFile);
             FileWriter fw = new FileWriter(outputFile);
             Scanner scanner = new Scanner(fr)) {
            String expression;
            while (scanner.hasNext()) {
                expression = scanner.nextLine();
                if (expression.startsWith("#") || expression.trim().equals("")) {
                    continue;
                }
                fw.write(String.valueOf(getAnswer(expression)));
                fw.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void consoleMethod() {
        Scanner scanner = new Scanner(System.in);
        String line;
        while (!(line = scanner.nextLine()).equals("exit")) {
            System.out.println(getAnswer(line));
        }
    }

    private static int getAnswer(String line) {
        int result = 0;
        line = line.replaceAll(" ", "");
        List<Integer> operandsList = Arrays.stream(line.split("\\D")).map(Integer::parseInt).collect(Collectors.toList());
        List<String> operatorsList = Arrays.stream(line.split("\\d")).filter(str -> !str.equals("")).collect(Collectors.toList());
        result = calc(operandsList.get(0), operandsList.get(1), operatorsList.get(0));
        for (int i = 2; i < operandsList.size(); i++) {
            result = calc(result, operandsList.get(i), operatorsList.get(i - 1));
        }
        return result;
    }

    private static int calc(int first, int second, String operator) {
        int result = 0;
        switch (operator) {
            case ("+"): result = first + second; break;
            case ("-"): result = first - second; break;
            case ("*"): result = first * second; break;
            case ("/"): result = first / second; break;
        }
        return result;
    }
}
