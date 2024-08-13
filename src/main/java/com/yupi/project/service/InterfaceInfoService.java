package com.yupi.project.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.branchapicommon.model.entity.InterfaceInfo;
import com.branch.branchapicommon.model.entity.InterfaceInfo;

import java.util.List;

/**
* @author Ee1ex
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-08-05 18:13:15
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);


}
