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