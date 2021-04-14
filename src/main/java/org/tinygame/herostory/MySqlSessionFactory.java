package org.tinygame.herostory;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: ljf
 * CreatedAt: 2021/4/14 下午11:05
 */
public final class MySqlSessionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlSessionFactory.class);

    private static SqlSessionFactory sqlSessionFactory;

    private MySqlSessionFactory() {
    }

    /**
     * 初始化
     */
    public static void init() {
        try {
            sqlSessionFactory = (new SqlSessionFactoryBuilder()).build(Resources.getResourceAsStream("MyBatisConfig.xml"));

//            SqlSession sqlSession = openSession();
//            sqlSession.getConnection().createStatement().execute("select -1");
//            sqlSession.close();
//            System.out.println("可以连接");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 开启 Mysql 会话
     *
     * @return Mysql 会话
     */
    public static SqlSession openSession() {
        if (null == sqlSessionFactory) {
            LOGGER.error("sqlSessionFactory 尚未初始化");
        }
        return sqlSessionFactory.openSession(true);
    }
}
