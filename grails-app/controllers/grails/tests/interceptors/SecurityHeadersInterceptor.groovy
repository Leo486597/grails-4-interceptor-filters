package grails.tests.interceptors

class SecurityHeadersInterceptor {

    SecurityHeadersInterceptor(){
        match(uri: '/beer/**')
    }

    boolean before() {
        println('SecurityHeadersInterceptor before')
        true
    }

    boolean after() {
        println('SecurityHeadersInterceptor after')
        true
    }
}
