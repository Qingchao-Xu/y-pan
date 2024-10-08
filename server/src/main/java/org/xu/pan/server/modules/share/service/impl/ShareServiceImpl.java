package org.xu.pan.server.modules.share.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.util.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import org.xu.pan.bloom.filter.core.BloomFilter;
import org.xu.pan.bloom.filter.core.BloomFilterManager;
import org.xu.pan.core.constants.YPanConstants;
import org.xu.pan.core.exception.YPanBusinessException;
import org.xu.pan.core.response.ResponseCode;
import org.xu.pan.core.utils.IdUtil;
import org.xu.pan.core.utils.JwtUtil;
import org.xu.pan.core.utils.UUIDUtil;
import org.xu.pan.server.common.cache.ManualCacheService;
import org.xu.pan.server.common.config.PanServerConfig;
import org.xu.pan.server.common.stream.channel.PanChannels;
import org.xu.pan.server.common.stream.event.log.ErrorLogEvent;
import org.xu.pan.server.modules.file.constants.FileConstants;
import org.xu.pan.server.modules.file.context.CopyFileContext;
import org.xu.pan.server.modules.file.context.FileDownloadContext;
import org.xu.pan.server.modules.file.context.QueryFileListContext;
import org.xu.pan.server.modules.file.entity.YPanUserFile;
import org.xu.pan.server.modules.file.enums.DelFlagEnum;
import org.xu.pan.server.modules.file.service.IUserFileService;
import org.xu.pan.server.modules.file.vo.YPanUserFileVO;
import org.xu.pan.server.modules.share.constants.ShareConstants;
import org.xu.pan.server.modules.share.context.*;
import org.xu.pan.server.modules.share.entity.YPanShare;
import org.xu.pan.server.modules.share.enums.ShareDayTypeEnum;
import org.xu.pan.server.modules.share.enums.ShareStatusEnum;
import org.xu.pan.server.modules.share.service.IShareFileService;
import org.xu.pan.server.modules.share.service.IShareService;
import org.xu.pan.server.modules.share.mapper.YPanShareMapper;
import org.springframework.stereotype.Service;
import org.xu.pan.server.modules.share.vo.*;
import org.xu.pan.server.modules.user.entity.YPanUser;
import org.xu.pan.server.modules.user.service.IUserService;
import org.xu.pan.stream.core.IStreamProducer;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author 23561
* @description 针对表【y_pan_share(用户分享表)】的数据库操作Service实现
* @createDate 2024-09-05 11:02:39
*/
@Service
@Slf4j
public class ShareServiceImpl extends ServiceImpl<YPanShareMapper, YPanShare>
    implements IShareService {

    @Autowired
    private PanServerConfig config;

    @Autowired
    private IShareFileService iShareFileService;

    @Autowired
    private IUserFileService iUserFileService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    @Qualifier(value = "shareManualCacheService")
    private ManualCacheService<YPanShare> cacheService;

    @Autowired
    private BloomFilterManager manager;

    @Autowired
    @Qualifier(value = "defaultStreamProducer")
    private IStreamProducer producer;

    private static final String BLOOM_FILTER_NAME = "SHARE_SIMPLE_DETAIL";

    /**
     * 创建分享链接
     * <p>
     * 1、拼装分享实体，保存到数据库
     * 2、保存分享和对应文件的关联关系
     * 3、拼装返回实体并返回
     *
     * @param context
     * @return
     */
    @Transactional(rollbackFor = YPanBusinessException.class)
    @Override
    public YPanShareUrlVO create(CreateShareUrlContext context) {
        saveShare(context);
        saveShareFiles(context);
        YPanShareUrlVO vo = assembleShareVO(context);
        afterCreate(context, vo);
        return vo;
    }

    /**
     * 查询用户的分享列表
     *
     * @param context
     * @return
     */
    @Override
    public List<YPanShareUrlListVO> getShares(QueryShareListContext context) {
        return baseMapper.selectShareVOListByUserId(context.getUserId());
    }

    /**
     * 取消分享链接
     * <p>
     * 1、校验用户操作权限
     * 2、删除对应的分享记录
     * 3、删除对应的分享文件关联关系记录
     *
     * @param context
     */
    @Transactional(rollbackFor = YPanBusinessException.class)
    @Override
    public void cancelShare(CancelShareContext context) {
        checkUserCancelSharePermission(context);
        doCancelShare(context);
        doCancelShareFiles(context);
    }

    /**
     * 校验分享码
     * <p>
     * 1、检查分享的状态是不是正常
     * 2、校验分享的分享码是不是正确
     * 3、生成一个短时间的分享token 返回给上游
     *
     * @param context
     * @return
     */
    @Override
    public String checkShareCode(CheckShareCodeContext context) {
        YPanShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        doCheckShareCode(context);
        return generateShareToken(context);
    }

    /**
     * 查询分享的详情
     * <p>
     * 1、校验分享的状态
     * 2、初始化分享实体
     * 3、查询分享的主体信息
     * 4、查询分享的文件列表
     * 5、查询分享者的信息
     *
     * @param context
     * @return
     */
    @Override
    public ShareDetailVO detail(QueryShareDetailContext context) {
        YPanShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        initShareVO(context);
        assembleMainShareInfo(context);
        assembleShareFilesInfo(context);
        assembleShareUserInfo(context);
        return context.getVo();
    }

    /**
     * 查询分享的简单详情
     * <p>
     * 1、校验分享的状态
     * 2、初始化分享实体
     * 3、查询分享的主体信息
     * 4、查询分享者的信息
     *
     * @param context
     * @return
     */
    @Override
    public ShareSimpleDetailVO simpleDetail(QueryShareSimpleDetailContext context) {
        YPanShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        initShareSimpleVO(context);
        assembleMainShareSimpleInfo(context);
        assembleShareSimpleUserInfo(context);
        return context.getVo();
    }

    /**
     * 获取下一级的文件列表
     * <p>
     * 1、校验分享的状态
     * 2、校验文件的ID确实在分享的文件列表中
     * 3、查询对应文件的子文件列表，返回
     *
     * @param context
     * @return
     */
    @Override
    public List<YPanUserFileVO> fileList(QueryChildFileListContext context) {
        YPanShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        List<YPanUserFileVO> allUserFileRecords = checkFileIdIsOnShareStatusAndGetAllShareUserFiles(context.getShareId(), Lists.newArrayList(context.getParentId()));
        Map<Long, List<YPanUserFileVO>> parentIdFileListMap = allUserFileRecords.stream().collect(Collectors.groupingBy(YPanUserFileVO::getParentId));
        List<YPanUserFileVO> yPanUserFileVOS = parentIdFileListMap.get(context.getParentId());
        if (CollectionUtils.isEmpty(yPanUserFileVOS)) {
            return Lists.newArrayList();
        }
        return yPanUserFileVOS;
    }

    /**
     * 转存至我的网盘
     * <p>
     * 1、校验分享状态
     * 2、校验文件ID是否合法
     * 3、执行保存我的网盘动作
     *
     * @param context
     */
    @Override
    public void saveFiles(ShareSaveContext context) {
        checkShareStatus(context.getShareId());
        checkFileIdIsOnShareStatus(context.getShareId(), context.getFileIdList());
        doSaveFiles(context);
    }

    /**
     * 分享的文件下载
     * <p>
     * 1、校验分享状态
     * 2、校验文件ID的合法性
     * 3、执行文件下载的动作
     *
     * @param context
     */
    @Override
    public void download(ShareFileDownloadContext context) {
        checkShareStatus(context.getShareId());
        checkFileIdIsOnShareStatus(context.getShareId(), Lists.newArrayList(context.getFileId()));
        doDownload(context);
    }

    /**
     * 刷新受影响的对应的分享的状态
     * <p>
     * 1、查询所有受影响的分享的ID集合
     * 2、去判断每一个分享对应的文件以及所有的父文件信息均为正常，该种情况，把分享的状态变为正常
     * 3、如果有分享的文件或者是父文件信息被删除，变更该分享的状态为有文件被删除
     *
     * @param allAvailableFileIdList
     */
    @Override
    public void refreshShareStatus(List<Long> allAvailableFileIdList) {
        List<Long> shareIdList = getShareIdListByFileIdList(allAvailableFileIdList);
        if (CollectionUtils.isEmpty(shareIdList)) {
            return;
        }
        Set<Long> shareIdSet = Sets.newHashSet(shareIdList);
        shareIdSet.stream().forEach(this::refreshOneShareStatus);
    }

    /**
     * 滚动查询已存在的分享ID
     * @param startId
     * @param limit
     * @return
     */
    @Override
    public List<Long> rollingQueryShareId(long startId, long limit) {
        return baseMapper.rollingQueryShareId(startId, limit);
    }

    @Override
    public boolean removeById(Serializable id) {
        return cacheService.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return cacheService.removeByIds(idList);
    }

    @Override
    public boolean updateById(YPanShare entity) {
        return cacheService.updateById(entity.getShareId(), entity);
    }

    @Override
    public boolean updateBatchById(Collection<YPanShare> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return true;
        }
        Map<Long, YPanShare> shareMap = entityList.stream().collect(Collectors.toMap(YPanShare::getShareId, e -> e));
        return cacheService.updateByIds(shareMap);
    }

    @Override
    public YPanShare getById(Serializable id) {
        return cacheService.getById(id);
    }

    @Override
    public List<YPanShare> listByIds(Collection<? extends Serializable> idList) {
        return cacheService.getByIds(idList);
    }

    /***********private*************/

    /**
     * 创建分享链接后置处理
     * @param context
     * @param vo
     */
    private void afterCreate(CreateShareUrlContext context, YPanShareUrlVO vo) {
        BloomFilter<Long> bloomFilter = manager.getFilter(BLOOM_FILTER_NAME);
        if (Objects.nonNull(bloomFilter)) {
            bloomFilter.put(context.getRecord().getShareId());
            log.info("crate share, add share id to bloom filter, shareId is {}", context.getRecord().getShareId());
        }
    }

    /**
     * 刷新一个分享的分享状态
     * <p>
     * 1、查询对应的分享信息，判断有效
     * 2、 去判断该分享对应的文件以及所有的父文件信息均为正常，该种情况，把分享的状态变为正常
     * 3、如果有分享的文件或者是父文件信息被删除，变更该分享的状态为有文件被删除
     *
     * @param shareId
     */
    private void refreshOneShareStatus(Long shareId) {
        YPanShare record = getById(shareId);
        if (Objects.isNull(record)) {
            return;
        }

        ShareStatusEnum shareStatus = ShareStatusEnum.NORMAL;
        if (!checkShareFileAvailable(shareId)) {
            shareStatus = ShareStatusEnum.FILE_DELETED;
        }

        if (Objects.equals(record.getShareStatus(), shareStatus.getCode())) {
            return;
        }

        doChangeShareStatus(record, shareStatus);
    }

    /**
     * 执行刷新文件分享状态的动作
     *
     * @param record
     * @param shareStatus
     */
    private void doChangeShareStatus(YPanShare record, ShareStatusEnum shareStatus) {
        record.setShareStatus(shareStatus.getCode());
        if (!updateById(record)) {
            producer.sendMessage(PanChannels.ERROR_LOG_OUTPUT, new ErrorLogEvent("更新分享状态失败，请手动更改状态，分享ID为：" + record.getShareId() + ", 分享" +
                    "状态改为：" + shareStatus.getCode(), YPanConstants.ZERO_LONG));
        }
    }

    /**
     * 检查该分享所有的文件以及所有的父文件均为正常状态
     *
     * @param shareId
     * @return
     */
    private boolean checkShareFileAvailable(Long shareId) {
        List<Long> shareFileIdList = getShareFileIdList(shareId);
        for (Long fileId : shareFileIdList) {
            if (!checkUpFileAvailable(fileId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查该文件以及所有的文件夹信息均为正常状态
     *
     * @param fileId
     * @return
     */
    private boolean checkUpFileAvailable(Long fileId) {
        YPanUserFile record = iUserFileService.getById(fileId);
        if (Objects.isNull(record)) {
            return false;
        }
        if (Objects.equals(record.getDelFlag(), DelFlagEnum.YES.getCode())) {
            return false;
        }
        if (Objects.equals(record.getParentId(), FileConstants.TOP_PARENT_ID)) {
            return true;
        }
        return checkUpFileAvailable(record.getParentId());
    }

    /**
     * 通过文件ID查询对应的分享ID集合
     *
     * @param allAvailableFileIdList
     * @return
     */
    private List<Long> getShareIdListByFileIdList(List<Long> allAvailableFileIdList) {
        QueryWrapper queryWrapper = Wrappers.query();
        queryWrapper.select("share_id");
        queryWrapper.in("file_id", allAvailableFileIdList);
        List<Long> shareIdList = iShareFileService.listObjs(queryWrapper, value -> (Long) value);
        return shareIdList;
    }

    /**
     * 执行分享文件下载的动作
     * 委托文件模块去做
     *
     * @param context
     */
    private void doDownload(ShareFileDownloadContext context) {
        FileDownloadContext fileDownloadContext = new FileDownloadContext();
        fileDownloadContext.setFileId(context.getFileId());
        fileDownloadContext.setUserId(context.getUserId());
        fileDownloadContext.setResponse(context.getResponse());
        iUserFileService.downloadWithoutCheckUser(fileDownloadContext);
    }

    /**
     * 执行保存我的网盘动作
     * 委托文件模块做文件拷贝的操作
     *
     * @param context
     */
    private void doSaveFiles(ShareSaveContext context) {
        CopyFileContext copyFileContext = new CopyFileContext();
        copyFileContext.setFileIdList(context.getFileIdList());
        copyFileContext.setTargetParentId(context.getTargetParentId());
        copyFileContext.setUserId(context.getUserId());
        iUserFileService.copy(copyFileContext);
    }

    /**
     * 校验文件ID是否属于某一个分享
     *
     * @param shareId
     * @param fileIdList
     */
    private void checkFileIdIsOnShareStatus(Long shareId, List<Long> fileIdList) {
        checkFileIdIsOnShareStatusAndGetAllShareUserFiles(shareId, fileIdList);
    }

    /**
     * 校验文件是否处于分享状态，返回该分享的所有文件列表
     *
     * @param shareId
     * @param fileIdList
     * @return
     */
    private List<YPanUserFileVO> checkFileIdIsOnShareStatusAndGetAllShareUserFiles(Long shareId, List<Long> fileIdList) {
        List<Long> shareFileIdList = getShareFileIdList(shareId);
        if (CollectionUtils.isEmpty(shareFileIdList)) {
            return Lists.newArrayList();
        }
        List<YPanUserFile> allFileRecords = iUserFileService.findAllFileRecordsByFileIdList(shareFileIdList);
        if (CollectionUtils.isEmpty(allFileRecords)) {
            return Lists.newArrayList();
        }
        allFileRecords = allFileRecords.stream()
                .filter(Objects::nonNull)
                .filter(record -> Objects.equals(record.getDelFlag(), DelFlagEnum.NO.getCode()))
                .collect(Collectors.toList());

        List<Long> allFileIdList = allFileRecords.stream().map(YPanUserFile::getFileId).collect(Collectors.toList());

        if (allFileIdList.containsAll(fileIdList)) {
            return iUserFileService.transferVOList(allFileRecords);
        }

        throw new YPanBusinessException(ResponseCode.SHARE_FILE_MISS);
    }

    /**
     * 拼装简单文件分享详情的用户信息
     *
     * @param context
     */
    private void assembleShareSimpleUserInfo(QueryShareSimpleDetailContext context) {
        YPanUser record = iUserService.getById(context.getRecord().getCreateUser());
        if (Objects.isNull(record)) {
            throw new YPanBusinessException("用户信息查询失败");
        }
        ShareUserInfoVO shareUserInfoVO = new ShareUserInfoVO();

        shareUserInfoVO.setUserId(record.getUserId());
        shareUserInfoVO.setUsername(encryptUsername(record.getUsername()));

        context.getVo().setShareUserInfoVO(shareUserInfoVO);
    }

    /**
     * 填充简单分享详情实体信息
     *
     * @param context
     */
    private void assembleMainShareSimpleInfo(QueryShareSimpleDetailContext context) {
        YPanShare record = context.getRecord();
        ShareSimpleDetailVO vo = context.getVo();
        vo.setShareId(record.getShareId());
        vo.setShareName(record.getShareName());
    }

    /**
     * 初始化简单分享详情的VO对象
     *
     * @param context
     */
    private void initShareSimpleVO(QueryShareSimpleDetailContext context) {
        ShareSimpleDetailVO vo = new ShareSimpleDetailVO();
        context.setVo(vo);
    }

    /**
     * 查询分享者的信息
     *
     * @param context
     */
    private void assembleShareUserInfo(QueryShareDetailContext context) {
        YPanUser record = iUserService.getById(context.getRecord().getCreateUser());
        if (Objects.isNull(record)) {
            throw new YPanBusinessException("用户信息查询失败");
        }
        ShareUserInfoVO shareUserInfoVO = new ShareUserInfoVO();

        shareUserInfoVO.setUserId(record.getUserId());
        shareUserInfoVO.setUsername(encryptUsername(record.getUsername()));

        context.getVo().setShareUserInfoVO(shareUserInfoVO);
    }

    /**
     * 加密用户名称
     *
     * @param username
     * @return
     */
    private String encryptUsername(String username) {
        StringBuffer stringBuffer = new StringBuffer(username);
        stringBuffer.replace(YPanConstants.TWO_INT, username.length() - YPanConstants.TWO_INT, YPanConstants.COMMON_ENCRYPT_STR);
        return stringBuffer.toString();
    }

    /**
     * 查询分享对应的文件列表
     * <p>
     * 1、查询分享对应的文件ID集合
     * 2、根据文件ID来查询文件列表信息
     *
     * @param context
     */
    private void assembleShareFilesInfo(QueryShareDetailContext context) {
        List<Long> fileIdList = getShareFileIdList(context.getShareId());

        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setUserId(context.getRecord().getCreateUser());
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());
        queryFileListContext.setFileIdList(fileIdList);

        List<YPanUserFileVO> yPanUserFileVOList = iUserFileService.getFileList(queryFileListContext);
        context.getVo().setYPanUserFileVOList(yPanUserFileVOList);
    }

    /**
     * 查询分享对应的文件ID集合
     *
     * @param shareId
     * @return
     */
    private List<Long> getShareFileIdList(Long shareId) {
        if (Objects.isNull(shareId)) {
            return Lists.newArrayList();
        }
        QueryWrapper queryWrapper = Wrappers.query();
        queryWrapper.select("file_id");
        queryWrapper.eq("share_id", shareId);
        List<Long> fileIdList = iShareFileService.listObjs(queryWrapper, value -> (Long) value);
        return fileIdList;
    }

    /**
     * 查询分享的主体信息
     *
     * @param context
     */
    private void assembleMainShareInfo(QueryShareDetailContext context) {
        YPanShare record = context.getRecord();
        ShareDetailVO vo = context.getVo();
        vo.setShareId(record.getShareId());
        vo.setShareName(record.getShareName());
        vo.setCreateTime(record.getCreateTime());
        vo.setShareDay(record.getShareDay());
        vo.setShareEndTime(record.getShareEndTime());
    }

    /**
     * 初始化文件详情的VO实体
     *
     * @param context
     */
    private void initShareVO(QueryShareDetailContext context) {
        ShareDetailVO vo = new ShareDetailVO();
        context.setVo(vo);
    }

    /**
     * 生成一个短期的分享token
     *
     * @param context
     * @return
     */
    private String generateShareToken(CheckShareCodeContext context) {
        YPanShare record = context.getRecord();
        String token = JwtUtil.generateToken(UUIDUtil.getUUID(), ShareConstants.SHARE_ID, record.getShareId(), ShareConstants.ONE_HOUR_LONG);
        return token;
    }

    /**
     * 校验分享码是不是正确
     *
     * @param context
     */
    private void doCheckShareCode(CheckShareCodeContext context) {
        YPanShare record = context.getRecord();
        if (!Objects.equals(context.getShareCode(), record.getShareCode())) {
            throw new YPanBusinessException("分享码错误");
        }
    }

    /**
     * 检查分享的状态是不是正常
     *
     * @param shareId
     * @return
     */
    private YPanShare checkShareStatus(Long shareId) {
        YPanShare record = getById(shareId);

        if (Objects.isNull(record)) {
            throw new YPanBusinessException(ResponseCode.SHARE_CANCELLED);
        }

        if (Objects.equals(ShareStatusEnum.FILE_DELETED.getCode(), record.getShareStatus())) {
            throw new YPanBusinessException(ResponseCode.SHARE_FILE_MISS);
        }

        if (Objects.equals(ShareDayTypeEnum.PERMANENT_VALIDITY.getCode(), record.getShareDayType())) {
            return record;
        }

        if (record.getShareEndTime().before(new Date())) {
            throw new YPanBusinessException(ResponseCode.SHARE_EXPIRE);
        }

        return record;
    }

    /**
     * 取消文件和分享的关联关系数据
     *
     * @param context
     */
    private void doCancelShareFiles(CancelShareContext context) {
        QueryWrapper queryWrapper = Wrappers.query();
        queryWrapper.in("share_id", context.getShareIdList());
        queryWrapper.eq("create_user", context.getUserId());
        if (!iShareFileService.remove(queryWrapper)) {
            throw new YPanBusinessException("取消分享失败");
        }
    }

    /**
     * 执行取消文件分享的动作
     *
     * @param context
     */
    private void doCancelShare(CancelShareContext context) {
        List<Long> shareIdList = context.getShareIdList();
        if (!removeByIds(shareIdList)) {
            throw new YPanBusinessException("取消分享失败");
        }
    }

    /**
     * 检查用户是否拥有取消对应分享链接的权限
     *
     * @param context
     */
    private void checkUserCancelSharePermission(CancelShareContext context) {
        List<Long> shareIdList = context.getShareIdList();
        Long userId = context.getUserId();
        List<YPanShare> records = listByIds(shareIdList);
        if (CollectionUtils.isEmpty(records)) {
            throw new YPanBusinessException("您无权限操作取消分享的动作");
        }
        for (YPanShare record : records) {
            if (!Objects.equals(userId, record.getCreateUser())) {
                throw new YPanBusinessException("您无权限操作取消分享的动作");
            }
        }
    }

    /**
     * 拼装对应的返回VO
     *
     * @param context
     * @return
     */
    private YPanShareUrlVO assembleShareVO(CreateShareUrlContext context) {
        YPanShare record = context.getRecord();
        YPanShareUrlVO vo = new YPanShareUrlVO();
        vo.setShareId(record.getShareId());
        vo.setShareName(record.getShareName());
        vo.setShareUrl(record.getShareUrl());
        vo.setShareCode(record.getShareCode());
        vo.setShareStatus(record.getShareStatus());
        return vo;
    }

    /**
     * 保存分享和分享文件的关联关系
     *
     * @param context
     */
    private void saveShareFiles(CreateShareUrlContext context) {
        SaveShareFilesContext saveShareFilesContext = new SaveShareFilesContext();
        saveShareFilesContext.setShareId(context.getRecord().getShareId());
        saveShareFilesContext.setShareFileIdList(context.getShareFileIdList());
        saveShareFilesContext.setUserId(context.getUserId());
        iShareFileService.saveShareFiles(saveShareFilesContext);
    }

    /**
     * 拼装分享的实体，并保存到数据库中
     *
     * @param context
     */
    private void saveShare(CreateShareUrlContext context) {
        YPanShare record = new YPanShare();

        record.setShareId(IdUtil.get());
        record.setShareName(context.getShareName());
        record.setShareType(context.getShareType());
        record.setShareDayType(context.getShareDayType());

        Integer shareDay = ShareDayTypeEnum.getShareDayByCode(context.getShareDayType());
        if (Objects.equals(YPanConstants.MINUS_ONE_INT, shareDay)) {
            throw new YPanBusinessException("分享天数非法");
        }

        record.setShareDay(shareDay);
        record.setShareEndTime(DateUtil.offsetDay(new Date(), shareDay));
        record.setShareUrl(createShareUrl(record.getShareId()));
        record.setShareCode(createShareCode());
        record.setShareStatus(ShareStatusEnum.NORMAL.getCode());
        record.setCreateUser(context.getUserId());
        record.setCreateTime(new Date());

        if (!save(record)) {
            throw new YPanBusinessException("保存分享信息失败");
        }

        context.setRecord(record);
    }

    /**
     * 创建分享的分享码
     *
     * @return
     */
    private String createShareCode() {
        return RandomStringUtils.randomAlphabetic(4).toLowerCase();
    }

    /**
     * 创建分享的URL
     *
     * @param shareId
     * @return
     */
    private String createShareUrl(Long shareId) {
        if (Objects.isNull(shareId)) {
            throw new YPanBusinessException("分享的ID不能为空");
        }
        String sharePrefix = config.getSharePrefix();
        if (sharePrefix.lastIndexOf(YPanConstants.SLASH_STR) == YPanConstants.MINUS_ONE_INT.intValue()) {
            sharePrefix += YPanConstants.SLASH_STR;
        }
        return sharePrefix + URLEncoder.encode(IdUtil.encrypt(shareId));
    }
}




