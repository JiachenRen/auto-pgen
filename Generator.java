import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.*;
import net.sf.extjwnl.dictionary.Dictionary;
import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

import java.util.ArrayList;


/**
 * Created by Jiachen on 2/24/18.
 * Generate a paragraph from the resources collected over the internet.
 */
public class Generator {
    private ArrayList<Item> items;
    private Dictionary dict;
    private static Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static Realiser realiser = new Realiser(lexicon);

    Generator(ArrayList<Item> items) {
        this.items = items;
        try {
            this.dict = Dictionary.getDefaultResourceInstance();
            this.getSynonyms(POS.ADJECTIVE, "thoughtful").forEach(System.out::println);
            System.out.println(getVerbTense("theft"));
        } catch (JWNLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getSynonyms(POS pos, String word) throws JWNLException {
        ArrayList<String> synonyms = new ArrayList<>();
        IndexWord indexWord = dict.lookupIndexWord(pos, word);
        if (indexWord == null) return synonyms;
        String inf = this.infinitiveFormOf(pos, word);
        for (long i : indexWord.getSynsetOffsets()) {
            Synset set = dict.getSynsetAt(pos, i);
            for (Word word1 : set.getWords()) {
                String lemma = word1.getLemma();
                if (lemma.equals(inf))
                    continue;
                if (!synonyms.contains(lemma) && !lemma.contains(inf)) {
                    synonyms.add(lemma);
                }
            }
        }
        return synonyms;
    }

    private VerbTense getVerbTense(String verb) {
        try {
            verb = verb.toLowerCase();
            IndexWord indexWord = dict.lookupIndexWord(POS.VERB, verb);
            if (indexWord == null) return null;
            if (indexWord.getLemma().equals(verb)) return VerbTense.INFINITIVE;
            WordElement word = lexicon.getWord(indexWord.getLemma(), LexicalCategory.VERB);
            if (this.conjugate(word, Tense.PRESENT).equals(verb)) {
                return VerbTense.THIRD_PERSON_SINGULAR;
            } else if (conjugate(word, Tense.PAST).equals(verb)) {
                return VerbTense.PAST;
            }
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String conjugate(WordElement word, Tense tense) {
        InflectedWordElement inflected = new InflectedWordElement(word);
        inflected.setFeature(Feature.TENSE, tense);
        return realiser.realise(inflected).getRealisation();
    }

    private String infinitiveFormOf(POS pos, String word) {
        try {
            IndexWord inf = dict.lookupIndexWord(pos, word);
            if (inf != null) {
                return inf.getLemma();
            }
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return "";
    }
}
