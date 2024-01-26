package com.forzlp.admin.service;

import com.forzlp.admin.dao.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.forzlp.admin.dto.resp.UserInfoRespDTO;
import org.springframework.stereotype.Service;

/**
* @author 70ash
* @description 针对表【t_user】的数据库操作Service
* @createDate 2024-01-22 14:39:00
*/
@Service
public interface UserService extends IService<User> {

    UserInfoRespDTO getUserByUsername(String username);
}
