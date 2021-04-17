package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

public class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync {

    private FetchUserHttpEndpointSync mFetchUserHttpEndpointSync;
    private UsersCache mUsersCache;

    public FetchUserUseCaseSyncImpl(FetchUserHttpEndpointSync fetchUserHttpEndpointSync, UsersCache usersCache) {
        this.mFetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
        this.mUsersCache = usersCache;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {
        FetchUserHttpEndpointSync.EndpointResult mEndpointResult;
        if (mUsersCache.getUser(userId) != null) {
            return new UseCaseResult(Status.SUCCESS, mUsersCache.getUser(userId));
        }

        try {
            mEndpointResult = mFetchUserHttpEndpointSync.fetchUserSync(userId);
        } catch (NetworkErrorException e) {
            return new UseCaseResult(Status.NETWORK_ERROR, null);
        }

        if (mEndpointResult.getStatus() == FetchUserHttpEndpointSync.EndpointStatus.SUCCESS) {
            User user = new User(mEndpointResult.getUserId(), mEndpointResult.getUsername());
            mUsersCache.cacheUser(user);
            return new UseCaseResult(Status.SUCCESS, user);
        } else if (mEndpointResult.getStatus() == FetchUserHttpEndpointSync.EndpointStatus.AUTH_ERROR || mEndpointResult.getStatus() == FetchUserHttpEndpointSync.EndpointStatus.GENERAL_ERROR) {
            return new UseCaseResult(Status.FAILURE, null);
        }
        throw new RuntimeException("Invalid status");
    }
}
