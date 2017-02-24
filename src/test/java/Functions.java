import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import cn.com.bsfit.frms.obj.DistinctedListObject;
import cn.com.bsfit.frms.obj.MergeableNumber;
import cn.com.bsfit.frms.obj.TimedItems;
import cn.com.bsfit.frms.obj.VarsNumber;


public class Functions {

	boolean isVarsNumberLessThan(MergeableNumber number, double limit){
		if(number == null)
			return true;
		return number.doubleValue() < limit;
	}
	
	VarsNumber mergePositionVarsNumber(Object obj){
		List positions = null;
		if(obj instanceof List)
			positions = (List)obj;
		if(positions == null)
			return null;
		VarsNumber var_pos = new VarsNumber();
		for (int i = 0; i < positions.size(); i++) {
			String[] posXY = ((String)positions.get(i)).split(",");
			double Y = Double.parseDouble(posXY[1]);
			var_pos.merge(new VarsNumber(Y));
		}
		return var_pos;
	}
	
	VarsNumber mergeSpeedVarsNumber(Object posObj, Object timeObj){
		List positions = null;
		List timePixs = null;
		if(posObj instanceof List)
			positions = (List)posObj;
		if(timeObj instanceof List)
			timePixs = (List)timeObj;
		if(positions == null || timePixs == null)
			return null;
		VarsNumber var_speed = new VarsNumber();
		for (int i = 1; i < timePixs.size(); i++) {
			String last_pos = (String) positions.get(i - 1);
			String cur_pos = (String) positions.get(i);
			long last_X = Long.parseLong(last_pos.split(",")[0]);
			long cur_X = Long.parseLong(cur_pos.split(",")[0]);
			var_speed.merge(new VarsNumber(1000
					* Math.abs((double) cur_X - last_X)
					/ (((Number)timePixs.get(i)).doubleValue() - ((Number)timePixs.get(i - 1))
							.doubleValue())));
		}
		return var_speed;
	}
	
	double[] linearRegression(Object posObj) {
		List positions = null;
		if(posObj instanceof List)
			positions = (List) posObj;
		double[] x = new double[positions.size()];
		double[] y = new double[positions.size()];
		
		for (int i = 0; i < positions.size(); i++) {
			String[] posXY = ((String)positions.get(i)).split(",");
			long X = Long.parseLong(posXY[0]);
			long Y = Long.parseLong(posXY[1]);
			x[i] = X;
			y[i] = Y;
		}
		double[] result = new double[4];
		int n = 0;
		// 计算总和
		double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
		while (n < x.length) {
			sumx += x[n];
			sumx2 += x[n] * x[n];
			sumy += y[n];
			n++;
		}
		// 求平均数
		double xbar = sumx / n;
		double ybar = sumy / n;
		// 计算系数
		double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
		for (int i = 0; i < n; i++) {
			xxbar += (x[i] - xbar) * (x[i] - xbar);
			yybar += (y[i] - ybar) * (y[i] - ybar);
			xybar += (x[i] - xbar) * (y[i] - ybar);
		}
		double beta1 = xybar / xxbar;
		double beta0 = ybar - beta1 * xbar;
		System.out.println("y = " + beta1 + " * x + " + beta0);
		result[0] = beta1;
		result[1] = beta0;
		double k = Math.abs(y[0] - y[y.length - 1])
				/ Math.abs(x[0] - x[x.length - 1]);
		double dy = Math.abs(y[0] - y[y.length - 1]) / 2 + y[0];
		result[2] = k;
		result[3] = dy;
		return result;
	}
	
	double linearCompare(double[] linear, int i1, int i2){
		double result = linear[i1] / linear[i2];
		if(result > 1)
			result = 1/result;
		return result;
	}
	
	public boolean isInXhours(Object ip_proxy_time, int x){
		long suspectTime = -1;
		if(ip_proxy_time instanceof Date)
			suspectTime = ((Date)ip_proxy_time).getTime();
		if(ip_proxy_time instanceof Number)
			suspectTime = new Double(String.valueOf(ip_proxy_time)).longValue();
		
		return suspectTime > (System.currentTimeMillis() - x*3600*1000L);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int getKeyAppearTimes(Object timedItem, int skipMinute, Object click_ts, int minuteCount){
		int count = 0;
		
		if(timedItem == null || !(timedItem instanceof TimedItems))
			return -1;
		
		TimedItems tt = (TimedItems)timedItem;
		
		long ts = System.currentTimeMillis();
		
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
				String clickTs = curSet.toString();
				if(set.contains(clickTs)){
					count++;
				}
			}
		}
		
		return count;
	}
	
}
