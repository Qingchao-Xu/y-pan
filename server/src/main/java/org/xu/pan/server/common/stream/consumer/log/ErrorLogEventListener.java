package org.xu.pan.server.common.stream.consumer.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.xu.pan.core.utils.IdUtil;
import org.xu.pan.server.common.stream.event.log.ErrorLogEvent;
import org.xu.pan.server.common.stream.channel.PanChannels;
import org.xu.pan.server.modules.log.entity.YPanErrorLog;
import org.xu.pan.server.modules.log.service.IErrorLogService;
import org.xu.pan.stream.core.AbstractConsumer;

import java.util.Date;

/**
 * 系统错误日志监听器
 */
@Component
public class ErrorLogEventListener extends AbstractConsumer {

    @Autowired
    private IErrorLogService iErrorLogService;

    /**
     * 监听系统错误日志事件，并保存到数据库中
     *
     * @param message
     */
    @StreamListener(PanChannels.ERROR_LOG_INPUT)
    public void saveErrorLog(Message<ErrorLogEvent> message) {
        if (isEmptyMessage(message)) {
            return;
        }
        ErrorLogEvent event = message.getPayload();
        YPanErrorLog record = new YPanErrorLog();
        record.setId(IdUtil.get());
        record.setLogContent(event.getErrorMsg());
        record.setLogStatus(0);
        record.setCreateUser(event.getUserId());
        record.setCreateTime(new Date());
        record.setUpdateUser(event.getUserId());
        record.setUpdateTime(new Date());
        iErrorLogService.save(record);
    }

}
