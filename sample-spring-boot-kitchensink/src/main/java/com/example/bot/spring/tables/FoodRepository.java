package com.example.bot.spring.tables;

import org.springframework.data.repository.CrudRepository;
import com.example.bot.spring.tables.Food;

public interface FoodRepository extends CrudRepository<Food, Long> {
	Food findById(Long foodID);
}