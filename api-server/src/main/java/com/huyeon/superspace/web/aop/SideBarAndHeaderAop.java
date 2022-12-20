package com.huyeon.superspace.web.aop;

import com.huyeon.superspace.web.common.dto.AppHeaderDto;
import com.huyeon.superspace.web.common.dto.SideBarDto;
import com.huyeon.superspace.web.common.service.SideBarAndHeaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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

        SideBarDto blankSideBar = sideBarAndHeaderService.getBlankSideBar(userEmail);
        AppHeaderDto blankHeader = sideBarAndHeaderService.getBlankHeader(userEmail);

        putAll(returnValue, blankSideBar, blankHeader);
    }

    @AfterReturning(value = "cutGroupPage()", returning = "returnValue")
    private void extract(JoinPoint joinPoint, Map<String, Object> returnValue) {
        Object[] args = joinPoint.getArgs();
        String userEmail = (String) args[USER_EMAIL];
        String groupUrl = (String) args[GROUP_URL];

        SideBarDto sideBar = sideBarAndHeaderService.getSideBar(userEmail, groupUrl);
        AppHeaderDto appHeader = sideBarAndHeaderService.getAppHeader(userEmail, groupUrl);

        putAll(returnValue, sideBar, appHeader);
    }

    private void putAll(Map<String, Object> response, SideBarDto sideBar, AppHeaderDto appHeader) {
        response.put("sideBar", sideBar);
        response.put("appHeader", appHeader);
    }
}
