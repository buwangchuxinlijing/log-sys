package com.log.collect.tools.user;

import javax.servlet.ServletRequest;

/**
 * @author lij
 * @description: TODO
 * @date 2023/3/1 16:07
 */
public interface LogUserInfoObtain<U extends UserModel> {

    U obtain(ServletRequest request);

}
