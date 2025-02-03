import {useState} from "react";
import {createAccountRequest} from "../../services/accountService";
import {Alert, Button, Col, Form, Row} from "react-bootstrap";
import {useNavigate} from "react-router-dom";

const CreateAccountForm = () => {
    const [ formData, setFormData ] = useState({
        accountType: ''
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
            await createAccountRequest(formData);
            setSuccessMessage(`Account creation request submitted successfully! Redirecting to Dashboard...`);
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

                <Form.Group as={Row} className="mb-3">
                    <Col sm={{ span: 10, offset: 2 }}>
                        <Button type="submit" variant="primary">Apply</Button>
                    </Col>
                </Form.Group>
            </Form>
        </div>
    )
}

export default CreateAccountForm;