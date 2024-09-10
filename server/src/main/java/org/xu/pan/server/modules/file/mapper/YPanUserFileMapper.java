package org.xu.pan.server.modules.file.mapper;

import org.apache.ibatis.annotations.Param;
import org.xu.pan.server.modules.file.context.FileSearchContext;
import org.xu.pan.server.modules.file.context.QueryFileListContext;
import org.xu.pan.server.modules.file.entity.YPanUserFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.xu.pan.server.modules.file.vo.FileSearchResultVO;
import org.xu.pan.server.modules.file.vo.YPanUserFileVO;

import java.util.List;

/**
* @author 23561
* @description 针对表【y_pan_user_file(用户文件信息表)】的数据库操作Mapper
* @createDate 2024-09-05 11:01:08
* @Entity org.xu.pan.server.modules.file.entity.YPanUserFile
*/
public interface YPanUserFileMapper extends BaseMapper<YPanUserFile> {

    /**
     * 查询用户的文件列表
     * @param context
     * @return
     */
    List<YPanUserFileVO> selectFileList(@Param("param") QueryFileListContext context);

    /**
     * 文件搜索
     * @param context
     * @return
     */
    List<FileSearchResultVO> searchFile(@Param("param") FileSearchContext context);
}




