package com.techyourchance.unittestingfundamentals.exercise1;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class NegativeNumberValidatorTest {
    NegativeNumberValidator SUT;

    @Before
    public void setup() {
        SUT = new NegativeNumberValidator();
    }

    @Test
    public void test1() {
        Boolean isNegative = SUT.isNegative(-1);
        Assert.assertThat(isNegative, is(true));
    }

    @Test
    public void test2() {
        Boolean isNegative = SUT.isNegative(0);
        Assert.assertThat(isNegative, is(false));
    }

    @Test
    public void test3() {
        Boolean isNegative = SUT.isNegative(1);
        Assert.assertThat(isNegative, is(false));
    }
}