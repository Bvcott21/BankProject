import React, { useState } from "react";
import { Button, Form, Alert } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import authService from '../services/authService';

const Register = () => {
    const [ formData, setFormData ] = useState({username: '', password: ''});
    const [ message, setMessage ] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => { 
        setFormData({...formData, [e.target.name]: e.target.value})
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await authService.register(formData);
            setMessage('Registration successful! Redirecting to login...');
            setTimeout(() => navigate('/login'), 3000);
        } catch(err) {
            if (err.response && err.response.data) {
                setMessage(`Registration failed: ${err.response.data}`);
                } else {
                    setMessage('Registration failed. Please try again.');
                }
        }
    };

    return (
        <div className='mt-5'>
            <h2>Register</h2>
            {message && <Alert variant='info'>{message}</Alert>}
            <Form onSubmit={handleSubmit}>
                <Form.Group className='mb-3' controlId='formUsername'>
                    <Form.Label>Username</Form.Label>
                    <Form.Control
                        type='text'
                        name='username'
                        placeholder='Enter username'
                        value={formData.username}
                        onChange={handleChange}
                        required
                    />
                    </Form.Group>
                    <Form.Group className='mb-3' controlId='formPassword'>
                        <Form.Label>Password</Form.Label>
                        <Form.Control 
                            type='password'
                            name='password'
                            placeholder='Enter password'
                            value={formData.password}
                            onChange={handleChange}
                            required
                        />
                    </Form.Group>
                <Button variant='primary' type='submit'>Register</Button>
            </Form>
        </div>
    )
}

export default Register;