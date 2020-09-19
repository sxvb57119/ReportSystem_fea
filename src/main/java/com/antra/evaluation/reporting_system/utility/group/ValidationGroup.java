package com.antra.evaluation.reporting_system.utility.group;

import javax.validation.GroupSequence;

@GroupSequence({ValidationFirst.class, ValidationSecond.class})
public interface ValidationGroup {
}
