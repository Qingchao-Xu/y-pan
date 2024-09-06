package org.xu.pan.server.modules.file.controller;

import com.google.common.base.Splitter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xu.pan.core.constants.YPanConstants;
import org.xu.pan.core.response.R;
import org.xu.pan.core.utils.IdUtil;
import org.xu.pan.server.common.utils.UserIdUtil;
import org.xu.pan.server.modules.file.constants.FileConstants;
import org.xu.pan.server.modules.file.context.QueryFileListContext;
import org.xu.pan.server.modules.file.enums.DelFlagEnum;
import org.xu.pan.server.modules.file.service.IUserFileService;
import org.xu.pan.server.modules.file.vo.YPanUserFileVO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 文件模块控制器
 */
@RestController
@Validated
@Api(tags = "文件模块")
public class FileController {

    @Autowired
    private IUserFileService iUserFileService;

    @ApiOperation(
            value = "查询文件列表",
            notes = "该接口提供了用户插叙某文件夹下面某些文件类型的文件列表的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @GetMapping("files")
    public R<List<YPanUserFileVO>> list(@NotBlank(message = "父文件夹ID不能为空") @RequestParam(value = "parentId", required = false) String parentId,
                                        @RequestParam(value = "fileTypes", required = false, defaultValue = FileConstants.ALL_FILE_TYPE) String fileTypes) {

        Long realParentId = IdUtil.decrypt(parentId);
        List<Integer> fileTypeArray = null;

        if (!Objects.equals(FileConstants.ALL_FILE_TYPE, fileTypes)) { // 不是查询全部类型，通过分割字符串，得到需要查询的类型数组
            fileTypeArray = Splitter.on(YPanConstants.COMMON_SEPARATOR).splitToList(fileTypes).stream().map(Integer::valueOf).collect(Collectors.toList());
        }

        QueryFileListContext context = new QueryFileListContext();
        context.setParentId(realParentId);
        context.setFileTypeArray(fileTypeArray);
        context.setUserId(UserIdUtil.get());
        context.setDelFlag(DelFlagEnum.NO.getCode());

        List<YPanUserFileVO> result = iUserFileService.getFileList(context);
        return R.data(result);
    }






}
