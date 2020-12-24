package com.imooc.oauth2.mapper;

import com.imooc.commons.model.pojo.Diners;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author pengfei.zhao
 * @date 2020/12/24 19:58
 */
public interface DinersMapper {

    /**
     * 根据手机号、用户名、邮箱查询食客信息
     * @param account
     * @return
     */
    @Select("select id, username, nickname, phone, email, " +
            "password, avatar_url, roles, is_valid from t_diners where " +
            "(username = #{account} or phone = #{account} or email = #{account})")
    Diners selectByAccountInfo(@Param("account")String account);
}
