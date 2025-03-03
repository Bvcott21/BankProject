import Container from 'react-bootstrap/Container';
import Button from 'react-bootstrap/Button';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Card from 'react-bootstrap/Card';
import Carousel from 'react-bootstrap/Carousel';
import { Link } from 'react-router-dom';
import '../assets/styles/pages/Home.css'; // Import custom styles
import savingsCard from '../assets/images/cards/savings-account-card.png'; // Placeholder
import checkingCard from '../assets/images/cards/checking-account-card.png'; // Placeholder
import businessCard from '../assets/images/cards/business-account-card.png'; // Placeholder

const Home = () => {
    return (
        <div className="home-page">
            {/* Hero Section */}
            <section className="hero-section">
                <Container className="hero-content">
                    <div className="hero-text">
                        <h1 className="display-4 fw-bold">Welcome to BuBank</h1>
                    </div>
                    <div className="hero-lead">
                        <p className="lead">Your trusted financial partner for secure and seamless banking.</p>
                    </div>
                    <Button as={Link} to="/register" className="hero-btn">Get Started</Button>
                </Container>
            </section>

            {/* Features Section */}
            <Container className="text-center my-5">
                <Row className="justify-content-center">
                    <Col md={4}>
                        <Card className="feature-card">
                            <Card.Body>
                                <Card.Title>Secure Transactions</Card.Title>
                                <Card.Text>Bank with confidence using our industry-leading security measures.</Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                    <Col md={4}>
                        <Card className="feature-card">
                            <Card.Body>
                                <Card.Title>24/7 Support</Card.Title>
                                <Card.Text>Our support team is always ready to assist you anytime, anywhere.</Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                    <Col md={4}>
                        <Card className="feature-card">
                            <Card.Body>
                                <Card.Title>Easy & Fast</Card.Title>
                                <Card.Text>Experience smooth and hassle-free banking with our intuitive platform.</Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            </Container>

            {/* Account Type Carousel */}
            <Container className="my-5 text-center">
                <h2 className="mb-4">Choose the Right Account for You</h2>
                <Carousel>
                    <Carousel.Item>
                        <img className="d-block mx-auto" src={savingsCard} alt="Savings Account" width="300" />
                        <Carousel.Caption>
                            <h3>Savings Account</h3>
                            <p>Grow your wealth with competitive interest rates and no hidden fees.</p>
                        </Carousel.Caption>
                    </Carousel.Item>

                    <Carousel.Item>
                        <img className="d-block mx-auto" src={checkingCard} alt="Checking Account" width="300" />
                        <Carousel.Caption>
                            <h3>Checking Account</h3>
                            <p>Manage daily expenses with ease and enjoy free transactions.</p>
                        </Carousel.Caption>
                    </Carousel.Item>

                    <Carousel.Item>
                        <img className="d-block mx-auto" src={businessCard} alt="Business Account" width="300" />
                        <Carousel.Caption>
                            <h3>Business Account</h3>
                            <p>Empower your business with flexible banking solutions tailored to your needs.</p>
                        </Carousel.Caption>
                    </Carousel.Item>
                </Carousel>
            </Container>
        </div>
    );
};

export default Home;