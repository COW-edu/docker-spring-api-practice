package com.cow.practice.todo;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/todos")
public class TodoController {

	private final TodoRepository todoRepository;

	public TodoController(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}

	@GetMapping // 200 OK, 목록 반환
	public List<Todo> findAll() {
		return todoRepository.findAll();
	}

	@GetMapping("/{id}") // 200 OK / 없으면 404
	public Todo findOne(@PathVariable Long id) {
		return todoRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "id " + id + " todo를 찾을 수 없습니다."));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED) // 201 / title이 비면 400(검증 실패, ApiExceptionHandler에서 처리)
	public Todo create(@Valid @RequestBody TodoRequest request) {
		return todoRepository.save(new Todo(request.title(), request.doneOrDefault()));
	}

	@PutMapping("/{id}") // 200 / 없으면 404
	public Todo update(@PathVariable Long id, @Valid @RequestBody TodoRequest request) {
		Todo todo = todoRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "id " + id + " todo를 찾을 수 없습니다."));
		todo.setTitle(request.title());
		todo.setDone(request.doneOrDefault());
		return todoRepository.save(todo);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204 / 없으면 404
	public void delete(@PathVariable Long id) {
		if (!todoRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id " + id + " todo를 찾을 수 없습니다.");
		}
		todoRepository.deleteById(id);
	}

}
