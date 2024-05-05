package Orakas.Filters;

import Orakas.Database.Database;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/*
Singleton class used to access score values used in filters.
 */
public class Scores {
    private static Scores scores;
    public  static  Scores getScores(){
        if(scores==null){
            scores= Database.loadScoreValues("default");
        }
        return scores;
    }

    private HashMap<String, Integer> scoresMap;
    @JsonCreator
    public Scores(HashMap<String, Integer> input){
        setScoresMap(input);
    }
    @JsonIgnore
    public Scores(){
        setScoresMap(new HashMap<>());
    }

    public HashMap<String, Integer> getScoresMap() {
        return scoresMap;
    }

    public void setScoresMap(HashMap<String, Integer> scoresMap) {
        this.scoresMap = scoresMap;
    }
    public void registerScore(String id, int value){
        scoresMap.put(id,value);
    }

    public void defValues(){
        ArrayList<String> titles = new ArrayList<>(Arrays.asList("neutral","positive","negative","emergency","HppHour"));
        ArrayList<Integer> values = new ArrayList<>(Arrays.asList(2,4,1,-9999999,1));
        for(String s : titles){
            registerScore(s,values.get(titles.indexOf(s)));
        }
    }
}
