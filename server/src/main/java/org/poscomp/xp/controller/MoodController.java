package org.poscomp.xp.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.poscomp.xp.error.Unauthorized;
import org.poscomp.xp.model.IndexedMood;
import org.poscomp.xp.model.User;
import org.poscomp.xp.repository.MoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;

/**
 * Created by dmilne on 6/11/14.
 */
@Controller
@Api(value = "moods", position = 3)
public class MoodController extends ControllerBase {

    @Autowired
    MoodRepository moodRepo ;

    @ApiOperation(
            value = "Lists moods that have been logged by all users (not just the caller) across their experiences",
            notes = "Lists moods that have been logged. This includes canonical moods (from existing mood lists) and ones logged by " +
                    "users within their experiences (although the latter will only show up once they have been logged at least " + MoodRepository.MIN_NONCANONICAL_SAMPLES + " times.\n\n" +
                    "The returned moods can be filtered to only those **near** a 2d (valence and arousal) coordinate"
    )
    @RequestMapping(value="/moods", method= RequestMethod.GET)
    public @ResponseBody
    Collection<IndexedMood> getMoods(

            @ApiParam(value = "An optional filter to restrict returned moods to only those located near the given 2d (valence and arousal) coordinate")
            @RequestParam(required=false)
            double[] near,

            @RequestHeader(value="Authorization")
            String auth

    ) throws Unauthorized {

        User caller = getCaller(auth) ;

        if (near == null || near.length != 2)
            return moodRepo.findAll() ;

        return moodRepo.findNear(near[0], near[1]) ;
    }

    @ApiOperation(
            value = "Returns a single mood",
            notes = "Returns a single mood, as identified by the given name."
    )
    @RequestMapping(value="/moods/{name}", method= RequestMethod.GET)
    public @ResponseBody IndexedMood getMood(

            @ApiParam(value = "The name of the mood to retrieve")
            @PathVariable
            String name,

            @RequestHeader(value="Authorization")
            String auth

    ) throws Unauthorized {

        User caller = getCaller(auth) ;

        return moodRepo.findOne(name) ;
    }

}
