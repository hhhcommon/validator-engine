import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import cn.com.bsfit.frms.obj.AvgNumber;
import cn.com.bsfit.frms.obj.CountNumber;
import cn.com.bsfit.frms.obj.DecreaseCountNumber;
import cn.com.bsfit.frms.obj.DistinctedListObject;
import cn.com.bsfit.frms.obj.IncreaseCountNumber;
import cn.com.bsfit.frms.obj.MaxContinuousCountNumber;
import cn.com.bsfit.frms.obj.MaxDecreaseCountNumber;
import cn.com.bsfit.frms.obj.MaxIncreaseCountNumber;
import cn.com.bsfit.frms.obj.MaxNumber;
import cn.com.bsfit.frms.obj.Mergeable;
import cn.com.bsfit.frms.obj.MergeableListObject;
import cn.com.bsfit.frms.obj.MergeableMapObject;
import cn.com.bsfit.frms.obj.MergeableNumber;
import cn.com.bsfit.frms.obj.MinNumber;
import cn.com.bsfit.frms.obj.ReplacedObject;
import cn.com.bsfit.frms.obj.SumNumber;
import cn.com.bsfit.frms.obj.TimedItems;
import cn.com.bsfit.frms.obj.VarpNumber;
import cn.com.bsfit.frms.obj.VarsNumber;

import com.alibaba.fastjson.JSON;

/**
 * 配置型条件需要的内置方法
 * @author cxy
 *
 */
public class GlobalFunctions {
	
	public boolean isMultipleOfX(Object obj , int x){
		if(obj == null || x ==0)
			return false;
		
		return new Double(getNumber(obj)).longValue() % x == 0;
	}
	
	/**
	 * 根据缓存对象，时间戳和时间窗口获取缓存计算结果对象
	 * @param obj    TimedItems object    
	 * @param time   frms_trans_time
	 * @param pattern    3pd
	 * @return    Mergeable对象
	 */
	public Object getCacheItem(Object obj, Object time, String pattern){
		if (obj == null || !(obj instanceof TimedItems)) {
	        return null;
	    }
	    TimedItems tt = (TimedItems) obj;
	    Object obj2;
	    if (pattern == null) {
	        obj2 = tt.getRaw();
	    } else
	        obj2 = tt.getRaw(time == null ? new Date() : time, pattern);
	    
	    return obj2;
	}
	
	/**
	 * 获取数值型结果
	 * @param obj  Mergeable对象
	 * @return     double 数值
	 */
	public double getNumber(Object obj){
		double item = -1D;
		if(obj instanceof Number && !(obj instanceof MergeableNumber)){
			item = Double.parseDouble(String.valueOf(obj));
		}
		if(obj instanceof SumNumber){
			item = ((SumNumber)obj).doubleValue();
		}
		if(obj instanceof CountNumber){
			item = ((CountNumber)obj).doubleValue();
		}
		if(obj instanceof AvgNumber){
			item = ((AvgNumber)obj).doubleValue();
		}
		if(obj instanceof DecreaseCountNumber){
			item = ((DecreaseCountNumber)obj).doubleValue();
		}
		if(obj instanceof IncreaseCountNumber){
			item = ((IncreaseCountNumber)obj).doubleValue();
		}
		if(obj instanceof MaxNumber){
			item = ((MaxNumber)obj).doubleValue();
		}
		if(obj instanceof MinNumber){
			item = ((MinNumber)obj).doubleValue();
		}
		if(obj instanceof DistinctedListObject){
			item = ((DistinctedListObject)obj).getSet().size();
		}
		if(obj instanceof MaxContinuousCountNumber){
			item = ((MaxContinuousCountNumber)obj).doubleValue();
		}
		if(obj instanceof MaxDecreaseCountNumber){
			item = ((MaxDecreaseCountNumber)obj).doubleValue();
		}
		if(obj instanceof MaxIncreaseCountNumber){
			item = ((MaxIncreaseCountNumber)obj).doubleValue();
		}
		if(obj instanceof VarpNumber){
			item = ((VarpNumber)obj).doubleValue();
		}
		if(obj instanceof VarsNumber){
			item = ((VarsNumber)obj).doubleValue();
		}
		if(obj instanceof ReplacedObject){
			Object rObject = ((ReplacedObject)obj).getObject();
			if(rObject instanceof Number)
				item = Double.parseDouble(String.valueOf(rObject));
		}
		if(obj instanceof String){
			String str = (String) obj;
			if(str.matches("^[+-]?([0-9]*\\.?[0-9]+|[0-9]+\\.?[0-9]*)([eE][+-]?[0-9]+)?$"))
				item = Double.parseDouble(str);
		}
		return item;
	}
	
