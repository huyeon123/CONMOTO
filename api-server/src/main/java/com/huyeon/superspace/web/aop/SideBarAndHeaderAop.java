package com.huyeon.superspace.web.aop;

import com.huyeon.superspace.web.common.service.SideBarAndHeaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
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

    @Pointcut("@annotation(com.huyeon.superspace.web.annotation.ManagerPage)")
    private void cutManagerPage() {
    }

    @AfterReturning(value = "cutNoGroupPage()", returning = "returnValue")
    private void attachInNotGroupPage(JoinPoint joinPoint, Map<String, Object> returnValue) {
        Object[] args = joinPoint.getArgs();
        String userEmail = (String) args[USER_EMAIL];

        Map<String, Object> blankHeaderAndSideBar = sideBarAndHeaderService.getBlankHeaderAndSideBar(userEmail);

        returnValue.putAll(blankHeaderAndSideBar);
    }

    @AfterReturning(value = "cutGroupPage()", returning = "returnValue")
    private void attachInGroupPage(JoinPoint joinPoint, Map<String, Object> returnValue) {
        if (isFailed(returnValue)) return;

        Object[] args = joinPoint.getArgs();
        String userEmail = (String) args[USER_EMAIL];
        String groupUrl = (String) args[GROUP_URL];

        Map<String, Object> headerAndSideBar = sideBarAndHeaderService.getHeaderAndSideBar(userEmail, groupUrl);

        returnValue.putAll(headerAndSideBar);
    }

    private boolean isFailed(Map<String, Object> returnValue) {
        String status = (String) returnValue.get("status");
        return status != null && status.startsWith("fail:");
    }

    @Around(value = "cutManagerPage()")
    private Object attachInManagerPage(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String userEmail = (String) args[USER_EMAIL];
        String groupUrl = (String) args[GROUP_URL];

        String role = sideBarAndHeaderService.getRole(userEmail, groupUrl);
        if (role.equals("ROLE_MANAGER")) return joinPoint.proceed();
        else return Map.of("status", "fail: 접근 권한이 없습니다.");
    }
}
