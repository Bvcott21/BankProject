import React, { useState } from "react";
import { Alert, Button, Form } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import "../../assets/styles/components/AuthForm.css"; // Import shared styles

const Login = () => {
    const { login, user } = useAuth();
    const [credentials, setCredentials] = useState({ username: "", password: "" });
    const [error, setError] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setCredentials({ ...credentials, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setIsLoading(true);

        try {
            await login(credentials);
            if (user?.role === "ROLE_CUSTOMER") navigate('/dashboard');
            if (user?.role === "ROLE_ADMIN") navigate('/admin/dashboard');
        } catch (err) {
            setError("Login failed. Please check your username and password.");
            console.error("Login error: ", err);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="auth-form-container mt-5">
            <h2>Login</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            <Form onSubmit={handleSubmit}>
                <Form.Group controlId="formUsername" className="mb-3">
                    <Form.Label>Username</Form.Label>
                    <Form.Control
                        type="text"
                        name="username"
                        placeholder="Enter username"
                        value={credentials.username}
                        onChange={handleChange}
                        required
                    />
                </Form.Group>

                <Form.Group controlId="formPassword" className="mb-3">
                    <Form.Label>Password</Form.Label>
                    <Form.Control
                        type="password"
                        name="password"
                        placeholder="Enter password"
                        value={credentials.password}
                        onChange={handleChange}
                        required
                    />
                </Form.Group>

                <Button type="submit" disabled={isLoading} className="auth-form-btn">
                    {isLoading ? "Logging in..." : "Login"}
                </Button>
            </Form>
        </div>
    );
};

export default Login;