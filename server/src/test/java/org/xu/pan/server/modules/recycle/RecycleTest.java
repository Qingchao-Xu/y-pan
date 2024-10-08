package org.xu.pan.server.modules.recycle;

import cn.hutool.core.lang.Assert;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.xu.pan.core.exception.YPanBusinessException;
import org.xu.pan.server.YPanServerLauncher;
import org.xu.pan.server.modules.file.context.CreateFolderContext;
import org.xu.pan.server.modules.file.context.DeleteFileContext;
import org.xu.pan.server.modules.file.context.QueryFileListContext;
import org.xu.pan.server.modules.file.enums.DelFlagEnum;
import org.xu.pan.server.modules.file.service.IUserFileService;
import org.xu.pan.server.modules.file.vo.YPanUserFileVO;
import org.xu.pan.server.modules.recycle.context.DeleteContext;
import org.xu.pan.server.modules.recycle.context.QueryRecycleFileListContext;
import org.xu.pan.server.modules.recycle.context.RestoreContext;
import org.xu.pan.server.modules.recycle.service.IRecycleService;
import org.xu.pan.server.modules.user.context.UserRegisterContext;
import org.xu.pan.server.modules.user.service.IUserService;
import org.xu.pan.server.modules.user.vo.UserInfoVO;

import java.util.List;

