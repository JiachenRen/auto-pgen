import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Jiachen on 2/25/18.
 * Commandline util for doing homework.
 */
public class CmdLine {
    private static Scanner scan = new Scanner(System.in);
    private static int minSentences = 2;
    private static int maxSentences = 7;
    private static boolean swapVerbs = true;
    private static boolean allowNounAsVerbs;
    private static boolean ignoreInfinitive;
    private static double swapRatio = 0.6;
    private static ColoredPrinter custom = new ColoredPrinter.Builder(1, false)
            .attribute(Ansi.Attribute.BOLD)
            .foreground(Ansi.FColor.BLACK)
            .build();
    private static ColoredPrinter prompt = new ColoredPrinter.Builder(1, false)
            .foreground(Ansi.FColor.GREEN)
            .attribute(Ansi.Attribute.BOLD)
            .build();
    private static ColoredPrinter par = new ColoredPrinter.Builder(1, false)
            .foreground(Ansi.FColor.CYAN)
            .build();
    private static ColoredPrinter success = new ColoredPrinter.Builder(1, false)
            .foreground(Ansi.FColor.MAGENTA)
            .build();
    private static ColoredPrinter progress = new ColoredPrinter.Builder(1, false)
            .foreground(Ansi.FColor.YELLOW)
            .attribute(Ansi.Attribute.BOLD)
            .build();
    private static ColoredPrinter err = new ColoredPrinter.Builder(1, false)
            .foreground(Ansi.FColor.RED)
            .attribute(Ansi.Attribute.BOLD)
            .build();
    private static ColoredPrinter clr = new ColoredPrinter.Builder(1, false)
            .build();
    private static Generator gen;

    public static void main(String args[]) throws IOException, InterruptedException {
        clr.clear();
        prompt.println("Welcome to Auto Paragraph Generator by Jiachen Ren");
        par.println("Do you wish to customize the paragraph generator? [Y/N]");
        if (bool()) customize();
        prompt.println("Initializing automatic paragraph generator...");
        gen = new Generator(swapVerbs, swapRatio);
        gen.setAllowNounAsVerbs(allowNounAsVerbs);
        gen.setIgnoreInfinitive(ignoreInfinitive);
        par.println("Please enter topics (the program will accept any designated delimiters); enter [done] to finish.");
        String topicsRaw = "";
        while (true) {
            String line = scan.nextLine();
            if (line.toLowerCase().equals("done")) {
                break;
            }
            topicsRaw += line + "\n";
        }
        par.println("Subject area of concern (Simply press [ENTER] if you wish to do a generic search)? [US History/English... ect.]");
        String postfix = " " + scan.nextLine();
        par.println("Please enter delimiter: ");
        String delimiter = scan.nextLine();
        par.println("Please enter delimiter between topic and generated paragraph: ");
        String outputDelimiter = scan.nextLine();
        prompt.println("Working...");
        String topics[], output = "";
        if (!delimiter.equals("")) topics = topicsRaw.split(delimiter);
        else topics = new String[]{topicsRaw};
        for (String topic : topics) {
            if (topic.endsWith("\n")) topic = topic.substring(0, topic.length() - 1);
            String rawContent = Extractor.search(topic + (postfix.equals(" ") ? "" : postfix));
            ArrayList<Item> items = Item.extractItemsFrom(rawContent);
            int numSentences = (int) (minSentences + Math.random() * (maxSentences - minSentences + 1));
            String paragraph = gen.generateSimpleParagraph(items, numSentences);
            progress.println(topic + outputDelimiter + "\n");
            success.println(paragraph);
            l("\n");
            output += topic + outputDelimiter + "\n" + paragraph + "\n";
        }
        prompt.println("Writing files...");
        Extractor.write("output", "generated", output);
        success.println("Done.");
        prompt.println("Generated document directory: " + Extractor.pathTo("output").replace(" ", "\\ ") + "generated.txt");
//        String cmd = "ls " + Extractor.pathTo("output").replace(" ", "\\ ") + "generated.txt";
//        System.out.println(cmd);
//        Process process = Runtime.getRuntime().exec(cmd);
//        process.waitFor();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        String line;
//        while ((line = reader.readLine()) != null) {
//            System.out.println(line);
//        }
    }


    private static void customize() {
        custom.println("Max number of sentences [1 - 20]: ");
        maxSentences = num(1, 20);
        custom.println("Min number of sentences [1 - 20]: ");
        minSentences = num(1, 20);
        custom.println("Do you wish to swap out some of the verbs? [Y/N]");
        if (bool()) {
            swapVerbs = true;
            custom.println("Do you wish to allow the generator to swap out verbs that are potentially nouns? [Y/N]");
            allowNounAsVerbs = bool();
            custom.println("Do you wish to ignore all verbs in infinitive form when swapping? [Y/N]");
            ignoreInfinitive = bool();
            custom.println("Ratio of verbs to be swapped out [0.0 - 1.0]: ");
            swapRatio = decimal(0, 1);
        }
    }

    private static boolean bool() {
        clr.print("");
        while (true) {
            String response = scan.nextLine().toLowerCase();
            switch (response) {
                case "y":
                    return true;
                case "yes":
                    return true;
                case "n":
                    return false;
                case "no":
                    return false;
            }
            err.println("Please enter \"y\" for yes, \"no\" for no.");
        }
    }

    private static void l(String s) {
        System.out.println(s);
    }

    private static int num(int min, int max) {
        clr.print("");
        while (true) {
            String response = scan.nextLine().toLowerCase();
            try {
                int num = Integer.valueOf(response);
                if (num <= max && num >= min) return num;
                else err.println("Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                err.println("Please enter a valid number.");
            }
        }
    }

    private static double decimal(double min, double max) {
        clr.print("");
        while (true) {
            String response = scan.nextLine().toLowerCase();
            try {
                double num = Double.valueOf(response);
                if (num <= max && num >= min) return num;
                else err.println("Please enter a decimal number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                err.println("Please enter a valid decimal number.");
            }
        }
    }

}
