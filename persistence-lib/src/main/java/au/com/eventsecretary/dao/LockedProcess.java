package au.com.eventsecretary.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LockedProcess {
    private Logger logger = LoggerFactory.getLogger(LockedProcess.class);

    private static class Lock {
        int count;
    }
    private Map<String, Lock> locks = new HashMap<>();

    public void process(String name, String lockId, Runnable process) {
        Object lock = getLock(lockId);
        long lockTime = System.currentTimeMillis();
        try {
            synchronized (lock) {
                if (System.currentTimeMillis() - lockTime > 10) {
                    logger.info("{}:Wait:{}", name, System.currentTimeMillis() - lockTime);
                }
                long start = System.currentTimeMillis();
                process.run();
                logger.info("{}:Duration:{}", name, System.currentTimeMillis() - start);
            }
        } finally {
            removeLock(lockId);
        }
    }

    private synchronized void removeLock(String lockId) {
        Lock lock = locks.get(lockId);
        if (lock == null) {
            return;
        }
        if (--lock.count == 0) {
            locks.remove(lockId);
        }
    }

    private synchronized Object getLock(String lockId) {
        Lock existing = locks.get(lockId);
        if (existing == null) {
            locks.put(lockId, existing = new Lock());
        }
        existing.count++;
        return existing;
    }
}
