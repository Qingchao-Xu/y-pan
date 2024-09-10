package org.xu.pan.storage.engine.fastdfs;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xu.pan.storage.engine.core.AbstractStorageEngine;
import org.xu.pan.storage.engine.core.context.DeleteFileContext;
import org.xu.pan.storage.engine.core.context.StoreFileChunkContext;
import org.xu.pan.storage.engine.core.context.StoreFileContext;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * FastDFS文件存储引擎的实现方案
 */
@Component
public class FastDFSStorageEngine extends AbstractStorageEngine {


    @Override
    protected void doStore(StoreFileContext context) throws IOException {

    }

    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {

    }

    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException {

    }
}
