package org.poscomp.xp.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poscomp.xp.model.IndexedMood;
import org.poscomp.xp.model.Mood;
import org.poscomp.xp.repository.MoodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Created by dmilne on 7/11/14.
 */
@Service
public class MoodInitializer implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(MoodInitializer.class) ;

    @Autowired
    MoodRepository moodRepo ;


    @Override
    public void afterPropertiesSet() throws Exception {

        if (moodRepo.size() > 0)
            return ;

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("moods.json").getFile()) ;

        ObjectMapper mapper = new ObjectMapper() ;

        List<Mood> moods = mapper.readValue(file, new TypeReference<List<Mood>>(){}) ;

        for (Mood mood:moods) {
            logger.info("Saving canonical mood " + mood) ;
            IndexedMood iMood = new IndexedMood(mood.getName(), true, mood.getValence(), mood.getArousal()) ;

            moodRepo.save(iMood) ;
        }

    }
}
