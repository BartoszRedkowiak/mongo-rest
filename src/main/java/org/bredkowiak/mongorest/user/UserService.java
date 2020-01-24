package org.bredkowiak.mongorest.user;

public interface UserService {

    User authenticate(String authToken);
    String issueToken(User user);
}
