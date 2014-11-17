package org.poscomp.xp.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poscomp.xp.config.WebConfig;
import org.poscomp.xp.model.Experience;
import org.poscomp.xp.model.Mood;
import org.poscomp.xp.model.User;
import org.poscomp.xp.model.Views;
import org.poscomp.xp.repository.ExperienceRepository;
import org.poscomp.xp.repository.MoodRepository;
import org.poscomp.xp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Created by dmilne on 11/11/14.
 */
@Service
public class DatabasePopulator implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(MoodInitializer.class) ;

    @Autowired
    ExperienceRepository experienceRepo ;

    @Autowired
    UserRepository userRepo ;

    @Autowired
    MoodRepository moodRepo ;

    //this is just to ensure database populator is initialized after mood initializer
    @Autowired
    MoodInitializer moodInitializer ;


    @Override
    public void afterPropertiesSet() throws Exception {

        User jim = userRepo.findByEmail("jim@jim.com") ;

        if (jim != null)
            return ;

        jim = new User("jim@jim.com","jim","jim") ;
        userRepo.save(jim) ;

        ObjectMapper mapper = new ObjectMapper() ;
        mapper.setDateFormat(WebConfig.dateFormat) ;

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("jims-experiences.json").getFile()) ;

        List<Views.Experience> experiences = mapper.readValue(file, new TypeReference<List<Views.Experience>>(){}) ;

        for (Views.Experience experience:experiences) {
            Experience e = new Experience(jim, experience) ;
            experienceRepo.save(e) ;

        }


    }
}
