package com.yeyang.redbook.distributed.id.generator.biz.core.segment.dao;

import com.yeyang.redbook.distributed.id.generator.biz.core.segment.model.LeafAlloc;

import java.util.List;

public interface IDAllocDao {
     List<LeafAlloc> getAllLeafAllocs();
     LeafAlloc updateMaxIdAndGetLeafAlloc(String tag);
     LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc);
     List<String> getAllTags();
}
