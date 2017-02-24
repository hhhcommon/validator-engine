package cn.com.bsfit.frms.pay.engine.loader;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import cn.com.bsfit.frms.base.load.EngineLoader;
import cn.com.bsfit.frms.base.load.LoadTask;
import cn.com.bsfit.frms.obj.AuditObject;
import cn.com.bsfit.frms.obj.MemCachedItem;
import cn.com.bsfit.frms.serial.MemCachedItemUtils;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.BatchPolicy;
import com.aerospike.client.policy.Replica;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoPool;

public class DimensionDataLoader implements EngineLoader {
    @Value("${frms.engine.biz.code:BR_VAL}")
    private String bizCode;
    private Logger logger = LoggerFactory.getLogger(DimensionDataLoader.class);

    @Value("${frms.engine.threadSize:8}")
    private int coreThreadSize;
    
    private @Value("${frms.common.aerospike.ns:bsfit}") String defaultNs;
    private @Value("${frms.common.aerospike.set:frms}") String defaultSet;
    
    @Autowired
    private AerospikeClient aerospikeClient;
    
    @Autowired
    private KryoPool kryoPool;

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
                    final Map<String, MemCachedItem> memMap = new HashMap<String, MemCachedItem>();
                    Object ip, dfp, uuid, aid;
                    for (AuditObject ao : aos) {
                        ip = ao.get("ip");
                        dfp = ao.get("dfp");
                        uuid = ao.get("uuid");
                        aid = ao.get("authId");
                        if (!StringUtils.isEmpty(ip)) {
                        	MemCachedItem item = new MemCachedItem(ip.toString(), "IP", bizCode, 10L);
                        	String key = item.getMemCachedKey();
                            if(item != null)
                            	memMap.put(key, item);
                        }

                        if (!StringUtils.isEmpty(dfp)) {
                        	MemCachedItem item = new MemCachedItem(dfp.toString(), "DFP", bizCode, 10L);
                        	String key = item.getMemCachedKey();
                            if(item != null)
                            	memMap.put(key, item);
                        }
                        
                        if (!StringUtils.isEmpty(uuid) && !StringUtils.isEmpty(aid)) {
                        	MemCachedItem item = new MemCachedItem(uuid.toString()+"-"+aid.toString(), "UUID_AID", bizCode, 10L);
                        	String key = item.getMemCachedKey();
                            if(item != null)
                            	memMap.put(key, item);
                        }
                    }
                    
                    Key[] keys = new Key[memMap.size()];
                    int i = 0;
                    for (String str : memMap.keySet()) {
                        keys[i++] = new Key(defaultNs, defaultSet, str);
                    }
                    BatchPolicy batchPolicy = new BatchPolicy();
                    batchPolicy.replica = Replica.MASTER_PROLES;
                    Record[] records = aerospikeClient.get(batchPolicy, keys);

                    long t1 = System.currentTimeMillis();

                    MemCachedItem tempItem;
                    Kryo kryo = kryoPool.borrow();
                    long cacheSize = 0;
                    int size = 0;
                    try {
                        for (Record record : records) {
                            if (record != null) {
                                size++;
                                tempItem = MemCachedItemUtils.asMemCachedItem(record, kryo);
                                memMap.put(tempItem.getMemCachedKey(), tempItem);
                                for (Object obj : record.bins.values()) {
                                    if (obj instanceof byte[]) {
                                        cacheSize += ((byte[]) obj).length;
                                    }
                                }
                            }
                        }
                    } finally {
                        kryoPool.release(kryo);
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
