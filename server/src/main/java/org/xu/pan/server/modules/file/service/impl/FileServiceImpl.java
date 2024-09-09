package org.xu.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.xu.pan.server.modules.file.context.QueryRealFileListContext;
import org.xu.pan.server.modules.file.entity.YPanFile;
import org.xu.pan.server.modules.file.service.IFileService;
import org.xu.pan.server.modules.file.mapper.YPanFileMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
* @author 23561
* @description 针对表【y_pan_file(物理文件信息表)】的数据库操作Service实现
* @createDate 2024-09-05 11:01:08
*/
@Service
public class FileServiceImpl extends ServiceImpl<YPanFileMapper, YPanFile>
    implements IFileService {

    /**
     * 根据条件查询用户的实际文件列表
     *
     * @param context
     * @return
     */
    @Override
    public List<YPanFile> getFileList(QueryRealFileListContext context) {
        Long userId = context.getUserId();
        String identifier = context.getIdentifier();
        LambdaQueryWrapper<YPanFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Objects.nonNull(userId), YPanFile::getCreateUser, userId);
        queryWrapper.eq(StringUtils.isNotBlank(identifier), YPanFile::getIdentifier, identifier);
        return list(queryWrapper);
    }

}




