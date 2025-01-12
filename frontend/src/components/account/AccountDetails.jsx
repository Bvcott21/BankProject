import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {fetchAccounts} from "../../services/accountService";
import {fetchTransactionsByAccount} from "../../services/txnService";
import Spinner from "react-bootstrap/Spinner";
import {Alert, Card} from "react-bootstrap";
import Table from "react-bootstrap/Table";

const AccountDetails = () => {
    const { accountNumber } = useParams();
    const [ account, setAccount ] = useState(null);
    const [ transactions, setTransactions ] = useState([]);
    const [ loading, setLoading ] = useState(true);
    const [ error, setError ] = useState(null);

    useEffect(() => {
        const loadAccountDetails = async () => {
            try {
                const accounts = await fetchAccounts();
                const selectedAccount = accounts.find((acc) => acc.accountNumber === accountNumber);
                setAccount(selectedAccount);

                const txnData = await fetchTransactionsByAccount(accountNumber);
                setTransactions(txnData);
            } catch(e) {
                setError(e.message || "Error loading account details");
            } finally {
                setLoading(false);
            }
        };
        loadAccountDetails();
    }, [accountNumber]);

    if (loading) {
        return (
            <div className="text-center mt-3">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
            </div>
        );
    }

    if (error) {
        return <Alert variant="danger">{error}</Alert>;
    }

    return (
        <div className="container mt-4">
            {account && (
                <Card className="mb-4">
                    <Card.Body>
                        <Card.Title>{`Account Number: ${account.accountNumber}`}</Card.Title>
                        <Card.Text>
                            <strong>Balance: </strong> ${account.balance.toFixed(2)}
                        </Card.Text>
                        <Card.Text>
                            <strong>Account Type:</strong>{" "}
                            {account.overdraftLimit !== undefined
                                ? "CHECKING"
                                : account.creditLimit !== undefined
                                    ? "BUSINESS"
                                    : "SAVINGS"
                            }
                        </Card.Text>
                        {account.overdraftLimit && (
                            <Card.Text>
                                <strong>Overdraft Limit: </strong> ${account.overdraftLimit.toFixed(2)}
                            </Card.Text>
                        )}
                        {account.creditLimit && (
                            <Card.Text>
                                <strong>Credit Limit: </strong> ${account.creditLimit.toFixed(2)}
                            </Card.Text>
                        )}
                        {account.interestRate && (
                            <Card.Text>
                                <strong>Interest Rate</strong> ${account.interestRate.toFixed(2)}%
                            </Card.Text>
                        )}
                    </Card.Body>
                </Card>
            )}

            <h3>Transaction History</h3>

            {transactions.length === 0 ? (
                <p>No transactions found for this account.</p>
            ) : (
                <Table striped bordered hover>
                    <thead>
                    <tr>
                        <th>Transaction ID</th>
                        <th>Type</th>
                        <th>Amount</th>
                        <th>Time</th>
                        <th>Receiving Account</th>
                    </tr>
                    </thead>
                    <tbody>
                        {transactions.map((txn) => (
                            <tr key={txn.transactionId}>
                                <td>{txn.transactionId}</td>
                                <th>{txn.transactionType}</th>
                                <td>{txn.amount.toFixed(2)}</td>
                                <td>{new Date(txn.timestamp).toLocaleString()}</td>
                                <td>{txn.receivingAccountNumber || "N/A"}</td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            )}
        </div>
    )

}

export default AccountDetails;