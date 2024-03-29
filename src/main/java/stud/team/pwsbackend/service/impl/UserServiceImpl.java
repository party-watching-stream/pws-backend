package stud.team.pwsbackend.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stud.team.pwsbackend.domain.*;
import stud.team.pwsbackend.dto.*;
import stud.team.pwsbackend.exception.message.IncorrectCredentialsException;
import stud.team.pwsbackend.exception.user.UserNotFoundException;
import stud.team.pwsbackend.mapper.GlobalRoleMapper;
import stud.team.pwsbackend.mapper.StreamMapper;
import stud.team.pwsbackend.mapper.UserMapper;
import stud.team.pwsbackend.repository.GlobalRoleRepository;
import stud.team.pwsbackend.repository.PlaylistRepository;
import stud.team.pwsbackend.repository.UserRepository;
import stud.team.pwsbackend.repository.VideoRepository;
import stud.team.pwsbackend.service.UserService;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private GlobalRoleRepository globalRoleRepository;
    private VideoRepository videoRepository;
    private PlaylistRepository playlistRepository;
    private UserMapper userMapper;
    private GlobalRoleMapper roleMapper;
    private StreamMapper streamMapper;
    private PasswordEncoder passwordEncoder;


    public List<UserDto> getAllUsers() {
        var users = userRepository.findAll();
        return userMapper.mapToDto(users);
    }

    public UserDto addUser(NewUserDto userDto) {
        User user = userMapper.mapToEntity(userDto);
        Password userPassword = new Password();

        String passwordHash = passwordEncoder.encode(userDto.getPassword());
        userPassword.setPasswordHash(passwordHash);

        user.setPassword(userPassword);
        userPassword.setUser(user);

        user = userRepository.save(user);
        return userMapper.mapToDto(user);
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    public UserDto getUser(long userId) throws UserNotFoundException {
        User user = findUserById(userId);
        return userMapper.mapToDto(user);
    }

    public void updateUser(long userId, UserDto userDto) throws UserNotFoundException {
        User user = findUserById(userId);
        userMapper.updateEntity(user, userDto);
    }

    @Override
    public void updatePassword(long userId, UpdatePasswordRequestDto updPassword) {
        var user = userRepository.findById(userId).orElseThrow();

        String hash = user.getPassword().getPasswordHash();

        if (passwordEncoder.matches(updPassword.getOldPassword(), hash)) {
            Password userPassword = user.getPassword();
            String passwordHash = passwordEncoder.encode(updPassword.getNewPassword());
            userPassword.setPasswordHash(passwordHash);
        }
    }

    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void addRoleToUser(long userId, long roleId) {
        var user = userRepository.findById(userId).orElseThrow();
        var role = globalRoleRepository.findById(roleId).orElseThrow();
        user.getGlobalRoles().add(role);
    }

    @Override
    public void deleteRoleByUser(long userId, long roleId) {
        var user = userRepository.findById(userId).orElseThrow();
        var role = globalRoleRepository.findById(roleId).orElseThrow();
        user.getGlobalRoles().remove(role);
    }

    @Override
    public void setBanStatusByUser(long userId, boolean ban) {
        var user = userRepository.findById(userId).orElseThrow();
        user.setBanned(ban);
    }

    private User findUserById(long userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public UserDto login(@Valid LoginRequestDto loginRequestDto) throws IncorrectCredentialsException {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        var user = userRepository.findByEmail(email).orElseThrow(IncorrectCredentialsException::new);
        var userId = user.getIdUser();

        String hash = user.getPassword().getPasswordHash();

        if (passwordEncoder.matches(password, hash)) {
            return userMapper.mapToDto(user);
        }
        throw new IncorrectCredentialsException();
    }

    public UserDto register(NewUserDto newUserDto) {
        UserDto userDto = addUser(newUserDto);
        User user = userRepository.findById(userDto.getIdUser())
                .orElseThrow(IllegalStateException::new);

        var userRole = globalRoleRepository
                .findByTitle("USER")
                .orElseThrow(IllegalStateException::new);

        user.setGlobalRoles(List.of(userRole));
        return userMapper.mapToDto(user);
    }

    @Override
    public List<UserDto> findUsersByName(String search) {
        var users = userRepository.findUserByName(search);
        return userMapper.mapToDto(users);
    }

    @Override
    public List<GlobalRoleDto> getAllRolesByUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return roleMapper.mapToDto(user.getGlobalRoles());
    }

    @Override
    public StreamDto getActiveStream(long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        for (Stream stream : user.getUsersStreams()) {
            if (stream.getActiveStream()) {
                return streamMapper.streamToDto(stream);
            }
        }
        return null;
    }

    @Override
    public void deleteVideoByUser(long userId, long videoId) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow();
        Video video = videoRepository.findById(videoId).orElseThrow();
        user.getUserVideos().remove(video);
        video.setUser(null);
    }

    @Override
    public void deletePlaylistByUser(long userId, long playlistId) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow();
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow();
        user.getUserPlaylists().remove(playlist);
        playlist.setUser(null);
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setGlobalRoleRepository(GlobalRoleRepository globalRoleRepository) {
        this.globalRoleRepository = globalRoleRepository;
    }

    @Autowired
    public void setVideoRepository(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Autowired
    public void setPlaylistRepository(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    @Autowired
    public void setRoleMapper(GlobalRoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Autowired
    public void setStreamMapper(StreamMapper streamMapper) {
        this.streamMapper = streamMapper;
    }
}
