import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.*;
import net.sf.extjwnl.dictionary.Dictionary;
import net.sf.extjwnl.dictionary.MorphologicalProcessor;
import net.sf.extjwnl.dictionary.morph.BaseFormSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jiachen on 2/24/18.
 * Generate a paragraph from the resources collected over the internet.
 */
public class Generator {
    private ArrayList<Item> items;
    private Dictionary dict;

    Generator(ArrayList<Item> items) {
        this.items = items;
        try {
            this.dict = Dictionary.getDefaultResourceInstance();
            this.getSynonyms(POS.VERB, "invented").forEach(System.out::println);
        } catch (JWNLException e) {
            e.printStackTrace();
        }

    }

    private ArrayList<String> getSynonyms(POS pos, String word) throws JWNLException {
        ArrayList<String> synonyms = new ArrayList<>();
        IndexWord indexWord = dict.lookupIndexWord(pos, word);
        System.out.println(indexWord.getSenses());
//        for (long i : indexWord.getSynsetOffsets()) {
//            Synset set = dict.getSynsetAt(pos, i);
////            System.out.println(set.getWords());
//            for (Word word1 : set.getWords()) {
//                System.out.println(word1.getSenseKey());
//                synonyms.add(word1.getLemma());
//            }
//        }

        return synonyms;
    }
}
