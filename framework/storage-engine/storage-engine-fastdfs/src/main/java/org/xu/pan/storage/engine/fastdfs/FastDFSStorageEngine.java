package org.xu.pan.storage.engine.fastdfs;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xu.pan.core.constants.YPanConstants;
import org.xu.pan.core.exception.YPanFrameworkException;
import org.xu.pan.core.utils.FileUtils;
import org.xu.pan.storage.engine.core.AbstractStorageEngine;
import org.xu.pan.storage.engine.core.context.*;
import org.xu.pan.storage.engine.fastdfs.config.FastDFSStorageEngineConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * FastDFS文件存储引擎的实现方案
 */
@Component
public class FastDFSStorageEngine extends AbstractStorageEngine {

    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FastDFSStorageEngineConfig config;


    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        StorePath storePath = client.uploadFile(config.getGroup(), context.getInputStream(), context.getTotalSize(), FileUtils.getFileExtName(context.getFilename()));
        context.setRealPath(storePath.getFullPath());
    }

    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {
        List<String> realFilePathList = context.getRealFilePathList();
        if (CollectionUtils.isNotEmpty(realFilePathList)) {
            realFilePathList.stream().forEach(client::deleteFile);
        }
    }

    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException {
        throw new YPanFrameworkException("FastDFS不支持分片上传的操作");
    }

    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {
        throw new YPanFrameworkException("FastDFS不支持分片上传的操作");
    }

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException {
        String realPath = context.getRealPath();
        String group = realPath.substring(YPanConstants.ZERO_INT, realPath.indexOf(YPanConstants.SLASH_STR));
        String path = realPath.substring(realPath.indexOf(YPanConstants.SLASH_STR) + YPanConstants.ONE_INT);

        DownloadByteArray downloadByteArray = new DownloadByteArray();
        byte[] bytes = client.downloadFile(group, path, downloadByteArray);

        OutputStream outputStream = context.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }
}
