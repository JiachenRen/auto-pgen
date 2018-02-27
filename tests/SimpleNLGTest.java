package tests;

import com.sun.org.apache.bcel.internal.classfile.FieldOrMethod;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.framework.*;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

/**
 * Created by Jiachen on 2/24/18.
 * Test the usability of SimpleNLC library.
 */
public class SimpleNLGTest {
    private static Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static Realiser realiser = new Realiser(lexicon);

    public static void main(String args[]) {
        new SimpleNLGTest();
    }

    private SimpleNLGTest() {
        testCannedSentence();
        conjugate("extrapolate", Tense.PAST);
        toPlural("thesis");
    }

    private void testCannedSentence() {
        NLGElement s1 = nlgFactory.createSentence("My name is Jiachen Ren");
        String output = realiser.realiseSentence(s1);
        System.out.println(output);
    }

    private void conjugate(String verb, Tense tense) {
        WordElement word = lexicon.getWord(verb, LexicalCategory.VERB);
        InflectedWordElement inflected = new InflectedWordElement(word);
        inflected.setFeature(Feature.TENSE, tense);
        String past = realiser.realise(inflected).getRealisation();
        System.out.println(past);
    }

    private void toPlural(String word) {
        WordElement noun = lexicon.getWord(word, LexicalCategory.NOUN);
        InflectedWordElement inflected = new InflectedWordElement(noun);
        inflected.setPlural(true);
        System.out.println(realiser.realise(inflected).getRealisation());
    }
}
