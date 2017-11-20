package com.example.bot.spring.tables;

import org.springframework.data.repository.CrudRepository;
import com.example.bot.spring.tables.Food;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends CrudRepository<Food, Long> {
	public Food findByFoodID(Long foodID);
}