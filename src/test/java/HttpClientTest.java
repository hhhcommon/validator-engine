import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Test;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;


public class HttpClientTest {

	@Test
	public void test() throws InterruptedException{
		int poolSize = 4;
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();
        connMgr.setMaxTotal(poolSize + 1);
        connMgr.setDefaultMaxPerRoute(poolSize);
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();
        final RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
        FastJsonHttpMessageConverter fastjson = new FastJsonHttpMessageConverter();
        fastjson.setFeatures(SerializerFeature.WriteClassName, SerializerFeature.BrowserCompatible, SerializerFeature.DisableCircularReferenceDetect);
        converters.add(fastjson);
//        template.setMessageConverters(converters);

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
        List<Callable<Object>> threads = new ArrayList<Callable<Object>>();
        for(int j = 0 ; j < 20; j++){
        	final int tmpJ = j;
        	threads.add(new Callable() {

				@Override
				public Object call() throws Exception {
					int i=0;
			        while(i++ < 50)
			        	template.getForEntity("http://test5.bangruitech.com/slide/refresh?authId=7aAQFWvJi4z0oVlAV505C6a4Fi2EcBTT&rank=3&signId=9&ts=1488714914905&uuid=BT6z8Iu5aZj_oQu7GZeZEoNiuvpYKLf4&vid=8&hashCode=BbdmL3mS1AkO7O7nUMWvdASqeBokjbfhCnbB0pi2cEQM2bSf-u3YFNczhQJIuNkBV9PSBbkR5njDzbE-patfUw6ii-cMhaoll_sP8MD1RsgySyxK38T6RMIThvRw0SvTRVfuddh5_5K23O2sIsH4bfDELvMXa0cu&returnUrl=http://test10.bangruitech.com/", String.class);
			        System.out.println("thread-"+tmpJ+" done");
					return null;
				}
			});
        }
        
        executor.invokeAll(threads);
	}
}
