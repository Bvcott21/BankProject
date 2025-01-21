import api from "./api";

export const fetchAccounts = async () => {
    try {
        const response = await api.get("/accounts");
        return response.data;
    } catch(error) {
        console.error("Error fetching accounts: ", error);
        throw error;
    }
};

export const createAccountRequest = async (accountRequestData) => {
    try {
        const response = await api.post("/accounts/create-request", accountRequestData);
        return response.data;
    } catch(error) {
        console.error("Error creating account: ", error);
        throw error;
    }
};

export const fetchAccountCreationRequests = async () => {
    try {
        const response = await api.get("/account-requests");
        return response.data;
    } catch(error) {
        console.error("Error fetching account creation requests: ", error);
        throw error;
    }
}