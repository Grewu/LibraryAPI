package com.modsen.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class KafkaTopicConfig {
  public static final String TOPIC_BOOK_CREATED = "book-created";
  public static final String TOPIC_BOOK_DELETED = "book-deleted";
  public static final String BOOK_TRACKER_GROUP_ID = "book-tracker-group";

}
