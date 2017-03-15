

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.hornetq.utils.json.JSONObject;
import org.junit.Test;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import cn.com.bsfit.frms.obj.AuditObject;
import cn.com.bsfit.frms.obj.AuditResult;
import cn.com.bsfit.frms.obj.Risk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

public class EngineTest {

    public static void main(String[] args) throws ParseException, IOException {
        int poolSize = 4;
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();
        connMgr.setMaxTotal(poolSize + 1);
        connMgr.setDefaultMaxPerRoute(poolSize);
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
        FastJsonHttpMessageConverter fastjson = new FastJsonHttpMessageConverter();
        fastjson.setFeatures(SerializerFeature.WriteClassName, SerializerFeature.BrowserCompatible, SerializerFeature.DisableCircularReferenceDetect);
        converters.add(fastjson);
        template.setMessageConverters(converters);
        String url = "http://localhost:8080/audit";
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<AuditObject> audits = new ArrayList<AuditObject>();
        AuditObject ao=new AuditObject();
        ao.setBizCode("SLIDE.RANK2");
        ao.put("ip", "172.30.12.1");
        ao.put("resource_type", "1");
//		ao.putAll(JSON.parseObject("{\"respTime\":2592000,\"timestamp\":1482458464976,\"frms_biz_code\":\"SLIDE.RANK2\",\"positions\":[\"444,784\",\"462,786\",\"478,785\",\"494,786\",\"505,789\",\"507,790\",\"507,790\",\"509,790\",\"516,790\",\"518,790\",\"519,792\",\"501,794\",\"489,794\",\"481,794\",\"476,796\",\"474,797\",\"470,797\",\"465,797\",\"463,800\",\"461,799\",\"459,797\",\"456,797\",\"454,795\",\"456,794\",\"460,793\",\"462,792\",\"463,792\",\"465,793\",\"469,793\",\"470,793\",\"468,796\",\"467,796\",\"466,796\",\"464,796\",\"462,796\",\"460,796\",\"459,796\"],\"ua\":\"Mozilla/5.0 (Linux; U; Android 4.4.4; zh-cn; Coolpad 8297-C00 Build/KTU84P) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.2 Mobile Safari/537.36\",\"randomX\":59,\"timeCost\":6873000,\"tryTimes\":1,\"frms_trans_time\":1482458473271,\"timePixs\":[177000,236000,284000,347000,408000,468000,506000,571000,615000,715000,761000,932000,980000,1079000,1122000,1123000,1206000,1267000,1327000,1378000,1441000,1528000,1807000,2320000,2522000,2581000,2765000,2923000,3138000,3309000,4054000,4131000,4266000,5305000,5426000,5589000,6163000],\"frms_ip_addr\":\"219.155.179.247\",\"randomY\":13,\"frms_uuid\":\"e734759b-8c49-4345-a93e-cb3ca519c999\",\"frms_device_id\":\"\"}"));
        ao.setTransTime(new Date(1482110121883L));
		audits.add(ao);
        long begin = System.currentTimeMillis();
        List list = template.postForObject(url, audits, List.class);
        long end = System.currentTimeMillis();
        for(Object obj : list){
        	AuditResult ar = JSON.parseObject(JSON.toJSONString(obj), AuditResult.class);
        	for(Risk r : ar.getRisks()){
        		System.out.println(r.getRuleName());
        	}
        }
		System.out.print(String.format("call engine service cost %d ms", end - begin));
		httpClient.close();
    }
    
    @Test
    public void httpTest(){
    	PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try{
        	//1 设置连接属性
        	URL realUrl = new URL("http://10.100.1.154:8181/audit");
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("method", "post");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("User-Agent", "Apache-HttpClient/4.2.6 (java 1.5)");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            
            //2 构建 List<AuditObject> 对象           
            AuditObject ao=new AuditObject();
            ao.setBizCode("PAY.BUY");
    		ao.setUserId("1234567800");
    		//ao.put("frms_trans_code", "CONSUME");
    		ao.put("frms_trans_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-05-06 09:09:00"));
    		//ao.put("frms_trans_time", new Date());
    		ao.put("frms_phone_no", "18388233116");
    		ao.put("frms_trade_type", "WITHDRAW");
    		ao.put("frms_trans_vol", 12000l);
    		//ao.put("frms_pay_to_phone", "13701055027");
    		//ao.put("frms_bank_card_no", "6217001140009201846");
    		ao.put("frms_ip_addr", "192.168.19.128");
    		ao.put("frms_id_no", "352225196903021521");
    		//ao.put("frms_certificate_no", "331022198906071895");
    		ao.put("frms_finger_print", "0kdj-121k-jkks-32kd");    		
    		ao.put("frms_user_name", "邦盛科技");       
    		
    		List<AuditObject> audits = new ArrayList<AuditObject>();
    		audits.add(ao);
    		
    		//3 转换成 json字符串发送到引擎程序
            String json= JSON.toJSONString(audits, SerializerFeature.WriteClassName, SerializerFeature.BrowserCompatible, SerializerFeature.DisableCircularReferenceDetect);          	          		
            System.out.println(json);                       
            out.print(json);
            out.flush();
            
            // 4 读取返回值,转化为AuditResult 
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }           
            
