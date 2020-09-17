package com.antra.evaluation.reporting_system;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.GenericValidator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class test {
    @Test
    public void test() {
        assertTrue(GenericValidator.isDate("2019-02-28", "yyyy-MM-dd", true));
        assertTrue(GenericValidator.isDate("2019-02-28 13:12:12", "yyyy-MM-dd HH:mm:ss", true));
        assertTrue(NumberUtils.isCreatable("1E10"));
    }
}
