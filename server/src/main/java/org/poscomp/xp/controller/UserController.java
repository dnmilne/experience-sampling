package org.poscomp.xp.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.poscomp.xp.error.NotFound;
import org.poscomp.xp.error.Unauthorized;
import org.poscomp.xp.model.User;
import org.poscomp.xp.model.Views;
import org.poscomp.xp.util.RandomKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by dmilne on 6/11/14.
 */
@Controller
@Api(value = "users", position = 1)
public class UserController extends ControllerBase {

    private static Logger logger = LoggerFactory.getLogger(UserController.class) ;

    @ApiOperation(
            value = "Returns the caller",
            notes = "Returns the caller."
    )
    @RequestMapping(value="/users/me", method= RequestMethod.GET)
    public @ResponseBody User getMe(

            @RequestHeader(value="Authorization")
            String auth
    ) throws Unauthorized {

        User caller = getCaller(auth) ;

        return caller ;
    }

    @ApiOperation(
            value = "Either registers a new user, or edits a existing one",
            notes = "Either registers a new user, or edits an existing one\n\n" +
                    "* To register a new user, simply specify the user's details without an id (which will be automatically assigned). No authentication is required to do this.\n" +
                    "* To edit an existing user, specify the new details and the user's id. You must authenticate as the user you are attempting to edit."
    )
    @RequestMapping(value="/users", method= RequestMethod.POST)
    public @ResponseBody Views.Me postUser(

            @ApiParam(value = "A json object representing the new or edited user", required = true)
            @RequestBody Views.Me me,

            @RequestHeader(value="Authorization", required=false)
            String auth
    ) throws Unauthorized, NotFound {


        if (me.getId() == null) {

            //this is a new user registration

            if (!isEmailUnused(me.getEmail()))
                throw new Unauthorized("An account with that email already exists") ;

            if (!isScreenNameUnused(me.getScreenName()))
                throw new Unauthorized("An account with that screen name already exists") ;

            User newUser = new User(me) ;

            userRepo.save(newUser) ;


            return new Views.Me(newUser).redactPassword();

        } else {

            User existingUser = getCaller(auth) ;

            if (existingUser == null)
                throw new NotFound("That user does not exist (do not specify ids for new users)") ;

            if (!existingUser.getEmail().equals(me.getEmail()))
                if (!isEmailUnused(me.getEmail()))
                    throw new Unauthorized("An account with that email already exists") ;

            if (!existingUser.getScreenName().equals(me.getScreenName()))
                if (!isScreenNameUnused(me.getScreenName()))
                    throw new Unauthorized("An account with that screen name already exists") ;

            existingUser.update(me);

            userRepo.save(existingUser) ;

            return new Views.Me(existingUser).redactPassword() ;
        }

    }

    public boolean isEmailUnused(String email) {

        User u = userRepo.findByEmail(email) ;

        return u == null ;
    }

    public boolean isScreenNameUnused(String screenName) {

        User u = userRepo.findByScreenName(screenName) ;

        return u == null ;
    }

}
