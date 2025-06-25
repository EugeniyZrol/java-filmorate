package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import java.util.Set;

@Data
public class CommonFriendsResponse {
    private Long userId1;
    private Long userId2;
    private Set<UserResponse> commonFriends;

    public CommonFriendsResponse(Long userId1, Long userId2, Set<UserResponse> commonFriends) {
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.commonFriends = commonFriends;
    }
}