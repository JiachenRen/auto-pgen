package tests;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.dictionary.Dictionary;
import static components.ColoredPrinters.*;

/**
 * Created by Jiachen on 2/26/18.
 * Test class for extjwnl-1.9.4 library.
 */
public class WordNetTest {
    private Dictionary dict = Dictionary.getDefaultResourceInstance();

    private WordNetTest() throws JWNLException {
        IndexWord word = dict.lookupIndexWord(POS.ADJECTIVE, "covered");
        boldRed.println(word);
    }

    public static void main(String args[]) throws Exception {
        new WordNetTest();
    }
}
