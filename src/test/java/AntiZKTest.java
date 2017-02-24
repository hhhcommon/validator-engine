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
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.jws.soap.SOAPBinding;

import org.junit.Test;

import cn.com.bsfit.frms.obj.AuditObject;
import cn.com.bsfit.frms.obj.DistinctedListObject;
import cn.com.bsfit.frms.obj.MemCachedItem;
import cn.com.bsfit.frms.obj.Mergeable;
import cn.com.bsfit.frms.obj.MergeableMapObject;
import cn.com.bsfit.frms.obj.ReplacedObject;
import cn.com.bsfit.frms.obj.TimedItems;
import cn.com.bsfit.frms.proc.base.calc.CalcTask;
import cn.com.bsfit.frms.proc.base.calc.CalcTaskBuilder;
import cn.com.bsfit.frms.proc.base.calc.Cond;
import cn.com.bsfit.frms.proc.base.calc.Key;
import cn.com.bsfit.frms.proc.base.calc.Method;
import cn.com.bsfit.frms.proc.base.calc.Variable;

import com.alibaba.fastjson.JSON;

public class AntiZKTest {

	@Test
	public void test() throws InterruptedException, ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		CalcTask task = Task();

		List<Object> list = new ArrayList<Object>();

		Date date = sdf.parse("2016-11-15 09:57:10");

//		for (int i = 0; i < 10; i++) {
//			AuditObject info = new AuditObject();
//			info.setTransTime(sdf.parse("2016-11-15 09:57:10"));
//			info.put("click_ts", Arrays.asList(1, 2, 3).toString());
//			info.put("dfp", "dfp");
//			list.add(info);
//		}
		
		// request
		AuditObject ao = new AuditObject();
		ao.put("source-ip", "10.100.1.11");
		ao.put("dest-ip", "10.100.1.12");
		ao.put("source-port", "10010");
		ao.put("dest-port", "80");
		ao.put("msg_no", "1");
//		ao.put("status-code", "");
		ao.put("dfp", "dfp1111111111");
		ao.put("tcp_direction", "client-to-server");
		ao.put("oper_type", "1");
		ao.setTransTime(new Date());
		list.add(ao);
		AuditObject ao1 = new AuditObject();
		ao1.put("source-ip", "10.100.1.11");
		ao1.put("dest-ip", "10.100.1.12");
		ao1.put("source-port", "10010");
		ao1.put("dest-port", "80");
		ao1.put("msg_no", "2");
//		ao1.put("status-code", "");
		ao1.put("dfp", "dfp2222222222");
		ao1.setTransTime(new Date());
		ao1.put("tcp_direction", "client-to-server");
		ao1.put("oper_type", "1");
		list.add(ao1);

		Map<Class<?>, List<Object>> map = new HashMap<Class<?>, List<Object>>();
		map.put(list.get(0).getClass(), list);

		Set<MemCachedItem> set = task.calc(map);

		System.out.println(set);

		MemCachedItem item = null;
		for (MemCachedItem memCachedItem : set) {
			if (item == null)
				item = memCachedItem;
			else {
				item.merge(memCachedItem);
			}
		}
		
		// response
		list.clear();
		AuditObject aoresp = new AuditObject();
		aoresp.put("source-ip", "10.100.1.12");
		aoresp.put("dest-ip", "10.100.1.11");
		aoresp.put("source-port", "80");
		aoresp.put("dest-port", "10010");
		aoresp.put("msg_no", "1");
		aoresp.put("status-code", "200");
//		aoresp.put("dfp", "dfp1111111111");
		aoresp.setTransTime(new Date());
		aoresp.put("tcp_direction", "server-to-client");
		list.add(aoresp);
		AuditObject aoresp1 = new AuditObject();
		aoresp1.put("source-ip", "10.100.1.12");
		aoresp1.put("dest-ip", "10.100.1.11");
		aoresp1.put("source-port", "80");
		aoresp1.put("dest-port", "10010");
		aoresp1.put("msg_no", "2");
		aoresp1.put("status-code", "200");
//		aoresp1.put("dfp", "dfp1111111111");
		aoresp1.setTransTime(new Date());
		aoresp1.put("tcp_direction", "server-to-client");
		list.add(aoresp1);

		Map<Class<?>, List<Object>> map1 = new HashMap<Class<?>, List<Object>>();
		map1.put(list.get(0).getClass(), list);

		Set<MemCachedItem> set1 = task.calc(map1);

		System.out.println(set1);

