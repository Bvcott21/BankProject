import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Header = () => {
    const navigate = useNavigate();
    const { user } = useAuth();
    const isLoggedIn = !!localStorage.getItem('accessToken');
    const userRole = localStorage.getItem('role');

    return (
        <Navbar bg='dark' data-bs-theme='dark' expand='lg' className='bg-body-tertiary'>
            {console.log("[Header component] - isLoggedIn? ", isLoggedIn, 'userRole: ', userRole)}
            {console.log("[Header component] - localStorage: ", user?.role)}
            <Container>
                <Navbar.Brand>BuBank</Navbar.Brand>
                <Navbar.Toggle aria-controls='basic-navbar-nav' />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        <Nav.Link>Link 1</Nav.Link>
                        <Nav.Link>Link 2</Nav.Link>
                        <Nav.Link>Link 3</Nav.Link>
                        <Nav.Link>Profile</Nav.Link>
                        <Nav.Link>Sign In</Nav.Link>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    )
}

export default Header;