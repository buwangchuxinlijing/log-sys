package com.log.collect.tools.user;


/**
 * @author lij
 * @description: TODO
 * @date 2023/3/1 16:26
 */

public class UserModel<Role extends  UserRoleInfo> {
    @Override
    public String toString() {
        return "UserModel{" +
                "userName='" + userName + '\'' +
                ", nickname='" + nickname + '\'' +
                ", roleModel=" + roleModel.toString() +
                '}';
    }

    private String userName;
    private String nickname;
    private Role roleModel;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Role getRoleModel() {
        return roleModel;
    }

    public void setRoleModel(Role roleModel) {
        this.roleModel = roleModel;
    }
}
