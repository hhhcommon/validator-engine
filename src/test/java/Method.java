import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import cn.com.bsfit.frms.obj.AuditObject;
import cn.com.bsfit.frms.obj.AvgNumber;
import cn.com.bsfit.frms.obj.CountNumber;
import cn.com.bsfit.frms.obj.DecreaseCountNumber;
import cn.com.bsfit.frms.obj.DistinctedListObject;
import cn.com.bsfit.frms.obj.IncreaseCountNumber;
import cn.com.bsfit.frms.obj.MaxContinuousCountNumber;
import cn.com.bsfit.frms.obj.MaxDecreaseCountNumber;
import cn.com.bsfit.frms.obj.MaxIncreaseCountNumber;
import cn.com.bsfit.frms.obj.MaxNumber;
import cn.com.bsfit.frms.obj.MemCachedItem;
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
import cn.com.bsfit.frms.proc.base.calc.CalcTask;
import cn.com.bsfit.frms.proc.base.calc.CalcTaskBuilder;
import cn.com.bsfit.frms.proc.base.calc.Key;
import cn.com.bsfit.frms.proc.base.calc.Variable;

import com.alibaba.fastjson.JSON;

/**
 * version 1.0.0
 * @author cxy
 *
 */

public class Method {
	
	@Test
	public void ttest(){
		DistinctedListObject dl = new DistinctedListObject(1d);
		dl.merge(new DistinctedListObject(2d));
		Assert.assertEquals(dl, addCurrentExtanded(new DistinctedListObject(1d), 2d, true));
	}
	
	/**
	 * 加法运算
	 * @param obj1 Mergeable/Number 类型
	 * @param obj2 Mergeable/Number 类型
	 * @return number of obj1+obj2
	 */
	double plus(Object obj1, Object obj2) {
	    if (obj1 == null || obj2 == null) return -1D;
	    double item1 = 0D, item2 = 0D;
	    item1 = getNumber(obj1);
	    item2 = getNumber(obj2);
	    return item1 + item2;
	}

	/**
	 * 减法运算
	 * @param obj1 Mergeable/Number 类型
	 * @param obj2 Mergeable/Number 类型
	 * @return number of obj1-obj2
	 */
	double minus(Object obj1, Object obj2) {
		if (obj1 == null || obj2 == null)
			return -1D;
		double item1 = 0D, item2 = 0D;
		item1 = getNumber(obj1);
	    item2 = getNumber(obj2);
		return item1 - item2;
	}
	
	/**
	 * 乘法运算
	 * @param obj1 Mergeable/Number 类型
	 * @param obj2 Mergeable/Number 类型
	 * @return number of obj1*obj2
	 */
	double multiply(Object obj1, Object obj2) {
	    if (obj1 == null || obj2 == null) return -1D;
	    double item1 = 0D, item2 = 0D;
	    item1 = getNumber(obj1);
	    item2 = getNumber(obj2);
	    if (item2 == 0D) return 0D;
	    return item1 * item2;
	}
	
	/**
	 * 大于等于
	 * @param obj Mergeable/Number 类型
	 * @param threshold Mergeable/Number 类型
	 * @return boolean of obj1>=obj2
	 */
	boolean isGreaterThanOrEqualTo(Object obj, Object threshold) {
	    if (obj == null || threshold == null) 
	    	return false;
	    double item = getNumber(obj);
	    double item2 = getNumber(threshold);
	    return item >= item2;
	}
	
	/**
	 * 小于
	 * @param obj Mergeable/Number 类型
	 * @param threshold Mergeable/Number 类型
	 * @return boolean of obj1<obj2
	 */
	boolean isLessThan(Object obj, Object threshold) {
	    // if (obj == null || threshold == null) {
	    //     System.out.println("isLessThan return false");
	    //     return false;
	    // }
	    double item = getNumber(obj);
	    double item2 = getNumber(threshold);
	    return item < item2;
	}
	
	/**
	 * 小于等于
	 * @param obj Mergeable/Number 类型
	 * @param threshold Mergeable/Number 类型
	 * @return boolean of obj1<=obj2
	 */
	boolean isLessThanOrEqualTo(Object obj, Object threshold) {
	    // if (obj == null) return false;
	    double item = getNumber(obj);
	    double item2 = getNumber(threshold);
	    return item <= item2;
	}
	
