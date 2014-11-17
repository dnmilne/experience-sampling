package org.poscomp.xp.repository;

import org.bson.types.ObjectId;
import org.poscomp.xp.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by dmilne on 6/11/14.
 */
@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

    User findByEmail(String email) ;

    User findByScreenName(String screenName) ;
}