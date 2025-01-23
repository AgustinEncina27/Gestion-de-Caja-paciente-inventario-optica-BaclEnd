package com.springboot.backend.optica.dao;

import org.springframework.data.repository.CrudRepository;

import com.springboot.backend.optica.modelo.User;

public interface IUserDao extends CrudRepository<User, Long>  {

	public User findByUsername(String username);
	
}
