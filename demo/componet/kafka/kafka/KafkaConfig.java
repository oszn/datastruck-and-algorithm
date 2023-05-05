package com.example.demo.component.mq.kafka;


import java.util.HashMap;
import java.util.Map;

import com.example.demo.component.mq.MqMsgMO;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;

import com.google.common.collect.Maps;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private Boolean autoCommit;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;
    //
    @Value("${spring.kafka.consumer.max-poll-records}")
    private Integer maxPollRecords;
    //
    @Value("${spring.kafka.producer.linger}")
    private int linger;

    @Value("${spring.kafka.producer.retries}")
    private Integer retries;

    @Value("${spring.kafka.producer.batch-size}")
    private Integer batchSize;

    @Value("${spring.kafka.producer.buffer-memory}")
    private Integer bufferMemory;

    @Value("$(spring.kafka.consumer.properties.spring.json.trusted.packages)")
    private String trust;

    //cankao :https://blog.csdn.net/tmeng521/article/details/90901925
    public Map<String, Object> producerConfigs() {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        //设置重试次数
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        //达到batchSize大小的时候会发送消息
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        //延时时间，延时时间到达之后计算批量发送的大小没达到也发送消息
        props.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        //缓冲区的值
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        //序列化手段
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        //producer端的消息确认机制,-1和all都表示消息不仅要写入本地的leader中还要写入对应的副本中
        props.put(ProducerConfig.ACKS_CONFIG, "-1");//单个brok 推荐使用'1'
        //单条消息的最大值以字节为单位,默认值为1048576
//        props.put(ProducerConfig.LINGER_MS_CONFIG, 10000);
        //设置broker响应时间，如果broker在60秒之内还是没有返回给producer确认消息，则认为发送失败
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 60000);
        //指定拦截器(value为对应的class)
        //props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, "com.te.handler.KafkaProducerInterceptor");
        //设置压缩算法(默认是木有压缩算法的)
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");//snappy
        return props;
    }


    @Bean //创建一个kafka管理类，相当于rabbitMQ的管理类rabbitAdmin,没有此bean无法自定义的使用adminClient创建topic
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> props = new HashMap<>();
        //配置Kafka实例的连接地址
        //kafka的地址，不是zookeeper
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        KafkaAdmin admin = new KafkaAdmin(props);
        return admin;
    }

//    @Bean  //kafka客户端，在spring中创建这个bean之后可以注入并且创建topic,用于集群环境，创建对个副本
//    public AdminClient adminClient() {
//        return AdminClient.create(kafkaAdmin().getConfig());
//    }


    @Bean
    public ProducerFactory<String, MqMsgMO> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean("KafkaTemplate")
    public KafkaTemplate<String, MqMsgMO> KafkaTemplate() {
        return new KafkaTemplate<String, MqMsgMO>(producerFactory());
    }


    @Bean
    public ConsumerFactory<String, MqMsgMO> consumerConfigs() {
        JsonDeserializer<MqMsgMO> deserializer = new JsonDeserializer<>(MqMsgMO.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> props = Maps.newHashMap();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG)
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
//        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 180000);
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 90000);
//        props.put(ConsumerConfig)
//        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 900000);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
//        props.put(JsonDeserializer)
        ConsumerFactory<String, MqMsgMO> ret = new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
        return ret;
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MqMsgMO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MqMsgMO> factory = new ConcurrentKafkaListenerContainerFactory<>();


        factory.setConsumerFactory(consumerConfigs());
        //设置为批量消费，每个批次数量在Kafka配置参数中设置ConsumerConfig.MAX_POLL_RECORDS_CONFIG
        factory.setBatchListener(false);
        // set the retry template
//        factory.setRetryTemplate(retryTemplate());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean(KafkaConstant.MULTI_THREAD_FACTORY)
    public ConcurrentKafkaListenerContainerFactory<String, MqMsgMO> kafkaMulThreadListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MqMsgMO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerConfigs());
        //设置为批量消费，每个批次数量在Kafka配置参数中设置ConsumerConfig.MAX_POLL_RECORDS_CONFIG
        factory.setBatchListener(true);
        factory.setConcurrency(2);
        // set the retry template
//        factory.setRetryTemplate(retryTemplate());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
//    @Bean
//    public KafkaListenerContainerFactory<?> multipleKafka(){
//
//    }


}