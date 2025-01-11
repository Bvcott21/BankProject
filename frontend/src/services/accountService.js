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

export const createAccount = async (accountData) => {
    try {
        const response = await api.post("/accounts/create", accountData);
        return response.data;
    } catch(error) {
        console.error("Error creating account: ", error);
        throw error;
    }
};