	/**
	 * 等于，可用于字符型的equal和数值型的==
	 * @param obj
	 * @param compareObject
	 * @return
	 */
	boolean isEqulaTo(Object obj, Object compareObject) {
		if (obj == null || compareObject == null)
			return false;
		if (compareObject instanceof Number) {
			double item = getNumber(obj);
			return item == getNumber(compareObject);
		}
		if (compareObject instanceof String) {
			if (obj instanceof String)
				return obj.equals(compareObject);
			if (obj instanceof Mergeable) {
				String item = getString(obj);
				return item.equals(compareObject);
			}
		}
		return getString(obj).equals(getString(compareObject));
	}
	
	/**
	 * 不等于，参见isEqulaTo
	 * @param obj
	 * @param compareObject
	 * @return
	 */
	boolean isNotEqualTo(Object obj, Object compareObject) {
	    return !isEqulaTo(obj, compareObject);
	}
	
	/**
	 * 已舍弃，同isNotEqualTo
	 * @param obj
	 * @param compareObject
	 * @return
	 */
	boolean isDifferent(Object obj, Object compareObject) {
//	    if (obj == null || compareObject == null) return false;
//	    if (obj instanceof String) {
//	        String item = (String) obj;
//	        if (compareObject instanceof String) return !item.equals((String) compareObject);
//	        if (compareObject instanceof Mergeable) {
//	            String co = getString(compareObject);
//	            return !item.equals(co);
//	        }
//	    }
//	    if (obj instanceof Mergeable) {
//	        String item = getString(obj);
//	        if (compareObject instanceof String) return !item.equals((String) compareObject);
//	        if (compareObject instanceof Mergeable) {
//	            String co = getString(compareObject);
//	            return !item.equals(co);
//	        }
//	    }
	    return false;
	}
	
	/**
	 * 获取obj中的时间戳，该方法需要根据具体实施场景修改
	 * @param obj
	 * @return
	 */
	Date getTime(Object obj) {
		return new Date();
	}
	
	/**
	 * 是否在名单中
	 * @param transTime 时间戳，用于判断一些类似有过期时间的名单
	 * @param item 需要判断的obj
	 * @return boolean in namelist
	 */
	boolean isInNamelist(Object transTime, Object item) {
		if (item instanceof Boolean){
			return (Boolean)item;
		}
		//为了测试线上跑批的数据服务规则，在探头中设置值为字符串“true”，“false”
		if (item instanceof String){
			return Boolean.valueOf((String)item);
		}
	    if (item == null || !(item instanceof ReplacedObject)) {
	        return false;
	    }
	    if (!(item instanceof Mergeable)) {
//	        return getTimeDistance(item, transTime) < ResettableItem.parseDuration("1M");
	    	return getTimeDistance(item, transTime) < TimedItems.parse("1M");
	    }
	    ReplacedObject ro = (ReplacedObject) item;
	    if (ro.getObject() == null){
	    	return false;
	    } 
	    Date expTime = (Date) ro.getObject();
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
	
	/**
	 * 是否在区间内，支持开闭区间
	 * @param obj 需要判断的对象
	 * @param pattern 区间表达式，如(1,2)或(1,2]
	 * @return boolean in range
	 */
	boolean isInRange(Object obj, String pattern) {
	    if (obj == null || pattern == null) return false;
	    pattern = pattern.replaceAll(" ", "");
	    if (!pattern.matches("[\\[(][\\-\\+]?[\\w\\.]+,[\\-\\+]?[\\w\\.]+[\\])]")) return false;
	    String[] strs = pattern.substring(1, pattern.length() - 1).split(",");
	    double start = Double.parseDouble(strs[0]);
	    double end = Double.parseDouble(strs[1]);
	    boolean startInclude = false, endInclude = false;
	    if (pattern.startsWith("[")) startInclude = true;
	    if (pattern.endsWith("]")) endInclude = true;
	    double item = getNumber(obj);
	    if (startInclude & endInclude) {
	        return item >= start & item <= end;
	    }
	    if (!startInclude & endInclude) {
	        return item > start & item <= end;
	    }
	    if (startInclude & !endInclude) {
	        return item >= start & item < end;
	    }
	    if (!startInclude & !endInclude) {
	        return item > start & item < end;
	    }
	    return false;
	}
	
	@Test
	public void testIsInRange(){
		System.out.println(isInRange(10, "[9.1,10.1]"));
		System.out.println(isInRange(10, "[9,10)"));
		System.out.println(isInRange(-10, "[-19.1,+1.1]"));
	}
	
	/**
	 * 不在区间中，参见isInRange
	 * @param obj
	 * @param pattern
	 * @return
	 */
	boolean isNotInRange(Object obj, String pattern) {
	    return !isInRange(obj, pattern);
	}
	
	/**
	 * 属于，用于判断对象是否在集合中或在区间中
	 * @param obj1   需要判断的对象
	 * @param obj2   集合或区间表达式，当为区间表达式时，同isInRange
	 * @return
	 */
	boolean belongsTo(Object obj1, Object obj2) {
		if(obj2 instanceof String){
				String pattern = (String)obj2;
				if(pattern.matches("[\\[(]\\d+,\\d+[\\])]"))
					return isInRange(obj1, pattern);
			}
	    return isContains(obj2, obj1);
	}
	
	/**
	 * 包含，用于判断集合是否包含某一obj
	 * @param maxObj 集合对象 MergeableListObject/DistinctedListObject/String, String时格式为{a,b,c}或(a,b,c)
	 * @param minObj 单一对象
	 * @return
	 */
	boolean isContains(Object maxObj, Object minObj) {
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
	            return mlObject.asList().contains(minObj);
	        }
	        if (maxObj instanceof DistinctedListObject) {
	            DistinctedListObject mlObject = (DistinctedListObject) maxObj;
	            return mlObject.getSet().contains(minObj);
	        }
	        return false;
	}
	
