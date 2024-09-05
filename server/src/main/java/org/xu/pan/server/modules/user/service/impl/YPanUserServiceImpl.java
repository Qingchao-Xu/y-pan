package org.xu.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.xu.pan.server.modules.user.entity.YPanUser;
import org.xu.pan.server.modules.user.service.YPanUserService;
import org.xu.pan.server.modules.user.mapper.YPanUserMapper;
import org.springframework.stereotype.Service;

/**
* @author 23561
* @description 针对表【y_pan_user(用户信息表)】的数据库操作Service实现
* @createDate 2024-09-05 10:58:27
*/
@Service
public class YPanUserServiceImpl extends ServiceImpl<YPanUserMapper, YPanUser>
    implements YPanUserService{

}




