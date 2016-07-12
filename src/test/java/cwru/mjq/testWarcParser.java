package cwru.mjq;


import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;


public class testWarcParser {

    @Test
    public void test_randomSample() {
        List<Integer> s = WarcParser.randomSample(100, 10);
        assertThat(s.size(), is(10));
        for (int i : s) {
            assertThat(i, lessThan(100));
            assertThat(i, greaterThan(-1));
        }
        for (int i = 0; i < s.size(); i++) {
            int n = s.get(i);
            for (int j = 0; j < s.size(); j++) {
                if (i == j) {
                    continue;
                }
                int m = s.get(j);
                assertThat(n, not(is(m)));
            }
        }
    }

    @Test
    public void test_randomSample_bigSample() {
        List<Integer> s = WarcParser.randomSample(10, 100);
        assertThat(s.size(), is(10));
    }

    @Test(expected=RuntimeException.class)
    public void test_randomSample_outOfBoundsPop() {
        WarcParser.randomSample(-10, 100);
    }

    @Test(expected=RuntimeException.class)
    public void test_randomSample_outOfBoundsSample() {
        WarcParser.randomSample(100, -100);
    }
}
