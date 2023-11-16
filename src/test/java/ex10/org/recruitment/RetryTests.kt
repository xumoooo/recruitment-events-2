package ex10.org.recruitment

import kotlin.test.assertEquals
import kotlin.test.assertFails
import org.ex10.recruitment.base.RetryUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`

class RetryTests {

    @Test
    fun retryWithNegativeDelay() {
        // arrange
        val action = mock(Runnable::class.java)
        `when`(action.run())
            .thenAnswer { }

        // act
        val e = assertThrows<AssertionError> {
            RetryUtils.retryWithDelay(3, -1, action)
        }

        // assert
        Mockito.verify(action, never()).run()
    }

    @Test
    fun retry() {
        // arrange
        val action = mock(Runnable::class.java)
        `when`(action.run())
            .thenThrow(RuntimeException("Exception 1"))
            .thenThrow(RuntimeException("Exception 2"))
            .thenAnswer { }

        // act
        RetryUtils.retryWithDelay(3, 0, action)

        // assert
        Mockito.verify(action, times(3)).run()
    }

    @Test
    fun retryFailed() {
        // arrange
        val action = mock(Runnable::class.java)
        `when`(action.run())
            .thenThrow(RuntimeException("Exception 1"))
            .thenThrow(RuntimeException("Exception 2"))

        // act
        val e = assertFails {
            RetryUtils.retryWithDelay(2, 0, action)
        }

        // assert
        assertEquals(e.message, "Exception 2")
    }
}