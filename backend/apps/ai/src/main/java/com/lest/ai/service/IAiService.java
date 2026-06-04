package com.lest.ai.service;

import com.lest.ai.domain.AiProvider;
import java.util.List;
import java.util.Map;

public interface IAiService
{
    List<AiProvider> selectProviderList(AiProvider provider);
    AiProvider selectProviderById(Long providerId);
    int insertProvider(AiProvider provider);
    int updateProvider(AiProvider provider);
    int deleteProvider(Long providerId);
    String chat(String prompt);
    String chatWithContext(Long providerId, List<Map<String, String>> messages);
}
