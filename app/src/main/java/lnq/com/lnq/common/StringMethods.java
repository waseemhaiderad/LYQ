package lnq.com.lnq.common;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StringMethods {

    public static boolean validateKeys(JSONObject response, String key) {
        return response.has(key) && !response.isNull(key) && key != null;
    }

    public static ArrayList<String> getAlphabetsArray() {
        ArrayList<String> alphabetsList = new ArrayList<>();
        alphabetsList.add("A");
        alphabetsList.add("B");
        alphabetsList.add("C");
        alphabetsList.add("D");
        alphabetsList.add("E");
        alphabetsList.add("F");
        alphabetsList.add("G");
        alphabetsList.add("H");
        alphabetsList.add("I");
        alphabetsList.add("J");
        alphabetsList.add("K");
        alphabetsList.add("L");
        alphabetsList.add("M");
        alphabetsList.add("N");
        alphabetsList.add("O");
        alphabetsList.add("P");
        alphabetsList.add("Q");
        alphabetsList.add("R");
        alphabetsList.add("S");
        alphabetsList.add("T");
        alphabetsList.add("U");
        alphabetsList.add("V");
        alphabetsList.add("W");
        alphabetsList.add("X");
        alphabetsList.add("Y");
        alphabetsList.add("Z");
        return alphabetsList;
    }

}
