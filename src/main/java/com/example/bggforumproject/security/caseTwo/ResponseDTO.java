package com.example.bggforumproject.security.caseTwo;

public class ResponseDTO {
    private long id;
    private String username;
    private String jwt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public ResponseDTO(long id, String username, String jwt) {
        this.id = id;
        this.username = username;
        this.jwt = jwt;
    }

    public ResponseDTO() {
    }
}