	public double getMergeableMapNumber(Object obj, Object obj2, Object obj3){
		double item = -1D;
		if(obj instanceof MergeableMapObject){
			item = 0D;
			double num = -1D;
			String functionName = String.valueOf(obj2);
			if(obj3 instanceof Number){
				num = Double.parseDouble(String.valueOf(obj3));
			}
			if(num != -1){
				MergeableMapObject mmo = (MergeableMapObject)obj;
				for(Entry<Object, Object> entry : mmo.asMap().entrySet()){
					double objNum = getNumber(entry.getValue());
					boolean result = false;
					result = executeExpression(objNum , functionName , num);
					if(result)
						item++;
				}
			}
		}
		return item;
	}
	
	@Test
	public void test() {
		System.out.println(isMultipleOfX(new ReplacedObject(100), 11));
	}
	
	public static void main(String[] args) {
		
	}
	
	/**
	 * 获取字符型结果
	 * @param obj  Mergeable对象
	 * @return     String 字符串
	 */
	public String getString(Object obj){
		if(obj == null)
			return null;
		String item = null;
		if(obj instanceof ReplacedObject){
			Object rObject = ((ReplacedObject)obj).getObject();
			if(rObject instanceof String)
				item = String.valueOf(rObject);
		}
		return item == null? String.valueOf(item):item;
	}
	
	/**
	 * 相加:obj1+obj2
	 * @param obj1   Mergeable对象/Number对象
	 * @param obj2   Mergeable对象/Number对象
	 * @return       double 计算结果
	 */
	public double plus(Object obj1, Object obj2){
		if(obj1 == null || obj2 == null)
			return -1D;
		double item1 = 0D, item2 = 0D;
		if(obj1 instanceof Mergeable)
			item1 = getNumber(obj1);
		if(obj1 instanceof Number)
			item1 = Double.parseDouble(String.valueOf(item1));
		if(obj2 instanceof Mergeable)
			item2 = getNumber(obj2);
		if(obj2 instanceof Number)
			item2 = Double.parseDouble(String.valueOf(item2));
		return item1 + item2;
	}
	
	/**
	 * 相减:obj1-obj2
	 * @param obj1    Mergeable对象/Number对象
	 * @param obj2    Mergeable对象/Number对象
	 * @return        double 计算结果
	 */
	public double minus(Object obj1, Object obj2){
		if(obj1 == null || obj2 == null)
			return -1D;
		double item1 = 0D, item2 = 0D;
		if(obj1 instanceof Mergeable)
			item1 = getNumber(obj1);
		if(obj1 instanceof Number)
			item1 = Double.parseDouble(String.valueOf(item1));
		if(obj2 instanceof Mergeable)
			item2 = getNumber(obj2);
		if(obj2 instanceof Number)
			item2 = Double.parseDouble(String.valueOf(item2));
		return item1 - item2;
	}
	
	/**
	 * 相除:obj1/obj2
	 * @param obj1    Mergeable对象/Number对象
	 * @param obj2    Mergeable对象/Number对象
	 * @return        double 计算结果
	 */
	public double devide(Object obj1, Object obj2){
		if(obj1 == null || obj2 == null)
			return -1D;
		double item1 = 0D, item2 = 0D;
		if(obj1 instanceof Mergeable)
			item1 = getNumber(obj1);
		if(obj1 instanceof Number)
			item1 = Double.parseDouble(String.valueOf(item1));
		if(obj2 instanceof Mergeable)
			item2 = getNumber(obj2);
		if(obj2 instanceof Number)
			item2 = Double.parseDouble(String.valueOf(item2));
		if(item2 == 0D)
			return Double.MAX_VALUE;
		return item1 / item2;
	}
	
