package grails.tests.interceptors

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class SecurityHeadersInterceptorSpec extends Specification implements InterceptorUnitTest<SecurityHeadersInterceptor> {

    def setup() {
    }

    def cleanup() {

    }

    void "Test securityHeaders interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"securityHeaders")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}
