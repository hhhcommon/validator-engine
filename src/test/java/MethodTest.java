import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Test;

import cn.com.bsfit.frms.obj.AuditObject;
import cn.com.bsfit.frms.obj.DistinctedListObject;
import cn.com.bsfit.frms.obj.MaxContinuousCountNumber;
import cn.com.bsfit.frms.obj.MaxIncreaseCountNumber;
import cn.com.bsfit.frms.obj.MemCachedItem;
import cn.com.bsfit.frms.obj.Mergeable;
import cn.com.bsfit.frms.obj.TimedItems;
import cn.com.bsfit.frms.proc.base.calc.CalcTask;
import cn.com.bsfit.frms.proc.base.calc.CalcTaskBuilder;
import cn.com.bsfit.frms.proc.base.calc.Cond;
import cn.com.bsfit.frms.proc.base.calc.Key;
import cn.com.bsfit.frms.proc.base.calc.Method;
import cn.com.bsfit.frms.proc.base.calc.Variable;

import com.alibaba.fastjson.JSON;

public class MethodTest {
	
	@Test
	public void test() throws InterruptedException, ParseException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		CalcTask task = Task();
		
		List<Object> list = new ArrayList<Object>();
		
		Date date = sdf.parse("2016-11-15 09:57:10");
		
		for(int i = 0; i<10; i++){
			AuditObject info = new AuditObject();
			info.setTransTime(new Date(date.getTime() + 1000*3600L * i));
			if(i == 5)
				info.put("amount", 0);
			else
				info.put("amount", 100*i);
			info.put("dfp", "dfp");
			list.add(info);
		}
		
		Map<Class<?>, List<Object>> map = new HashMap<Class<?>, List<Object>>();
		map.put(list.get(0).getClass(), list);
		
		Set<MemCachedItem> set = task.calc(map);
		
		System.out.println(set);
		
		MemCachedItem item = null;
		for(MemCachedItem memCachedItem : set){
			if(item == null)
				item=memCachedItem;
			else {
				item.merge(memCachedItem);
			}
		}
		
		TimedItems tt = (TimedItems) item.getRaw("test");
		
		System.out.println(tt.getRaw(new Date(), "100000pm"));
		
	}
	
	public CalcTask Task(){
		return CalcTaskBuilder.getInstance("test")
			    .selectMemCachedItems("DFP", "ANTI_SPIDER", "1h").selectObjectType(AuditObject.class)
			    .objectKey(new Key() {
			        public String key(Object arg0) {
			            AuditObject ao = (AuditObject) arg0;
			            return ao.get("dfp").toString();
			        }
			    }).expirePattern("10000000pm").varDate(new Variable < Date > () {
			        public Date select(Object arg0) {
			            AuditObject ao = (AuditObject) arg0;
			            return ao.getTransTime();
			        }
			    }).method(new Method() {
			        public Mergeable invoke(Object arg0) {
			            AuditObject ao = (AuditObject) arg0;
			            
			            return new MaxIncreaseCountNumber(((Integer)ao.get("amount")));
			        }
			    }).build();
	}

}
