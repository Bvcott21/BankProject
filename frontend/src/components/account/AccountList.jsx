import React, { useEffect, useState } from "react";
import { fetchAccounts } from "../../services/accountService";
import { useNavigate } from "react-router-dom";
import Table from "react-bootstrap/Table";
import Button from "react-bootstrap/Button";
import Spinner from "react-bootstrap/Spinner";
import Alert from "react-bootstrap/Alert";

const AccountList = () => {
    const [accounts, setAccounts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const loadAccounts = async () => {
            try {
                const accountsData = await fetchAccounts();
                setAccounts(accountsData);
            } catch (error) {
                setError(error);
            } finally {
                setLoading(false);
            }
        };

        loadAccounts();
    }, []);

    const columns = [
        "Account Number",
        "Account Type",
        "Balance",
        "Overdraft Limit",
        "Credit Limit",
        "Interest Rate",
        "Actions",
    ];

    const getAttributeValue = (account, attribute) => {
        switch (attribute) {
            case "Account Number":
                return account.accountNumber || "N/A";
            case "Account Type":
                if (account.overdraftLimit !== undefined) return "CHECKING";
                if (account.creditLimit !== undefined) return "BUSINESS";
                if (account.interestRate !== undefined) return "SAVINGS";
                return "Unknown";
            case "Balance":
                return `$${account.balance.toFixed(2)}`;
            case "Overdraft Limit":
                return account.overdraftLimit !== undefined
                    ? `$${account.overdraftLimit.toFixed(2)}`
                    : "N/A";
            case "Credit Limit":
                return account.creditLimit !== undefined
                    ? `$${account.creditLimit.toFixed(2)}`
                    : "N/A";
            case "Interest Rate":
                return account.interestRate !== undefined
                    ? `${account.interestRate.toFixed(2)}%`
                    : "N/A";
            default:
                return "N/A";
        }
    };

    if (loading) {
        return (
            <div style={{ textAlign: "center", margin: "20px" }}>
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
            </div>
        );
    }

    if (error) {
        return <Alert variant="danger">Error: {error.message || "Unknown Error"}</Alert>;
    }

    return (
        <div>
            <h2>My Accounts</h2>
            <Table striped bordered hover responsive>
                <thead>
                <tr>
                    {columns.map((column) => (
                        <th key={column}>{column}</th>
                    ))}
                </tr>
                </thead>
                <tbody>
                {accounts.map((account) => (
                    <tr key={account.accountNumber}>
                        {columns.map((column) => (
                            <td key={column}>
                                {column === "Actions" ? (
                                    <Button
                                        variant="primary"
                                        onClick={() => navigate(`/accounts/${account.accountNumber}`)}
                                    >
                                        View Details
                                    </Button>
                                ) : (
                                    getAttributeValue(account, column)
                                )}
                            </td>
                        ))}
                    </tr>
                ))}
                </tbody>
            </Table>
        </div>
    );
};

export default AccountList;