	/**
	 * 相乘:obj1*obj2
	 * @param obj1    Mergeable对象/Number对象
	 * @param obj2    Mergeable对象/Number对象
	 * @return        double 计算结果
	 */
	public double multiply(Object obj1, Object obj2){
		if(obj1 == null || obj2 == null)
			return -1D;
		double item1 = 0D, item2 = 0D;
		if(obj1 instanceof Mergeable)
			item1 = getNumber(obj1);
		if(obj1 instanceof Number)
			item1 = Double.parseDouble(String.valueOf(item1));
		if(obj2 instanceof Mergeable)
			item2 = getNumber(obj2);
		if(obj2 instanceof Number)
			item2 = Double.parseDouble(String.valueOf(item2));
		if(item2 == 0D)
			return 0D;
		return item1 * item2;
	}
	
	/**
	 * 大于：when obj>threshold, then true
	 * @param obj        Mergeable对象
	 * @param threshold  Number对象
	 * @return
	 */
	public boolean isGreaterThan(Object obj , Object threshold){
		if(obj == null || threshold == null)
			return false;
		double item = getNumber(obj);
		double item2 = getNumber(threshold);
		return item > item2;
	}
	
	/**
	 * 大于等于：when obj>=threshold, then true
	 * @param obj        Mergeable对象
	 * @param threshold  Number对象
	 * @return
	 */
	public boolean isGreaterThanOrEqualTo(Object obj , Object threshold){
		if(obj == null || threshold == null)
			return false;
		double item = getNumber(obj);
		double item2 = getNumber(threshold);
		return item >= item2;
	}
	
	/**
	 * 小于：when obj<threshold, then true
	 * @param obj        Mergeable对象
	 * @param threshold  Number对象
	 * @return
	 */
	public boolean isLessThan(Object obj , Object threshold){
		if(obj == null || threshold == null)
			return false;
		double item = getNumber(obj);
		double item2 = getNumber(threshold);
		return item < item2;
	}
	
	/**
	 * 小于等于：when obj<threshold, then true
	 * @param obj        Mergeable对象
	 * @param threshold  Number对象
	 * @return
	 */
	public boolean isLessThanOrEqualTo(Object obj , Object threshold){
		if(obj == null || threshold == null)
			return false;
		double item = getNumber(obj);
		double item2 = getNumber(threshold);
		return item <= item2;
	}
	
	/**
	 * 相等：when obj == compareObject or obj.equals(compareObject) then true
	 * @param obj		Mergeable/String
	 * @param compareObject		Number/String
	 * @return
	 */
	public boolean isEqulaTo(Object obj , Object compareObject){
		if(obj == null)
			return false;
		if(compareObject instanceof Number){
			double item = getNumber(obj);
			return item == getNumber(compareObject);
		}
		if(compareObject instanceof String){
			if(obj instanceof String)
				return obj.equals(compareObject);
			if(obj instanceof Mergeable){
				String item = getString(obj);
				return item.equals(compareObject);
			}
		}
		return getString(obj).equals(getString(compareObject));
	}
	
	/**
	 * 不等于，即!isEqulaTo
	 * @param obj
	 * @param compareObject
	 * @return
	 */
	public boolean isNotEqualTo(Object obj , Object compareObject){
		return !isEqulaTo(obj, compareObject);
	}
	
	public boolean isDifferent(Object obj , Object compareObject){
		if(obj == null || compareObject == null)
			return false;
		if(obj instanceof String){
			String item = (String) obj;
			if(compareObject instanceof String)
				return !item.equals((String)compareObject);
			if(compareObject instanceof Mergeable){
				String co = getString(compareObject);
				return !item.equals(co);
			}
		}
		if(obj instanceof Mergeable){
			String item = getString(obj);
			if(compareObject instanceof String)
				return !item.equals((String)compareObject);
			if(compareObject instanceof Mergeable){
				String co = getString(compareObject);
				return !item.equals(co);
			}
		}
		return false;
	}
	
