package com.yeyang.redbook.user.biz.rpc;

import com.yeyang.redbook.distributed.id.generator.api.DistributedIdGeneratorFeignApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author: coder
 * @date: 2025/2/13 23:29
 * @version: v1.0.0
 * @description: 分布式 ID 生成服务
 **/
@Component
public class DistributedIdGeneratorRpcService {

    @Resource
    private DistributedIdGeneratorFeignApi distributedIdGeneratorFeignApi;

    /**
     * Leaf 号段模式：redbook ID 业务标识
     */
    private static final String BIZ_TAG_XIAOHASHU_ID = "leaf-segment-redbook-id";

    /**
     * Leaf 号段模式：用户 ID 业务标识
     */
    private static final String BIZ_TAG_USER_ID = "leaf-segment-user-id";

    /**
     * 调用分布式 ID 生成服务生成redbook ID
     *
     * @return
     */
    public String getRedbookId() {
        return distributedIdGeneratorFeignApi.getSegmentId(BIZ_TAG_XIAOHASHU_ID);
    }

    /**
     * 调用分布式 ID 生成服务用户 ID
     *
     * @return
     */
    public String getUserId() {
        return distributedIdGeneratorFeignApi.getSegmentId(BIZ_TAG_USER_ID);
    }
}
