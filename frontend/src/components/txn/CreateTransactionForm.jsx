import {useLocation, useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {fetchAccounts} from "../../services/accountService";
import {createTransaction} from "../../services/txnService";
import {Alert, Form, Row, Col, Button} from "react-bootstrap";

const CreateTransactionForm = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const [ formData, setFormData ] = useState({
        accountNumber : location.state?.accountNumber || "",
        transactionType: location.state?.transactionType || "",
        amount: "",
        receivingAccountNumber: "",
        transferToOwnAccount: false
    });
    const [ accounts, setAccounts ] = useState([]);
    const [ errorMessage, setErrorMessage ] = useState("");
    const [ successMessage, setSuccessMessage ] = useState("");

    useEffect(() => {
        const loadAccounts = async () => {
            try {
                const accountsData = await fetchAccounts();
                setAccounts(accountsData.filter((acc) => acc.accountNumber !== formData.accountNumber));
            } catch (e) {
                setErrorMessage("Error loading accounts.");
            }
        };

        if(formData.transactionType === "TRANSFER") {
            loadAccounts();
        }

    }, [formData.accountNumber, formData.transactionType]);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: type === "checkbox" ? checked : value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSuccessMessage("");
        setErrorMessage("");

        try {
            await createTransaction(formData);
            setSuccessMessage("Transaction created successfully! Redirecting to Dashboard...");
            setTimeout(() => { navigate("/dashboard") }, 2000);
        } catch (e) {
            setErrorMessage(`Error creating transaction: ${e.message}`);
        }
    };

    return (
        <div className="container mt-5">
            <h2>Create {formData.transactionType}</h2>
            {successMessage && <Alert variant="success">{successMessage}</Alert>}
            {errorMessage && <Alert variant="error">{errorMessage}</Alert>}
            <Form onSubmit={handleSubmit}>

                <Form.Group as={Row} className="mb-3" controlId="formAccountNumber">
                    <Form.Label>Account Number</Form.Label>
                    <Col sm="10">
                        <Form.Control
                            type="text"
                            name="accountNumber"
                            value={formData.accountNumber}
                            readOnly
                        />
                    </Col>
                </Form.Group>

                <Form.Group as={Row} className="mb-3" controlId="formAmount">
                    <Form.Label>Amount</Form.Label>
                    <Col sm="10">
                        <Form.Control
                            type="number"
                            name="amount"
                            value={formData.amount}
                            onChange={handleChange}
                            required
                        />
                    </Col>
                </Form.Group>

                {formData.transactionType === "TRANSFER" && (
                    <>
                        <Form.Group as={Row} className="mb-3" controlId="formTransferToOwnAccount">
                            <Form.Label>Transfer To Own Account</Form.Label>
                            <Col sm="10">
                                <Form.Check
                                    type="checkbox"
                                    name="transferToOwnAccount"
                                    checked={formData.transferToOwnAccount}
                                    onChange={handleChange}
                                    label="Yes"
                                />
                            </Col>
                        </Form.Group>
                        {formData.transferToOwnAccount ? (
                            <Form.Group as={Row} className="mb-3" controlId="formReceivingAccountDropdown">
                                <Form.Label>Receiving Account</Form.Label>
                                <Col sm="10">
                                    <Form.Select
                                        name="receivingAccountNumber"
                                        value={formData.receivingAccountNumber}
                                        onChange={handleChange}
                                        required
                                    >
                                        <option value="">Select an Account</option>
                                        {accounts.map((acc) => (
                                            <option value={acc.accountNumber} key={acc.accountNumber}>
                                                {acc.accountNumber} - {acc.balance.toFixed(2)}
                                            </option>
                                        ))}
                                    </Form.Select>
                                </Col>
                            </Form.Group>
                        ) : (
                            <Form.Group as={Row} className="mb-3" controlId="formReceivingAccountInput">
                                <Form.Label>Receiving Account</Form.Label>
                                <Col sm="10">
                                    <Form.Control
                                        type="text"
                                        name="receivingAccountNumber"
                                        value={formData.receivingAccountNumber}
                                        onChange={handleChange}
                                        placeholder="Enter receiving account number"
                                        required
                                    />
                                </Col>
                            </Form.Group>
                        )}
                    </>
                )}

                <Form.Group as={Row} className="mb-3">
                    <Col sm={{ span: 10, offset: 2}}>
                        <Button type="submit" variant="primary">
                            Submit
                        </Button>
                    </Col>
                </Form.Group>
            </Form>
        </div>
    )
}

export default CreateTransactionForm;