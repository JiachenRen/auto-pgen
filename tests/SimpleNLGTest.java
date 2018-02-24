package tests;

import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

/**
 * Created by Jiachen on 2/24/18.
 * Test the usability of SimpleNLC library.
 */
public class SimpleNLGTest {
    public static void main(String args[]) {
        Lexicon lexicon = Lexicon.getDefaultLexicon();
        NLGFactory nlgFactory = new NLGFactory(lexicon);
        Realiser realiser = new Realiser(lexicon);
    }
}
