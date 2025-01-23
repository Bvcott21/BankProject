import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {fetchAccountCreationRequestById} from "../../services/accountService";
import Spinner from "react-bootstrap/Spinner";
import {Alert, Badge, Card, Col, ListGroup, Row} from "react-bootstrap";

const AccountCreationRequestDetails = () => {
    const { requestId } = useParams();
    const [ requestDetails, setRequestDetails ] = useState(null);
    const [ loading, setLoading ] = useState(true);
    const [ error, setError ] = useState(null);
    const currentUsername = localStorage.getItem('username');

    useEffect(() => {
        const loadRequestDetails = async () => {
            try {
                const data = await fetchAccountCreationRequestById(requestId);
                setRequestDetails(data);
            } catch (error) {
                setError(error);
            } finally {
                setLoading(false);
            }
        };
        loadRequestDetails();
    }, [requestId]);

    if (loading) {
        return (
            <div style={{ textAlign: "center", marginTop: "20px" }}>
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
            </div>
        )
    }

    if(error) {
        return <Alert severity="danger">Error: {error.message || "Unknown Error"}</Alert>;
    }

    return (
        <div className="container mt-4">
            <h2 className="text-center mb-4">Request Details</h2>
            {requestDetails ? (
                <Row className="justify-content-center">
                    <Col md={8}>
                        <Card className="mb-4">
                            <Card.Header as="h4" className="bg-primary text-white">General Information</Card.Header>
                            <Card.Body>
                                <ListGroup variant="flush">
                                    <ListGroup.Item><strong>Request ID:</strong> {requestDetails.requestId}</ListGroup.Item>
                                    <ListGroup.Item><strong>Account Type:</strong> {requestDetails.accountType}</ListGroup.Item>
                                    <ListGroup.Item><strong>Status:</strong> <Badge bg="info">{requestDetails.status}</Badge></ListGroup.Item>
                                    <ListGroup.Item><strong>Requested By:</strong> {requestDetails.requestedByUsername}</ListGroup.Item>
                                    <ListGroup.Item><strong>Reviewed By:</strong> {requestDetails.reviewedByUsername || "Not yet reviewed"}</ListGroup.Item>
                                    <ListGroup.Item><strong>Created At:</strong> {new Date(requestDetails.createdAt).toLocaleString()}</ListGroup.Item>
                                    <ListGroup.Item>
                                        <strong>Reviewed At:</strong> {requestDetails.reviewedAt
                                        ? new Date(requestDetails.reviewedAt).toLocaleString()
                                        : "Not yet reviewed"}
                                    </ListGroup.Item>
                                </ListGroup>
                            </Card.Body>
                        </Card>

                        <Card>
                            <Card.Header as="h4" className="bg-secondary text-white">Admin Comments</Card.Header>
                            <Card.Body>
                                {requestDetails.adminComments.length > 0 ? (
                                    <ListGroup>
                                        {requestDetails.adminComments.map((comment, index) => (
                                            <ListGroup.Item
                                                key={index}
                                                className={
                                                    comment.username === currentUsername
                                                        ? "bg-light text-primary"
                                                        : ""
                                                }
                                            >
                                                <div>
                                                    <strong>{comment.username}</strong> -{" "}
                                                    <em>{new Date(comment.timestamp).toLocaleString()}</em>
                                                </div>
                                                <div>{comment.comment}</div>
                                            </ListGroup.Item>
                                        ))}
                                    </ListGroup>
                                ) : (
                                    <Alert variant="info" className="text-center">No comments yet.</Alert>
                                )}
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            ) : (
                <Alert variant="warning" className="text-center">No details available for this request.</Alert>
            )}
        </div>
    );
}

export default AccountCreationRequestDetails;