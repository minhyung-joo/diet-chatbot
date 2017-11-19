package com.example.bot.spring.tables;

import org.springframework.data.repository.CrudRepository;
import com.example.bot.spring.tables.Profile;

public interface RecommendationRepository extends CrudRepository<Recommendation, Long>{
	public Recommendation findById(long id);
	public Recommendation findByUniqueCode(long uniqueCode);
}