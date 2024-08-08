package com.yupi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.project.common.ErrorCode;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.model.entity.InterfaceInfo;
import com.yupi.project.model.entity.UserInterfaceInfo;
import com.yupi.project.mapper.UserInterfaceInfoMapper;
import com.yupi.project.model.entity.UserInterfaceInfo;
import com.yupi.project.service.UserInterfaceInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author Ee1ex
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
* @createDate 2024-08-08 16:54:51
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService{
    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 创建时，所有参数必须非空
        //接口id和用户id不能为空
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口id不能小于等于0");
            }
            if (userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户id不能小于等于0");
            }
        }
        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于0");
        }
        if (userInterfaceInfo.getTotalNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "调用次数不能小于0");
        }
        if (userInterfaceInfo.getStatus() < 0 || userInterfaceInfo.getStatus() > 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "状态只能是0或1");
        }
        if (ObjectUtils.isNotEmpty(userInterfaceInfo.getId()) && userInterfaceInfo.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id不能小于等于0");
        }

    }


    //统计接口调用次数
    public boolean invokeCount(long interfaceInfoId, long userId) {
        //判断是否为空
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //判断用户是否存在
        UserInterfaceInfo userInterfaceInfo = getOne(new QueryWrapper<UserInterfaceInfo>().eq("interfaceInfoId", interfaceInfoId).eq("userId", userId));
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //判断是否还有调用次数
        if (userInterfaceInfo.getLeftNum() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "调用次数不足");
        }

        //总调用次数统计加一
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId", interfaceInfoId).eq("userId", userId);
        //updateWrapper.gt("leftNum", 0);  //剩下次数为0会出错
        updateWrapper.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1");
        return this.update(updateWrapper);

    }

}




