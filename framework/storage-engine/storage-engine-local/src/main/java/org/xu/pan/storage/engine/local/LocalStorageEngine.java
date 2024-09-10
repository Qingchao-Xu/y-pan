package org.xu.pan.storage.engine.local;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xu.pan.core.utils.FileUtils;
import org.xu.pan.storage.engine.core.AbstractStorageEngine;
import org.xu.pan.storage.engine.core.context.DeleteFileContext;
import org.xu.pan.storage.engine.core.context.MergeFileContext;
import org.xu.pan.storage.engine.core.context.StoreFileChunkContext;
import org.xu.pan.storage.engine.core.context.StoreFileContext;
import org.xu.pan.storage.engine.local.config.LocalStorageEngineConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * 本地的文件存储引擎实现方案
 */
@Component
public class LocalStorageEngine extends AbstractStorageEngine {

    @Autowired
    private LocalStorageEngineConfig config;


    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        String basePath = config.getRootFilePath();
        String realFilePath = FileUtils.generateStoreFileRealPath(basePath, context.getFilename());
        FileUtils.writeStream2File(context.getInputStream(), new File(realFilePath), context.getTotalSize());
        context.setRealPath(realFilePath);
    }

    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {
        FileUtils.deleteFiles(context.getRealFilePathList());
    }

    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException {
        String basePath = config.getRootFileChunkPath();
        String realFilePath = FileUtils.generateStoreFileChunkRealPath(basePath, context.getIdentifier(), context.getChunkNumber());
        FileUtils.writeStream2File(context.getInputStream(), new File(realFilePath), context.getTotalSize());
        context.setRealPath(realFilePath);
    }

    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {
        String basePath = config.getRootFilePath();
        String realFilePath = FileUtils.generateStoreFileRealPath(basePath, context.getFilename());
        FileUtils.createFile(new File(realFilePath));
        List<String> chunkPaths = context.getRealPathList();
        for (String chunkPath : chunkPaths) {
            FileUtils.appendWrite(Paths.get(realFilePath), new File(chunkPath).toPath());
        }
        FileUtils.deleteFiles(chunkPaths);
        context.setRealPath(realFilePath);
    }
}
