import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Jiachen on 2/23/18.
 * Extracts search results from google.
 */
class Extractor {
    private static String[] API_KEYS = read("./resources/api_keys.txt").split("\n");
    private static String ENCODING = "UTF-8";
    private static boolean debug = false;

    static String search(String keyword) {
        try {
            String cached = cache(keyword);
            if (!cached.equals("")) return cached;

            URL url = new URL("https://www.googleapis.com/customsearch/v1?key=" + API_KEYS[0] + "&cx=013036536707430787589:_pqjad5hr1a&q=" + URLEncoder.encode(keyword, ENCODING) + "&alt=json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output, acc = "";
            if (debug) System.out.println("Response successfully retrieved from net...");
            while ((output = br.readLine()) != null) {
                acc += output + "\n";
            }
            cache(keyword, acc);
            conn.disconnect();
            return acc;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String cache(String keyword) {
        try {
            String encoded = URLEncoder.encode(keyword, ENCODING);
            File folder = new File("./resources/cache");
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles == null || listOfFiles.length == 0) return "";
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().equals(encoded + ".txt")) {
                    if (debug) System.out.println("retrieving from cache...");
                    return read(file.getPath());
                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static void cache(String keyword, String responseData) {
        try {
            PrintWriter writer;
            String encoded = URLEncoder.encode(keyword, ENCODING);
            writer = new PrintWriter("./resources/cache/" + encoded + ".txt", "UTF-8");
            writer.println(responseData);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    static String read(String filePath) {
        String acc = "";
        FileReader fileReader = null;
        try {

            fileReader = new FileReader(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (fileReader == null) return "";
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                acc += line + "\n";
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return acc;
    }
}
