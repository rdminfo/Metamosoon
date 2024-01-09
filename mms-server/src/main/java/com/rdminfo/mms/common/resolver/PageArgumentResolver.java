package com.rdminfo.mms.common.resolver;

import cn.hutool.core.util.StrUtil;
import com.rdminfo.mms.common.annotation.PageInfo;
import com.rdminfo.mms.common.constants.RegexConstant;
import com.rdminfo.mms.common.result.MPage;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * 分页数据参数解析
 *
 * @author rdminfo 2023/12/04 9:42
 */
@Component
public class PageArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(PageInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory){
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        String pageNumP = request.getParameter("pageCurrent");
        String pageSizeP = request.getParameter("pageSize");
        int pageCurrent = 1;
        int pageSize = 10;
        if (StrUtil.isNotEmpty(pageNumP) && pageNumP.matches(RegexConstant.INTEGER))
            pageCurrent = Integer.parseInt(pageNumP);

        if (StrUtil.isNotEmpty(pageSizeP) && pageNumP.matches(RegexConstant.INTEGER))
            pageSize = Integer.parseInt(pageSizeP);

        return new MPage(pageCurrent, pageSize);
    }
}
