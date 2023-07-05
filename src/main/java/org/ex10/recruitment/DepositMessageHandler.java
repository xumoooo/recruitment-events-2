package org.ex10.recruitment;

import org.ex10.recruitment.base.Deposit;
import org.ex10.recruitment.base.ExternalSystem;
import org.ex10.recruitment.base.Message;
import org.ex10.recruitment.base.MessageHandler;

public class DepositMessageHandler extends MessageHandler<Deposit> {

    private final ExternalSystem externalSystem;
    private final DepositPersistence depositPersistence;

    public DepositMessageHandler(ExternalSystem externalSystem, DepositPersistence depositPersistence) {
        this.externalSystem = externalSystem;
        this.depositPersistence = depositPersistence;
    }

    @Override
    public void handleMessage(Message<Deposit> message) {

    }
}
