package com.yeyang.redbook.comment.biz.rpc;

import com.yeyang.redbook.distributed.id.generator.api.DistributedIdGeneratorFeignApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author: coder
 * @date: 2025/2/13 23:29
 * @version: v1.0.0
 * @description: 分布式 ID 服务
 **/
@Component
public class DistributedIdGeneratorRpcService {

    @Resource
    private DistributedIdGeneratorFeignApi distributedIdGeneratorFeignApi;

    /**
     * 生成评论 ID
     *
     * @return
     */
    public String generateCommentId() {
        return distributedIdGeneratorFeignApi.getSegmentId("leaf-segment-comment-id");
    }

}
