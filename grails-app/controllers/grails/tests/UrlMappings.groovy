package grails.tests

class UrlMappings {

    static mappings = {
        name beer: '/beer/'(controller: 'beer', action: 'index')


        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
