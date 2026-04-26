package org.contactmgmt.dto;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String phone;

    public JwtResponse(String accessToken, Long id, String username, String email, String phone) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
    }

    public String getAccessToken() { return token; }
    public void setAccessToken(String accessToken) { this.token = accessToken; }

    public String getTokenType() { return type; }
    public void setTokenType(String tokenType) { this.type = tokenType; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
