package com.example.demo.component.mq.kafka;

import com.example.demo.component.mq.BusinessException;
import com.example.demo.component.mq.MqMsgMO;
import com.example.demo.component.mq.MqMsgProcessors;
import com.example.demo.pubdef.dto.ResultEnum;
import com.example.demo.pubdef.mo.BatchMO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class KafkaConsumerListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MqMsgProcessors mqMsgProcessors;

    @KafkaListener(topics = {KafkaConstant.KA_TOPIC_2}, groupId = "group1", containerFactory = "kafkaListenerContainerFactory")
    public void consumerMessage(ConsumerRecord<String, MqMsgMO> record, final Acknowledgment ack) {
//        logger.info("msg come!");
        process(record, false, ack);
    }

    @KafkaListener(topics = {KafkaConstant.KA_TOPIC_1}, groupId = "group2", containerFactory = KafkaConstant.MULTI_THREAD_FACTORY)
    public void consumerMessageTopic1(List<ConsumerRecord<String, MqMsgMO>> record, final Acknowledgment ack) {
//        logger.info("msg come!");
//        re.getId();
        batchProcess(record, false, ack);
    }

    private void process(ConsumerRecord<String, MqMsgMO> record, boolean ifBroadcast, final Acknowledgment ack) {
        MqMsgMO mqMsgMO = record.value();
        boolean prok = process(mqMsgMO, ifBroadcast);
        try {
            if (prok) {
                log.info("kafka success");
                ack.acknowledge();
            } else {
                log.info("kafka fail");
                ack.nack(1000);
            }
        } catch (Exception e) {
            log.info("kafka process error->{}", e.getMessage());
//            ack.nack(1000);
        }
    }

    private void batchProcess(List<ConsumerRecord<String, MqMsgMO>> records, boolean ifBroadcast, final Acknowledgment ack) {
//        for (ConsumerRecord<String, MqMsgMO> record : records) {
//            boolean ok = process(record.value(), false);
//            ack.nack(Math.toIntExact(record.offset()), 1000);
//        }

        StringBuilder body = new StringBuilder();
        body.append("[");
        for (ConsumerRecord<String, MqMsgMO> record : records) {
            body.append(record.value().getBody());
            body.append(",");
        }
        body.delete(body.length() - 1, body.length());
        body.append("]");
        MqMsgMO mqMsgMO0 = records.get(0).value();
        MqMsgMO mqMsgMO = new MqMsgMO(mqMsgMO0);
        mqMsgMO.setBody(body.toString());

        boolean prok = process(mqMsgMO, false);
        try {
            if (prok) {
                log.info("kafka success");
                ack.acknowledge();
            } else {
                log.info("kafka fail");
                ack.nack(1000);
            }
        } catch (Exception e) {
            log.info("kafka process error->{}", e.getMessage());
//            ack.nack(1000);
        }
    }


    private boolean process(MqMsgMO mqMsgBO, boolean ifBroadcast) {
        // 此时若服务还没准备好, 不消费消息

        boolean processOk = false;
        try {
            processOk = mqMsgProcessors.process(mqMsgBO, ifBroadcast);
        } catch (BusinessException e) {
            // 消费失败的消息已经被持久化存储后面会单独触发再次消费, 所以这里会给mq响应处理成功
            if (e.getResultEnum().equals(ResultEnum.MQ_MSG_CONSUME_FAILED)) {
                processOk = true;
            }
        }
        return processOk;
    }

}
