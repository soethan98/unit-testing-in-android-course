package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchReputationUseCaseSyncTest {

    FetchReputationUseCaseSync SUT;
    private final int REPUTATION = 1;

    @Mock
    GetReputationHttpEndpointSync mGetReputationHttpEndpointSyncMock;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchReputationUseCaseSync(mGetReputationHttpEndpointSyncMock);
        endpointSuccess();
    }

    @Test
    public void fetchReputationSync_success_successStatus() {
        //Arrange
        //Act
        UseCaseResult useCaseResult = SUT.fetchReputationSync();
        //Asset
        assertThat(useCaseResult.getStatus(), is(UseCaseResult.Status.SUCCESS));
    }

    @Test
    public void fetchReputationSync_success_correctReputationReturned() {
        //Arrange
        //Act
        UseCaseResult useCaseResult = SUT.fetchReputationSync();
        //Asset
        assertThat(useCaseResult.getReputation(), is(REPUTATION));

    }

    @Test
    public void fetchReputationSync_generalError_failureStatus() {
        //Arrange
        endpointFailedGeneralError();
        //Act
        UseCaseResult useCaseResult = SUT.fetchReputationSync();
        //Asset
        assertThat(useCaseResult.getStatus(), is(UseCaseResult.Status.FAILURE));
    }

    @Test
    public void fetchReputationSync_generalError_nonReputationReturned() {
        //Arrange
        endpointFailedGeneralError();
        //Act
        UseCaseResult useCaseResult = SUT.fetchReputationSync();
        //Asset
        assertThat(useCaseResult.getReputation(), is(0));
    }

    @Test
    public void fetchReputationSync_networkError_failureStatus() {
        //Arrange
        endpointFailedNetworkError();
        //Act
        UseCaseResult useCaseResult = SUT.fetchReputationSync();
        //Asset
        assertThat(useCaseResult.getStatus(), is(UseCaseResult.Status.NETWORK_ERROR));
    }

    @Test
    public void fetchReputationSync_networkError_nonReputationReturned() {
        //Arrange
        endpointFailedNetworkError();
        //Act
        UseCaseResult useCaseResult = SUT.fetchReputationSync();
        //Asset
        assertThat(useCaseResult.getReputation(), is(0));
    }


    private void endpointSuccess() {
        when(mGetReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.SUCCESS, 1));
    }

    private void endpointFailedGeneralError() {
        when(mGetReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR, 0));
    }

    private void endpointFailedNetworkError() {
        when(mGetReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.NETWORK_ERROR, 0));

    }
}