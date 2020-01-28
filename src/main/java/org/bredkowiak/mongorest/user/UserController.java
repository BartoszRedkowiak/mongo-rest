package org.bredkowiak.mongorest.user;

import org.bredkowiak.mongorest.exception.NotFoundException;
import org.bredkowiak.mongorest.validation.ValidationCreate;
import org.bredkowiak.mongorest.validation.ValidationUpdate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> findOne(@PathVariable("userId") String userId) throws NotFoundException {

        // TODO test czy do end-pointu dociera user o tym id lub admin

        User user = userService.findOne(userId);
        return ResponseEntity.status(HttpStatus.OK).body(toDTO(user));
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserDTO>> getPageOfUsers(@RequestParam("page") @Min(0) Integer page,
                                                        @RequestParam("size") @Min(1) Integer size) throws NotFoundException {

        // TODO admin-only access
        Pageable pageable = PageRequest.of(page, size);
        Page<User> resultPage = userService.findPageOfUsers(pageable);
        return ResponseEntity.status(200).body(toDTO(resultPage.getContent()));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Validated({ValidationCreate.class}) UserDTO userDTO) {

        //TODO only for non logged in users and admins
        User createdUser = userService.create(fromDTO(userDTO));
        return ResponseEntity.status(HttpStatus.OK).body(toDTO(createdUser));
    }


    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestBody @Validated({ValidationUpdate.class}) UserDTO userDTO) throws NotFoundException {

        //TODO if admin: update immediately; if user: compare ids
        User updatedUser = userService.update(fromDTO(userDTO));
        return ResponseEntity.status(HttpStatus.OK).body(toDTO(updatedUser));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable("userId") String userId) throws NotFoundException {
        //TODO if admin: set inactive immediately; if user: compare ids, then set inactive
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private UserDTO toDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    private List<UserDTO> toDTO(List<User> users) {
        return users.stream().map(e -> modelMapper.map(e, UserDTO.class)).collect(Collectors.toList());
    }

    private User fromDTO(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }


}
