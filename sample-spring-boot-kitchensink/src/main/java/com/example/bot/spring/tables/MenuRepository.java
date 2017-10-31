package com.example.bot.spring.tables;

import org.springframework.data.repository.CrudRepository;
import com.example.bot.spring.tables.Meal;

public interface MenuRepository extends CrudRepository<Menu, Long> {

}