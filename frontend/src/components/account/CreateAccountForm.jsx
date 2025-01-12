import {useState} from "react";
import {createAccount} from "../../services/accountService";
import {Alert, Button, Col, Form, Row} from "react-bootstrap";
import {useNavigate} from "react-router-dom";

const CreateAccountForm = () => {
    const [ formData, setFormData ] = useState({
        accountType: '',
        initialBalance: '',
        overdraftLimit: '',
        interestRate: '',
        creditLimit: ''
    });
    const [ successMessage, setSuccessMessage ] = useState('');
    const [ errorMessage, setErrorMessage ] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSuccessMessage('');
        setErrorMessage('');

        try{
            await createAccount(formData);
            setSuccessMessage(`Account created successfully! Redirecting to Dashboard...`);
            setTimeout(() => { navigate("/dashboard") }, 2000);
        } catch (error) {
            setErrorMessage(`Error creating account: ${error.message}`);
        }
    };

    return (
        <div className="container mt-5">
            <h2>Create Account</h2>
            {successMessage && <Alert variant="success">{successMessage}</Alert>}
            {errorMessage && <Alert variant="danger">{errorMessage}</Alert>}
            <Form onSubmit={handleSubmit}>
                <Form.Group as={Row} className="mb-3" controlId="formAccountType">
                    <Form.Label column sm="2">Account Type</Form.Label>
                    <Col sm="10">
                        <Form.Select
                            name="accountType"
                            value={formData.accountType}
                            onChange={handleChange}
                            required
                        >
                            <option value="">Select Account Type</option>
                            <option value="checking">Checking</option>
                            <option value="savings">Savings</option>
                            <option value="business">Business</option>
                        </Form.Select>
                    </Col>
                </Form.Group>
                <Form.Group as={Row} className="mb-3" controlId="formInitialBalance">
                    <Form.Label column sm="2">Initial Balance</Form.Label>
                    <Col sm="10">
                        <Form.Control
                            type="number"
                            name="initialBalance"
                            placeholder="Enter Initial Balance"
                            value={formData.initialBalance}
                            onChange={handleChange}
                            required
                        />
                    </Col>
                </Form.Group>
                {formData.accountType === "checking" && (
                    <Form.Group as={Row} className="mb-3" controlId="formOverdraftLimit">
                        <Form.Label column sm="2">Overdraft Limit</Form.Label>
                        <Col sm="10">
                            <Form.Control
                                type="number"
                                name="overdraftLimit"
                                placeholder="Enter Overdraft Limit"
                                value={formData.overdraftLimit}
                                onChange={handleChange}
                            />
                        </Col>
                    </Form.Group>
                )}
                {formData.accountType === "savings" && (
                    <Form.Group as={Row} className="mb-3" controlId="formInterestRate">
                        <Form.Label column sm="2">Interest Rate</Form.Label>
                        <Col sm="10">
                            <Form.Control
                                type="number"
                                name="interestRate"
                                placeholder="Enter Interest Rate"
                                value={formData.interestRate}
                                onChange={handleChange}
                            />
                        </Col>
                    </Form.Group>
                )}
                {formData.accountType === "business" && (
                    <Form.Group as={Row} className="mb-3" controlId="formCreditLimit">
                        <Form.Label column sm="2">Credit Limit</Form.Label>
                        <Col sm="10">
                            <Form.Control
                                type="number"
                                name="creditLimit"
                                placeholder="Enter Credit Limit"
                                value={formData.creditLimit}
                                onChange={handleChange}
                            />
                        </Col>
                    </Form.Group>
                )}
                <Form.Group as={Row} className="mb-3">
                    <Col sm={{ span: 10, offset: 2 }}>
                        <Button type="submit" variant="primary">Create Account</Button>
                    </Col>
                </Form.Group>
            </Form>
        </div>
    )
}

export default CreateAccountForm;