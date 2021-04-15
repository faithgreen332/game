package org.tinygame.herostory.login;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.login.db.IUserDao;
import org.tinygame.herostory.login.db.UserEntity;

import java.util.function.Function;

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

    public void userLogin(String userName, String password, Function<UserEntity, Void> callback) {
        if (null == userName || password == null) {
            return;
        }

        AsyncGetEntity oge = new AsyncGetEntity(userName, password) {
            // 这里肯定能拿到 userEntity
            @Override
            public void doFinish() {
                if (null != callback) {
                    callback.apply(this.get_userEntity());
                }
            }
        };
        AsyncOperationProcessor.getInstance().process(oge);
    }

    private class AsyncGetEntity implements IAsyncOperation {
        private final String _userName;
        private final String _password;

        private UserEntity _userEntity;

        public AsyncGetEntity(String _userName, String _password) {
            this._userName = _userName;
            this._password = _password;
        }

        public UserEntity get_userEntity() {
            return _userEntity;
        }

        @Override
        public void doAsync() {

            LOGGER.info("doAsync thread {}", Thread.currentThread().getName());
            try (SqlSession sqlSession = MySqlSessionFactory.openSession()) {
                IUserDao dao = sqlSession.getMapper(IUserDao.class);
                UserEntity userEntity = dao.getUserByName(_userName);
                if (null != userEntity) {
                    if (!_password.equals(userEntity.password)) {
                        throw new RuntimeException("密码错误");
                    }
                } else {
                    userEntity = new UserEntity();
                    userEntity.userName = _userName;
                    userEntity.password = _password;
                    userEntity.heroAvatar = "Hero_Shaman";
                    dao.insertInto(userEntity);
                }
                _userEntity = userEntity;
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