	@Test
	public void testIsContains(){
		MergeableListObject ml = new MergeableListObject(1l);
		ml.merge(new MergeableListObject(2l));
		System.out.println(isContains(ml, 1l));
		System.out.println(isContains(ml, 3l));
	}
	
	/**
	 * 不属于， 参见isContains
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	boolean notBelongsTo(Object obj1, Object obj2) {
	    return !isContains(obj2, obj1);
	}
	
	/**
	 * 包含当前流水，已舍弃，被addCurrentExtanded取代
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	Object addCurrent(Object obj1, Object obj2) {
//	    if (obj1 instanceof CountNumber) {
//	        CountNumber count = (CountNumber) obj1;
//	        count.setValue(count.intValue() + 1);
//	        return count;
//	    }
//	    if (obj1 instanceof SumNumber & obj2 != null) {
//	        SumNumber sum = (SumNumber) obj1;
//	        sum.setValue(sum.doubleValue() + Double.parseDouble(String.valueOf(obj2)));
//	        return sum;
//	    }
//	    if (obj1 instanceof AvgNumber & obj2 != null) {
//	        AvgNumber avg = (AvgNumber) obj1;
//	        avg.merge(new AvgNumber(Double.parseDouble(String.valueOf(obj2))));
//	        return avg;
//	    }
//	    if (obj1 instanceof DistinctedListObject & obj2 != null) {
//	        DistinctedListObject disObject = (DistinctedListObject) obj1;
//	        disObject.merge(new DistinctedListObject(obj2));
//	        return disObject;
//	    }
//	    if (obj1 instanceof MergeableListObject & obj2 != null) {
//	        MergeableListObject mlObject = (MergeableListObject) obj1;
//	        mlObject.merge(new MergeableListObject(obj2));
//	        return mlObject;
//	    }
	    return obj1;
	}
	
	/**
	 * 添加当前流水
	 * @param obj1   历史数据 Mergeable
	 * @param obj2   探头对象
	 * @param pass 是否需要将当前流水加入统计数据进行条件判断
	 * @return Mergeable
	 */
	Object addCurrentExtanded(Object obj1, Object obj2, boolean pass){
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
	
	/**
	 * 除法运算
	 * @param obj1 Mergeable/Number 类型
	 * @param obj2 Mergeable/Number 类型
	 * @return number of obj1/obj2
	 */
	double devide(Object obj1, Object obj2) {
	    if (obj1 == null || obj2 == null)
	        return -1D;
	    double item1 = getNumber(obj1), item2 = getNumber(obj2);
	    if (item2 == 0D)
	        return Double.MAX_VALUE;
	    return (item1 / item2);
	}
	
	/**
	 * 5.0,5.1中内置不可编辑的复杂主体需要，新版本已有多主体配置，已用不到该方法
	 * 获取复杂主体的主体值
	 * @param obj1 数据对象
	 * @param obj2 复杂主体
	 * @return 复杂主体值
	 */
	String getObjTagId(Object obj1, Object obj2) {
		return "";
	}
	
	/**
	 * 获取Mergeable对象
	 * @param obj TimedItems对象
	 * @param time 时间戳
	 * @param pattern 时间范围，如1d
	 * @return
	 */
	Object getCacheItem(Object obj, Object time, String pattern) {
	    if (obj == null || !(obj instanceof TimedItems)) {
	        return null;
	    }
	    TimedItems tt = (TimedItems) obj;
	    Object obj2;
	    if (pattern == null) {
	        //obj2 = tt.getRaw();
	        obj2 = tt.getPrevRaw(time == null ? new Date() : time);
	    } else{
	        obj2 = tt.getRaw(time == null ? new Date() : time, pattern);
	    }
	    return obj2;
	}
	
	/**
	 * 获取数值
	 * @param obj Number/Mergeable/String
	 * @return Number
	 */
	double getNumber(Object obj) {
	    double item = -1D;
	    if (obj instanceof Number & !(obj instanceof MergeableNumber)) {
	        item = Double.parseDouble(String.valueOf(obj));
	    }
	    if (obj instanceof SumNumber) {
	        item = ((SumNumber) obj).doubleValue();
	    }
	    if (obj instanceof CountNumber) {
	        item = ((CountNumber) obj).doubleValue();
	    }
	    if (obj instanceof AvgNumber) {
	        item = ((AvgNumber) obj).doubleValue();
	    }
	    if (obj instanceof DecreaseCountNumber) {
	        item = ((DecreaseCountNumber) obj).doubleValue();
	    }
	    if (obj instanceof IncreaseCountNumber) {
	        item = ((IncreaseCountNumber) obj).doubleValue();
	    }
	    if (obj instanceof MaxNumber) {
	        item = ((MaxNumber) obj).doubleValue();
	    }
	    if (obj instanceof MinNumber) {
	        item = ((MinNumber) obj).doubleValue();
	    }
	    if (obj instanceof DistinctedListObject) {
	        item = ((DistinctedListObject) obj).getSet().size();
	    }
	    if (obj instanceof MaxContinuousCountNumber) {
	        item = ((MaxContinuousCountNumber) obj).doubleValue();
	    }
	    if (obj instanceof MaxDecreaseCountNumber) {
	        item = ((MaxDecreaseCountNumber) obj).doubleValue();
	    }
	    if (obj instanceof MaxIncreaseCountNumber) {
	        item = ((MaxIncreaseCountNumber) obj).doubleValue();
	    }
	    if (obj instanceof VarpNumber) {
	        item = ((VarpNumber) obj).doubleValue();
	    }
	    if (obj instanceof VarsNumber) {
	        item = ((VarsNumber) obj).doubleValue();
	    }
	    if (obj instanceof ReplacedObject) {
	        Object rObject = ((ReplacedObject) obj).getObject();
	        if (rObject instanceof Number) item = Double.parseDouble(String.valueOf(rObject));
	    }
	    if(obj instanceof String){
				String str = (String) obj;
				if(str.matches("^[+-]?([0-9]*\\.?[0-9]+|[0-9]+\\.?[0-9]*)([eE][+-]?[0-9]+)?$"))
					item = Double.parseDouble(str);
			}
	    return item;
	}
	
	/**
	 * 大于运算
	 * @param obj Number/Mergeable
	 * @param threshold Number/Mergeable
	 * @return boolean obj>threshold
	 */
	boolean isGreaterThan(Object obj, Object threshold) {
	    if (obj == null || threshold == null) {
	    	return false;
	    }
	    double item = getNumber(obj);
	    double item2 = getNumber(threshold);
	    return item > item2;
	}
	
	/**
	 * 获取字符
	 * @param obj String/Mergeable
	 * @return 
	 */
	String getString(Object obj) {
	    if (obj == null) 
	    	return null;
	    String item = null;
	    if (obj instanceof ReplacedObject) {
	        Object rObject = ((ReplacedObject) obj).getObject();
	        if (rObject instanceof String) 
	        	item = String.valueOf(rObject);
	    }
	    return item == null? String.valueOf(obj):item;
	}
	
	/**
	 * 获取两个时间对象的时间差
	 * @param obj1 时间对象
	 * @param obj2 时间对此案
	 * @return 时间差毫秒数绝对值
	 */
	long getTimeDistance(Object obj1, Object obj2) {
	    long time1 = 0, time2 = 0;
	    if (obj1 instanceof Date) {
	        time1 = ((Date) obj1).getTime();
	    }
	    if (obj1 instanceof Long) {
	        time1 = (Long) obj1;
	    }
	    if (obj1 instanceof ReplacedObject) {
	        ReplacedObject ro = (ReplacedObject) obj1;
	        Object value = ro.getObject();
	        if (value instanceof Date) time1 = ((Date) value).getTime();
	        if (value instanceof Long) time1 = (Long) value;
	    }
	    if (obj2 instanceof Date) {
	        time2 = ((Date) obj2).getTime();
	    }
	    if (obj2 instanceof Long) {
	        time2 = (Long) obj2;
	    }
	    if (obj2 instanceof ReplacedObject) {
	        ReplacedObject ro = (ReplacedObject) obj2;
	        Object value = ro.getObject();
	        if (value instanceof Date) time2 = ((Date) value).getTime();
	        if (value instanceof Long) time2 = (Long) value;
	    }
	    return Math.abs(time1 - time2);
	}
	
	/**
	 * 获取多维度统计下满足相应条件的数量总和
	 * @param obj MergeableMapObject对象
	 * @param obj2 过滤方法名
	 * @param obj3 过滤阀值
	 * @return
	 */
	double getMergeableMapNumber(Object obj, Object obj2, Object obj3) {
	    double item = -1D;
	    if (obj instanceof MergeableMapObject) {
	        item = 0D;
	        double num = -1D;
	        String functionName = String.valueOf(obj2);
	        if (obj3 instanceof Number) {
	            num = Double.parseDouble(String.valueOf(obj3));
	        }
	        if (num != -1) {
	            MergeableMapObject mmo = (MergeableMapObject) obj;
	            for (Entry < Object, Object > entry: mmo.asMap().entrySet()) {
	                double objNum = getNumber(entry.getValue());
	                boolean result = false;
	                if ("isGreaterThan".equals(functionName)) {
	                    result = isGreaterThan(objNum, num);
	                }
	                if ("isGreaterThanOrEqualTo".equals(functionName)) {
	                    result = isGreaterThanOrEqualTo(objNum, num);
	                }
	                if ("isLessThan".equals(functionName)) {
	                    result = isLessThan(objNum, num);
	                }
	                if ("isLessThanOrEqualTo".equals(functionName)) {
	                    result = isLessThanOrEqualTo(objNum, num);
	                }
	                if (result)
	                    item++;
	            }
	        }
	    }
	    return item;
	}
	
	@Test
	public void test(){
		MergeableMapObject map = new MergeableMapObject("a", new CountNumber(1L));
		map.merge(new MergeableMapObject("a", new CountNumber(1L)));
		map.merge(new MergeableMapObject("a", new CountNumber(1L)));
		map.merge(new MergeableMapObject("a", new CountNumber(1L)));
		
		map.merge(new MergeableMapObject("b", new CountNumber(2L)));
		map.merge(new MergeableMapObject("b", new CountNumber(1L)));
		map.merge(new MergeableMapObject("b", new CountNumber(1L)));
		
		System.out.println(getMergeableMapNumber(map, "isGreaterThan", 2));
	}
	
	/**
	 * 判断是否在时间区间中，支持开闭区间和跨天参数，精确到小时
	 * @param obj 时间参数
	 * @param pattern 时间区间，如(6,16)或(22,8]
	 * @return
	 */
	boolean isInTimeRange(Object obj, String pattern){
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
		if(isless && !(hour == start || hour == end))
			result = !result;
		
		if(startInclude && hour == start)
			result = true;
		if(endInclude && hour == end)
			result = true;
		return result;
	}
	
	/**
	 * 是否以字符串x开头
	 * @param obj1 需要判断的对象
	 * @param obj2 特定字符
	 * @return
	 */
	boolean isStartWith(Object obj1, Object obj2) {
	    if (obj1 == null || obj2 == null)
	        return false;
	    if (obj1 instanceof ReplacedObject) {
	        ReplacedObject ro = (ReplacedObject) obj1;
	        Object valueObj = ro.getObject();
	        if (valueObj != null) {
	            return String.valueOf(valueObj).startsWith(String.valueOf(obj2));
	        }
	    }
	    if (obj1 instanceof String) {
	        return String.valueOf(obj1).startsWith(String.valueOf(obj2));
	    }
	    return false;
	}
	
	/**
	 * 获取x天前的当天0点时间错，用于时间起始点非当前时间的情况，但模板型条件界面上还没有改配置型，暂时没用
	 * @param dateObj 时间参数
	 * @param x 时间偏移量
	 * @return
	 */
	Date getXdaysAgo(Object dateObj, Object x){
		Calendar calendar = Calendar.getInstance();
		if(dateObj == null)
			calendar.setTime(new Date());
		if(dateObj instanceof Date)
			calendar.setTime((Date)dateObj);
		if(dateObj instanceof Long)
			calendar.setTimeInMillis((Long)dateObj);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DATE, -1*(new BigDecimal(String.valueOf(x)).intValue()-1));
		return calendar.getTime();
	}
	
