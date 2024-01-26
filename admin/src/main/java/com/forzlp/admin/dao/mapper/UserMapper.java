package com.forzlp.admin.dao.mapper;

import com.forzlp.admin.dao.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 70ash
* @description 针对表【t_user】的数据库操作Mapper
* @createDate 2024-01-22 14:39:00
* @Entity com.forzlp.admin.dao.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




