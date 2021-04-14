package org.tinygame.herostory.model;

/**
 * Author: ljf
 * CreatedAt: 2021/4/12 下午8:32
 */
public class User {

    /**
     * userId
     */
    public int userId;

    /**
     * user 头像
     */
    public String heroAvatar;

    public String userName;

    /**
     * 当前血量
     */
    public int currHp;

    /**
     * 移动状态
     */
    public final MoveState moveState = new MoveState();
}
