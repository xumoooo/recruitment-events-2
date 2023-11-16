package org.ex10.recruitment.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryUtils {
    private final static Logger logger = LoggerFactory.getLogger(RetryUtils.class);

    public static void retryWithDelay(int maxAttempts, long delay, Runnable action) { // todo(egor): add backoff in future
        assert delay >= 0;

        for (int attempt = 1; attempt <= maxAttempts; ++attempt) {
            try {
                action.run();
                break;
            } catch (Exception e) {
                if (attempt == maxAttempts) {
                    throw e;
                }
                logger.error("Attempt failed", e);
                try {
                    if (delay > 0) {
                        Thread.sleep(delay); // use kotlin coroutines in future?
                    }

                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
