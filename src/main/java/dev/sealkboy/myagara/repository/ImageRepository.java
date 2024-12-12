package dev.sealkboy.myagara.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.sealkboy.myagara.model.Image;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {

}
