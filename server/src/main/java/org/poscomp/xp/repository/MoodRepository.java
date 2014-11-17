package org.poscomp.xp.repository;

import org.poscomp.xp.model.Mood;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by dmilne on 6/11/14.
 */
@Repository
public class MoodRepository {


    public static final double MAX_DIST = Math.sqrt(0.1) ;

    @Autowired
    private MongoTemplate m;

    public Mood findOne(String feeling) {

        return m.findById(feeling, Mood.class) ;
    }

    public List<Mood> findNear(double[] near) {

        Query query = new Query() ;

        query.addCriteria(Criteria.where("valenceAndArousal").near(new Point(near[0], near[1])).maxDistance(MAX_DIST)) ;

        return m.find(query, Mood.class) ;
    }

    public Mood save(Mood mood) {

        m.save(mood) ;
        return mood ;
    }



    public long size() {
        return m.count(new Query(), Mood.class) ;
    }


    public List<Mood> findAll() {

        return m.findAll(Mood.class) ;
    }
}
