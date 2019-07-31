package com.dugonski.urlshortener.impl.repository;

import com.dugonski.urlshortener.impl.model.URLEntity;
import org.springframework.data.repository.CrudRepository;

public interface URLRepository extends CrudRepository<URLEntity, String> {

}
