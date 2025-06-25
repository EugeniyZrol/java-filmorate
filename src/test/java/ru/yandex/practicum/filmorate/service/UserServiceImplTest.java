//package ru.yandex.practicum.filmorate.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.storage.UserStorage;
//
//import java.util.Collection;
//import java.util.HashSet;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class UserServiceImplTest {
//    @Mock
//    private UserStorage userStorage;
//
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    private User user1;
//    private User user2;
//    private User user3;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        user1 = new User();
//        user1.setId(1L);
//        user1.setFriends(new HashSet<>());
//
//        user2 = new User();
//        user2.setId(2L);
//        user2.setFriends(new HashSet<>());
//
//        user3 = new User();
//        user3.setId(3L);
//        user3.setFriends(new HashSet<>());
//
//        // Настраиваем поведение findById
//        when(userStorage.findById(1L)).thenReturn(user1);
//        when(userStorage.findById(2L)).thenReturn(user2);
//        when(userStorage.findById(3L)).thenReturn(user3);
//    }
//
//    @Test
//    void findById_ShouldReturnUser() {
//        User foundUser = userStorage.findById(1L);
//
//        assertEquals(user1, foundUser);
//    }
//
//    @Test
//    void findById_UserNotFound_ShouldThrowNotFoundException() {
//        when(userStorage.findById(4L)).thenThrow(new NotFoundException("Пользователь с id = 4 не найден"));
//
//        assertThrows(NotFoundException.class, () -> userStorage.findById(4L));
//    }
//
//    @Test
//    void addFriends_ShouldAddFriend() {
//        userService.addFriends(1L, 2L);
//
//        assertTrue(user1.getFriends().contains(2L));
//        assertTrue(user2.getFriends().contains(1L));
//    }
//
//    @Test
//    void addFriends_UserNotFound_ShouldThrowNotFoundException() {
//        when(userStorage.findById(4L)).thenThrow(new NotFoundException("Пользователь с id = 4 не найден"));
//
//        assertThrows(NotFoundException.class, () -> userService.addFriends(1L, 4L));
//    }
//
//    @Test
//    void removeFriend_ShouldRemoveFriend() {
//        user1.getFriends().add(2L);
//        user2.getFriends().add(1L);
//
//        userService.removeFriend(1L, 2L);
//
//        assertFalse(user1.getFriends().contains(2L));
//        assertFalse(user2.getFriends().contains(1L));
//    }
//
//    @Test
//    void getCommonFriends_ShouldReturnCommonFriends() {
//        user1.getFriends().add(2L);
//        user2.getFriends().add(2L);
//        user2.getFriends().add(3L);
//
//        Collection<User> commonFriends = userService.getCommonFriends(1L, 2L);
//
//        assertEquals(1, commonFriends.size());
//        assertTrue(commonFriends.stream().anyMatch(user -> user.getId().equals(2L)));
//    }
//
//    @Test
//    void getCommonFriends_UserNotFound_ShouldThrowNotFoundException() {
//        when(userStorage.findById(4L)).thenThrow(new NotFoundException("Пользователь с id = 4 не найден"));
//
//        assertThrows(NotFoundException.class, () -> userService.getCommonFriends(1L, 4L));
//    }
//
//    @Test
//    void getFriends_ShouldReturnFriends() {
//        user1.getFriends().add(2L);
//        user1.getFriends().add(3L);
//
//        Collection<User> friends = userService.getFriends(1L);
//
//        assertEquals(2, friends.size());
//        assertTrue(friends.stream().anyMatch(user -> user.getId().equals(2L)));
//        assertTrue(friends.stream().anyMatch(user -> user.getId().equals(3L)));
//    }
//
//    @Test
//    void getFriends_UserNotFound_ShouldThrowNotFoundException() {
//        when(userStorage.findById(4L)).thenThrow(new NotFoundException("Пользователь с id = 4 не найден"));
//
//        assertThrows(NotFoundException.class, () -> userService.getFriends(4L));
//    }
//}