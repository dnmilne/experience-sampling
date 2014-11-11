package org.poscomp.xp.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.bson.types.ObjectId;
import org.poscomp.xp.error.Forbidden;
import org.poscomp.xp.error.NotFound;
import org.poscomp.xp.error.Unauthorized;
import org.poscomp.xp.model.Experience;
import org.poscomp.xp.model.IndexedMood;
import org.poscomp.xp.model.Mood;
import org.poscomp.xp.model.User;
import org.poscomp.xp.repository.ExperienceRepository;
import org.poscomp.xp.repository.MoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by dmilne on 6/11/14.
 */
@Controller
@Api(value = "experiences", position = 2)
public class ExperienceController extends ControllerBase {

    @Autowired
    private ExperienceRepository experienceRepo ;

    @Autowired
    private MoodRepository moodRepo ;

    @ApiOperation(
            value = "Lists experiences logged by the authenticated caller",
            notes = "Returns a list of experiences that have been logged by the caller, most recent experiences first.\n\n" +
                    "This will only return at most " + ExperienceRepository.PAGE_SIZE + " experiences" +
                    ", so use **before** to get more if necessary. You can also filter by one or more **tags**. "
    )
    @RequestMapping(value="/experiences", method= RequestMethod.GET)
    public @ResponseBody Collection<Experience> getExperiences(

            @ApiParam(value = "An optional filter to page through earlier experiences")
            @RequestParam(required = false)
            Date before,

            @ApiParam(value = "An optional filter to restrict experiences to those with all of the given tags")
            @RequestParam(required = false)
            String tags[],

            @RequestHeader(value="Authorization")
            String auth

    ) throws Unauthorized {

        User caller = getCaller(auth) ;

        return experienceRepo.find(caller.getId(), before, tags) ;

    }

    @ApiOperation(
            value = "Returns a single experience",
            notes = "Returns a single experience, identified by the given id."
    )
    @RequestMapping(value="/experiences/{experienceId}", method= RequestMethod.GET)
    public @ResponseBody Experience getExperience(

            @ApiParam(value = "The id of the experience", required = true)
            @PathVariable
            ObjectId experienceId,

            @RequestHeader(value="Authorization")
            String auth

    ) throws Unauthorized, Forbidden, NotFound {

        User caller = getCaller(auth) ;

        Experience experience = experienceRepo.findById(experienceId) ;

        if (experience == null)
            throw new NotFound("Experience does not exist") ;

        if (!experience.getUserId().equals(caller.getId()))
            throw new Forbidden("You may only look at your own experiences");

        return experience ;
    }


    @ApiOperation(
            value = "Either creates or edits an experience",
            notes = "Either creates a new experience or edits an existing one\n\n" +
                    "You should only specify an id when modifying an existing experience. Ids for new experiences will be defined automatically."
    )
    @RequestMapping(value="/experiences", method= RequestMethod.POST)
    public @ResponseBody Experience postExperience(

            @ApiParam(value = "A json object representing the created or edited experience", required = true)
            @RequestBody
            Experience experience,

            @RequestHeader(value="Authorization")
            String auth

    ) throws Unauthorized, Forbidden, NotFound {

        User caller = getCaller(auth) ;

        if (experience.getId() == null) {
            //this is a new experience

            Experience newExperience = new Experience(caller, experience) ;

            moodRepo.handleMoodModified(newExperience.getMoodBefore(), null) ;
            moodRepo.handleMoodModified(newExperience.getMoodAfter(), null);

            experienceRepo.save(newExperience) ;

            return newExperience ;

        } else {

            //this is editing an existing experience

            Experience existingExperience = experienceRepo.findById(experience.getId()) ;

            if (existingExperience == null)
                throw new NotFound("Experience does not exist. Do not specify ids for new experiences") ;

            if (!existingExperience.getUserId().equals(caller.getId()))
                throw new Forbidden("You may only edit your own experiences");

            existingExperience.update(experience) ;

            moodRepo.handleMoodModified(experience.getMoodBefore(), existingExperience.getMoodBefore()) ;
            moodRepo.handleMoodModified(experience.getMoodAfter(), existingExperience.getMoodAfter());

            experienceRepo.save(existingExperience) ;

            return existingExperience ;
        }

    }








}
