package org.xu.pan.server.common.stream.consumer.share;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.xu.pan.server.common.stream.channel.PanChannels;
import org.xu.pan.server.common.stream.event.file.DeleteFileEvent;
import org.xu.pan.server.common.stream.event.file.FileRestoreEvent;
import org.xu.pan.server.modules.file.entity.YPanUserFile;
import org.xu.pan.server.modules.file.enums.DelFlagEnum;
import org.xu.pan.server.modules.file.service.IUserFileService;
import org.xu.pan.server.modules.share.service.IShareService;
import org.xu.pan.stream.core.AbstractConsumer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 监听文件状态变更导致分享状态变更的处理器
 */
@Component
public class ShareStatusChangeListener extends AbstractConsumer {

    @Autowired
    private IUserFileService iUserFileService;

    @Autowired
    private IShareService iShareService;

    /**
     * 监听文件被删除之后，刷新所有受影响的分享的状态
     *
     * @param message
     */
    @StreamListener(PanChannels.DELETE_FILE_INPUT)
    public void changeShare2FileDeleted(Message<DeleteFileEvent> message) {
        if (isEmptyMessage(message)) {
            return;
        }
        DeleteFileEvent event = message.getPayload();
        List<Long> fileIdList = event.getFileIdList();
        if (CollectionUtils.isEmpty(fileIdList)) {
            return;
        }
        List<YPanUserFile> allRecords = iUserFileService.findAllFileRecordsByFileIdList(fileIdList);

        List<Long> allAvailableFileIdList = allRecords.stream()
                .filter(record -> Objects.equals(record.getDelFlag(), DelFlagEnum.NO.getCode()))
                .map(YPanUserFile::getFileId)
                .collect(Collectors.toList());
        allAvailableFileIdList.addAll(fileIdList);
        iShareService.refreshShareStatus(allAvailableFileIdList);
    }

    /**
     * 监听文件被还原后，刷新所有受影响的分享的状态
     *
     * @param message
     */
    @StreamListener(PanChannels.FILE_RESTORE_INPUT)
    public void changeShare2Normal(Message<FileRestoreEvent> message) {
        if (isEmptyMessage(message)) {
            return;
        }
        FileRestoreEvent event = message.getPayload();
        List<Long> fileIdList = event.getFileIdList();
        if (CollectionUtils.isEmpty(fileIdList)) {
            return;
        }
        List<YPanUserFile> allRecords = iUserFileService.findAllFileRecordsByFileIdList(fileIdList);
        List<Long> allAvailableFileIdList = allRecords.stream()
                .filter(record -> Objects.equals(record.getDelFlag(), DelFlagEnum.NO.getCode()))
                .map(YPanUserFile::getFileId)
                .collect(Collectors.toList());
        allAvailableFileIdList.addAll(fileIdList);
        iShareService.refreshShareStatus(allAvailableFileIdList);
    }

}
