package org.poscomp.xp;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.poscomp.xp.model.Experience;
import org.poscomp.xp.model.User;
import org.poscomp.xp.repository.ExperienceRepository;
import org.poscomp.xp.repository.MoodRepository;
import org.poscomp.xp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by dmilne on 11/11/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { })
public class ExperienceRetrieval {

    @Autowired
    ExperienceRepository experienceRepo ;

    @Autowired
    UserRepository userRepo ;

    @Test
    public void checkExperienceRetrieval() {



        //ObjectId userId = new ObjectId("546185a277c8a93f86338c21") ;

        User user = userRepo.findByEmail("jim@jim.com") ;

        Double[] before = {0.2,0.5} ;

        System.out.println("retrieving stuff for " + user.getId()) ;

        for (Experience experience:experienceRepo.find(user.getId(), null, before, null, null))
            System.out.println(experience.getDescription()) ;


    }


    @Configuration
    @ComponentScan("org.poscomp.xp")
    public static class Conf {

    }

}
