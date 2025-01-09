import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import authService from '../services/authService';
import { Form, Button, Alert } from 'react-bootstrap';

const Login = () => {
    const [ formData, setFormData ] = useState({username: '', password: ''});
    const [ error, setError ] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({...formData, [e.target.name]: e.target.value});
    };

    const handleSubmit =async (e) => {
        e.preventDefault();
        
        try{
            const response = await authService.login(formData);
            console.log("Login response:", response.data);
            const token = response.data.token;

            if(token) {
                localStorage.setItem('token', response.data.token);
                console.log("Token saved to localStorage:", token);
            } else {
                console.error("No token received in response");
            }
            navigate('/dashboard');
        } catch(err) {
            console.error("Login Error: ", error.response || error.message);
            setError('Invalid credentials. Please try again');
        }
    };

    return (
        <div className="mt-5">
        <h2>Login</h2>
        {error && <Alert variant="danger">{error}</Alert>}
        <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3" controlId="formUsername">
            <Form.Label>Username</Form.Label>
            <Form.Control
                type="text"
                name="username"
                placeholder="Enter Username"
                value={formData.username}
                onChange={handleChange}
                required
            />
            </Form.Group>
            <Form.Group className="mb-3" controlId="formPassword">
            <Form.Label>Password</Form.Label>
            <Form.Control
                type="password"
                name="password"
                placeholder="Enter Password"
                value={formData.password}
                onChange={handleChange}
                required
            />
            </Form.Group>
            <Button variant="primary" type="submit">
            Login
            </Button>
        </Form>
        </div>
    )
}

export default Login;