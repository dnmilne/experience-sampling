package org.poscomp.xp.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * A prompt for someone to log an experience
 */
@Document
public class Prompt {

    @Id
    private ObjectId id ;

    private Date date ;

    private ObjectId experience ;



}
