package com.techyourchance.testdoublesfundamentals.exercise4;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.techyourchance.testdoublesfundamentals.example4.LoginUseCaseSync;
import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

public class FetchUserProfileUseCaseSyncTest {

    public static final String USER_ID = "userId";
    public static final String FULL_NAME = "fullName";
    public static final String IMAGE_URL = "imageUrl";
    FetchUserProfileUseCaseSync SUT;
     UserCacheTd userCacheTd;
     UserProfileHttpEndpointSyncTd userProfileHttpEndpointSyncTd;


    @Before
    public void setUp() throws Exception {
        userCacheTd = new UserCacheTd();
        userProfileHttpEndpointSyncTd = new UserProfileHttpEndpointSyncTd();
        SUT = new FetchUserProfileUseCaseSync(userProfileHttpEndpointSyncTd, userCacheTd);
    }

    @Test
    public void fetchUserProfileSync_success_userIdPassedToEndpoint() throws Exception {
        SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(userProfileHttpEndpointSyncTd.mUserId, is(USER_ID));

    }

    @Test
    public void fetchUserProfileSync_success_userCached() throws Exception {
        SUT.fetchUserProfileSync(USER_ID);
        User cachedUser = userCacheTd.getUser(USER_ID);
        Assert.assertThat(cachedUser.getUserId(), is(USER_ID));
        Assert.assertThat(cachedUser.getImageUrl(), is(IMAGE_URL));
        Assert.assertThat(cachedUser.getFullName(), is(FULL_NAME));
    }

    @Test
    public void fetchUserProfileSync_generalError_userNotCached() throws Exception {
        userProfileHttpEndpointSyncTd.mGeneralError = true;
        SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(userCacheTd.getUser(USER_ID), is(nullValue()));
    }

    @Test
    public void fetchUserProfileSync_authError_userNotCached() throws Exception {
        userProfileHttpEndpointSyncTd.mAuthError = true;
        SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(userCacheTd.getUser(USER_ID), is(nullValue()));

    }

    @Test
    public void fetchUserProfileSync_serverError_userNotCached() throws Exception {
        userProfileHttpEndpointSyncTd.mIsServerError = true;
        SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(userCacheTd.getUser(USER_ID), is(nullValue()));
    }

    @Test
    public void fetchUserProfileSync_success_successReturned() throws Exception {
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.SUCCESS));
    }

    @Test
    public void fetchUserProfileSync_serverError_failureReturned() throws Exception {
        userProfileHttpEndpointSyncTd.mIsServerError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserProfileSync_authError_failureReturned() throws Exception {
        userProfileHttpEndpointSyncTd.mAuthError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserProfileSync_generalError_failureReturned() throws Exception {
        userProfileHttpEndpointSyncTd.mGeneralError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserProfileSync_networkError_networkErrorReturned() throws Exception {
        userProfileHttpEndpointSyncTd.mIsNetworkError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }


    private static class UserCacheTd implements UsersCache {
        private List<User> mUsers = new ArrayList<>(1);

        @Override
        public void cacheUser(User user) {
            User cachedUser = getUser(user.getUserId());
            if (cachedUser != null) {
                mUsers.remove(cachedUser);
            }
            mUsers.add(user);
        }

        @Nullable
        @Override
        public User getUser(String userId) {
            for (User user : mUsers) {
                if (user.getUserId().equals(userId)) {
                    return user;
                }
            }
            return null;
        }
    }

    private static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync {
        public String mUserId = "";
        public boolean mGeneralError;
        public boolean mAuthError;
        public boolean mIsServerError;
        public boolean mIsNetworkError;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            mUserId = userId;
            if (mGeneralError) {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", "", "");
            } else if (mAuthError) {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", "", "");
            } else if (mIsServerError) {
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", "", "");
            } else if (mIsNetworkError) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID, FULL_NAME, IMAGE_URL);
            }
        }
    }
}