package com.example.kafkams.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.listener.config.TopicPartitionInitialOffset;
import org.springframework.kafka.listener.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.listener.MessageListener;
import org.springframework.kafka.listener.listener.config.ContainerProperties;
import org.springframework.kafka.listener.listener.config.TopicPartitionInitialOffset;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KafkaConsumer {

    private final String bootstrapServers = "localhost:9092"; // Update with your Kafka server address
    private final String groupId = "your-group-id"; // Update with your consumer group ID

    @KafkaListener(topics = "your-topic-name", groupId = groupId)
    public void listen(ConsumerRecord<String, String> record) {
        System.out.println("Received Message: " + record.value());
        // Add your message processing logic here
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    public ConcurrentMessageListenerContainer<String, String> kafkaListenerContainerFactory() {
        ContainerProperties containerProps = new ContainerProperties("your-topic-name");
        containerProps.setMessageListener(new MessageListener<String, String>() {
            @Override
            public void onMessage(ConsumerRecord<String, String> record) {
                listen(record);
            }
        });
        return new ConcurrentMessageListenerContainer<>(consumerFactory(), containerProps);
    }
}