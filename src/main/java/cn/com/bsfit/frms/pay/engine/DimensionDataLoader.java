package cn.com.bsfit.frms.pay.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import cn.com.bsfit.frms.base.load.EngineLoader;
import cn.com.bsfit.frms.base.load.LoadTask;
import cn.com.bsfit.frms.obj.AuditObject;
import cn.com.bsfit.frms.obj.MemCachedItem;

public class DimensionDataLoader extends RedisBaseNoSqlLoader implements EngineLoader {
    @Value("${frms.engine.biz.code:BR_VAL}")
    private String bizCode;
    private Logger logger = LoggerFactory.getLogger(DimensionDataLoader.class);

    @Value("${frms.engine.threadSize:8}")
    private int coreThreadSize;

    @Override
    public List<LoadTask> getTask(Object... objects) throws IOException {
        if (objects == null || objects.length < 1) {
            return Collections.emptyList();
        }
        List<AuditObject> aos = new ArrayList<AuditObject>();
        for (Object obj : objects) {
            if (obj == null)
                continue;
            if (obj instanceof AuditObject) {
                AuditObject auditObj = (AuditObject) obj;
                if (logger.isDebugEnabled())
                    logger.debug("AuditObject:{}", auditObj);

                if ("0".equals(auditObj.get("frms_trans_time")))
                    auditObj.setTransTime(new Date());
                if (StringUtils.isEmpty(auditObj.get("frms_biz_code"))) {
                    auditObj.put("frms_biz_code", bizCode);
                }

                aos.add(auditObj);
            }
        }

        List<LoadTask> loadTaskList = new ArrayList<LoadTask>(1);
        loadTaskList.add(cacheLoadTask(aos));

        return loadTaskList;
    }

    private LoadTask cacheLoadTask(final List<AuditObject> aos) {
        return new LoadTask() {
            @Override
            public List<? extends Object> call() throws Exception {
                List<MemCachedItem> items = new ArrayList<MemCachedItem>();
                if (aos != null) {

                    long t0 = System.currentTimeMillis();
                    final Map<String, byte[]> memMap = new HashMap<String, byte[]>();
                    Object ip, dfp, uuid, aid;
                    byte[] item;
                    for (AuditObject ao : aos) {
                        ip = ao.get("ip");
                        dfp = ao.get("dfp");
                        uuid = ao.get("uuid");
                        aid = ao.get("authId");
                        if (!StringUtils.isEmpty(ip)) {
                        	String key = new MemCachedItem(ip.toString(), "IP", bizCode, 10L).getMemCachedKey();
                            item = getMemCachedItem(key);
                            if(item != null)
                            	memMap.put(key, item);
                        }

                        if (!StringUtils.isEmpty(dfp)) {
                        	String key = new MemCachedItem(dfp.toString(), "DFP", bizCode, 10L).getMemCachedKey();
                            item = getMemCachedItem(key);
                            if(item != null)
                            	memMap.put(key, item);
                        }
                        
                        if (!StringUtils.isEmpty(uuid) && !StringUtils.isEmpty(aid)) {
                        	String key = new MemCachedItem(uuid.toString()+"-"+aid.toString(), "UUID_AID", bizCode, 10L)
                        						.getMemCachedKey();
                            item = getMemCachedItem(key);
                            if(item != null)
                            	memMap.put(key, item);
                        }
                    }

                    long t1 = System.currentTimeMillis();
                    long cacheSize = 0;
                    int size = 0;
                    for(String key : memMap.keySet()){
                    	size++;
                    	byte[] bts = memMap.get(key);
                    	cacheSize += bts.length;
                    	items.add(valueSerializer.deserialize(bts));
                    }

                    long t2 = System.currentTimeMillis();

                    logger.info("total:[{}]ms, read:[{}]ms, des:[{}]ms, {} byts of {} items", t2 - t0, t1 - t0, t2 - t1,
                        cacheSize, size);
                }

                return items;
            }
        };
    }

    @Override
    public Collection<String> memCachedKeys(AuditObject auditobject) {
        return null;
    }
}
