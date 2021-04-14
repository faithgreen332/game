package org.tinygame.herostory.login.db;

/**
 * Author: ljf
 * CreatedAt: 2021/4/14 下午10:59
 */
public interface IUserDao {

    UserEntity getUserByName(String userName);

    void insertInto(UserEntity newEntity);
}
