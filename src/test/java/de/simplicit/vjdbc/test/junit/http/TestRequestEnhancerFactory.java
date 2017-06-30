package de.simplicit.vjdbc.test.junit.http;

import de.simplicit.vjdbc.servlet.RequestEnhancer;
import de.simplicit.vjdbc.servlet.RequestEnhancerFactory;

public class TestRequestEnhancerFactory implements RequestEnhancerFactory {
    public RequestEnhancer create() {
        return new TestRequestEnhancer();
    }
}