/**
 * 回收站模块单元测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = YPanServerLauncher.class)
@Transactional
public class RecycleTest {

    @Autowired
    private IUserFileService iUserFileService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IRecycleService iRecycleService;

    /**
     * 测试查询回收站文件列表成功
     */
    @Test
    public void testQueryRecyclesSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        // 创建一个文件夹
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-old");

        Long fileId = iUserFileService.createFolder(context);
        Assert.notNull(fileId);

        // 删掉该文件夹
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = Lists.newArrayList();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);
        iUserFileService.deleteFile(deleteFileContext);

        // 查询回收站列表，校验列表的长度为1
        QueryRecycleFileListContext queryRecycleFileListContext = new QueryRecycleFileListContext();
        queryRecycleFileListContext.setUserId(userId);
        List<YPanUserFileVO> recycles = iRecycleService.recycles(queryRecycleFileListContext);

        Assert.isTrue(CollectionUtils.isNotEmpty(recycles) && recycles.size() == 1);
    }

    /**
     * 测试文件还原成功
     */
    @Test
    public void testFileRestoreSuccess() {

        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        // 创建一个文件夹
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-old");

        Long fileId = iUserFileService.createFolder(context);
        Assert.notNull(fileId);

        // 创建其子文件夹
        CreateFolderContext context2 = new CreateFolderContext();
        context2.setParentId(fileId);
        context2.setUserId(userId);
        context2.setFolderName("folder-name-old2");

        Long fileId2 = iUserFileService.createFolder(context2);
        Assert.notNull(fileId2);

        // 删掉该文件夹
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = Lists.newArrayList();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);
        iUserFileService.deleteFile(deleteFileContext);

        // 查询文件列表，校验列表的长度为1, 说明删除了父文件夹，子文件夹也没有被删除，后面如何解决呢
        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setParentId(fileId);
        queryFileListContext.setUserId(userId);
        queryFileListContext.setFileTypeArray(null);
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());
        List<YPanUserFileVO> result = iUserFileService.getFileList(queryFileListContext);
        System.out.println(result.size());

        // 文件还原
        RestoreContext restoreContext = new RestoreContext();
        restoreContext.setUserId(userId);
        restoreContext.setFileIdList(Lists.newArrayList(fileId));
        iRecycleService.restore(restoreContext);

    }

    /**
     * 测试文件还原失败-错误的用户ID
     */
    @Test(expected = YPanBusinessException.class)
    public void testFileRestoreFailByWrongUserId() {

        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        // 创建一个文件夹
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-old");

        Long fileId = iUserFileService.createFolder(context);
        Assert.notNull(fileId);

        // 删掉该文件夹
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = Lists.newArrayList();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);
        iUserFileService.deleteFile(deleteFileContext);

        // 文件还原
        RestoreContext restoreContext = new RestoreContext();
        restoreContext.setUserId(userId + 1);
        restoreContext.setFileIdList(Lists.newArrayList(fileId));
        iRecycleService.restore(restoreContext);
    }

    /**
     * 测试文件还原失败-错误的文件名称
     */
    @Test(expected = YPanBusinessException.class)
    public void testFileRestoreFailByWrongFilename1() {

        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        // 创建一个文件夹
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-1");

        Long fileId = iUserFileService.createFolder(context);
        Assert.notNull(fileId);

        // 删掉该文件夹
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = Lists.newArrayList();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);
        iUserFileService.deleteFile(deleteFileContext);

        context.setFolderName("folder-name-1");
        fileId = iUserFileService.createFolder(context);
        Assert.notNull(fileId);

        // 文件还原
        RestoreContext restoreContext = new RestoreContext();
        restoreContext.setUserId(userId);
        restoreContext.setFileIdList(Lists.newArrayList(fileId));
        iRecycleService.restore(restoreContext);
    }

    /**
     * 测试文件还原失败-错误的文件名称
     */
    @Test(expected = YPanBusinessException.class)
    public void testFileRestoreFailByWrongFilename2() {

        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        // 创建一个文件夹
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-1");
        Long fileId1 = iUserFileService.createFolder(context);
        Assert.notNull(fileId1);

        // 删掉该文件夹
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = Lists.newArrayList();
        fileIdList.add(fileId1);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);
        iUserFileService.deleteFile(deleteFileContext);

        context.setFolderName("folder-name-1");
        Long fileId2 = iUserFileService.createFolder(context);
        Assert.notNull(fileId2);

        fileIdList.add(fileId2);
        iUserFileService.deleteFile(deleteFileContext);

        // 文件还原
        RestoreContext restoreContext = new RestoreContext();
        restoreContext.setUserId(userId);
        restoreContext.setFileIdList(Lists.newArrayList(fileId1, fileId2));
        iRecycleService.restore(restoreContext);
    }

    /**
     * 测试文件删除失败-错误的用户ID
     */
    @Test(expected = YPanBusinessException.class)
    public void testFileDeleteFailByWrongUserId() {

        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        // 创建一个文件夹
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-1");
        Long fileId = iUserFileService.createFolder(context);
        Assert.notNull(fileId);

        // 删掉该文件夹
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = Lists.newArrayList();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);
        iUserFileService.deleteFile(deleteFileContext);

        // 文件彻底删除
        DeleteContext deleteContext = new DeleteContext();
        deleteContext.setUserId(userId + 1);
        deleteContext.setFileIdList(Lists.newArrayList(fileId));
        iRecycleService.delete(deleteContext);
    }

    /**
     * 测试文件删除成功
     */
    @Test
    public void testFileDeleteSuccess() {

        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        // 创建一个文件夹
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-1");
        Long fileId = iUserFileService.createFolder(context);
        Assert.notNull(fileId);

        // 删掉该文件夹
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = Lists.newArrayList();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);
        iUserFileService.deleteFile(deleteFileContext);

        // 文件彻底删除
        DeleteContext deleteContext = new DeleteContext();
        deleteContext.setUserId(userId);
        deleteContext.setFileIdList(Lists.newArrayList(fileId));
        iRecycleService.delete(deleteContext);
    }

    /************************************************private************************************************/

    /**
     * 用户注册
     *
     * @return 新用户的ID
     */
    private Long register() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = iUserService.register(context);
        Assert.isTrue(register.longValue() > 0L);
        return register;
    }

    /**
     * 查询登录用户的基本信息
     *
     * @param userId
     * @return
     */
    private UserInfoVO info(Long userId) {
        UserInfoVO userInfoVO = iUserService.info(userId);
        Assert.notNull(userInfoVO);
        return userInfoVO;
    }

    private final static String USERNAME = "imooc";
    private final static String PASSWORD = "123456789";
    private final static String QUESTION = "question";
    private final static String ANSWER = "answer";

    /**
     * 构建注册用户上下文信息
     *
     * @return
     */
    private UserRegisterContext createUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername(USERNAME);
        context.setPassword(PASSWORD);
        context.setQuestion(QUESTION);
        context.setAnswer(ANSWER);
        return context;
    }

}
