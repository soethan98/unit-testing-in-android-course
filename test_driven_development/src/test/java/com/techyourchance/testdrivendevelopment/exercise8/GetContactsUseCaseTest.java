package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class GetContactsUseCaseTest {

    @Mock
    GetContactsHttpEndpoint mGetContactsEndpointMock;
    @Mock
    GetContactsUseCase.Listener mListenerMock1;
    @Mock
    GetContactsUseCase.Listener mListenerMock2;

    @Captor
    ArgumentCaptor<List<Contact>> mAcContactList;

    private GetContactsUseCase SUT;

    public static final double AGE = 10.0;
    public static final String IMAGE_URL = "image_url";
    public static final String FULL_NUMBER = "full_number";
    public static final String FULL_NAME = "full_name";
    public static final String ID = "id";
    public static final String FILTER_TERM = "filter_term";


    @Before
    public void setUp() throws Exception {
        SUT = new GetContactsUseCase(mGetContactsEndpointMock);
        success();
    }

    @Test
    public void GetContacts_correctTermPassedToEndpoint() throws Exception {
        //Arrange
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        //Act
        SUT.fetchContactsAndNotify(FILTER_TERM);
        //Asset
        verify(mGetContactsEndpointMock).getContacts(ac.capture(), any(GetContactsHttpEndpoint.Callback.class));
        assertThat(ac.getValue(), is(FILTER_TERM));

    }

    @Test
    public void GetContacts_success_observerNotifiedWithCorrectData() throws Exception {
        //Arrange
        //Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.fetchContactsAndNotify(FILTER_TERM);
        //Asset
        verify(mListenerMock1).onContactItemsFetched(mAcContactList.capture());
        verify(mListenerMock2).onContactItemsFetched(mAcContactList.capture());
        List<List<Contact>> captured = mAcContactList.getAllValues();
        List<Contact> capture1 = captured.get(0);
        List<Contact> capture2 = captured.get(1);
        assertThat(capture1, is(getContact()));
        assertThat(capture2, is(getContact()));

    }

    @Test
    public void GetContacts_success_unsubscribeObserverNotNotified() throws Exception {
        //Arrange
        //Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.unregisterListener(mListenerMock2);
        SUT.fetchContactsAndNotify(FILTER_TERM);
        //Asset
        verify(mListenerMock1).onContactItemsFetched(any(List.class));
        verifyNoMoreInteractions(mListenerMock2);
    }

    @Test
    public void GetContacts_generalError_subscribeObserverNotifiedWithFailure() throws Exception {
        //Arrange
        ArgumentCaptor<GetContactsUseCase.FailureReason> ac = ArgumentCaptor.forClass(GetContactsUseCase.FailureReason.class);
        generalError();
        //Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.fetchContactsAndNotify(FILTER_TERM);
        //Asset
        verify(mListenerMock1).onContactFetchFailed(ac.capture());
        verify(mListenerMock2).onContactFetchFailed(ac.capture());
        assertThat(ac.getValue(), is(GetContactsUseCase.FailureReason.GENERAL_ERROR));

    }

    @Test
    public void GetContacts_networkError_subscribeObserverNotifiedWithFailure() throws Exception {
        ArgumentCaptor<GetContactsUseCase.FailureReason> ac = ArgumentCaptor.forClass(GetContactsUseCase.FailureReason.class);
        //Arrange
        networkError();
        //Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.fetchContactsAndNotify(FILTER_TERM);
        //Asset
        verify(mListenerMock1).onContactFetchFailed(ac.capture());
        verify(mListenerMock2).onContactFetchFailed(ac.capture());
        assertThat(ac.getValue(), is(GetContactsUseCase.FailureReason.NETWORK_ERROR));

    }


    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                GetContactsHttpEndpoint.Callback callback = (GetContactsHttpEndpoint.Callback) args[1];
                callback.onGetContactsSucceeded(getContactScheme());
                return null;
            }
        }).when(mGetContactsEndpointMock).getContacts(anyString(), any(GetContactsHttpEndpoint.Callback.class));
    }

    private void generalError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                GetContactsHttpEndpoint.Callback callback = (GetContactsHttpEndpoint.Callback) args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.GENERAL_ERROR);
                return null;
            }
        }).when(mGetContactsEndpointMock).getContacts(anyString(), any(GetContactsHttpEndpoint.Callback.class));
    }

    private void networkError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                GetContactsHttpEndpoint.Callback callback = (GetContactsHttpEndpoint.Callback) args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.NETWORK_ERROR);
                return null;
            }
        }).when(mGetContactsEndpointMock).getContacts(anyString(), any(GetContactsHttpEndpoint.Callback.class));
    }

    private List<ContactSchema> getContactScheme() {
        List<ContactSchema> schemas = new ArrayList<>();
        schemas.add(new ContactSchema(ID, FULL_NAME, FULL_NUMBER, IMAGE_URL, AGE));
        return schemas;
    }

    private List<Contact> getContact() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(ID, FULL_NAME, IMAGE_URL));
        return contacts;
    }
}