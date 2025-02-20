package com.bside.mzoffice.user.dto.response;

import com.bside.mzoffice.user.domain.SnsType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserResponse {
    private SnsType snsType;
    private String id;
    private String name;
    private String email;
}
