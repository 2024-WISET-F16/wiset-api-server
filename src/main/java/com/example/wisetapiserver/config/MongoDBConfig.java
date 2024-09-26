package com.example.wisetapiserver.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoDBConfig {

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Value("${spring.data.mongodb.database}")
    private String illuminanceDB;

    @Bean
    public MongoClient mongoClient(){
        return MongoClients.create(uri);
    }

    // illuminance db
    @Bean("illuminanceTemplate")
    @Primary
    public MongoTemplate illuminanceTemplate(MappingMongoConverter converter){
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoClient(), illuminanceDB), converter);
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoMappingContext context, MongoCustomConversions conversions) {
        MappingMongoConverter converter = new MappingMongoConverter(new SimpleMongoClientDatabaseFactory(mongoClient(), illuminanceDB), context);
        converter.setCustomConversions(conversions);
        // _class 필드 비활성화
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }
}
