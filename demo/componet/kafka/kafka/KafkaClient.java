package com.example.demo.component.mq.kafka;

import com.example.demo.component.mq.BusinessException;
import com.example.demo.component.mq.MqMsgMO;
import com.example.demo.component.mq.MqMsgTypeEnum;
import com.example.demo.component.mq.amqp.AmqpConstant;
import com.example.demo.pubdef.dto.ResultEnum;
import com.example.demo.pubdef.mo.MqMsgBodyMO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Component
public class KafkaClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private KafkaTemplate<String, MqMsgMO> kafkaTemplate;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate<String, MqMsgMO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTaskMsg(String topic, MqMsgMO msgBO) {
        log.info("topic->{},body->{}", topic, msgBO);
        try {
//            kafkaTemplate.convertAndSend(AmqpConstant.TASK_EXCHANGE, queue, msgBO);
            ListenableFuture<SendResult<String, MqMsgMO>> future = kafkaTemplate.send(topic, msgBO);
            future.addCallback(new ListenableFutureCallback<SendResult<String, MqMsgMO>>() {

                @Override
                public void onSuccess(SendResult<String, MqMsgMO> sendResult) {
//                    logger.info("生产者成功发送消息到" + topic + "-> " + sendResult.getProducerRecord().value().toString());
                }
                @Override
                public void onFailure(Throwable throwable) {
                    logger.error("生产者发送消息：{} 失败，原因：{}", msgBO.toString(), throwable.getMessage());
                }
            });
//            logger.info("result->{}",sendResult);
        } catch (Exception e) {
            logger.error("Failed to send task amqp message, msgBO:{}, e:", msgBO, e);
            throw new BusinessException(ResultEnum.AMQP_MSG_SEND_FAIL);
        }
    }
    public void sendTaskMsg(String topic,String key, MqMsgMO msgBO) {
        log.info("topic->{},body->{}", topic, msgBO);
        try {
//            kafkaTemplate.convertAndSend(AmqpConstant.TASK_EXCHANGE, queue, msgBO);
            ListenableFuture<SendResult<String, MqMsgMO>> future = kafkaTemplate.send(topic,key, msgBO);
            future.addCallback(new ListenableFutureCallback<SendResult<String, MqMsgMO>>() {
                @Override
                public void onSuccess(SendResult<String, MqMsgMO> sendResult) {
//                    logger.info("生产者成功发送消息到" + topic + "-> " + sendResult.getProducerRecord().value().toString());
                }
                @Override
                public void onFailure(Throwable throwable) {
                    logger.error("生产者发送消息：{} 失败，原因：{}", msgBO.toString(), throwable.getMessage());
                }
            });
//            logger.info("result->{}",sendResult);
        } catch (Exception e) {
            logger.error("Failed to send task amqp message, msgBO:{}, e:", msgBO, e);
            throw new BusinessException(ResultEnum.AMQP_MSG_SEND_FAIL);
        }
    }
}
