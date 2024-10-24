package com.kostenko.server

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class TodoController(@Autowired val todoRepo: TodoRepository) {
    @MessageMapping("getTodo")
    fun getTodo(id: Long): Mono<Todo> = Mono.justOrEmpty(todoRepo.findTodoById(id))

    @MessageMapping("addTodo")
    fun addTodo(todo: Todo): Mono<Todo> = Mono.justOrEmpty(todoRepo.save(todo))

    @MessageMapping("getTodos")
    fun getTodos(): Flux<Todo> = Flux.fromIterable(todoRepo.findAll())

    @MessageMapping("deleteTodo")
    fun deleteTodo(id: Long): Mono<Void> {
        val todo = todoRepo.findTodoById(id)
        todoRepo.delete(todo)
        return Mono.empty()
    }

    @MessageMapping("todoChannel")
    fun todoChannel(todos: Flux<Todo>): Flux<Todo> {
        return todos.flatMap { Mono.fromCallable { todoRepo.save(it) } }.collectList()
            .flatMapMany { Flux.fromIterable(it) }
    }
}