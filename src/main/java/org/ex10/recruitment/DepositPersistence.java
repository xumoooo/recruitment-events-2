package org.ex10.recruitment;

import org.ex10.recruitment.base.Deposit;

public interface DepositPersistence {
    // add methods as needed
    void safeDepositIfNotExists(Deposit deposit); // idempotent by id
}
