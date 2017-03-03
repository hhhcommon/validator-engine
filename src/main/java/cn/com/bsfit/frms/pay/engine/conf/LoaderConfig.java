package cn.com.bsfit.frms.pay.engine.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;
import javax.jms.Queue;

import org.hornetq.api.jms.HornetQJMSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import cn.com.bsfit.frms.pay.engine.loader.DimensionDataLoader;
import cn.com.bsfit.frms.pay.engine.publish.StandardPublishHandlerImpl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

@Configuration
public class LoaderConfig implements FrmsConfigurable {
	Logger logger = LoggerFactory.getLogger(getClass());
	
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
		List<DataLoader> dataLoaderList = new ArrayList<DataLoader>(1);
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
	
	@Value("${frms.license.file:default.lic}")
	String licenseFile;
	
	@PostConstruct
	public void init(){
		logger.info("setting license file to {}", licenseFile);
    	System.setProperty("frms.license", licenseFile);
	}

	KryoFactory factory = new KryoFactory() {
		public Kryo create() {
			Kryo kryo = new Kryo();
			return kryo;
		}
	};
	
	@Bean
    @Primary
    public KryoPool kyInit() {
        KryoPool pool = new KryoPool.Builder(factory).softReferences().build();
        return pool;
    }
}
