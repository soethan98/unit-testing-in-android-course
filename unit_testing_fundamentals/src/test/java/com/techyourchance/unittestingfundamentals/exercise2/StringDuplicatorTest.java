package com.techyourchance.unittestingfundamentals.exercise2;

import com.sun.org.apache.xpath.internal.operations.Bool;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringDuplicatorTest {

    StringDuplicator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new StringDuplicator();
    }

    @Test
    public void duplicate_emptyString_emptyStringReturned() {
        String value = SUT.duplicate("");
        assertThat(value, is(""));
    }

    @Test
    public void duplicate_singleString_stringReturned() {
        String value = SUT.duplicate("a");
        assertThat(value, is("aa"));
    }

    @Test
    public void duplicate_longString_duplicatedStringReturned() throws Exception {
        String result = SUT.duplicate("Soe Than");
        assertThat(result, is("Soe ThanSoe Than"));
    }
}