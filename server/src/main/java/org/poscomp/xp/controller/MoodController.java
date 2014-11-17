package org.poscomp.xp.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.poscomp.xp.error.Unauthorized;
import org.poscomp.xp.model.Mood;
import org.poscomp.xp.model.User;
import org.poscomp.xp.model.Views;
import org.poscomp.xp.repository.MoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
            value = "Lists moods that are available",
            notes = "Lists moods that are available to users.  They can be filtered to only those **near** a 2d (valence and arousal) coordinate"
    )
    @RequestMapping(value="/moods", method= RequestMethod.GET)
    public @ResponseBody Collection<Views.Mood> getMoods(

            @ApiParam(value = "An optional filter to restrict returned moods to only those located near the given 2d (valence and arousal) coordinate")
            @RequestParam(required=false)
            double[] near,

            @RequestHeader(value="Authorization")
            String auth

    ) throws Unauthorized {

        User caller = getCaller(auth) ;

        if (near == null || near.length != 2)
            return hydrate(moodRepo.findAll()) ;

        return hydrate(moodRepo.findNear(near)) ;
    }

    @ApiOperation(
            value = "Returns a single mood",
            notes = "Returns a single mood, as identified by the given name."
    )
    @RequestMapping(value="/moods/{name}", method= RequestMethod.GET)
    public @ResponseBody Views.Mood getMood(

            @ApiParam(value = "The name of the mood to retrieve")
            @PathVariable
            String name,

            @RequestHeader(value="Authorization")
            String auth

    ) throws Unauthorized {

        User caller = getCaller(auth) ;

        return new Views.Mood(moodRepo.findOne(name)) ;
    }


    private List<Views.Mood> hydrate(List<Mood> moods) {

        List<Views.Mood> hydrated = new ArrayList<Views.Mood>() ;

        for (Mood mood:moods)
            hydrated.add(new Views.Mood(mood)) ;

        return hydrated ;
    }

}
