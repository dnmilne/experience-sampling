package org.poscomp.xp.config;


import org.poscomp.xp.error.BadRequest;
import org.poscomp.xp.error.Forbidden;
import org.poscomp.xp.error.NotFound;
import org.poscomp.xp.error.Unauthorized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by dmilne on 10/06/2014.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadRequest.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody BadRequest handle(BadRequest error){

        logger.warn("Bad request", error) ;
        return error ;
    }

    @ExceptionHandler(Unauthorized.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public @ResponseBody Unauthorized handle(Unauthorized error){
        logger.warn("Unauthorized", error) ;
        return error ;
    }

    @ExceptionHandler(Forbidden.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public @ResponseBody Forbidden handle(Forbidden error){
        logger.warn("Forbidden", error) ;
        return error ;
    }

    @ExceptionHandler(NotFound.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public @ResponseBody NotFound handle(NotFound error){
        logger.warn("Not found", error) ;
        return error ;
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody Exception handle(Exception error){

        logger.warn("Internal server error", error) ;
        return error ;
    }
}
