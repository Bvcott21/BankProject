import { accountApi } from "./api";

export const fetchAccounts = async () => {
    try {
        const response = await accountApi.get("/accounts");
        return response.data;
    } catch(error) {
        console.error("Error fetching accounts: ", error);
        throw error;
    }
};

export const createAccountRequest = async (accountRequestData) => {
    try {
        const response = await accountApi.post("/accounts/create-request", accountRequestData);
        return response.data;
    } catch(error) {
        console.error("Error creating account: ", error);
        throw error;
    }
};

export const fetchAccountCreationRequests = async () => {
    try {
        const response = await accountApi.get("/account-requests");
        return response.data;
    } catch(error) {
        console.error("Error fetching account creation requests: ", error);
        throw error;
    }
}

export const fetchAccountCreationRequestById = async (requestId) => {
    try {
        const response = await accountApi.get(`/account-requests/${requestId}`);
        return response.data;
    } catch(error) {
        console.error("Error fetching account creation request: ", error);
        throw error;
    }
}

export const addCommentToAccountCreationRequest = async (requestId, formData) => {
    try {
        const response = await accountApi.post(`/account-requests/comment/${requestId}`, formData);
        return response.data;
    } catch(error) {
        console.error("Error adding comment: ", error);
        throw error;
    }
}

export const updateAccountCreationRequestStatus = async (requestId, newStatus, accountDetails) => {
    try {
        const response = await accountApi.post(`/account-requests/${requestId}?newStatus=${newStatus}`, accountDetails);
        return response.data;
    } catch(error) {
        console.error("Error updating account creation status: ", error);
        throw error;
    }
}