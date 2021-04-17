package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.example7.eventbus.LoggedInEvent;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateUsernameUseCaseSyncTest {

    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";

    UpdateUsernameUseCaseSync SUT;
    @Mock
    EventBusPoster mEventBusPosterMock;
    @Mock
    UpdateUsernameHttpEndpointSync mUpdateUsernameHttpEndpointSyncMock;
    @Mock
    UsersCache mUserCacheMock;

    @Before
    public void setUp() throws Exception {
        SUT = new UpdateUsernameUseCaseSync(mUpdateUsernameHttpEndpointSyncMock, mUserCacheMock, mEventBusPosterMock);
        success();
    }

    @Test
    public void updateUserName_success_userIdAndUserNamePassedToEndpoint() throws Exception {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(mUpdateUsernameHttpEndpointSyncMock, times(1)).updateUsername(ac.capture(), ac.capture());
        List<String> acList = ac.getAllValues();
        Assert.assertThat(acList.get(0), is(USER_ID));
        Assert.assertThat(acList.get(1), is(USERNAME));
    }

    @Test
    public void updateUserName_success_userCached() throws Exception {
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(mUserCacheMock).cacheUser(ac.capture());
        User user = ac.getValue();
        Assert.assertThat(user.getUserId(), is(USER_ID));
        Assert.assertThat(user.getUsername(), is(USERNAME));
    }

    @Test
    public void updateUsername_generalError_userNotCached() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mUserCacheMock);
    }

    @Test
    public void updateUsername_serverError_userNotCached() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mUserCacheMock);
    }

    @Test
    public void updateUsername_authError_userNotCached() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mUserCacheMock);
    }

    @Test
    public void updateUserName_success_loggedInEventPosted() throws Exception {
        ArgumentCaptor<Object> ac = ArgumentCaptor.forClass(Object.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(mEventBusPosterMock).postEvent(ac.capture());
        Object obj = ac.getValue();
        Assert.assertThat(obj, instanceOf(UserDetailsChangedEvent.class));
    }

    @Test
    public void updateUserName_generalError_loggedInEventNoPosted() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mEventBusPosterMock);
    }

    @Test
    public void updateUserName_authError_loggedInEventNoPosted() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mEventBusPosterMock);
    }

    @Test
    public void updateUserName_serverError_loggedInEventNoPosted() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mEventBusPosterMock);
    }

    @Test
    public void updateUserName_success_successReturned() throws Exception {
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult = SUT.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(useCaseResult, is(UpdateUsernameUseCaseSync.UseCaseResult.SUCCESS));
    }


    @Test
    public void updateUsername_serverError_failureReturned() throws Exception {
        serverError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_authError_failureReturned() throws Exception {
        authError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_generalError_failureReturned() throws Exception {
        generalError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_networkError_failureReturned() throws Exception {
        networkError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }


    private void success() throws Exception {
        when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS, USER_ID, USERNAME));
    }


    private void generalError() throws Exception {
        when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR, "", ""));
    }

    private void authError() throws Exception {
        when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.AUTH_ERROR, "", ""));
    }

    private void serverError() throws Exception {
        when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString())).thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.SERVER_ERROR, "", ""));
    }

    private void networkError() throws Exception {
        doThrow(new NetworkErrorException()).when(mUpdateUsernameHttpEndpointSyncMock).updateUsername(anyString(), anyString());
    }


}