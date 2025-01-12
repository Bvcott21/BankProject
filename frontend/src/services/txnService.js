import api from "./api";

export const fetchTransactionsByAccount = async (accountNumber) => {
    const response = await api.get(`/transactions/${accountNumber}`);
    return response.data;
}

export const createTransaction = async (transactionData) => {
    const response = await api.post(`/transactions`, transactionData);
    return response.data;
}