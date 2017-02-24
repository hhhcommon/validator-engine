package cn.com.bsfit.frms.pay.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.jms.Queue;

import org.hornetq.api.jms.HornetQJMSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import cn.com.bsfit.frms.base.config.FrmsConfigurable;
import cn.com.bsfit.frms.base.load.DataLoader;
import cn.com.bsfit.frms.engine.load.BasicDataLoader;
import cn.com.bsfit.frms.engine.publish.PublishHandler;
import cn.com.bsfit.frms.engine.rank.SumRiskAnalyzer;

@Configuration
public class LoaderConfig implements FrmsConfigurable {
	@Value("${frms.engine.cloud.threadSize:7}")
	private int cloudThreadSize;

	@Bean
	@Primary
	public SumRiskAnalyzer sumRiskAnalyzer() {
		return new SumRiskAnalyzer();
	}
	
	@Autowired
    @Qualifier("mobNoSqlDataLoader")
    DataLoader mobNoSqlDataLoader;
	
	@Bean(name = "mobNoSqlDataLoader")
    DataLoader mobNoSqlDataLoader() {
        DataLoader loader = new DimensionDataLoader();
        return loader;
    }
	
	@Bean
	BasicDataLoader basicDataLoader() {
		List<DataLoader> dataLoaderList = new ArrayList<>(1);
		dataLoaderList.add(mobNoSqlDataLoader);
		BasicDataLoader basicDataLoader = new BasicDataLoader();
		basicDataLoader.setDataLoaders(dataLoaderList);
		return basicDataLoader;
	}

	@Bean(destroyMethod = "shutdown", name = "cloudThreadPool")
	public ThreadPoolExecutor cloudThreadPool() {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cloudThreadSize);
		return executor;
	}
	
	@Bean(name = "standardPublishHandler")
    @Primary
    public PublishHandler publishHandler(){ 
        return new StandardPublishHandlerImpl(); 
    }
    
    @Value("${frms.engine.risk.queue:FrmsRiskArchiveQueue}")
    private String riskQueueName;
	
	@Bean
    public Queue riskQueue() {
        return HornetQJMSClient.createQueue(riskQueueName);
    }
}
