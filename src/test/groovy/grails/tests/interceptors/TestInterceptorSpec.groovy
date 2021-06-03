package grails.tests.interceptors

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class TestInterceptorSpec extends Specification implements InterceptorUnitTest<TestInterceptor> {

    def setup() {
    }

    def cleanup() {

    }

    void "Test test interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"test")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}
