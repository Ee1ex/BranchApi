package com.branch.branchapiinterface;

import com.branch.branchapiclientsdk.client.BranchApiClient;
import com.branch.branchapiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class BranchapiInterfaceApplicationTests {

    @Resource
    private BranchApiClient branchApiClient;



    @Test
    void contextLoads() {
        String name = branchApiClient.getNameByGet("yupi");
        User user = new User();
        user.setUsername("yupi");
        String username = branchApiClient.getUsernameByPost(user);
        System.out.println(name);
        System.out.println(username);

    }

}
