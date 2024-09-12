package org.xu.pan.bloom.filter.local.test;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xu.pan.bloom.filter.core.BloomFilter;
import org.xu.pan.bloom.filter.local.LocalBloomFilterManager;
import org.xu.pan.core.constants.YPanConstants;

@SpringBootTest(classes = LocalBloomFilterTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootApplication(scanBasePackages = YPanConstants.BASE_COMPONENT_SCAN_PATH + ".bloom.filter.local")
@Slf4j
public class LocalBloomFilterTest {

    @Autowired
    private LocalBloomFilterManager manager;

    /**
     * 测试本地布隆过滤器
     */
    @Test
    public void localBloomFilterTest() {
        BloomFilter<Integer> bloomFilter = manager.getFilter("test");
        long failNum = 0L;
        for (int i = 0; i < 1000000; i++) {
            bloomFilter.put(i);
        }
        for (int i = 1000000; i < 1100000; i++) {
            boolean result = bloomFilter.mightContain(i);
            if (result) {
                failNum++;
            }
        }
        log.info("test num {}, fail num {}", 1000000, failNum);
    }

}