	/**
	 * 数值在范围内
	 * @param obj		Mergeable对象
	 * @param pattern		像 [a,b], ()开区间，[]闭区间
	 * @return
	 */
	public boolean isInRange(Object obj, String pattern){
		if(obj == null || pattern == null)
			return false;
		pattern = pattern.replaceAll(" ", "");
		if(!pattern.matches("[\\[(]\\d+,\\d+[\\])]"))
			return false;
		String[] strs = pattern.substring(1, pattern.length()-1).split(",");
		double start = Double.parseDouble(strs[0]);
		double end = Double.parseDouble(strs[1]);
		boolean startInclude = false, endInclude = false;
		if(pattern.startsWith("["))
			startInclude = true;
		if(pattern.endsWith("]"))
			endInclude = true;
		double item = getNumber(obj);
		if(startInclude && endInclude){
			return item >= start && item <= end;
		}
		if(!startInclude && endInclude){
			return item > start && item <= end;
		}
		if(startInclude && !endInclude){
			return item >= start && item < end;
		}
		if(!startInclude && !endInclude){
			return item > start && item < end;
		}
		return false;
	}
	
	public boolean isNotInRange(Object obj, String pattern){
		return !isInRange(obj, pattern);
	}
	
	/**
	 * 获取两个对象的时间差（绝对值）
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public long getTimeDistance(Object obj1, Object obj2){
		long time1 = 0, time2 = 0;
		if(obj1 instanceof Date){
			time1 = ((Date)obj1).getTime();
		}
		if(obj1 instanceof Long){
			time1 = (Long)obj1;
		}
		if(obj1 instanceof ReplacedObject){
			ReplacedObject ro = (ReplacedObject)obj1;
			Object value = ro.getObject();
			if(value instanceof Date)
				time1 = ((Date)value).getTime();
			if(value instanceof Long)
				time1 = (Long) value;
		}
		if(obj2 instanceof Date){
			time2 = ((Date)obj2).getTime();
		}
		if(obj2 instanceof Long){
			time2 = (Long)obj2;
		}
		if(obj2 instanceof ReplacedObject){
			ReplacedObject ro = (ReplacedObject)obj2;
			Object value = ro.getObject();
			if(value instanceof Date)
				time2 = ((Date)value).getTime();
			if(value instanceof Long)
				time2 = (Long) value;
		}
		return Math.abs(time1 - time2);
	}
	
	/**
	 * 在名单中，数据服务名单默认1M有效期
	 * @param item
	 * @param transTime
	 * @return
	 */
	public boolean isInNamelist(Object item, Object transTime){
		if (item == null || !(item instanceof ReplacedObject)) {
			return false;
		}
		if (!(item instanceof Mergeable)) {
			return getTimeDistance(item, transTime) < TimedItems.parse("1M");
		}

		ReplacedObject ro = (ReplacedObject) item;

		// 无过期日期 说明失效
		if (ro.getObject() == null)
			return false;
		Date expTime = (Date) ro.getObject();
		
		// 过了失效日期 失效
		if (transTime instanceof Date) {
			if (expTime.getTime() - ((Date) transTime).getTime() > 0) {
				return true;
			}
		} else if (transTime instanceof Long) {
			if (expTime.getTime() - (Long) transTime > 0) {
				return true;
			}
		}
		return false;
	}
	
	public boolean belongsTo(Object obj1, Object obj2){
		if(obj2 instanceof String){
			String pattern = (String)obj2;
			pattern = pattern.replaceAll(" ", "");
			if(pattern.matches("[\\[(]\\d+,\\d+[\\])]"))
				return isInRange(obj1, pattern);
		}
		return isContains(obj2, obj1);
	}
	
