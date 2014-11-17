package org.poscomp.xp.repository;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.poscomp.xp.model.Experience;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
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

    private static Logger logger = LoggerFactory.getLogger(ExperienceRepository.class) ;

    public static final int PAGE_SIZE = 20 ;



    @Autowired
    private MongoTemplate m;

    public Experience findById(ObjectId id) {

        return m.findById(id, Experience.class) ;
    }

    public List<Experience> findModifiedAfter(ObjectId userId, Date modifiedAfter) {

        Query query = new Query(Criteria.where("userId").is(userId)) ;

        query.addCriteria(
                Criteria.where("modifiedAt").gt(modifiedAfter)) ;

        return m.find(query, Experience.class) ;

    }

    public List<Experience> find(ObjectId userId, Date before, Double[] moodBeforeNear, Double[] moodAfterNear, String... tags) {

        Query query = new Query(Criteria.where("userId").is(userId)) ;

        if (before != null)
            query.addCriteria(Criteria.where("date").lt(before)) ;

        if (tags != null && tags.length > 0)
            query.addCriteria(Criteria.where("tags").all(tags)) ;

        logger.info(StringUtils.join(moodBeforeNear)) ;

        if (moodBeforeNear != null && moodBeforeNear.length == 2)
            query.addCriteria(
                    Criteria.where("moodBefore.valenceAndArousal")
                            .near(new Point(moodBeforeNear[0], moodBeforeNear[1]))
                            .maxDistance(MoodRepository.MAX_DIST)
            ) ;

        if (moodAfterNear != null && moodAfterNear.length == 2)
            query.addCriteria(
                    Criteria.where("moodAfter.valenceAndArousal")
                            .near(new Point(moodAfterNear[0], moodAfterNear[1]))
                            .maxDistance(MoodRepository.MAX_DIST)
            ) ;

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
