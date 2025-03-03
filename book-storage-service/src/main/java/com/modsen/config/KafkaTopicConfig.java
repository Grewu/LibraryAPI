package com.modsen.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

  public static final String TOPIC_BOOK_CREATED = "book-created";
  public static final String TOPIC_BOOK_DELETED = "book-deleted";
  public static final int NUM_PARTITIONS = 1;
  public static final short REPLICATION_FACTOR = (short) 1;

  @Bean
  public NewTopic bookCreatedTopic() {
    return new NewTopic(TOPIC_BOOK_CREATED, NUM_PARTITIONS, REPLICATION_FACTOR);
  }

  @Bean
  public NewTopic bookDeleted() {
    return new NewTopic(TOPIC_BOOK_DELETED, NUM_PARTITIONS, REPLICATION_FACTOR);
  }
}
