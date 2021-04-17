package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchUserUseCaseSyncImplTest {

    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final User USER = new User(USER_ID, USERNAME);


    FetchUserUseCaseSyncImpl SUT;
    @Mock
    UsersCache mUserCacheMock;
    @Mock
    FetchUserHttpEndpointSync mFetchUserHttpEndpointSyncMock;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchUserUseCaseSyncImpl(mFetchUserHttpEndpointSyncMock, mUserCacheMock);
        userNotInCached();
        endpointSuccess();
    }


    @Test
    public void fetchUserSync_userNotInCached_correctIdPassedToEndpoint() throws Exception {
        //Arrange
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        //Act
        SUT.fetchUserSync(USER_ID);
        //Assert
        verify(mFetchUserHttpEndpointSyncMock).fetchUserSync(ac.capture());
        assertThat(ac.getValue(), is(USER_ID));
    }

    @Test
    public void fetchUserSync_userNotInCachedEndpointSuccess_successStatus() throws Exception {
        //Arrange
        //Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        //Assert
        assertThat(useCaseResult.getStatus(), is(FetchUserUseCaseSync.Status.SUCCESS));
    }

    @Test
    public void fetchUserSync_userNotInCachedEndpointSuccess_correctUserReturned() throws Exception {
        //Arrange
        //Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);

        //Assert
        assertThat(useCaseResult.getUser(), is(USER));
    }

    @Test
    public void fetchUserSync_userNotInCachedEndpointSuccess_userCached() throws Exception {
        //Arrange
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        //Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        //Assert
        verify(mUserCacheMock).cacheUser(ac.capture());
        assertThat(ac.getValue(), is(USER));
    }

    @Test
    public void fetchUserSync_userNotInCachedEndpointAuthError_failureStatus() throws Exception {
        //Arrange
        endpointAuthError();
        //Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        //Assert
        assertThat(useCaseResult.getStatus(), is(FetchUserUseCaseSync.Status.FAILURE));
    }


    @Test
    public void fetchUserSync_userNotInCachedEndpointAuthError_nullUserReturned() throws Exception {
        //Arrange
        endpointAuthError();
        //Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        //Assert
        assertThat(useCaseResult.getUser(), nullValue());
    }

    @Test
    public void fetchUserSync_userNotInCachedEndpointAuthError_nothingCached() throws Exception {
        //Arrange
        endpointAuthError();
        //Act
        SUT.fetchUserSync(USER_ID);
        //Assert
        verify(mUserCacheMock, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUserSync_userNotInCachedEndpointServerError_failureStatus() throws Exception {
        //Arrange
        endpointServerError();
        //Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        //Assert
        assertThat(useCaseResult.getStatus(), is(FetchUserUseCaseSync.Status.FAILURE));

    }

    @Test
    public void fetchUserSync_userNotInCachedEndpointServerError_nullUserReturned() throws Exception {
        //Arrange
        endpointServerError();
        //Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        //Assert
        assertThat(useCaseResult.getUser(), is(nullValue()));
    }

    @Test
    public void fetchUserSync_userNotInCachedEndpointServerError_nothingCached() throws Exception {
        //Arrange
        endpointServerError();
        //Act
        SUT.fetchUserSync(USER_ID);
        //Assert
        verify(mUserCacheMock, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUserSync_userNotInCachedNetworkError_networkErrorStatus() throws Exception {
        //Arrange
        endpointNetworkError();
        //Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        //Assert
        assertThat(useCaseResult.getStatus(), is(FetchUserUseCaseSync.Status.NETWORK_ERROR));
    }

    @Test
    public void fetchUserSync_userNotInCachedNetworkError_nullUserReturned() throws Exception {
        //Arrange
        endpointNetworkError();
        //Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        //Assert
        assertThat(useCaseResult.getUser(), is(nullValue()));
    }


    @Test
    public void fetchUserSync_userNotInCachedNetworkError_nothingCached() throws Exception {
        //Arrange
        endpointNetworkError();
        //Act
        SUT.fetchUserSync(USER_ID);
        //Assert
        verify(mUserCacheMock, never()).cacheUser(any(User.class));

    }

    @Test
    public void fetchUserSync_userInCached_correctUserIdPassedToCache() throws Exception {
        //Arrange
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        //Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        //Assert
        verify(mUserCacheMock).getUser(ac.capture());
        assertThat(ac.getValue(), is(USER_ID));
    }

    @Test
    public void fetchUserSync_userInCached_successStatus() throws Exception {
        //Arrange
        userInCached();
        //Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        //Assert
        assertThat(useCaseResult.getStatus(), is(FetchUserUseCaseSync.Status.SUCCESS));
    }


    @Test
    public void fetchUserSync_userInCached_endpointNotPolled() throws Exception {
        //Arrange
        userInCached();
        //Act
        SUT.fetchUserSync(USER_ID);
        //Assert
        verify(mFetchUserHttpEndpointSyncMock, never()).fetchUserSync(any(String.class));
    }

    @Test
    public void fetchUserSync_userInCached_correctCachedUserReturned() throws Exception {
        //Arrange
        userInCached();
        //Act
        FetchUserUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);
        //Assert
        assertThat(useCaseResult.getUser(), is(USER));
    }


    private void userInCached() throws Exception {
        when(mUserCacheMock.getUser(any(String.class))).thenReturn(USER);
    }

    private void userNotInCached() throws Exception {
        when(mUserCacheMock.getUser(anyString())).thenReturn(null);
    }

    private void endpointSuccess() throws Exception {
        when(mFetchUserHttpEndpointSyncMock.fetchUserSync(any(String.class))).thenReturn(new FetchUserHttpEndpointSync.EndpointResult(FetchUserHttpEndpointSync.EndpointStatus.SUCCESS, USER_ID, USERNAME));
    }

    private void endpointAuthError() throws Exception {
        when(mFetchUserHttpEndpointSyncMock.fetchUserSync(any(String.class))).thenReturn(new FetchUserHttpEndpointSync.EndpointResult(FetchUserHttpEndpointSync.EndpointStatus.AUTH_ERROR, "", ""));
    }

    private void endpointServerError() throws Exception {
        when(mFetchUserHttpEndpointSyncMock.fetchUserSync(any(String.class))).thenReturn(new FetchUserHttpEndpointSync.EndpointResult(FetchUserHttpEndpointSync.EndpointStatus.GENERAL_ERROR, "", ""));
    }

    private void endpointNetworkError() throws Exception {
        when(mFetchUserHttpEndpointSyncMock.fetchUserSync(any(String.class))).thenThrow(new NetworkErrorException());

    }
}