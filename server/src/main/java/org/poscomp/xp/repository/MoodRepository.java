package org.poscomp.xp.repository;

import org.poscomp.xp.model.IndexedMood;
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

    public static final int MIN_NONCANONICAL_SAMPLES = 5 ;

    @Autowired
    private MongoTemplate m;

    public IndexedMood findOne(String feeling) {

        return m.findById(IndexedMood.normalize(feeling), IndexedMood.class) ;
    }

    public List<IndexedMood> findNear(double valence, double arousal) {

        Query query = new Query() ;

        query.addCriteria(Criteria.where("averageValenceAndArousal").near(new Point(valence, arousal))) ;
        query.addCriteria(
                new Criteria().orOperator(Criteria.where("canonical").is(true), Criteria.where("totalSamples").gte(MIN_NONCANONICAL_SAMPLES))
        ) ;

        query.limit(10) ;

        return m.find(query, IndexedMood.class) ;
    }

    public IndexedMood save(IndexedMood mood) {

        m.save(mood) ;
        return mood ;
    }



    public long size() {
        return m.count(new Query(), IndexedMood.class) ;
    }


    public List<IndexedMood> findAll() {

        Query query = new Query() ;
        query.addCriteria(
                new Criteria().orOperator(Criteria.where("totalSamples").gte(MIN_NONCANONICAL_SAMPLES), Criteria.where("canonical").is(true))
        ) ;

        return m.find(query, IndexedMood.class) ;
    }



    public void handleMoodModified(Mood newMood, Mood oldMood) {

        if (newMood == null && oldMood == null)
            return ;

        String name ;
        if (newMood!= null)
            name = newMood.getName() ;
        else
            name = oldMood.getName() ;


        IndexedMood mood = findOne(name) ;

        if (mood == null) {

            if (newMood == null)
                return ;

            mood = new IndexedMood(name, false, newMood.getValence(), newMood.getArousal()) ;
        } else {

            if (newMood != null)
                mood.addSample(newMood);

            if (oldMood != null)
                mood.removeSample(oldMood);
        }

        save(mood) ;
    }
}
