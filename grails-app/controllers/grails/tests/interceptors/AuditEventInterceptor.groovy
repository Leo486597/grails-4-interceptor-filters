package grails.tests.interceptors

class AuditEventInterceptor {

    AuditEventInterceptor(){
        matchAll()
    }

    boolean before() {
        println('AuditEventInterceptor before')
        true
    }

    boolean after() {
        println('AuditEventInterceptor after')
        true
    }
}


