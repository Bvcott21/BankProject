import api from "./api";

export const fetchTransactionsByAccount = async (accountNumber) => {
    const response = await api.get(`/transactions/${accountNumber}`);
    return response.data;
}