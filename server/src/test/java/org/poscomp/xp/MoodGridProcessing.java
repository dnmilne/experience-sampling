package org.poscomp.xp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;
import org.poscomp.xp.model.Mood;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmilne on 13/11/14.
 */
public class MoodGridProcessing {


    @Test
    public void processMoodGrid() throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("moodGrid.csv").getFile()) ;
        BufferedReader reader = new BufferedReader(new FileReader(file)) ;


        List<Mood> moods = new ArrayList<Mood>() ;

        String line ;
        double arousal = 0.9 ;

        while ((line = reader.readLine()) != null) {

            line = line.trim() ;
            if (line.length() == 0)
                break ;

            double valence = -0.9 ;

            for (String moodName:line.split(",")) {

                moodName = moodName.trim() ;
                if (moodName.length() == 0)
                    continue ;

                moods.add(new Mood(moodName, valence, arousal)) ;

                valence = valence + 0.2 ;
            }

            arousal = arousal - 0.2 ;
        }

        ObjectMapper mapper = new ObjectMapper() ;
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        System.out.println(mapper.writeValueAsString(moods)) ;
    }
}
