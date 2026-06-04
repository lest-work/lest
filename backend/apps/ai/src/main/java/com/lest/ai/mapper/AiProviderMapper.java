package com.lest.ai.mapper;

import com.lest.ai.domain.AiProvider;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AiProviderMapper
{
    List<AiProvider> selectProviderList(AiProvider provider);
    AiProvider selectProviderById(Long providerId);
    AiProvider selectEnabledProvider();
    int insertProvider(AiProvider provider);
    int updateProvider(AiProvider provider);
    int deleteProviderById(Long providerId);
}
