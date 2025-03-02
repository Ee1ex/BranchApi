package com.yupi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.branch.branchapicommon.model.entity.UserInterfaceInfo;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.mapper.UserInterfaceInfoMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class UserInterfaceInfoServiceImplTest {

    @Mock
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @InjectMocks
    private UserInterfaceInfoServiceImpl userInterfaceInfoService;

    private UserInterfaceInfo userInterfaceInfo;

    @Before
    public void setUp() {
        userInterfaceInfo = new UserInterfaceInfo();
        userInterfaceInfo.setId(1L);
        userInterfaceInfo.setUserId(1L);
        userInterfaceInfo.setInterfaceInfoId(1L);
        userInterfaceInfo.setTotalNum(0);
        userInterfaceInfo.setLeftNum(10);
        userInterfaceInfo.setStatus(0);
        userInterfaceInfo.setCreateTime(new Date());
        userInterfaceInfo.setUpdateTime(new Date());
        userInterfaceInfo.setIsDelete(0);
    }

    @Test
    public void testInvokeCountSuccess() {
        Mockito.when(userInterfaceInfoMapper.selectOne(new QueryWrapper<UserInterfaceInfo>()
                .eq("interfaceInfoId", userInterfaceInfo.getInterfaceInfoId())
                .eq("userId", userInterfaceInfo.getUserId()))).thenReturn(userInterfaceInfo);

        boolean result = userInterfaceInfoService.invokeCount(userInterfaceInfo.getInterfaceInfoId(), userInterfaceInfo.getUserId());

        Assert.assertTrue(result);

        Mockito.verify(userInterfaceInfoMapper, Mockito.times(1)).selectOne(new QueryWrapper<UserInterfaceInfo>()
                .eq("interfaceInfoId", userInterfaceInfo.getInterfaceInfoId())
                .eq("userId", userInterfaceInfo.getUserId()));

        Mockito.verify(userInterfaceInfoMapper, Mockito.times(1)).update(userInterfaceInfo, new QueryWrapper<UserInterfaceInfo>()
                .eq("interfaceInfoId", userInterfaceInfo.getInterfaceInfoId())
                .eq("userId", userInterfaceInfo.getUserId()));
    }

    @Test(expected = BusinessException.class)
    public void testInvokeCountWithNegativeUserId() {
        userInterfaceInfoService.invokeCount(userInterfaceInfo.getInterfaceInfoId(), -1L);
    }

    @Test(expected = BusinessException.class)
    public void testInvokeCountWithNonExistentUser() {
        Mockito.when(userInterfaceInfoMapper.selectOne(new QueryWrapper<UserInterfaceInfo>()
                .eq("interfaceInfoId", userInterfaceInfo.getInterfaceInfoId())
                .eq("userId", userInterfaceInfo.getUserId()))).thenReturn(null);

        userInterfaceInfoService.invokeCount(userInterfaceInfo.getInterfaceInfoId(), userInterfaceInfo.getUserId());
    }

    @Test(expected = BusinessException.class)
    public void testInvokeCountNoLeftNum() {
        userInterfaceInfo.setLeftNum(0);
        Mockito.when(userInterfaceInfoMapper.selectOne(new QueryWrapper<UserInterfaceInfo>()
                .eq("interfaceInfoId", userInterfaceInfo.getInterfaceInfoId())
                .eq("userId", userInterfaceInfo.getUserId()))).thenReturn(userInterfaceInfo);

        userInterfaceInfoService.invokeCount(userInterfaceInfo.getInterfaceInfoId(), userInterfaceInfo.getUserId());
    }
}
