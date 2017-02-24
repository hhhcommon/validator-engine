package cn.com.bsfit.frms.pay.engine;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import cn.com.bsfit.frms.engine.publish.PublishHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * frms-engine.4.1.0起,风险推送由外部实现,本实现为标准的消息操作类
 * @since 2016-12-14
 */
public class StandardPublishHandlerImpl implements PublishHandler {
	Logger logger = LoggerFactory.getLogger(StandardPublishHandlerImpl.class);
	
	
	/**
	 * 标准队列操作类
	 */
	@Autowired 
	private JmsTemplate jmsTemplate;
	 
	
	/**
	 * 风险预警队列
	 */
	@Autowired
	private Queue riskQueue;
	
	@Override
	public void process(int messageType, Object publishObjs) {
		try {
			switch (messageType) {
				case PublishHandler.RISK_MSG_TYPE: {
					@SuppressWarnings("unchecked")
					List<List<Object>> objs = (List<List<Object>>) publishObjs;
					pushArchives(riskQueue, objs);
					logger.info("{} risk objs published.", objs.size());
					break;
				}
				default: {
					logger.info("messageType can't match,messageType = {}",
							messageType);
				}
			}
		} catch (Exception e) {
			logger.error("引擎推送到队列失败", e);
		}
	}

	/**
	 * 推送风险信息，包括风险、授信、分组、LOG
	 * @param queue
	 * @param objs
	 */
	private void pushArchives(Queue queue,final List<List<Object>> objs) {
		jmsTemplate.send(queue, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(JSON.toJSONString(objs,
						SerializerFeature.BrowserCompatible,
						SerializerFeature.WriteClassName,
						SerializerFeature.DisableCircularReferenceDetect));
			}
		});
	}

}
