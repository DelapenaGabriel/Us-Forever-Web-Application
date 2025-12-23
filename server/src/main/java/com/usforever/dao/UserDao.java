package com.usforever.dao;



import com.usforever.model.User;

import java.util.List;

public interface UserDao {

    List<User> getUsers();

    User getUserById(int userId);

    User getUserByEmail(String email);

    User createUser(User newUser);
}
