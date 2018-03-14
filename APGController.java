import components.Extractor;
import components.Generator;
import components.Item;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

import static components.ColoredPrinters.boldGreen;
import static components.ColoredPrinters.boldYellow;
import static components.ColoredPrinters.magenta;

/**
 * Created by Jiachen on 3/14/18.
 * Auto Paragraph Generator Controller
 */
@SuppressWarnings("unused")
public class APGController {

    public TextField delimiter;
    public TextField subjectArea;
    public CheckBox sentenceShuffling;
    public CheckBox synReplacement;
    public CheckBox citeSources;
    public CheckBox crsCtxWordSwapping;
    public CheckBox posSpecSynMapping;
    public TextArea topics;
    public Button generate;
    public TextArea generatedPars;
    public ScrollBar minNumSentences;
    public ScrollBar maxNumSentences;

    private Generator generator;

    void initListeners(Generator generator) {
        this.generator = generator;
        l("Initializing event listeners... ");
        sentenceShuffling.selectedProperty().addListener((observable, oldValue, newValue) -> {
            generator.setShuffleSentences(newValue);
        });
        synReplacement.selectedProperty().addListener((observable, oldValue, newValue) -> {
            generator.setSwapWords(newValue);
        });
        citeSources.selectedProperty().addListener((observable, oldValue, newValue) -> {
            generator.setIncludeSources(newValue);
        });
        crsCtxWordSwapping.selectedProperty().addListener((observable, oldValue, newValue) -> {
            generator.setCrossContextWordSwapping(newValue);
        });
        posSpecSynMapping.selectedProperty().addListener((observable, oldValue, newValue) -> {
            generator.setPosSpecificSynMapping(newValue);
        });
    }

    public void generateButtonPressed(MouseEvent mouseEvent) {
        String topics[], output = "";
        String delimiter = this.delimiter.getText();
        String topicsRaw = this.topics.getText();
        if (!delimiter.equals("")) topics = topicsRaw.split(delimiter);
        else topics = new String[]{topicsRaw};
        String subjectArea = this.subjectArea.getText();
        int minSentences = (int) this.minNumSentences.getValue();
        int maxSentences = (int) this.maxNumSentences.getValue();

        for (String topic : topics) {
            if (topic.endsWith("\n")) topic = topic.substring(0, topic.length() - 1);
            String rawContent = Extractor.search(topic + (subjectArea.equals(" ") ? "" : subjectArea));
            ArrayList<Item> items = Item.extractItemsFrom(rawContent);
            int numSentences = (int) (minSentences + Math.random() * (maxSentences - minSentences + 1));
            String paragraph = generator.generateSimpleParagraph(items, numSentences);
//            boldYellow.println(topic);
//            magenta.println(paragraph);
//            l("\n");
            output += topic + "\n" + paragraph + "\n";
        }
        generatedPars.setText(output);

        boldGreen.println("Writing files...");
        Extractor.write("output", "generated", output);
    }

    private void l(Object... os) {
        for (Object o : os) {
            p(o, "\n");
        }
    }


    private void p(Object... os) {
        for (Object o : os) {
            System.out.print(o);
        }
    }
}
