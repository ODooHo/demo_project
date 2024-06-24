package com.example.demo.global.auth.oauth.response;

import lombok.ToString;

import java.util.Map;

@ToString
public class KakaoResponse {

    private final Map<String, Object> properties;

    private final Map<String,Object> account;

    private final String providerId;

    public KakaoResponse(Map<String,Object> attributes){
        this.properties = (Map<String, Object>) attributes.get("properties");
        this.account = (Map<String, Object>) attributes.get("kakao_account");
        this.providerId = attributes.get("id").toString();
    }

    public String getProviderId(){
        return "kakao";
    }

    public String getEmail(){
        return this.account.get("email").toString();
    }

    public String getName(){
        return this.properties.get("nickname").toString();
    }

    public String getProfileImage(){
        return this.properties.get("profile_image").toString();
    }
}
