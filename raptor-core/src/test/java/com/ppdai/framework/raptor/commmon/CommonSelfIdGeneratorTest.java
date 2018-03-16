package com.ppdai.framework.raptor.commmon;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ppdai.framework.raptor.common.CommonSelfIdGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;

public class CommonSelfIdGeneratorTest {

    @Test
    public void testThreadSafe() throws InterruptedException, ExecutionException {
        int nthread = 10;
        int idPerThread = 1000;

        CommonSelfIdGenerator commonSelfIdGenerator = new CommonSelfIdGenerator();


        CountDownLatch countDownLatch  = new CountDownLatch(nthread);

        Callable<List<Long>> generate = () -> {
            ArrayList<Long> ids = Lists.newArrayList();
            countDownLatch.countDown();
            countDownLatch.await();
            for (int i = 0; i < idPerThread; i++) {
                long l = commonSelfIdGenerator.generateId();
                ids.add(l);
            }
            return ids;
        };

        ExecutorService executorService = Executors.newFixedThreadPool(nthread);
        List<Future<List<Long>>> futureList = Lists.newArrayList();
        for (int i = 0; i < nthread; i++) {
            Future<List<Long>> futureResult = executorService.submit(generate);
            futureList.add(futureResult);
        }
        countDownLatch.await();

        HashSet<Long> allIds = Sets.newHashSet();
        for (Future<List<Long>> result : futureList) {
            List<Long> longs = result.get();
            allIds.addAll(longs);
        }

        Assert.assertTrue(allIds.size() == nthread * idPerThread);


    }




}