		MemCachedItem item1 = null;
		for (MemCachedItem memCachedItem : set1) {
			if (item1 == null)
				item1 = memCachedItem;
			else {
				item1.merge(memCachedItem);
			}
		}

		item.merge(item1);

		 System.out.println(JSON.toJSONString(item, true));

//		TimedItems tt = (TimedItems) item.getRaw("同设备过去X分钟的点击流的时间戳");
//
//		System.out.println(JSON.toJSON(tt.getAllItems()));
//		long time = tt.allItems.entrySet().iterator().next().getKey();
//		System.out.println("getRaw:");
//		System.out.println(tt.getRaw(date, "100000pm"));
//
//		System.out.println(getKeyAppearTimes(tt, 1, Arrays.asList(1, 2, 3), 10));
	}

	public CalcTask Task() {
		return CalcTaskBuilder.getInstance("同一")
				.selectMemCachedItems("IP", "ANTI_ZK", "1h")
				.selectObjectType(AuditObject.class).objectCond(new Cond() {
					public boolean cond(Object obj) {
						AuditObject ao = (AuditObject) obj;
						return isNotEmpty(ao.get("source-ip")) && isNotEmpty(ao.get("source-port"))
								&& isNotEmpty(ao.get("dest-ip"))
								&& isNotEmpty(ao.get("dest-port"))
								&& isNotEmpty(ao.get("msg_no"))
								&& isNotEmpty(ao.get("tcp_direction"));
//								&& isNotEmpty(ao.get("oper_type"));
					}
				}).objectKey(new Key() {
					public String key(Object arg0) {
						AuditObject ao = (AuditObject) arg0;
						String sip = ao.get("source-ip").toString();
						String sport = ao.get("source-port").toString();
						String dip = ao.get("dest-ip").toString();
						String dport = ao.get("dest-port").toString();
						if("client-to-server".equals(ao.get("tcp_direction")))
							return sip+":"+sport+"-"+dip+":"+dport;
						else
							return dip+":"+dport+"-"+sip+":"+sport;
					}
				}).expirePattern("10pm").varDate(new Variable<Date>() {
					public Date select(Object arg0) {
						AuditObject ao = (AuditObject) arg0;
						return ao.getTransTime();
					}
				}).method(new Method() {
					public Mergeable invoke(Object arg0) {
						AuditObject ao = (AuditObject) arg0;
						MergeableMapObject map = new MergeableMapObject();
						if(ao.containsKey("dfp"))
							map.put("dfp", new ReplacedObject(ao.get("dfp")));
						if(ao.containsKey("status-code"))
							map.put("status-code", new ReplacedObject(ao.get("status-code")));
						return new MergeableMapObject(ao.get("msg_no"), map);
					}
				}).build();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int getKeyAppearTimes(Object timedItem, int skipMinute,
			Object click_ts, int minuteCount) {
		int count = 0;

		if (timedItem == null || !(timedItem instanceof TimedItems))
			return -1;

		TimedItems tt = (TimedItems) timedItem;

		long ts = System.currentTimeMillis() - skipMinute * 60 * 1000L;

		TreeMap map = new TreeMap(new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				return ((Long) o2).compareTo((Long) o1);
			}
		});
		map.putAll(tt.getAllItems());

		Object obj = tt.getRaw(ts, "10000pm");
		if (obj instanceof DistinctedListObject) {
			DistinctedListObject dlo = (DistinctedListObject) obj;
			Set set = dlo.getSet();
			String clickTs = click_ts.toString();
			if (set.contains(clickTs)) {
				count++;
			}
		}

		Iterator<Entry> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = iterator.next();
			long tmpTs = (Long) entry.getKey();
			tmpTs -= skipMinute * 60 * 1000L;
			if (tmpTs < ts - minuteCount * 60 * 1000L)
				break;

			Set curSet = null;
			Object valueObject = entry.getValue();
			if (valueObject instanceof DistinctedListObject) {
				DistinctedListObject dlo = (DistinctedListObject) valueObject;
				curSet = dlo.getSet();
			} else {
				continue;
			}
			Object tmpObj = tt.getRaw(tmpTs, "10000pm");

			if (tmpObj instanceof DistinctedListObject) {
				DistinctedListObject dlo = (DistinctedListObject) tmpObj;
				Set set = dlo.getSet();
				String clickTs = curSet.toArray()[0].toString();
				if (set.contains(clickTs)) {
					if (iterator.hasNext())
						count++;
				}
			}
		}

		return count;
	}

	boolean isNotEmpty(Object obj) {
		return !(obj == null || "".equals(obj.toString()));
	}
}