	/**
	 * Collection obj1 contains item obj2 , return true
	 * case1 : DistinctedListObject/MergeableListObject contains string , 当前设备不在历史设备中
	 * case2 : string contains string , 归属地在高危地中
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public boolean isContains(Object maxObj, Object minObj){
		if (maxObj == null || minObj == null)
            return false;
        if (minObj instanceof Collection)
            return false;
        if (maxObj instanceof String) {
            String maxString = (String) maxObj;
            if (maxString.startsWith("{") & maxString.endsWith("}"))
                maxString = maxString.substring(1, maxString.length() - 1);
            if (maxString.startsWith("(") && maxString.endsWith(")"))
                maxString = maxString.substring(1, maxString.length() - 1);
            List< String > MaxList = Arrays.asList(maxString.split(","));
            //当两个对象都是字符串时，分割小对象，判断大对象是否包含小对象
            if (minObj instanceof String){
                String minString = (String) minObj;
                List< String > minList = Arrays.asList(minString.split(","));
                for (String s1 : MaxList){
                    for (String s2 :minList){
                    	if(s2.matches("[\u4E00-\u9FA5]+")){
                    		if(s1.contains(s2))
	                        return true;
                    	}
	                    else{
	                    	if(s1.equals(s2))
	                    	return true;
	                    }
                    }
                }
            }else {
                return  MaxList.contains(getString(minObj));
            }

        }
        if (maxObj instanceof MergeableListObject) {
            MergeableListObject mlObject = (MergeableListObject) maxObj;
            return mlObject.asList().contains(String.valueOf(minObj));
        }
        if (maxObj instanceof DistinctedListObject) {
            DistinctedListObject mlObject = (DistinctedListObject) maxObj;
            return mlObject.getSet().contains(String.valueOf(minObj));
        }
        return false;
	}
	
	public boolean notBelongsTo(Object obj1, Object obj2){
		return !isContains(obj2, obj1);
	}
	
	public Object addCurrent(Object obj1, Object obj2, Object passObj){
		boolean pass = false;
		if(passObj == null)
			pass = false;
		if(passObj instanceof Boolean)
			pass = (Boolean)passObj;
		if(passObj != null && !(passObj instanceof Boolean))
			pass = true;
		if(obj1 instanceof CountNumber){
			CountNumber count = (CountNumber)obj1;
			if(pass)
				count.setValue(count.intValue()+1);
			return count;
		}
		if(obj1 instanceof SumNumber && obj2 != null){
			SumNumber sum = (SumNumber)obj1;
			if(pass)
				sum.setValue(sum.doubleValue()+Double.parseDouble(String.valueOf(obj2)));
			return sum;
		}
		if(obj1 instanceof AvgNumber && obj2 != null){
			AvgNumber avg = (AvgNumber)obj1;
			if(pass)
				avg.merge(new AvgNumber(Double.parseDouble(String.valueOf(obj2))));
			return avg;
		}
		if(obj1 instanceof DistinctedListObject && obj2 != null){
			DistinctedListObject disObject = (DistinctedListObject)obj1;
			if(pass)
				disObject.merge(new DistinctedListObject(obj2));
			return disObject;
		}
		if(obj1 instanceof MergeableListObject && obj2 != null){
			MergeableListObject mlObject = (MergeableListObject)obj1;
			if(pass)
				mlObject.merge(new MergeableListObject(obj2));
			return mlObject;
		}
		return obj1;
	}
	
	public boolean isInTimeRange(Object obj, String pattern){
		if(obj == null || pattern == null)
			return false;
		pattern = pattern.replaceAll(" ", "");
		if(!pattern.matches("[\\[(]\\d+,\\d+[\\])]"))
			return false;
		String[] strs = pattern.substring(1, pattern.length()-1).split(",");
		int start = Integer.parseInt(strs[0]);
		int end = Integer.parseInt(strs[1]);
		boolean startInclude = false, endInclude = false;
		if(pattern.startsWith("["))
			startInclude = true;
		if(pattern.endsWith("]"))
			endInclude = true;
		
		Date time = new Date();
		if(obj instanceof Long){
			time = new Date((Long)obj);
		}
		if(obj instanceof Date){
			time = (Date)obj;
		}
		if(obj instanceof ReplacedObject){
			ReplacedObject ro = (ReplacedObject)obj;
			Object value = ro.getObject();
			if(value instanceof Date)
				time = (Date)value;
			if(value instanceof Long)
				time = new Date((Long) value);
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		boolean isless = start > end ;
		if(isless){
			int tmp = start;
			start = end;
			end = tmp;
			boolean tmpb = startInclude;
			startInclude = endInclude;
			endInclude = tmpb;
		}
		boolean result = false;
		result = hour > start && hour < end;
		if(isless && !(hour == start || hour == end)){
			result = !result;
		}
		if(startInclude && hour == start)
			result = true;
		if(endInclude && hour == end)
			result = true;
		return result;
	}
	
	@Test
	public void testIsInTimeRange() throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		System.out.println("测试样本：(22,23,00,01,02,03)");
		System.out.println("跨天");
		System.out.println("[23,2)");
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-15 22:00:00"), "[23,2)"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-15 23:00:00"), "[23,2)"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 00:00:00"), "[23,2)"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 01:00:00"), "[23,2)"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 02:00:00"), "[23,2)"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 03:00:00"), "[23,2)"));
		
		System.out.println("[23,2]");
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-15 22:00:00"), "[23,2]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-15 23:00:00"), "[23,2]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 00:00:00"), "[23,2]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 01:00:00"), "[23,2]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 02:00:00"), "[23,2]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 03:00:00"), "[23,2]"));
		
		System.out.println("(23,2]");
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-15 22:00:00"), "(23,2]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-15 23:00:00"), "(23,2]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 00:00:00"), "(23,2]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 01:00:00"), "(23,2]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 02:00:00"), "(23,2]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 03:00:00"), "(23,2]"));
		
		System.out.println("[23,2]");
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-15 22:00:00"), "[23,2]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-15 23:00:00"), "[23,2]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 00:00:00"), "[23,2]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 01:00:00"), "[23,2]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 02:00:00"), "[23,2]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 03:00:00"), "[23,2]"));
		
		System.out.println("非跨天");
		System.out.println("[2,23]");
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-15 22:00:00"), "[2,23]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-15 23:00:00"), "[2,23]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 00:00:00"), "[2,23]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 01:00:00"), "[2,23]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 02:00:00"), "[2,23]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 03:00:00"), "[2,23]"));
		
		System.out.println("(2,23]");
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-15 22:00:00"), "(2,23]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-15 23:00:00"), "(2,23]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 00:00:00"), "(2,23]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 01:00:00"), "(2,23]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 02:00:00"), "(2,23]"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 03:00:00"), "(2,23]"));
		
		System.out.println("[2,23)");
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-15 22:00:00"), "[2,23)"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-15 23:00:00"), "[2,23)"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 00:00:00"), "[2,23)"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 01:00:00"), "[2,23)"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 02:00:00"), "[2,23)"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 03:00:00"), "[2,23)"));
		
		System.out.println("(2,23)");
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-15 22:00:00"), "(2,23)"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-15 23:00:00"), "(2,23)"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 00:00:00"), "(2,23)"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 01:00:00"), "(2,23)"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 02:00:00"), "(2,23)"));
		System.out.println(isInTimeRange(sdf.parseObject("2017-02-16 03:00:00"), "(2,23)"));
	}
	
	public boolean isInterger(Object obj){
		try {
			Integer.parseInt(String.valueOf(obj));
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	public boolean isStartWith(Object obj1 , Object obj2){
		if(obj1 == null || obj2 == null)
			return false;
		if(obj1 instanceof ReplacedObject){
			ReplacedObject ro = (ReplacedObject)obj1;
			Object valueObj = ro.getObject();
			if(valueObj != null){
				return String.valueOf(valueObj).startsWith(String.valueOf(obj2));
			}
		}
		if(obj1 instanceof String){
			return String.valueOf(obj1).startsWith(String.valueOf(obj2));
		}
		return false;
	}
	
	public boolean executeExpression(Object object , Object oper , Object param){
		boolean result = false;
		if("isGreaterThan".equals(oper)){
			result = isGreaterThan(object, param);
		}if("isGreaterThanOrEqualTo".equals(oper)){
			result = isGreaterThanOrEqualTo(object, param);
		}if("isLessThan".equals(oper)){
			result = isLessThan(object, param);
		}if("isLessThanOrEqualTo".equals(oper)){
			result = isLessThanOrEqualTo(object, param);
		}if("isEqulaTo".equals(oper)){
			result = isEqulaTo(object, param);
		}if("isNotEqualTo".equals(oper)){
			result = isNotEqualTo(object, param);
		}
		return result;
	}
	
	@Test
	public void test1() throws ParseException{
		MergeableMapObject mmo = new MergeableMapObject("key1", new CountNumber(3L));
		mmo.put("key2", new CountNumber(2L));
		mmo.put("key3", new CountNumber(1L));
		System.out.println(getTopN(mmo, 2));
	}
	
	public Object getTopN(Object mmoObj, Integer n){
		if(!(mmoObj instanceof MergeableMapObject))
			return null;
		MergeableMapObject mmo = (MergeableMapObject)mmoObj;
		List<String> result = new ArrayList<String>();
		SortedSet<Map<String, Object>> set = new TreeSet<Map<String, Object>>(new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				Double value1 = new Double(String.valueOf(o1.get("value")));
				Double value2 = new Double(String.valueOf(o2.get("value")));
				return value1.compareTo(value2);
			}
		});
		for(Entry<Object, Object> entry : mmo.asMap().entrySet()){
			double objNum = getNumber(entry.getValue());
			String key = entry.getKey().toString();
			Map<String, Object> tmpMap = new HashMap<String, Object>();
			tmpMap.put("key", key);
			tmpMap.put("value", objNum);
			set.add(tmpMap);
		}
		for(Map<String, Object> map : set){
			if(result.size() < n){
				result.add((String) map.get("key"));
			}
		}
		return result;
	}
	
	public Object getMergeableMapObject(Object obj, Object obj2, Object obj3){
		if(obj instanceof MergeableMapObject){
			Double num = -1D;
			String functionName = String.valueOf(obj2);
			if(obj3 instanceof Number){
				num = Double.parseDouble(String.valueOf(obj3));
			}
			if(num != -1){
				MergeableMapObject mmo = (MergeableMapObject)obj;
				if("getTopN".equals(functionName)){
					return getTopN(mmo, num.intValue());
				}
				else{
					double item = 0D;
					DistinctedListObject dis = new DistinctedListObject();
					for(Entry<Object, Object> entry : mmo.asMap().entrySet()){
						Object o = entry.getValue();
						if(o instanceof DistinctedListObject){
							dis.merge((DistinctedListObject)o);
						}else {
							double objNum = getNumber(o);
							boolean result = false;
							result = executeExpression(objNum , functionName , num);
							if(result)
								item++;
						}
					}
					if(dis.value().intValue() != 0)
						return dis;
					else
						return item;
				}
			}
		}
		return null;
	}
	
	@Test
	public void testSortedList(){
		SortedSet<Map<String, Object>> set = new TreeSet<Map<String, Object>>(new Comparator<Map<String, Object>>() {

			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				Integer value1 = new Integer(String.valueOf(o1.get("value")));
				Integer value2 = new Integer(String.valueOf(o2.get("value")));
				return value1.compareTo(value2);
			}
		});
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key", "key1");
		map.put("value", 3);
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("key", "key2");
		map1.put("value", 2);
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("key", "key3");
		map2.put("value", 1);
		
		set.add(map);
		set.add(map1);
		set.add(map2);
		
		System.out.println(JSON.toJSONString(set));
	}
	
	public Date getXdaysAgo(Object dateObj, Object x){
		Calendar calendar = Calendar.getInstance();
		if(dateObj == null)
			calendar.setTime(new Date());
		if(dateObj instanceof Date)
			calendar.setTime((Date)dateObj);
		if(dateObj instanceof Long)
			calendar.setTimeInMillis((Long)dateObj);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DATE, -1*(new BigDecimal(String.valueOf(x)).intValue()-1));
		return calendar.getTime();
	}
	
	@Test
	public void testGetXdaysAgo(){
		System.out.println(belongsTo(3, "1,2,3"));
		System.out.println(belongsTo("3", "1,2,3"));
	}
	
}
