package com.usforever.controller;

import com.usforever.dao.UserDao;
import com.usforever.exception.DaoException;
import com.usforever.model.LoginDto;
import com.usforever.model.LoginResponseDto;
import com.usforever.model.RegisterUserDto;
import com.usforever.model.User;
import com.usforever.security.jwt.TokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * AuthenticationController is a class used for handling requests to authenticate Users.
 *
 * It depends on an instance of a UserDao for retrieving and storing user data. This is provided
 * through dependency injection.
 */

@RequestMapping("/api")
@RestController
@CrossOrigin
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserDao userDao;

    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserDao userDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public LoginResponseDto login(@Valid @RequestBody LoginDto loginDto) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            if(authentication.isAuthenticated()){
                String jwt = tokenProvider.createToken(authentication, false);
                User user = userDao.getUserByEmail(loginDto.getEmail());
                return new LoginResponseDto(jwt, user);
            }

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); //Status Code: 401 = The user isn't allowed to perform this action
        }
        catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "DAO error - " + e.getMessage()); //Status Code: 500 = API itself has a problem and can't fulfill the request at this time
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public User register(@Valid @RequestBody RegisterUserDto newUser) {

        if(!newUser.isPasswordsMatch()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password and confirm password do not match"); //Status Code: 400 = Data validation problem
        }

        try {
            User user = userDao.createUser(new User(newUser.getName(), newUser.getDisplayName(), newUser.getEmail(), newUser.getPassword(), newUser.getRole()));
            return user;
        }
        catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "DAO error - " + e.getMessage()); //Status Code: 500 = API itself has a problem and can't fulfill the request at this time
        }
    }

}