            // 结果转化为  AuditResult
            List<AuditResult> res=JSON.parseArray(result,AuditResult.class);           
            System.out.println(res);
        }catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
    } 
    
    @Test
    public void httpBatchOrderTest(){
    	PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try{
        	
        	URL realUrl = new URL("http://10.15.5.133:9180/audit");
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("method", "post");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("User-Agent", "Apache-HttpClient/4.2.6 (java 1.5)");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            String jsonStr = "{'@type':'cn.com.bsfit.frms.obj.AuditObject','frms_biz_code':'PAY.BUY',"
            +"'frms_uuid':'fca0ae13-787d-4c66-a62f-059dfe00072d','frms_pay_type':'4','frms_total_vol':1100000,"
            		+"'frms_bank_card_type':2,'frms_pay_mch_level':1,'frms_user_id':'1001'}";
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("@type","cn.com.bsfit.frms.obj.AuditObject");
            map.put("frms_biz_code","PAY.BUY");
            map.put("frms_uuid", "fca0ae13-787d-4c66-a62f-059dfe00072d");
            map.put("frms_total_vol", 1100000L);
            map.put("frms_pay_type", "4");
            map.put("frms_bank_card_type", "2");
            map.put("frms_pay_mch_level", "1");
            map.put("frms_user_id", "1001");
            List<Map>list=new ArrayList<Map>();
            list.add(map);
            JSONObject jsonObj = new JSONObject(map);
            out.print(map.toString());
            out.flush();
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            
            System.out.println("============================================="+result);
        }catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }   
    
    @Test
    public void httpClient() throws Exception{
    	PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();
        connMgr.setMaxTotal(5);
        connMgr.setDefaultMaxPerRoute(5);
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();
        HttpPost httppost = new HttpPost("http://10.100.1.154:8180/audit"); 
        httppost.addHeader("Content-Type","application/json");
        httppost.addHeader("User-Agent", "Apache-HttpClient/4.2.6 (java 1.5)");
        httppost.addHeader("Accept-Charset", "UTF-8");
        //2 构建 List<AuditObject> 对象           
        AuditObject ao=new AuditObject();
        ao.setBizCode("PAY.BUY");
		ao.setUserId("1234567800");
		//ao.put("frms_trans_code", "CONSUME");
		ao.put("frms_trans_time",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-05-06 09:09:00"));
		//ao.put("frms_trans_time", new Date());
		ao.put("frms_phone_no", "18388233116");
		ao.put("frms_trade_type", "WITHDRAW");
		ao.put("frms_trans_vol", 12000l);
		//ao.put("frms_pay_to_phone", "13701055027");
		//ao.put("frms_bank_card_no", "6217001140009201846");
		ao.put("frms_ip_addr", "192.168.19.128");
		ao.put("frms_id_no", "352225196903021521");
		//ao.put("frms_certificate_no", "331022198906071895");
		ao.put("frms_finger_print", "0kdj-121k-jkks-32kd");    		
		ao.put("frms_user_name", "邦盛科技");       
		
		List<AuditObject> audits = new ArrayList<AuditObject>();
		audits.add(ao);
        
		//3 转换成 json字符串发送到引擎程序
        String json= JSON.toJSONString(audits, SerializerFeature.WriteClassName, SerializerFeature.BrowserCompatible, SerializerFeature.DisableCircularReferenceDetect);          	          		
        System.out.println(json);   
        
        InputStream is=new ByteArrayInputStream(json.getBytes());
        InputStreamEntity  streamEntity= new InputStreamEntity(is); 
    	
        streamEntity.setContentType("application/json");
        httppost.setEntity(streamEntity);  
        CloseableHttpResponse response = httpClient.execute(httppost);  
        
        HttpEntity entity = response.getEntity();  
        String res= EntityUtils.toString(entity, "UTF-8");
        
        List<AuditResult> result=JSON.parseArray(res, AuditResult.class);
        System.out.println("res="+result);

        response.close();    
    }
}

