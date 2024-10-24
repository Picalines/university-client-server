package com.kostenko.client

import org.reactivestreams.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/todos")
class TodoController(@Autowired val requester: RSocketRequester) {
    @GetMapping
    fun getTodos(): Publisher<Todo> {
        return requester
            .route("getTodos")
            .data(Todo(0, "", false))
            .retrieveFlux(Todo::class.java)
    }

    @GetMapping("/{id}")
    fun getTodo(@PathVariable id: Long): Mono<Todo> {
        return requester
            .route("getTodo")
            .data(id)
            .retrieveMono(Todo::class.java)
    }

    @PostMapping
    fun addTodo(@RequestBody todo: Todo): Mono<Todo> {
        return requester
            .route("addTodo")
            .data(todo)
            .retrieveMono(Todo::class.java)
    }

    @PostMapping("/exp")
    fun addMultipleTodos(@RequestBody todos: TodoList): Flux<Todo> {
        return requester
            .route("todoChannel")
            .data(Flux.fromIterable(todos.todos))
            .retrieveFlux(Todo::class.java)
    }

    @DeleteMapping("/{id}")
    fun deleteTodo(@PathVariable id: Long): Mono<Void> {
        return requester.route("deleteTodo").data(id).send()
    }
}