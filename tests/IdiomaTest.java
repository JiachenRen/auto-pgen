package tests;

import components.Idioma;
import net.sf.extjwnl.data.POS;

import java.util.ArrayList;
import java.util.Collections;

import static components.ColoredPrinters.*;

/**
 * Created by Jiachen on 2/26/18.
 * Test for components.Idioma class.
 */
public class IdiomaTest {
    public static void main(String args[]) {
        String word = "key";

        test("getPos(String word): ", word);
        boldGreen.println(Idioma.getPos(word));

        test("lookup(POS pos, String word): ", "verb", word);
        boldGreen.println(Idioma.lookup(POS.VERB, word));


        test("isExclusively(POS pos, String word): ", "verb", word);
        boldGreen.println(Idioma.isExclusively(POS.VERB, word));
    }

    private static void test(String method, String... args) {
        ArrayList<String> argsArr = new ArrayList<>();
        Collections.addAll(argsArr, args);
        argsArr.forEach(arg -> boldRed.print("<" + arg + "> "));
        boldBlack.print(method);
    }
}
