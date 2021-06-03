package grails.tests.interceptors

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class AuditEventInterceptorSpec extends Specification implements InterceptorUnitTest<AuditEventInterceptor> {

    def setup() {
    }

    def cleanup() {

    }

    void "Test auditEvent interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"beer")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}
