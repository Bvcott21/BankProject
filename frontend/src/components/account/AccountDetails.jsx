import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {fetchAccounts} from "../../services/accountService";
import {fetchTransactionsByAccount} from "../../services/txnService";
import Spinner from "react-bootstrap/Spinner";
import {Alert, Card} from "react-bootstrap";
import Table from "react-bootstrap/Table";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";

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

    const formatTransactionType = (txn) => {
        return txn.transactionType;
    };

    const renderTransactionCard = (txn) => (
        <Card key={txn.transactionNumber} className="mb-3 feature-card">
            <Card.Body>
                <Card.Title className="transaction-title">{txn.transactionNumber}</Card.Title>
                {txn.transactionType !== "N/A" && <Card.Text>{formatTransactionType(txn)}</Card.Text>}
                {txn.amount !== "N/A" && <Card.Text>Amount: ${txn.amount.toFixed(2)}</Card.Text>}
                {txn.timestamp !== "N/A" && <Card.Text>Time: {new Date(txn.timestamp).toLocaleString()}</Card.Text>}
                {txn.transferDirection === "RECEIVER" && txn.senderAccountNumber !== "N/A" && <Card.Text>Sender: {txn.senderAccountNumber}</Card.Text>}
                {txn.transferDirection === "SENDER" && txn.receivingAccountNumber !== "N/A" && <Card.Text>Receiver: {txn.receivingAccountNumber}</Card.Text>}
                {txn.merchantName && txn.merchantName !== "N/A" && <Card.Text>Merchant Name: {txn.merchantName}</Card.Text>}
                {txn.merchantCategory && txn.merchantCategory !== "N/A" && <Card.Text>Merchant Category: {txn.merchantCategory}</Card.Text>}
            </Card.Body>
        </Card>
    );

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
                <Card className="mb-4 feature-card">
                    <Card.Body>
                        <Card.Title className="text-center">{`Account Number: ${account.accountNumber}`}</Card.Title>
                        <Card.Text className="text-center">
                            <strong>Balance: </strong> ${account.balance.toFixed(2)}
                        </Card.Text>
                        <Card.Text className="text-center">
                            <strong>Account Type:</strong>{" "}
                            {account.overdraftLimit !== undefined
                                ? "CHECKING"
                                : account.creditLimit !== undefined
                                    ? "BUSINESS"
                                    : "SAVINGS"
                            }
                        </Card.Text>
                        {account.overdraftLimit && (
                            <Card.Text className="text-center">
                                <strong>Overdraft Limit: </strong> ${account.overdraftLimit.toFixed(2)}
                            </Card.Text>
                        )}
                        {account.creditLimit && (
                            <Card.Text className="text-center">
                                <strong>Credit Limit: </strong> ${account.creditLimit.toFixed(2)}
                            </Card.Text>
                        )}
                        {account.interestRate && (
                            <Card.Text className="text-center">
                                <strong>Interest Rate: </strong> {account.interestRate.toFixed(2)}%
                            </Card.Text>
                        )}
                    </Card.Body>
                </Card>
            )}

            <h3 className="text-center">Transaction History</h3>

            {transactions.length === 0 ? (
                <p className="text-center">No transactions found for this account.</p>
            ) : (
                <>
                    <div className="d-none d-lg-block">
                        <Table striped bordered hover responsive className="table-sm">
                            <thead>
                            <tr>
                                <th>Transaction Number</th>
                                <th>Type</th>
                                <th>Amount</th>
                                <th>Time</th>
                                <th>Sender</th>
                                <th>Receiver</th>
                                <th>Merchant Name</th>
                                <th>Merchant Category</th>
                            </tr>
                            </thead>
                            <tbody>
                            {transactions.map((txn) => (
                                <tr key={txn.transactionNumber}>
                                    <td>{txn.transactionNumber}</td>
                                    <td>{formatTransactionType(txn)}</td>
                                    <td>{txn.amount.toFixed(2)}</td>
                                    <td>{new Date(txn.timestamp).toLocaleString()}</td>
                                    <td>
                                        {txn.transferDirection === "RECEIVER"
                                            ? txn.senderAccountNumber
                                            : "N/A"}
                                    </td>
                                    <td>
                                        {txn.transferDirection === "SENDER"
                                            ? txn.receivingAccountNumber
                                            : "N/A"}
                                    </td>
                                    <td>{txn.merchantName || "N/A"}</td>
                                    <td>{txn.merchantCategory || "N/A"}</td>
                                </tr>
                            ))}
                            </tbody>
                        </Table>
                    </div>
                    <div className="d-lg-none">
                        <Row>
                            {transactions.map((txn) => (
                                <Col xs={12} key={txn.transactionNumber}>
                                    {renderTransactionCard(txn)}
                                </Col>
                            ))}
                        </Row>
                    </div>
                </>
            )}
        </div>
    );
};

export default AccountDetails;