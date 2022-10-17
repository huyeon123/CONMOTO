package com.huyeon.superspace.web.aop;

import com.huyeon.superspace.web.common.dto.AppHeaderDto;
import com.huyeon.superspace.web.common.dto.SideBarDto;
import com.huyeon.superspace.global.model.UserDetailsImpl;
import com.huyeon.superspace.web.common.service.SideBarAndHeaderService;
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

    @Pointcut("@annotation(com.huyeon.superspace.web.annotation.NotGroupPage)")
    private void cutNoGroupPage() {

    }

    @Pointcut("@annotation(com.huyeon.superspace.web.annotation.GroupPage)")
    private void cutGroupPage() {}

    @Before("cutNoGroupPage()")
    private void extractNoGroupPageArgs(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        UserDetailsImpl userDetails = (UserDetailsImpl) args[USER_DETAIL];
        Model model = (Model) args[args.length - 1];

        SideBarDto blankSideBar = sideBarAndHeaderService.getBlankSideBar(userDetails.getUser());
        AppHeaderDto blankHeader = sideBarAndHeaderService.getBlankHeader(userDetails.getUser());

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
        AppHeaderDto appHeader = sideBarAndHeaderService.getAppHeader(userDetails.getUser(), groupUrl);

        addAttribute(model, sideBar, appHeader);
    }

    private void addAttribute(Model model, SideBarDto sideBar, AppHeaderDto appHeader) {
        model.addAttribute("sideBar", sideBar);
        model.addAttribute("appHeader", appHeader);
    }
}
