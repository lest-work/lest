package com.lest.ai.controller;

import com.lest.ai.domain.AiProvider;
import com.lest.ai.service.IAiService;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.core.web.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController extends BaseController
{
    @Autowired
    private IAiService aiService;

    @GetMapping("/provider/list")
    public TableDataInfo providerList(AiProvider provider)
    {
        startPage();
        List<AiProvider> list = aiService.selectProviderList(provider);
        return getDataTable(list);
    }

    @GetMapping("/provider/{providerId}")
    public AjaxResult getProvider(@PathVariable Long providerId)
    {
        return success(aiService.selectProviderById(providerId));
    }

    @PostMapping("/provider")
    public AjaxResult addProvider(@RequestBody AiProvider provider)
    {
        return toAjax(aiService.insertProvider(provider));
    }

    @PutMapping("/provider")
    public AjaxResult editProvider(@RequestBody AiProvider provider)
    {
        return toAjax(aiService.updateProvider(provider));
    }

    @DeleteMapping("/provider/{providerId}")
    public AjaxResult removeProvider(@PathVariable Long providerId)
    {
        return toAjax(aiService.deleteProvider(providerId));
    }

    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody Map<String, Object> params)
    {
        String prompt = (String) params.get("prompt");
        String response = aiService.chat(prompt);
        return success(response);
    }

    @PostMapping("/chat/context")
    public AjaxResult chatWithContext(@RequestBody Map<String, Object> params)
    {
        Long providerId = params.get("providerId") != null ? ((Number) params.get("providerId")).longValue() : null;
        @SuppressWarnings("unchecked")
        List<Map<String, String>> messages = (List<Map<String, String>>) params.get("messages");
        String response = aiService.chatWithContext(providerId, messages);
        return success(response);
    }
}
