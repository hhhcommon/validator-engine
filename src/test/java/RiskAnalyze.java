import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.bsfit.frms.obj.CountNumber;
import cn.com.bsfit.frms.obj.MergeableMapObject;
import cn.com.bsfit.frms.obj.MergeableNumber;
import cn.com.bsfit.frms.obj.VarsNumber;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class RiskAnalyze {
	
	public static long analyzeTs(String timeString, long timestamp) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		String tsString = sdf.format(new Date(timestamp));
//		timeString = tsString.split(" ")[0] +" "+ timeString;
		
		long logTime = sdf.parse(timeString).getTime();
		return Math.abs(timestamp - logTime);
	}
	
	public static Map<String, Integer> getMaxSamePosCount(Object posObj){
		List positions = null;
		if(posObj instanceof List)
			positions = (List)posObj;
		if(positions == null)
			return null;
		
		Map resultMap = new HashMap();
		
		MergeableMapObject xmap = new MergeableMapObject();
		MergeableMapObject ymap = new MergeableMapObject();
		MergeableMapObject posMap = new MergeableMapObject();
		Double maxJumpX = 0D;
		Double maxJumpY = 0D;
		Double tmpX = -1D;
		Double tmpY = -1D;
		for (int i = 0; i < positions.size(); i++) {
			String[] posXY = ((String)positions.get(i)).split(",");
			double X = Double.parseDouble(posXY[0]);
			double Y = Double.parseDouble(posXY[1]);
			xmap.merge(new MergeableMapObject(X, new CountNumber(1L)));
			ymap.merge(new MergeableMapObject(Y, new CountNumber(1L)));
			posMap.merge(new MergeableMapObject(positions.get(i), new CountNumber(1L)));
			if(tmpX != -1){
				double tmp = Math.abs(X -tmpX);
				maxJumpX = tmp>maxJumpX?tmp:maxJumpX;
				tmpX = X;
			}
			if(tmpY != -1){
				double tmp = Math.abs(Y -tmpY);
				maxJumpY = tmp>maxJumpY?tmp:maxJumpY;
				tmpY = Y;
			}
			if(tmpX == -1)
				tmpX = X;
			if(tmpY == -1)
				tmpY = Y;
		}
		int maxX = getMaxNumberOfMergeableMap(xmap);
		int maxY = getMaxNumberOfMergeableMap(ymap);
		int maxpos = getMaxNumberOfMergeableMap(posMap);
		
		resultMap.put("x", maxX);
		resultMap.put("y", maxY);
		resultMap.put("pos", maxpos);
		resultMap.put("jx", maxJumpX.intValue());
		resultMap.put("jy", maxJumpY.intValue());
		return resultMap;
	}
	
	public static int getMaxNumberOfMergeableMap(MergeableMapObject mapObject){
		int max = 0;
		for(Object object : mapObject.asMap().keySet()){
			Object valueObject = mapObject.get(object);
			if(valueObject instanceof Number){
				int num = ((Number)valueObject).intValue();
				max = num>max?num:max;
			}
		}
		return max;
	}
	
	public static VarsNumber mergeSpeedVarsNumber(Object posObj, Object timeObj){
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
			long last_X = new Double(last_pos.split(",")[0]).longValue();
			long cur_X = new Double(cur_pos.split(",")[0]).longValue();
			if((((Number)timePixs.get(i)).doubleValue() - ((Number)timePixs.get(i - 1))
							.doubleValue()) != 0)
				var_speed.merge(new VarsNumber(1000
					* Math.abs((double) cur_X - last_X)
					/ (((Number)timePixs.get(i)).doubleValue() - ((Number)timePixs.get(i - 1))
							.doubleValue())));
		}
		return var_speed;
	}
	
	public static VarsNumber mergePositionVarsNumber(Object obj){
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
	
	public static int getContinuousBackCont(Object obj, int limit, int backPix){
		List positions = null;
		if(obj instanceof List)
			positions = (List)obj;
		if(positions == null)
			return -1;
		int count = 0;
		boolean inc = true;
		double turnPoint = 0;
		double tmpCount = 0;
		boolean skip = false;
		for(int i = 1; i< positions.size()-limit; i++){
			String[] posXY = ((String)positions.get(i)).split(",");
			double X = Double.parseDouble(posXY[0]);
			String[] lastXY = ((String)positions.get(i-1)).split(",");
			double lastX = Double.parseDouble(lastXY[0]);
			
			if(X == lastX)
				continue;
			
			if(inc == (X>lastX)){
				if(Math.abs(X-lastX) >= backPix){
					tmpCount++;
				}else if(!skip && tmpCount < limit){
					tmpCount = 0;
				}
				if(tmpCount == limit){
					tmpCount++;
					count++;		//System.err.println(i+" "+X);
					skip = true;
				}
			}else {
				inc = X>lastX;
				turnPoint = lastX;
				tmpCount = 0;
				skip = false;
			}
		}
		return count;
	}
	
	public static double getSlideDis(Object obj){
		List positions = null;
		if(obj instanceof List)
			positions = (List)obj;
		if(positions == null)
			return -1;
		if(positions.size() <= 1)
			return -1;
		
		String start = ((String) positions.get(0)).split(",")[0];
		String end = ((String) positions.get(positions.size()-1)).split(",")[0];
		
		return Math.abs(new Double(start) - new Double(end));
	}
	
	public static void analyze(String timeString, JSONObject object, OutputStream writer) throws ParseException, IOException{
		long time_span = analyzeTs(timeString, object.getLongValue("timestamp"));
		int tryTime = object.getIntValue("tryTimes");
		int timeCost = object.getIntValue("timeCost");
		int respTime = object.getIntValue("respTime");
		List posList = Arrays.asList(object.getJSONArray("positions").toArray());
		List timeList = Arrays.asList(object.getJSONArray("timePixs").toArray());
		VarsNumber var_pos = mergePositionVarsNumber(posList);
		VarsNumber var_speed = mergeSpeedVarsNumber(posList, timeList);
		int maxX = getMaxSamePosCount(posList).get("x");
		int maxY = getMaxSamePosCount(posList).get("y");
		int maxPos = getMaxSamePosCount(posList).get("pos");
		int jx = getMaxSamePosCount(posList).get("jx");
		int jy = getMaxSamePosCount(posList).get("jy");
		int bc = getContinuousBackCont(posList, 5, 1);
		double slideDis = getSlideDis(posList);
		int randomX = object.getIntValue("randomX");
		
		String max = maxX+"\t"+maxY+"\t"+maxPos+"\t"+jx+"\t"+jy+"\t"+bc+"\t"+slideDis+"\t"+randomX;
		writer.write((time_span+"\t"+tryTime+"\t"+timeCost+"\t"+respTime+"\t"+
				var_pos.doubleValue()+"\t"+var_speed.doubleValue()+"\t"+max).getBytes("UTF-8"));
		
	}
	
	public static String getString(String line, String pattern){
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(line);
		if(matcher.find()){
			return matcher.group(1);
		}
		return "";
	}

	public static void main(String[] args) throws ParseException, IOException {
		Scanner scanner = new Scanner(new File("C:\\cygwin64\\home\\cxy\\slide\\tmp.log"), "UTF-8");	
		File file = new File("C:\\cygwin64\\home\\cxy\\slide\\risk-1-2(2).xls");
//		if(!file.exists())
//			file.createNewFile();
//		else {
//			file.delete();
//			file.createNewFile();
//		}
//		OutputStream out = new FileOutputStream(file, true);
		OutputStream out = System.out;
		out.write(("line\t"+"time_span"+"\t"+"tryTime"+"\t"+"timeCost"+"\t"+"respTime"+"\t"+
				"var_pos"+"\t"+"var_speed\tmaxSameX\tmaxSameY\tmaxSamePos\tmaxJumpX\tmaxJumpY\tcbc\tslideDis\trandomX\trule").getBytes());
		int lineNo = 0;
		String line= "";
		while (scanner.hasNext()) {
			try {
				line = scanner.nextLine();
				lineNo++;
				String timeString = getString(line, "(\\d+\\-\\d+\\-\\d+ \\d+:\\d+:\\d+\\.\\d+)");
				JSONObject object = JSON.parseObject(getString(line, "(\\{.*\\})"));
				if(object == null)
					throw new Exception("risk line");
				out.write(("\n"+lineNo+"\t").getBytes());
				analyze(timeString, object, out);
//				if(lineNo >= 2)
//					break;
			} catch (Exception e) {
				if("risk line".equals(e.getMessage()) && 
						line.matches("\\d+\\-\\d+\\-\\d+ \\d+:\\d+:\\d+\\.\\d+ : \\[.*\\]"))
					out.write(("\t"+getString(line, "(\\[.*\\])")).getBytes("UTF-8"));
				else {
					System.err.println(lineNo + " line analyze failed with lineString: "+ line);
				}
			}
//			break;
		}
		scanner.close();
	}
}
