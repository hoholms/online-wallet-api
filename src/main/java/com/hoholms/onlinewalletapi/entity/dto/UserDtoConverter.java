package com.hoholms.onlinewalletapi.entity.dto;

import com.hoholms.onlinewalletapi.entity.Authority;
import com.hoholms.onlinewalletapi.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class UserDtoConverter {

    public User fromDto(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .authority(Collections.singleton(Authority.USER))
                .build();
    }
}
