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

public class CalcTaskTest {
	
	@Test
	public void test() throws InterruptedException, ParseException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		CalcTask task = Task();
		
		List<Object> list = new ArrayList<Object>();
		
		Date date = sdf.parse("2016-11-15 09:57:10");
		
		for(int i = 0; i<10; i++){
			AuditObject info = new AuditObject();
			info.setTransTime(sdf.parse("2016-11-15 09:57:10"));
			info.put("click_ts", Arrays.asList(1,2,3).toString());
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
		
		item.merge(item);
		
//		System.out.println(JSON.toJSONString(item, false));
		
		TimedItems tt = (TimedItems) item.getRaw("同设备过去X分钟的点击流的时间戳");
		
		System.out.println(JSON.toJSON(tt.getAllItems()));
		long time = tt.allItems.entrySet().iterator().next().getKey();
		System.out.println("getRaw:");
		System.out.println(tt.getRaw(date, "100000pm"));
		
		System.out.println(getKeyAppearTimes(tt, 1, Arrays.asList(1,2,3), 10));
	}
	
	public CalcTask Task(){
		return CalcTaskBuilder.getInstance("同设备过去X分钟的点击流的时间戳")
			    .selectMemCachedItems("DFP", "ANTI_SPIDER", "1h").selectObjectType(AuditObject.class).objectCond(
			        new Cond() {
			            public boolean cond(Object arg0) {
			                AuditObject ao = (AuditObject) arg0;
			                return isNotEmpty(ao.get("dfp")) && null != ao.get("click_ts");
			            }
			        }).objectKey(new Key() {
			        public String key(Object arg0) {
			            AuditObject ao = (AuditObject) arg0;
			            return ao.get("dfp").toString();
			        }
			    }).expirePattern("10pm").varDate(new Variable < Date > () {
			        public Date select(Object arg0) {
			            AuditObject ao = (AuditObject) arg0;
			            return ao.getTransTime();
			        }
			    }).method(new Method() {
			        public Mergeable invoke(Object arg0) {
			            AuditObject ao = (AuditObject) arg0;
			            return new DistinctedListObject(ao.get("click_ts"));
			        }
			    }).build();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int getKeyAppearTimes(Object timedItem, int skipMinute, Object click_ts, int minuteCount){
		int count = 0;
		
		if(timedItem == null || !(timedItem instanceof TimedItems))
			return -1;
		
		TimedItems tt = (TimedItems)timedItem;
		
		long ts = System.currentTimeMillis() - skipMinute*60*1000L;
		
		TreeMap map = new TreeMap(new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				return ((Long)o2).compareTo((Long)o1);
			}
		});
		map.putAll(tt.getAllItems());
		
		Object obj = tt.getRaw(ts, "10000pm");
		if(obj instanceof DistinctedListObject){
			DistinctedListObject dlo = (DistinctedListObject) obj;
			Set set = dlo.getSet();
			String clickTs = click_ts.toString();
			if(set.contains(clickTs)){
				count++;
			}
		}
		
		Iterator<Entry> iterator = map.entrySet().iterator();
		while(iterator.hasNext()){
			Entry entry = iterator.next();
			long tmpTs = (Long) entry.getKey();
			tmpTs -= skipMinute*60*1000L;
			if(tmpTs < ts - minuteCount*60*1000L)
				break;
			
			Set curSet = null;
			Object valueObject = entry.getValue();
			if(valueObject instanceof DistinctedListObject){
				DistinctedListObject dlo = (DistinctedListObject) valueObject;
				curSet = dlo.getSet();
			}else {
				continue;
			}
			Object tmpObj = tt.getRaw(tmpTs, "10000pm");
			
			if(tmpObj instanceof DistinctedListObject){
				DistinctedListObject dlo = (DistinctedListObject) tmpObj;
				Set set = dlo.getSet();
				String clickTs = curSet.toArray()[0].toString();
				if(set.contains(clickTs)){
					if(iterator.hasNext())
						count++;
				}
			}
		}
		
		return count;
	}
	
	boolean isNotEmpty (Object obj){
		return !(obj == null || "".equals(obj.toString()));
	}

}
