package grails.tests.interceptors

import grails.tests.FilterConfig
import groovy.transform.CompileStatic
import grails.tests.InterceptorMultiFiltersExtension

@CompileStatic
class SecurityInterceptor extends InterceptorMultiFiltersExtension {

    SecurityInterceptor() {
        // the maximum scope of the interceptor
        matchAll()

        // sub filters, execute by sequence
        def filter1 = new FilterConfig(
                matcher: toMatcher(controller: 'beer', this).excludes(controller: 'beer', action: 'list'),
                before: { isMatch ->
                    println('filterConfig1 before')

                    if (isMatch) {
                        println('filterConfig1 blocked')
                        return false
                    }

                    println('filterConfig1 continue')
                }
        )

        def filter2 = new FilterConfig(
                matcher: toMatcher(uri: '/**', this),
                before: { isMatch ->
                    println('filterConfig2 before')
                    println('filterConfig1 continue')
                }
        )

        super.addFilters([filter1, filter2])
    }

    boolean before() {
        super.before(request)
    }

    boolean after() {
        super.after(request)
    }
}
