package com.twino.homework.service;

import com.twino.homework.common.DateMatcher;
import com.twino.homework.common.staticAccess.EntityGenerator;
import com.twino.homework.db.entity.BlacklistEntity;
import com.twino.homework.db.entity.UserEntity;
import com.twino.homework.db.repository.BlacklistRepository;
import com.twino.homework.db.repository.UserRepository;
import com.twino.homework.exception.UserNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceImplTest {
    @Mock
    UserServiceImpl userService;
    @Mock
    UserRepository userRepository;
    @Mock
    BlacklistRepository blacklistRepository;
    @Mock
    Logger logger;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "blacklistRepository", blacklistRepository);
        ReflectionTestUtils.setField(userService, "logger", logger);
    }

    public static Object[][] dataProviderForAddToBlacklistByUuidTest() {
        return new Object[][]{
            {true, true},
            {false, true},
            {true, false},
            {false, false},
        };
    }
    @ParameterizedTest
    @MethodSource("dataProviderForAddToBlacklistByUuidTest")
    public void addToBlacklistByUuidTest(boolean userExists, boolean isDuplicate) throws UserNotFoundException {
        String uuidMock = "uuid mock";

        when(userService.addToBlacklistByUuid(uuidMock)).thenCallRealMethod();
        if (! userExists) {
            try {
                userService.addToBlacklistByUuid(uuidMock);
            } catch (UserNotFoundException e) {
                assertEquals(String.format("user with uuid %s not found", uuidMock), e.getMessage());
            }
            return;
        }

        UserEntity userMock = EntityGenerator.getTestUserEntity();
        BlacklistEntity savingBlacklistEntity = new BlacklistEntity();
        userMock.setBlacklistsById(savingBlacklistEntity);

        savingBlacklistEntity.setUserByUserId(userMock);
        userMock.setBlacklistsById(savingBlacklistEntity);
        when(userRepository.findByUniqueId(uuidMock)).thenReturn(userMock);

        if (! isDuplicate) {
            when(blacklistRepository.save(savingBlacklistEntity)).thenReturn(savingBlacklistEntity);
        } else {
            when(blacklistRepository.save(savingBlacklistEntity))
                .thenThrow(Mockito.mock(ConstraintViolationException.class));
        }

        assertEquals(
            userMock,
            userService.addToBlacklistByUuid(uuidMock)
        );

        if (! isDuplicate) {
            verify(logger, never()).warn(anyString());
        } else {
            verify(logger, times(1)).warn(String.format("duplicate key on adding user %s", uuidMock));
        }
    }

    public static Object[][] dataProviderForTestGetUserEntityTest() {
        return new Object[][]{
            {"name mock", "surname mock", true, true},
            {"name mock", "surname mock", true, false},
            {"name mock", "surname mock", false, true},
            {"name mock", "surname mock", false, false},
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderForTestGetUserEntityTest")
    public void getUserEntityTest(String name, String surname, boolean userExists, boolean isDuplicate) {
        DateMatcher dateMatcher = new DateMatcher();
        UserEntity user = EntityGenerator.getTestUserEntity();
        when(userService.getUserEntity(name, surname)).thenCallRealMethod();
        when(userRepository.findByNameAndSurname(name, surname)).thenReturn(userExists ? user : null);

        if (userExists) {
            assertEquals(user, userService.getUserEntity(name, surname));
            verify(userRepository, never()).save(any());
            return;
        }

        if (isDuplicate) {
            when(userRepository.save(any(UserEntity.class))).thenThrow(Mockito.mock(ConstraintViolationException.class));

            when(userService.getUserAfterDuplicateEntry(name, surname)).thenReturn(user);
            UserEntity result = userService.getUserEntity(name, surname);

            assertEquals(user, result);
            return;
        }

        when(userRepository.save(user)).thenReturn(user);
        UserEntity result = userService.getUserEntity(name, surname), expectedResult = new UserEntity();

        expectedResult.setSurname(surname);
        expectedResult.setName(name);
        expectedResult.setUniqueId(result.getUniqueId());
        expectedResult.setCreated(result.getCreated());
        dateMatcher.wasNow(result.getCreated());

        assertEquals(expectedResult, result);

        verify(userRepository, times(1)).save(result);
    }


    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void isUserBlacklistedTest(boolean isBlacklisted) {
        UserEntity user = Mockito.mock(UserEntity.class);
        when(userService.isUserBlacklisted(user)).thenCallRealMethod();

        when(blacklistRepository.findByUserByUserId(user))
            .thenReturn(isBlacklisted ? Mockito.mock(BlacklistEntity.class) : null);

        assertEquals(isBlacklisted, userService.isUserBlacklisted(user));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void getUserAfterDuplicateEntryTest(boolean userFound) {
        String name = "unit test name", surname = "unit test surname";
        when(userService.getUserAfterDuplicateEntry(name, surname)).thenCallRealMethod();
        UserEntity user = Mockito.mock(UserEntity.class), expectingResult = user;

        when(userRepository.findByNameAndSurname(name, surname)).thenReturn(userFound ? user : null);

        if (! userFound) {
            expectingResult = Mockito.mock(UserEntity.class);
            when(userService.retryWithPause(name, surname)).thenReturn(expectingResult);
        }

        assertEquals(expectingResult, userService.getUserAfterDuplicateEntry(name, surname));
    }

    public static Object[][] dataProviderForRetryWithPauseTest() {
        return new Object[][]{
            {true, true},
            {false, true},
            {true, false},
            {false, false},
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderForRetryWithPauseTest")
    public void retryWithPauseTest(boolean isInterrupted, boolean userFound) throws InterruptedException {
        String name = "test name";
        String surname = "test surname";
        UserEntity userEntity = userFound ? Mockito.mock(UserEntity.class) : null;

        when(userService.retryWithPause(name, surname)).thenCallRealMethod();
        when(userRepository.findByNameAndSurname(name, surname)).thenReturn(userEntity);

        InterruptedException exceptionMock = null;
        if (isInterrupted) {
            exceptionMock = mock(InterruptedException.class);
            doThrow(exceptionMock).when(userService).pause(10);
        } else {
            doNothing().when(userService).pause(10);
        }

        if (! userFound) {
            try {
                userService.retryWithPause(name, surname);
            } catch (RuntimeException e) {
                assertEquals(String.format("could not create user %s, %s could be master slave problem", name, surname), e.getMessage());
            }
        } else {
            userService.retryWithPause(name, surname);
        }

        if (isInterrupted) {
            verify(logger, times(1)).warn("interrupted sleep timeout on repeat user search delay", exceptionMock);
        } else {
            verify(logger, never()).warn(any());
        }
    }
//    protected UserEntity retryWithPause(String name, String surname) {
//        try {
//            pause(10);
//        } catch (InterruptedException e) {
//            logger.warn("interrupted sleep timeout on repeat user search delay", e);
//        }
//
//        UserEntity user = this.userRepository.findByNameAndSurname(name, surname);
//
//        if (user == null) {
//            throw new RuntimeException(String.format("could not create user %s, %s could be master slave problem", name, surname));
//        }
//
//        return user;
//    }

    @Test
    public void pauseTest() throws InterruptedException {
        DateMatcher dateMatcher = new DateMatcher();
        doCallRealMethod().when(userService).pause(1);

        userService.pause(1);

        dateMatcher.wasDelay(new Timestamp(System.currentTimeMillis()), 1);
    }
}
