import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import logo from '../assets/images/bubank_logo.png';


const Header = () => {
    const navigate = useNavigate();
    const { user, logout } = useAuth();
    const isLoggedIn = !!localStorage.getItem('accessToken');

    const handleLogout = () => {
        logout();
        navigate('/');
    }

    return (
        <Navbar expand='lg' className='bg-body-tertiary background-blue'>
            <Container>
                <Navbar.Brand as={Link} to="/">
                <img src={logo} alt="BuBank Logo" className="img-fluid" style={{ maxHeight: "50px", objectFit: "contain" }} />
                    BuBank
                </Navbar.Brand>

                <Navbar.Toggle aria-controls='basic-navbar-nav' />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        { isLoggedIn && 
                            <Nav.Link as={Link} to={user?.role === "ROLE_CUSTOMER" ? "/dashboard" : "/admin/dashboard"}>Dashboard</Nav.Link> 
                        }
                    </Nav>
                    <Nav className="ms-auto">
                        {!isLoggedIn && 
                            <>    
                                <Nav.Link as={Link} to="/login">Sign In</Nav.Link>
                                <Nav.Link as={Link} to='/register'>Register</Nav.Link>
                            </>
                        }
                        {isLoggedIn && 
                            <Nav.Link onClick={handleLogout}>Logout</Nav.Link>
                        }
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    )
}

export default Header;