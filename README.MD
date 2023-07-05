# Ex10 Exchange Recruitment Task

## Task Description

Implement a [MessageHandler](src/main/java/org/ex10/recruitment/base/MessageHandler.java) which
handles [Deposit](src/main/java/org/ex10/recruitment/base/Deposit.java)
event [Messages](src/main/java/org/ex10/recruitment/base/Message.java)

The implementation should be added the
skeleton [DepositMessageHandler](src/main/java/org/ex10/recruitment/DepositMessageHandler.java)

### Handling a Deposit

A deposit needs to be sent to an [ExternalSystem](src/main/java/org/ex10/recruitment/base/ExternalSystem.java), and
information must be stored about a deposit for later display to users. Persistence should be done using
a [DepositPersistence](src/main/java/org/ex10/recruitment/DepositPersistence.java) instance. For the purpose of this
task, we do not require an implementation of `DepositPersistence`, just adding the required methods to the interface
would suffice.

### Gotchas

1. Messages are delivered with at-least-once delivery guarantee.
2. The `ExternalSystem` is not idempotent, it is a critical need to ensure that a deposit is never sent twice.
3. If you wish to make use of a Transactional Persistence Layer, please make it clear what assumptions are made