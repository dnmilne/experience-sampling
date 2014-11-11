package org.poscomp.xp.repository;

import org.bson.types.ObjectId;
import org.poscomp.xp.model.Experience;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by dmilne on 6/11/14.
 */
@Repository
public class ExperienceRepository {

    public static final int PAGE_SIZE = 20 ;

    @Autowired
    private MongoTemplate m;

    public Experience findById(ObjectId id) {

        return m.findById(id, Experience.class) ;
    }

    public List<Experience> find(ObjectId userId, Date before, String... tags) {

        Query query = new Query(Criteria.where("userId").is(userId)) ;

        if (before != null)
            query.addCriteria(Criteria.where("date").lt(before)) ;

        if (tags != null && tags.length > 0)
            query.addCriteria(Criteria.where("tags").all(tags)) ;

        query.addCriteria(Criteria.where("deleted").is(false)) ;

        query.with(new Sort(Sort.Direction.DESC, "date")) ;
        query.limit(PAGE_SIZE) ;

        return m.find(query, Experience.class) ;
    }

    public Experience save(Experience experience) {

        m.save(experience) ;
        return experience ;
    }



}
