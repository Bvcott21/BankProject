import React, { useEffect, useState } from "react";
import Alert from "react-bootstrap/Alert";
import Button from "react-bootstrap/Button";
import Card from "react-bootstrap/Card";
import Col from "react-bootstrap/Col";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Spinner from "react-bootstrap/Spinner";
import { useNavigate } from "react-router-dom";
import { fetchAccounts } from "../../services/accountService";

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

    const renderCardText = (label, value) => {
        if(value === "N/A") {
            return null;
        } else if(value !== "N/A" && label === null) {
            return <Card.Text className="text-center">{value}</Card.Text>
        } else {
            return <Card.Text>{label}: {value}</Card.Text> 
        }
    };

    return (
        <Container>
            <h2 className="text-center my-4">My Accounts</h2>
            {loading ? (
                <div className="d-flex justify-content-center my-4">
                    <Spinner animation="border" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </Spinner>
                </div>
            ) : error ? (
                <Alert variant="danger" className="text-center">
                    Error: {error.message || "Unknown Error"}
                </Alert>
            ) : (
                <Row>
                    {accounts.map((account) => (
                        <Col xs={12} md={6} lg={4} key={account.accountNumber} className="mb-3">
                            <Card className="feature-card">
                                <Card.Body>
                                    <Card.Title className="text-center">{account.accountNumber}</Card.Title>
                                    {renderCardText(null, getAttributeValue(account, "Account Type"))}
                                    {renderCardText("Balance", getAttributeValue(account, "Balance"))}
                                    {renderCardText("Overdraft Limit", getAttributeValue(account, "Overdraft Limit"))}
                                    {renderCardText("Credit Limit", getAttributeValue(account, "Credit Limit"))}
                                    {renderCardText("Interest Rate", getAttributeValue(account, "Interest Rate"))}
                                    <div className="d-flex flex-column flex-md-row justify-content-center flex-wrap">
                                        <Button
                                            variant="primary"
                                            onClick={() => navigate(`/accounts/${account.accountNumber}`)}
                                            className="mb-2 mb-md-2 me-md-2 w-100 w-md-100"
                                        >
                                            View Details
                                        </Button>
                                        <Button
                                            variant="success"
                                            onClick={() =>
                                                navigate("/create-transaction", {
                                                    state: { accountNumber: account.accountNumber, transactionType: "DEPOSIT" },
                                                })
                                            }
                                            className="mb-2 mb-md-2 me-md-2 w-100 w-md-100"
                                        >
                                            Deposit
                                        </Button>
                                        <Button
                                            variant="warning"
                                            onClick={() =>
                                                navigate("/create-transaction", {
                                                    state: { accountNumber: account.accountNumber, transactionType: "WITHDRAWAL" },
                                                })
                                            }
                                            className="mb-2 mb-md-2 me-md-2 w-100 w-md-100"
                                        >
                                            Withdraw
                                        </Button>
                                        <Button
                                            variant="info"
                                            onClick={() =>
                                                navigate("/create-transaction", {
                                                    state: { accountNumber: account.accountNumber, transactionType: "TRANSFER" },
                                                })
                                            }
                                            className="w-100 w-md-100"
                                        >
                                            Transfer
                                        </Button>
                                    </div>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            )}
        </Container>
    );
};

export default AccountList;