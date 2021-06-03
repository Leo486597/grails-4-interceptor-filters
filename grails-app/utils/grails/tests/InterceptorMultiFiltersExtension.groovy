package grails.tests

import grails.interceptors.Matcher
import grails.web.mapping.UrlMappingInfo
import groovy.transform.CompileStatic
import org.grails.plugins.web.interceptors.UrlMappingMatcher
import org.grails.web.mapping.mvc.UrlMappingsHandlerMapping
import javax.servlet.http.HttpServletRequest
import java.util.concurrent.ConcurrentLinkedDeque
import grails.artefact.Interceptor

/**
 * Lifecycle method names enum for a filter
 */
@CompileStatic
enum FilterLifeCycleNameEnum {
    before,
    after,
    afterView
}

/**
 * Filter config model
 */
@CompileStatic
class FilterConfig {
    Matcher matcher
    Closure before
    Closure after
    Closure afterView
}

/**
 * A base class for interceptor to support multi filters feature like grails 2
 */
@CompileStatic
abstract class InterceptorMultiFiltersExtension {
    // using a concurrent deque to handle thread safe forward and backward linkedList traverse
    private ConcurrentLinkedDeque<FilterConfig> filters =
            new ConcurrentLinkedDeque<FilterConfig>()

    /**
     * Convert the filter conditions to an UrlMappingMatcher object
     * @param arguments the filter conditions
     * @param interceptor the current interceptor
     * @return matcher an UrlMappingMatcher object
     */
    static Matcher toMatcher(Map arguments, Interceptor interceptor) {
        def matcher = new UrlMappingMatcher(interceptor)
        matcher.matches(arguments)
        return matcher
    }

    /**
     * Determine if the request match with the matcher
     * @param request HttpServletRequest
     * @param matcher Matcher
     * @return Whether the request does match the matcher
     * @link Interceptor
     */
    static boolean doesMatch(HttpServletRequest request, Matcher matcher) {
        HttpServletRequest req = request
        String ctxPath = req.contextPath
        String uri = req.requestURI
        String noCtxUri = uri - ctxPath
        boolean checkNoCtxUri = ctxPath && uri.startsWith(ctxPath)

        def matchedInfo = request.getAttribute(UrlMappingsHandlerMapping.MATCHED_REQUEST)

        UrlMappingInfo grailsMappingInfo = (UrlMappingInfo) matchedInfo

        boolean matchUri = matcher.doesMatch(uri, grailsMappingInfo, req.method)
        boolean matchNoCtxUri = matcher.doesMatch(noCtxUri, grailsMappingInfo, req.method)

        if (matcher.isExclude() && matchUri && matchNoCtxUri) {
            // Exclude interceptors are special because with only one of the conditions being false the interceptor
            // won't be applied to the request
            return true
        } else if (!matcher.isExclude() && (matchUri || (checkNoCtxUri && matchNoCtxUri))) {
            return true
        }

        return false
    }

    /**
     * Apply filters on the request in different lifecycles
     * @param request httpServletRequest
     * @param filters need to apply on the request
     * @param lifecycleFunc lifecycle method
     * @return result of the filtering
     */
    static private boolean applyFiltersToRequest(HttpServletRequest request,
                                                 ConcurrentLinkedDeque<FilterConfig> filters,
                                                 FilterLifeCycleNameEnum type) {
        // get iterator by filters order
        def iterator = type == FilterLifeCycleNameEnum.before ? filters.iterator() : filters.descendingIterator()

        while (iterator.hasNext()) {
            def filter = iterator.next()

            // escape the filter if the matcher or lifecycle method is null
            if (!filter.matcher) continue

            def filterLifecycleMethod

            switch (type) {
                case FilterLifeCycleNameEnum.before:
                    filterLifecycleMethod = filter.before
                    break
                case FilterLifeCycleNameEnum.after:
                    filterLifecycleMethod = filter.after
                    break
                case FilterLifeCycleNameEnum.afterView:
                    filterLifecycleMethod = filter.afterView
                    break
                default:
                    filterLifecycleMethod = null
            }

            if (!filterLifecycleMethod) continue

            def result = filterLifecycleMethod(doesMatch(request, filter.matcher))

            if (result == false) {
                // break the loop and reject the request from the interceptor
                return false
            }

            // otherwise(null or true), continue to the next filter
        }

        // finally pass to the next interceptor
        return true
    }

    /**
     * add filters to the deque by sequence
     * @param filters
     * @return
     */
    def addFilters(List<FilterConfig> filters) {
        filters.each { filter -> this.filters.addLast(filter) }
    }

    /**
     * Base interceptor before lifecycle method
     * @param request
     * @return boolean determine go to next interceptor or reject the request
     */
    boolean before(HttpServletRequest request) {
        return applyFiltersToRequest(request, filters, FilterLifeCycleNameEnum.before)
    }

    /**
     * Base interceptor after lifecycle method
     * @param request
     * @return boolean determine go to next interceptor or reject the request
     */
    boolean after(HttpServletRequest request) {
        return applyFiltersToRequest(request, filters, FilterLifeCycleNameEnum.after)
    }

    /**
     * Base interceptor afterView lifecycle method
     * @param request
     */
    void afterView(HttpServletRequest request) {
        applyFiltersToRequest(request, filters, FilterLifeCycleNameEnum.afterView)
    }
}
