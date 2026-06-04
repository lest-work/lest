package com.lest.openapi.mapper;

import com.lest.openapi.domain.ApiKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ApiKeyMapper
{
    List<ApiKey> selectApiKeyList(ApiKey apiKey);
    ApiKey selectApiKeyById(Long keyId);
    ApiKey selectApiKeyByValue(@Param("apiKey") String apiKey);
    int insertApiKey(ApiKey apiKey);
    int updateApiKey(ApiKey apiKey);
    int deleteApiKeyById(Long keyId);
}
