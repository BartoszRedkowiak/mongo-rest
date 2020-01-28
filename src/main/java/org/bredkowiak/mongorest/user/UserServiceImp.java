package org.bredkowiak.mongorest.user;

import com.mongodb.MongoWriteException;
import org.bredkowiak.mongorest.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImp implements UserService {

    private final String NOT_FOUND_MESSAGE = "No user with given id present in database";
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findOne(String id) throws NotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()){
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
        return user.get();
    }

    @Override
    public User create(User user) throws MongoWriteException{
        User savedUser = userRepository.insert(user);
        return savedUser;
    }

    @Override
    public User update(User user) {
        User updatedUser = userRepository.save(user);
        return updatedUser;
    }

    @Override
    public void delete(String id) throws NotFoundException {
        boolean test = userRepository.existsById(id);
        if (!test){
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
        userRepository.deleteById(id);
    }

    @Override
    public Page<User> findPageOfUsers(Pageable pageable) throws NotFoundException {
        Page<User> users = userRepository.findAll(pageable);
        if (!users.hasContent()){
            throw new NotFoundException("Cannot generate page with provided query criteria");
        }
        return users;
    }
}
