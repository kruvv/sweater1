package com.example.sweater.service;

import com.example.sweater.domain.Role;
import com.example.sweater.domain.User;
import com.example.sweater.repos.UserRepo;
import org.assertj.core.internal.bytebuddy.asm.Advice;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static com.sun.javaws.JnlpxArgs.verify;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserSeviceTest {

    @Autowired
    private UserSevice userSevice;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private MailSender mailSender;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void addUser() {
        User user = new User();

        user.setEmail("some@mail.ru");

        boolean isUserCreated = userSevice.addUser(user);

        /** проверка создания пользователя
         *
         */
        Assert.assertTrue(isUserCreated);

        /** проверак получения пользователем роли
         *
         */
        Assert.assertTrue(CoreMatchers.is(user.getRoles()).matches(Collections.singleton(Role.USER)));

        /** проверка сохранения пользователя в базе
         *
         */
        Mockito.verify(userRepo, Mockito.times(1)).save(user);


        /** проверка на отправку пользователю активационного кода
         *
         */
        Mockito.verify(mailSender, Mockito.times(1))
                .send(ArgumentMatchers.eq(user.getEmail()),
                        ArgumentMatchers.eq("Activation code"),
                        ArgumentMatchers.contains("Welcome to Sweater."));

        /** Можно и так для получения проверки на любую полученную строку
         * вместо Activation code  и  Welcome to Sweater.
         */
        Mockito.verify(mailSender, Mockito.times(1))
                .send(ArgumentMatchers.eq(user.getEmail()),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString());

    }

    /** Проверка на отсутвие пользователя в базе данных
     *
     */
    @Test
    public void addUserFailTest() {
        User user = new User();

        user.setUsername("John");

        Mockito.doReturn(new User())
                .when(userRepo)
                .findByUsername("John");

        boolean isUserCreated = userSevice.addUser(user);

        Assert.assertFalse(isUserCreated);

        /** проверка сохранения пользователя в базе
         *
         */
        Mockito.verify(userRepo, Mockito.times(0)).save(ArgumentMatchers.any(User.class));


        /** проверка на отправку пользователю активационного кода
         *
         */

        Mockito.verify(mailSender, Mockito.times(0))
                .send(ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString());

    }

    /** проверка полученя пользователем активационного кода
     *
      */
    @Test
    public void activateUser() {

        User user = new User();
        user.setActivationCode("bingo!");
        Mockito.doReturn(user)
                .when(userRepo)
                .findByActivationCode("activate");

        boolean isUserActivated = userSevice.activateUser("activate");

        Assert.assertTrue(isUserActivated);
        Assert.assertNull(user.getActivationCode());

        /** проверка сохранения пользователя в базе
         *
         */
        Mockito.verify(userRepo, Mockito.times(1)).save(user);

    }

    @Test
    public void activateUserFailtest() {
        boolean isUserActivated = userSevice.activateUser("activate me");

        Assert.assertFalse(isUserActivated);

        Mockito.verify(userRepo, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
    }
}