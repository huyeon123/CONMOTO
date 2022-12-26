package com.huyeon.superspace.web.aop;

import com.huyeon.superspace.web.common.service.SideBarAndHeaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SideBarAndHeaderAop {

    private static final int USER_EMAIL = 0;
    private static final int GROUP_URL = 1;

    private final SideBarAndHeaderService sideBarAndHeaderService;

    @Pointcut("@annotation(com.huyeon.superspace.web.annotation.NotGroupPage)")
    private void cutNoGroupPage() {

    }

    @Pointcut("@annotation(com.huyeon.superspace.web.annotation.GroupPage)")
    private void cutGroupPage() {
    }

    @AfterReturning(value = "cutNoGroupPage()", returning = "returnValue")
    private void extractNoGroupPageArgs(JoinPoint joinPoint, Map<String, Object> returnValue) {
        Object[] args = joinPoint.getArgs();
        String userEmail = (String) args[USER_EMAIL];

        Map<String, Object> blankHeaderAndSideBar = sideBarAndHeaderService.getBlankHeaderAndSideBar(userEmail);

        returnValue.putAll(blankHeaderAndSideBar);
    }

    @AfterReturning(value = "cutGroupPage()", returning = "returnValue")
    private void extract(JoinPoint joinPoint, Map<String, Object> returnValue) {
        Object[] args = joinPoint.getArgs();
        String userEmail = (String) args[USER_EMAIL];
        String groupUrl = (String) args[GROUP_URL];

        Map<String, Object> headerAndSideBar = sideBarAndHeaderService.getHeaderAndSideBar(userEmail, groupUrl);

        returnValue.putAll(headerAndSideBar);
    }
}
