package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exceptions.DublicateEmailErrorException;
import ru.practicum.shareit.user.exceptions.EmailErrorException;
import ru.practicum.shareit.user.model.User;

import java.lang.module.FindException;
import java.util.*;

@Repository
public class InMemoryUserDao implements UserDao {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long currentId = 1;

    @Override
    public User createUser(User user) {
        if (Objects.isNull(user.getEmail()))
            throw new EmailErrorException("Пустой эл.адрес!");
        if (emails.contains(user.getEmail()))
            throw new DublicateEmailErrorException("Уже существует адрес " + user.getEmail());
        user.setId(currentId);
        currentId++;
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User updateUser(long userId, User user) {
        if (!users.containsKey(userId)) {
            throw new FindException("Не существует пользователь с id " + userId);
        }

        User updatedUser = users.get(userId);

        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }

        String updatedEmail = users.get(userId).getEmail();
        String newEmail = user.getEmail();

        if (newEmail != null && !updatedEmail.equals(newEmail)) {
            emails.remove(updatedEmail);
            if (!emails.contains(newEmail)) {
                emails.add(newEmail);
                updatedUser.setEmail(newEmail);
            } else {
                throw new DublicateEmailErrorException("Указанный E-mail (" + newEmail + ") используется другим юзером");
            }
        }

        return updatedUser;
    }

    @Override
    public User findUserById(long userId) {
        if (!users.containsKey(userId)) throw new RuntimeException("Не найден юзер " + userId);
        return users.get(userId);
    }

    @Override
    public void deleteUserById(long userId) {
        if (!users.containsKey(userId)) throw new RuntimeException("Не найден юзер при удалении записи" + userId);
        emails.remove(users.get(userId).getEmail());
        users.remove(userId);
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

}
