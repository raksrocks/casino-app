package com.englyn.auth.service;

import com.englyn.auth.model.GameLaunchRequest;
import com.englyn.auth.model.GameResponse;

public interface GameLaunchService {
    GameResponse launchGame(GameLaunchRequest request);
}
