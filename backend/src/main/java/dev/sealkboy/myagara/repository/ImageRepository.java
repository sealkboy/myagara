package dev.sealkboy.myagara.repository;
import dev.sealkboy.myagara.model.Image;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {
}