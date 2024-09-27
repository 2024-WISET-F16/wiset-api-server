package com.example.wisetapiserver.service.illuminance;

import com.example.wisetapiserver.domain.Illuminance;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IlluminanceService {

    @Qualifier("illuminanceTemplate")
    private final MongoTemplate illuminanceTemplate;

    public Optional<Illuminance> getLatestData() {
        Query query = new Query(); //findFirstByOrderByIdDesc
        query.with(Sort.by(Sort.Direction.DESC, "_id"));  // _id 기준 내림차순 정렬
        return Optional.ofNullable(illuminanceTemplate.findOne(query, Illuminance.class));
    }
}
