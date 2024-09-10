package org.xu.pan.storage.engine.oss;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import lombok.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xu.pan.storage.engine.core.AbstractStorageEngine;
import org.xu.pan.storage.engine.core.context.DeleteFileContext;
import org.xu.pan.storage.engine.core.context.StoreFileChunkContext;
import org.xu.pan.storage.engine.core.context.StoreFileContext;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 对接阿里云OSS的文件存储引擎实现方案
 */
@Component
public class OSSStorageEngine extends AbstractStorageEngine {


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
