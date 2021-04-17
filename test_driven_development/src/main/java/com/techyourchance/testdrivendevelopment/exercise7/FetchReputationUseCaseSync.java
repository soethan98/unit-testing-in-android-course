package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

public class FetchReputationUseCaseSync {

    private final GetReputationHttpEndpointSync mGetReputationHttpEndpointSync;

    public FetchReputationUseCaseSync(GetReputationHttpEndpointSync getReputationHttpEndpointSync) {
        this.mGetReputationHttpEndpointSync = getReputationHttpEndpointSync;
    }

    public UseCaseResult fetchReputationSync() {
        GetReputationHttpEndpointSync.EndpointResult endpointResult;
        endpointResult = mGetReputationHttpEndpointSync.getReputationSync();
        if (endpointResult.getStatus() == GetReputationHttpEndpointSync.EndpointStatus.SUCCESS) {
            return new UseCaseResult(UseCaseResult.Status.SUCCESS, 1);

        } else if (endpointResult.getStatus() == GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR) {
            return new UseCaseResult(UseCaseResult.Status.FAILURE, 0);

        } else if (endpointResult.getStatus() == GetReputationHttpEndpointSync.EndpointStatus.NETWORK_ERROR) {
            return new UseCaseResult(UseCaseResult.Status.NETWORK_ERROR, 0);

        }

        throw new RuntimeException("Invalid Status");
    }


}

class UseCaseResult {
    public enum Status {
        SUCCESS,
        FAILURE,
        NETWORK_ERROR
    }

    private final UseCaseResult.Status mStatus;

    private final int mReputation;

    public UseCaseResult(UseCaseResult.Status status, int mReputation) {
        mStatus = status;
        this.mReputation = mReputation;
    }

    public UseCaseResult.Status getStatus() {
        return mStatus;
    }

    public int getReputation() {
        return mReputation;
    }
}
