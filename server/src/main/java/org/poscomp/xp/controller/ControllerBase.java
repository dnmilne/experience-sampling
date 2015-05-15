package org.poscomp.xp.controller;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.bson.types.ObjectId;
import org.poscomp.xp.error.Unauthorized;
import org.poscomp.xp.model.User;
import org.poscomp.xp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import java.nio.charset.Charset;

/**
 * Created by dmilne on 6/11/14.
 */
public class ControllerBase {

    private static Logger logger = LoggerFactory.getLogger(ControllerBase.class) ;

    private static final String USER_ID = "userId" ;

    @Autowired
    protected UserRepository userRepo ;


    public User getCaller(String auth) throws Unauthorized {

        if (auth == null || !auth.startsWith("Basic"))
            throw new Unauthorized("No authorization header") ;

        //logger.info("header: " + auth) ;

        String base64Credentials = auth.substring("Basic".length()).trim();
        String credentials = new String(Base64.decode(base64Credentials), Charset.forName("UTF-8"));

        String[] values = credentials.split(":",2);

        if (values.length != 2)
            throw new Unauthorized("Invalid authorization header") ;

        String screenName = values[0] ;
        String password = values[1] ;

        //logger.info("screenName " + screenName) ;
        //logger.info("password " + password) ;

        User user = userRepo.findByScreenName(screenName) ;

        if (user == null)
            throw new Unauthorized("Invalid screenName") ;

        if (!user.getPassword().equals(password))
            throw new Unauthorized("Invalid password") ;

        return user ;
    }

}
