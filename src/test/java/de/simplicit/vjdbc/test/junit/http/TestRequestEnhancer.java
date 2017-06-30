package de.simplicit.vjdbc.test.junit.http;

import de.simplicit.vjdbc.servlet.RequestEnhancer;
import de.simplicit.vjdbc.servlet.RequestModifier;

public class TestRequestEnhancer implements RequestEnhancer {
    public void enhanceConnectRequest(RequestModifier requestModifier) {
        requestModifier.addRequestHeader("connect-test-property", "connect-test-value");
    }

    public void enhanceProcessRequest(RequestModifier requestModifier) {
        requestModifier.addRequestHeader("process-test-property", "process-test-value");
    }
}
