package com.zipjung.backend.repository;

import com.zipjung.backend.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoCustomRepository {

}
