package org.xu.pan.server.modules.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.xu.pan.server.modules.log.entity.YPanErrorLog;
import org.xu.pan.server.modules.log.service.IErrorLogService;
import org.xu.pan.server.modules.log.mapper.YPanErrorLogMapper;
import org.springframework.stereotype.Service;

/**
* @author 23561
* @description 针对表【y_pan_error_log(错误日志表)】的数据库操作Service实现
* @createDate 2024-09-05 11:01:53
*/
@Service
public class ErrorLogServiceImpl extends ServiceImpl<YPanErrorLogMapper, YPanErrorLog>
    implements IErrorLogService {

}




