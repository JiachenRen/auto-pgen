# Auto-Paragraph-Generator #
A program that helps you complete your english/history homework. Just kidding. This program is for RESEARCH purposes ONLY.

**USE AT YOUR OWN RISK (HOPEFULLY FOR RESEARCH PURPOSES), I DO NOT ASSUME ANY RESPONSIBILITIES.**

## How to use ##
1) Download the latest version Auto.Paragraph.Generator.jar from releases tab.
2) Open your terminal on Linux/MacOS/Windows system. 
3) Type in `java -jar [path to .jar file]`, or just drag the `.jar` file into the terminal in place of `[path to .jar file]`
4) Follow the instructions.
5) Access the generated paragraphs in the indicated directory using your favorate text editor.

## Features ##

### Batch processing of term/definition pairs ###
Tired of defining your vocabulary for us history one by one? Try the batch processing feature. Just enter all the concepts/terms/words you want to lookup and designate a delimeter and let the program do all the hard work.

### Filtration of invalid sources ###
Invalid sentences are automatically filtered so they won't appear in your paragraph.
![Obfuscation Demonstration](https://raw.githubusercontent.com/JiachenRen/Auto-Paragraph-Generator/master/documents/screenshots/invalid_sentence_filtration.png)

### Automatic Obfuscation ###
The program utilizes WordNet, SimpleNLG, and CoreNLP to analyze source paragraph and swap out verbs and adjectives with their synonyms. 
![Obfuscation Demonstration](https://raw.githubusercontent.com/JiachenRen/Auto-Paragraph-Generator/master/documents/screenshots/obfuscation.png)

### Automatic generation of citations ###
If you desire, the program could automatically generate footnote & citations.
![Obfuscation Demonstration](https://raw.githubusercontent.com/JiachenRen/Auto-Paragraph-Generator/master/documents/screenshots/source_generation.png)

### Search result caching ###
The program will attempt to construct a cache of queried terms as to produce a faster user experience.

### Detailed documentation of workflow ###
So... if you ever want to make a program like this by yourself, I made my footsteps fairly easy to follow. Everything is transparent, not "magical". The console will tell you exactly what it does to generate your paragraph.
![Obfuscation Demonstration](https://raw.githubusercontent.com/JiachenRen/Auto-Paragraph-Generator/master/documents/screenshots/workflow.png)
