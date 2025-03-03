package com.modsen.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@EnableKafka
@Configuration
public class KafkaTopicConfig {
  public static final String TOPIC_BOOK_CREATED = "book-created";
  public static final String TOPIC_BOOK_DELETED = "book-deleted";
  public static final String BOOK_TRACKER_GROUP_ID = "book-tracker-group";

  @Bean
  public ConsumerFactory<String, String> consumerFactory() {
    Map<String, Object> consumerProps = new HashMap<>();
    consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-service:9092");
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "book-tracker-group");
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    return new DefaultKafkaConsumerFactory<>(consumerProps);
  }
}
