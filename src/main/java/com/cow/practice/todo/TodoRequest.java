package com.cow.practice.todo;

import jakarta.validation.constraints.NotBlank;

public record TodoRequest(@NotBlank String title, Boolean done) {

	public boolean doneOrDefault() {
		return done != null && done;
	}

}
