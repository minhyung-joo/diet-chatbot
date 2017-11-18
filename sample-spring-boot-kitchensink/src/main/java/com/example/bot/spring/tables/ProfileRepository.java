package com.example.bot.spring.tables;

import org.springframework.data.repository.CrudRepository;
import com.example.bot.spring.tables.Profile;

public interface ProfileRepository extends CrudRepository<Profile, Long>{
	public Profile findByID(long id);
	public Profile findByUserID(String userID);
}