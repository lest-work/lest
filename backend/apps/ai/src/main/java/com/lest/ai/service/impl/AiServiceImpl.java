package com.lest.ai.service.impl;

import com.lest.ai.domain.AiProvider;
import com.lest.ai.mapper.AiProviderMapper;
import com.lest.ai.service.IAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AiServiceImpl implements IAiService
{
    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);

    @Autowired
    private AiProviderMapper aiProviderMapper;

    @Override
    public List<AiProvider> selectProviderList(AiProvider provider)
    {
        return aiProviderMapper.selectProviderList(provider);
    }

    @Override
    public AiProvider selectProviderById(Long providerId)
    {
        return aiProviderMapper.selectProviderById(providerId);
    }

    @Override
    public int insertProvider(AiProvider provider)
    {
        provider.setCreateAt(new Date());
        return aiProviderMapper.insertProvider(provider);
    }

    @Override
    public int updateProvider(AiProvider provider)
    {
        provider.setUpdateAt(new Date());
        return aiProviderMapper.updateProvider(provider);
    }

    @Override
    public int deleteProvider(Long providerId)
    {
        return aiProviderMapper.deleteProviderById(providerId);
    }

    @Override
    public String chat(String prompt)
    {
        AiProvider provider = aiProviderMapper.selectEnabledProvider();
        if (provider == null)
        {
            return "AI provider not configured. Please configure an AI provider first.";
        }
        return "[AI Response from " + provider.getProviderName() + " (" + provider.getModel() + ")] " + prompt + " - This is a placeholder response. Implement actual AI API call here.";
    }

    @Override
    public String chatWithContext(Long providerId, List<Map<String, String>> messages)
    {
        AiProvider provider = providerId != null ? aiProviderMapper.selectProviderById(providerId) : aiProviderMapper.selectEnabledProvider();
        if (provider == null)
        {
            return "AI provider not configured.";
        }
        return "[AI Response] Context-aware response placeholder. Provider: " + provider.getProviderName() + ", Model: " + provider.getModel();
    }
}
