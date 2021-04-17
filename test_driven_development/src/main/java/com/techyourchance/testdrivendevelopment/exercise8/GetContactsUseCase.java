package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

public class GetContactsUseCase {

    public final GetContactsHttpEndpoint mGetContactsHttpEndpoint;
    private List<Listener> mListeners = new ArrayList<>();

    public GetContactsUseCase(GetContactsHttpEndpoint getContactsHttpEndpoint) {
        this.mGetContactsHttpEndpoint = getContactsHttpEndpoint;
    }


    public void fetchContactsAndNotify(String filterTerm) {
        mGetContactsHttpEndpoint.getContacts(filterTerm, new GetContactsHttpEndpoint.Callback() {
            @Override
            public void onGetContactsSucceeded(List<ContactSchema> cartItems) {
                for (Listener listener : mListeners) {
                    listener.onContactItemsFetched(getContactFromSchema(cartItems));
                }
            }

            @Override
            public void onGetContactsFailed(GetContactsHttpEndpoint.FailReason failReason) {
                for (Listener listener : mListeners) {
                    if (failReason == GetContactsHttpEndpoint.FailReason.GENERAL_ERROR) {
                        listener.onContactFetchFailed(FailureReason.GENERAL_ERROR);
                    } else {
                        listener.onContactFetchFailed(FailureReason.NETWORK_ERROR);
                    }
                }

            }
        });
    }

    public void registerListener(Listener listener) {
        mListeners.add(listener);
    }


    private List<Contact> getContactFromSchema(List<ContactSchema> schemas) {
        List<Contact> contacts = new ArrayList<>();
        for (ContactSchema schema : schemas) {
            contacts.add(new Contact(schema.getId(), schema.getFullName(), schema.getImageUrl()));
        }
        return contacts;
    }

    public void unregisterListener(Listener listener) {
        mListeners.remove(listener);
    }

    public interface Listener {
        void onContactItemsFetched(List<Contact> capture);

        void onContactFetchFailed(FailureReason failureReason);
    }


    public enum FailureReason {GENERAL_ERROR, NETWORK_ERROR}
}
