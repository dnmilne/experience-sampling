package org.poscomp.xp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Test;
import org.poscomp.wefeel4j.Client;
import org.poscomp.wefeel4j.WeFeelException;
import org.poscomp.wefeel4j.model.Emotion;
import org.poscomp.xp.model.Mood;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmilne on 7/11/14.
 */
public class MoodAnalysis {


    private List<String> getMoodNames() throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("moods.txt").getFile()) ;
        BufferedReader reader = new BufferedReader(new FileReader(file)) ;

        String line ;

        List<String> moodNames = new ArrayList<String>() ;

        while ((line = reader.readLine()) != null) {

            String mood = line.trim();

            if (line.length() == 0 || line.startsWith("#"))
                continue;

            moodNames.add(mood);
        }

        return moodNames ;
    }

    private Map<String,Emotion> getEmotions() throws UnirestException, IOException, WeFeelException {

        Map<String,Emotion> emotions = new HashMap<String, Emotion>() ;

        Client client = new Client() ;
        Emotion.Node root = client.getEmotionTree() ;

        for (Emotion.Node child:root.getChildren())
            addEmotion(child, emotions) ;

        return emotions ;
    }

    private Map<String,Emotion> addEmotion(Emotion.Node emotion, Map<String,Emotion> emotions) {

        emotions.put(emotion.getName(), emotion) ;

        if (emotion.getChildren() == null)
            return emotions ;

        for (Emotion.Node child:emotion.getChildren())
            addEmotion(child,emotions) ;

        return emotions ;
    }


    @Test
    public void gatherMoods() throws IOException, WeFeelException, UnirestException {

        List<String> moodNames = getMoodNames() ;



        Map<String,Emotion> emotions = getEmotions() ;

        List<Mood> moods = new ArrayList<Mood>() ;

        int moodsWithoutEmotions = 0 ;
        int moodsWithoutNorms = 0 ;

        for (String moodName:moodNames) {
            //System.out.println(moodName) ;

            Emotion emotion = emotions.get(moodName) ;

            if (emotion == null) {
                moodsWithoutEmotions++ ;
                continue ;
            }

            if (emotion.getNorms() == null) {
                moodsWithoutNorms++ ;
                continue ;
            }

            double valence = (emotion.getNorms().getValence() / 5) -1 ;
            double arousal = (emotion.getNorms().getArousal() /5) -1 ;


            //System.out.println(emotion) ;
            moods.add(new Mood(moodName, valence, arousal)) ;
        }

        System.out.println(moodNames.size() + " mood names") ;
        System.out.println(moodsWithoutEmotions + " without emotions") ;
        System.out.println(moodsWithoutNorms + " without norms") ;
        System.out.println(moods.size() + " fully resolved") ;


        ObjectMapper mapper = new ObjectMapper() ;
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        System.out.println(mapper.writeValueAsString(moods)) ;


    }
}
