package org.tinygame.herostory.login;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.login.db.IUserDao;
import org.tinygame.herostory.login.db.UserEntity;

/**
 * Author: ljf
 * CreatedAt: 2021/4/14 下午11:22
 */
public final class LoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    private static final LoginService INSTANCE = new LoginService();

    private LoginService() {
    }

    public static LoginService getInstance() {
        return INSTANCE;
    }

    public UserEntity userLogin(String userName, String password) {
        if (null == userName || password == null) {
            return null;
        }
        LOGGER.info("logService Thread = {}", Thread.currentThread().getName());
        try (SqlSession sqlSession = MySqlSessionFactory.openSession()) {
            IUserDao dao = sqlSession.getMapper(IUserDao.class);
            UserEntity userEntity = dao.getUserByName(userName);
            if (null != userEntity) {
                if (!password.equals(userEntity.password)) {
                    throw new RuntimeException("密码错误");
                }
            } else {
                userEntity = new UserEntity();
                userEntity.userName = userName;
                userEntity.password = password;
                userEntity.heroAvatar = "Hero_Shaman";
                dao.insertInto(userEntity);
            }
            return userEntity;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
}
