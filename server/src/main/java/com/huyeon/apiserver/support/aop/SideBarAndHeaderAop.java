package com.huyeon.apiserver.support.aop;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.HeaderDto;
import com.huyeon.apiserver.model.dto.SideBarDto;
import com.huyeon.apiserver.service.SideBarAndHeaderService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Aspect
@Component
@RequiredArgsConstructor
public class SideBarAndHeaderAop {

    private static final int USER_DETAIL = 0;
    private static final int GROUP_URL = 1;

    private final SideBarAndHeaderService sideBarAndHeaderService;

    @Pointcut("execution(* com.huyeon.apiserver.controller.workspace.*.*Page(*, *))")
    private void cutNoGroupPage() {

    }

    @Pointcut("execution(* com.huyeon.apiserver.controller.workspace.*.*Page(*, String, ..))")
    private void cutGroupPage() {}

    @Before("cutNoGroupPage()")
    private void extractNoGroupPageArgs(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        UserDetailsImpl userDetails = (UserDetailsImpl) args[USER_DETAIL];
        Model model = (Model) args[args.length - 1];

        SideBarDto blankSideBar = sideBarAndHeaderService.getBlankSideBar(userDetails.getUser());
        HeaderDto blankHeader = sideBarAndHeaderService.getBlankHeader(userDetails.getUser());

        addAttribute(model, blankSideBar, blankHeader);
    }

    @Before("cutGroupPage()")
    private void extractGroupPageArgs(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        UserDetailsImpl userDetails = (UserDetailsImpl) args[USER_DETAIL];
        String groupUrl = (String) args[GROUP_URL];
        Model model = (Model) args[args.length - 1];

        addSideBardAndHeaderInModel(userDetails, groupUrl, model);
    }

    private void addSideBardAndHeaderInModel(UserDetailsImpl userDetails, String groupUrl, Model model) {
        SideBarDto sideBar = sideBarAndHeaderService.getSideBar(userDetails, groupUrl);
        HeaderDto appHeader = sideBarAndHeaderService.getAppHeader(userDetails.getUser(), groupUrl);

        addAttribute(model, sideBar, appHeader);
    }

    private void addAttribute(Model model, SideBarDto sideBar, HeaderDto appHeader) {
        model.addAttribute("sideBar", sideBar);
        model.addAttribute("appHeader", appHeader);
    }
}
