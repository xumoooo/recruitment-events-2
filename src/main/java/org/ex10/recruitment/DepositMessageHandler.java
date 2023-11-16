package org.ex10.recruitment;

import org.ex10.recruitment.base.Deposit;
import org.ex10.recruitment.base.ExternalSystem;
import org.ex10.recruitment.base.Message;
import org.ex10.recruitment.base.MessageHandler;
import org.ex10.recruitment.base.RetryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepositMessageHandler extends MessageHandler<Deposit> {

    private final static Logger logger = LoggerFactory.getLogger(RetryUtils.class);

    private final ExternalSystem externalSystem;
    private final DepositPersistence depositPersistence;

    private final long persitenceRetryDelay = 100; //ms
    private final long externalSystemRetryDelay = 500; //ms

    public DepositMessageHandler(ExternalSystem externalSystem, DepositPersistence depositPersistence) {
        this.externalSystem = externalSystem;
        this.depositPersistence = depositPersistence;
    }

    @Override
    public void handleMessage(Message<Deposit> message) {
        logger.info("Try process deposit. Offset: " + message.offset() + " Deposit: " + message.event());

        RetryUtils.retryWithDelay(5, persitenceRetryDelay, () -> {
            depositPersistence.safeDepositIfNotExists(message.event());
        });

        RetryUtils.retryWithDelay(5, externalSystemRetryDelay, () -> {
            externalSystem.submitDeposit(message.event());
        });

        message.acknowledge();
    }
}
