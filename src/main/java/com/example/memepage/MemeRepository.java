package com.example.memepage;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemeRepository extends MongoRepository<Meme, ObjectId> {
    Optional<Meme> findById(ObjectId id);
}
