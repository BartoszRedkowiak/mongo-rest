package org.bredkowiak.mongorest.user;

import com.mongodb.MongoWriteException;
import org.bredkowiak.mongorest.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User findOne(String id) throws NotFoundException;
    User create(User user) throws MongoWriteException;
    User update(User user);
    void delete(String id) throws NotFoundException;
    Page<User> findPageOfUsers (Pageable pageable) throws NotFoundException;

}
