package ex10.org.recruitment

import kotlin.test.assertEquals
import kotlin.test.assertFails
import org.ex10.recruitment.DepositMessageHandler
import org.ex10.recruitment.DepositPersistence
import org.ex10.recruitment.base.Deposit
import org.ex10.recruitment.base.ExternalSystem
import org.ex10.recruitment.base.Message
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.never
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class DepositMessageHandlerTest {

    @Spy
    var externalSystem: ExternalSystem = ExternalSystem()

    @Mock
    lateinit var depositPersistence: DepositPersistence

    val depositMessageHandler by lazy { DepositMessageHandler(externalSystem, depositPersistence) }

    @Test
    fun sendDeposit() { // happy path
        // arrange
        val deposit = Deposit("1", "Bob", 2L)
        val message = spy(Message(deposit, 10))

        // act
        depositMessageHandler.handleMessage(message)

        // assert
        val inOrder = inOrder(depositPersistence, externalSystem, message)
        inOrder.verify(depositPersistence).safeDepositIfNotExists(deposit)
        inOrder.verify(externalSystem).submitDeposit(deposit)
        inOrder.verify(message).acknowledge()
    }

    @Test
    fun sendMultipleDeposits() { // happy path
        // arrange
        val deposit1 = Deposit("1", "Bob", 2L)
        val message1 = spy(Message(deposit1, 10))
        val deposit2 = Deposit("2", "Alice", 3L)
        val message2 = spy(Message(deposit2, 11))

        // act
        depositMessageHandler.handleMessage(message1)
        depositMessageHandler.handleMessage(message2)

        // assert
        val inOrder = inOrder(depositPersistence, externalSystem, message1, message2)
        inOrder.verify(depositPersistence).safeDepositIfNotExists(deposit1)
        inOrder.verify(externalSystem).submitDeposit(deposit1)
        inOrder.verify(message1).acknowledge()
        inOrder.verify(depositPersistence).safeDepositIfNotExists(deposit2)
        inOrder.verify(externalSystem).submitDeposit(deposit2)
        inOrder.verify(message2).acknowledge()
    }

    @Test
    fun sendDepositWithFailedExternalService() {
        // arrange
        val deposit = Deposit("1", "Bob", 2L)
        val message = spy(Message(deposit, 10))

        `when`(externalSystem.submitDeposit(deposit))
            .thenThrow(RuntimeException("NetworkError"))
            .thenCallRealMethod()

        // act
        depositMessageHandler.handleMessage(message)

        // assert
        val inOrder = inOrder(depositPersistence, externalSystem, message)
        inOrder.verify(depositPersistence).safeDepositIfNotExists(deposit)
        inOrder.verify(externalSystem, times(2)).submitDeposit(deposit)
        inOrder.verify(message).acknowledge()
    }

    @Test
    fun sendDepositWithFailedExternalServiceWithMaxRetriesExceed() {
        // arrange
        val deposit = Deposit("1", "Bob", 2L)
        val message = spy(Message(deposit, 10))

        `when`(externalSystem.submitDeposit(deposit))
            .thenThrow(RuntimeException("NetworkError 1"))
            .thenThrow(RuntimeException("NetworkError 2"))
            .thenThrow(RuntimeException("NetworkError 3"))
            .thenThrow(RuntimeException("NetworkError 4"))
            .thenThrow(RuntimeException("NetworkError 5"))

        // act
        val e = assertFails {
            depositMessageHandler.handleMessage(message)
        }

        // assert
        assertEquals(e.message, "NetworkError 5")
        val inOrder = inOrder(depositPersistence, externalSystem, message)
        inOrder.verify(depositPersistence).safeDepositIfNotExists(deposit)
        inOrder.verify(externalSystem, times(5)).submitDeposit(deposit)
        inOrder.verify(message, never()).acknowledge()
    }

    @Test
    fun sendDepositWithFailedPersistence() {
        // arrange
        val deposit = Deposit("1", "Bob", 2L)
        val message = spy(Message(deposit, 10))

        `when`(depositPersistence.safeDepositIfNotExists(deposit))
            .thenThrow(RuntimeException("DatabaseError"))
            .thenAnswer { }

        // act
        depositMessageHandler.handleMessage(message)

        // assert
        val inOrder = inOrder(depositPersistence, externalSystem, message)
        inOrder.verify(depositPersistence, times(2)).safeDepositIfNotExists(deposit)
        inOrder.verify(externalSystem).submitDeposit(deposit)
        inOrder.verify(message).acknowledge()
    }

    @Test
    fun sendDepositWithFailedPersistenceWithMaxRetriesExceed() {
        // arrange
        val deposit = Deposit("1", "Bob", 2L)
        val message = spy(Message(deposit, 10))

        `when`(depositPersistence.safeDepositIfNotExists(deposit))
            .thenThrow(RuntimeException("DatabaseError 1"))
            .thenThrow(RuntimeException("DatabaseError 2"))
            .thenThrow(RuntimeException("DatabaseError 3"))
            .thenThrow(RuntimeException("DatabaseError 4"))
            .thenThrow(RuntimeException("DatabaseError 5"))
            .thenAnswer { }

        // act
        val e = assertFails {
            depositMessageHandler.handleMessage(message)
        }

        // assert
        assertEquals(e.message, "DatabaseError 5")
        val inOrder = inOrder(depositPersistence, externalSystem, message)
        inOrder.verify(depositPersistence, times(5)).safeDepositIfNotExists(deposit)
        inOrder.verify(externalSystem, never()).submitDeposit(deposit)
        inOrder.verify(message, never()).acknowledge()
    }
}
