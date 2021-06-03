package grails.tests.api

import grails.converters.JSON

class BeerController {
    List<Map> sample = [[name: 1, description: 'beer 1'], [name: 2, description: 'beer 2']]

    def index() {
        render(view: 'index', model: [beers: sample, title: 'beer'])
    }

    def list() {
        render sample as JSON
    }
}
