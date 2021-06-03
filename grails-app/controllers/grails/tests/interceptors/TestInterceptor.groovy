package grails.tests.interceptors


class TestInterceptor {

    boolean before() { true }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
