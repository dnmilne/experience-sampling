package org.poscomp.xp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.poscomp.xp.model.IndexedMood;
import org.poscomp.xp.repository.MoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by dmilne on 7/11/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { })
public class MoodRetrieval {

    @Autowired
    MoodRepository moodRepo ;


    @Test
    public void checkMoodRetrieval() {

        for (IndexedMood mood:moodRepo.findNear(-0.248, -0.53499))
            System.out.println(mood.getName()) ;
    }


    @Configuration
    @ComponentScan("org.poscomp.xp")
    public static class Conf {

    }
}
