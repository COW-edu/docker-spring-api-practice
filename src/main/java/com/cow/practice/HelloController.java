package com.cow.practice;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/hello") // 200 OK
	public Map<String, String> hello() {
		return Map.of("message", "Hello, Docker + Spring!");
	}

}
