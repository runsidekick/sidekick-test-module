package com.runsidekick.testmode.service.impl;

import com.runsidekick.testmode.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author yasin.kalafat
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Value("${sidekick.rest.apikey:}")
    private String apiToken;

    @Override
    public String getApiToken() {
        return apiToken;
    }

}