	@Test
	public void ttttest(){
		System.out.println(getXdaysAgo(new Date(), 2));
	}
	
	/**
	 * 是否特殊值得整数倍
	 * @param obj 数值
	 * @param x 特殊值
	 * @return
	 */
	boolean isMultipleOfX(Object obj , int x){
		if(obj == null || x ==0)
			return false;
		
		return new Double(getNumber(obj)).longValue() % x == 0;
	}
	
	/**
	 * 是否特殊金额(之前产品提的),
	 * @param oAmount 金额数值
	 * @return 1001:true, 1200:false
	 */
	boolean isSpecialAmount(Object oAmount) {
	    if (oAmount == null || !(oAmount instanceof Long))
	        return (false);
	    // 单位转化成元
	    long lAmount = ((Long) oAmount) / 1000;
	    return Math.abs(lAmount - (lAmount / 1000) * 1000) < 100;
	}
	
	public CalcTask Task(){
		return CalcTaskBuilder.getInstance("同一用户最近一段时间的交易次数").selectMemCachedItems("用户", "TEST", true)
        .selectObjectType(AuditObject.class).objectKey(new Key() {
            public String key(Object obj) {
                return String.valueOf(((AuditObject) obj).get("user_id"));
            }
        }).expirePattern("2py").varDate(new Variable<Date>() {
            public Date select(Object obj) {
                return ((AuditObject) obj).getTransTime();
            }
        }).method(new cn.com.bsfit.frms.proc.base.calc.Method() {
            public Mergeable invoke(Object obj) {
                return new CountNumber(1L);
            }
        }).varKey("同一用户最近一段时间的交易次数").build();
	}
	
	@Test
	public void testTask() throws InterruptedException, ParseException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		CalcTask task = Task();
		
		List<Object> list = new ArrayList<Object>();
		
		// 造数据
		for(int i = 0; i<10; i++){
			AuditObject info = new AuditObject();
			info.setTransTime(sdf.parse("2016-12-15 09:57:10"));
			info.put("user_id", "1");
			list.add(info);
		}
		
		Map<Class<?>, List<Object>> map = new HashMap<Class<?>, List<Object>>();
		map.put(list.get(0).getClass(), list);
		
		// 计算脚本计算
		Set<MemCachedItem> set = task.calc(map);
		
		System.out.println(JSON.toJSONString(set, true));
		
		MemCachedItem item = null;
		for(MemCachedItem memCachedItem : set){
			if(item == null)
				item=memCachedItem;
			else {
				item.merge(memCachedItem);
			}
		}
		
		// 获取计算脚本计算结果
		TimedItems tt = (TimedItems) item.getRaw("同一用户最近一段时间的交易次数");

		System.out.println(JSON.toJSONString(getCacheItem(tt, new Date(), "1py"), true));
		
		// TODO 添加条件判断逻辑
	}
}
