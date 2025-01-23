import {useNavigate, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {
    addCommentToAccountCreationRequest,
    fetchAccountCreationRequestById,
    updateAccountCreationRequestStatus
} from "../../services/accountService";
import Spinner from "react-bootstrap/Spinner";
import {Alert, Badge, Button, Card, Col, Form, ListGroup, Row} from "react-bootstrap";
import RequestStatus from "../../map/RequestStatus";

const AccountCreationRequestDetails = () => {
    const navigate = useNavigate();
    const { requestId } = useParams();
    const [requestDetails, setRequestDetails] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [newComment, setNewComment] = useState("");
    const [commentError, setCommentError] = useState(null);
    const [submitting, setSubmitting] = useState(false);
    const currentUsername = localStorage.getItem('username');
    const [showApprovalFields, setShowApprovalFields] = useState(false);
    const [accountFields, setAccountFields] = useState({
        initialBalance: '',
        overdraftLimit: '',
        interestRate: '',
        creditLimit: '',
    });

    const isPending = requestDetails?.status === "PENDING";

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

    const handleCommentSubmit = async (e) => {
        e.preventDefault();
        setCommentError(null);
        setSubmitting(true);

        try {
            const updatedRequest = await addCommentToAccountCreationRequest(requestId, { comment: newComment });
            setRequestDetails(updatedRequest);
            setNewComment(""); // clear input box
        } catch (error) {
            setCommentError(error.message || "Failed to add comment.");
        } finally {
            setSubmitting(false);
        }
    };

    const handleApproveClick = () => {
        setShowApprovalFields(true);
    };

    const handleCancelApprove = () => {
        setShowApprovalFields(false);
    };

    const handleFinalApprove = async () => {
        try {
            const accountDetails = {
                initialBalance: accountFields.initialBalance,
                accountType: requestDetails.accountType,
                overdraftLimit: accountFields.overdraftLimit || null,
                interestRate: accountFields.interestRate || null,
                creditLimit: accountFields.creditLimit || null,
            };

            const updatedRequest = await updateAccountCreationRequestStatus(
                requestId,
                "APPROVED",
                accountDetails
            );
            setRequestDetails(updatedRequest);
        } catch (error) {
            setError("Failed to approve request: " + error.message);
        }
    };

    const handleReject = async () => {
        try {
            const updatedRequest = await updateAccountCreationRequestStatus(requestId, "REJECTED", null);
            setRequestDetails(updatedRequest);
        } catch (error) {
            setError("Failed to reject request: " + error.message);
        }
    };

    const renderAccountFields = () => {
        switch (requestDetails.accountType?.toLowerCase()) {
            case "checking":
                return (
                    <Form.Group className="mb-3">
                        <Form.Label>Overdraft Limit</Form.Label>
                        <Form.Control
                            type="number"
                            value={accountFields.overdraftLimit}
                            onChange={(e) =>
                                setAccountFields((prev) => ({
                                    ...prev,
                                    overdraftLimit: e.target.value,
                                }))
                            }
                            required
                        />
                    </Form.Group>
                );
            case "savings":
                return (
                    <Form.Group className="mb-3">
                        <Form.Label>Interest Rate (%)</Form.Label>
                        <Form.Control
                            type="number"
                            value={accountFields.interestRate}
                            onChange={(e) =>
                                setAccountFields((prev) => ({
                                    ...prev,
                                    interestRate: e.target.value,
                                }))
                            }
                            required
                        />
                    </Form.Group>
                );
            case "business":
                return (
                    <Form.Group className="mb-3">
                        <Form.Label>Credit Limit</Form.Label>
                        <Form.Control
                            type="number"
                            value={accountFields.creditLimit}
                            onChange={(e) =>
                                setAccountFields((prev) => ({
                                    ...prev,
                                    creditLimit: e.target.value,
                                }))
                            }
                            required
                        />
                    </Form.Group>
                );
            default:
                return null;
        }
    };

    const getStatusBadgeColour = (status) => {
        switch (status) {
            case "PENDING":
                return "warning";
            case "APPROVED":
                return "success";
            case "REJECTED":
                return "danger";
            default:
                return "secondary";
        }
    };

    if (loading) {
        return (
            <div style={{ textAlign: "center", marginTop: "20px" }}>
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
            </div>
        );
    }

    if (error) {
        return <Alert severity="danger">Error: {error.message || "Unknown Error"}</Alert>;
    }

    return (
        <div className="container mt-4">
            <div className="text-center mb-4">
                <Button variant="secondary" onClick={() => navigate("/admin/dashboard")}>
                    Back to Admin Dashboard
                </Button>
            </div>
            <h2 className="text-center mb-4">Request Details</h2>
            {requestDetails ? (
                <Row className="justify-content-center">
                    <Col md={8}>
                        <Card className="mb-4">
                            <Card.Header as="h4" className="bg-primary text-white">
                                General Information
                                {showApprovalFields && (
                                    <Button
                                        variant="warning"
                                        size="sm"
                                        style={{ float: "right" }}
                                        onClick={handleCancelApprove}
                                    >
                                        Cancel
                                    </Button>
                                )}
                            </Card.Header>
                            <Card.Body>
                                <ListGroup variant="flush">
                                    <ListGroup.Item><strong>Request ID:</strong> {requestDetails.requestId}</ListGroup.Item>
                                    <ListGroup.Item><strong>Account Type:</strong> {requestDetails.accountType}</ListGroup.Item>
                                    <ListGroup.Item>
                                        <strong>Status:</strong>
                                        <Badge bg={getStatusBadgeColour(requestDetails.status)}>
                                            {RequestStatus[requestDetails.status] || "Unknown"}
                                        </Badge>
                                    </ListGroup.Item>
                                    <ListGroup.Item><strong>Requested By:</strong> {requestDetails.requestedByUsername}</ListGroup.Item>
                                    <ListGroup.Item><strong>Reviewed By:</strong> {requestDetails.reviewedByUsername || "Not yet reviewed"}</ListGroup.Item>
                                    <ListGroup.Item><strong>Created At:</strong> {new Date(requestDetails.createdAt).toLocaleString()}</ListGroup.Item>
                                    <ListGroup.Item>
                                        <strong>Reviewed At:</strong> {requestDetails.reviewedAt
                                        ? new Date(requestDetails.reviewedAt).toLocaleString()
                                        : "Not yet reviewed"}
                                    </ListGroup.Item>
                                </ListGroup>
                                {!showApprovalFields && (
                                    <div className="d-flex justify-content-between mt-3">
                                        <Button
                                            variant="success"
                                            onClick={handleApproveClick}
                                            disabled={!isPending}
                                        >
                                            Approve
                                        </Button>
                                        <Button
                                            variant="danger"
                                            onClick={handleReject}
                                            disabled={!isPending}
                                        >
                                            Reject
                                        </Button>
                                    </div>
                                )}
                                {showApprovalFields && (
                                    <>
                                        <Form.Group className="mt-3">
                                            <Form.Label>Initial Balance</Form.Label>
                                            <Form.Control
                                                type="number"
                                                value={accountFields.initialBalance}
                                                onChange={(e) =>
                                                    setAccountFields((prev) => ({
                                                        ...prev,
                                                        initialBalance: e.target.value,
                                                    }))
                                                }
                                                required
                                            />
                                        </Form.Group>
                                        {renderAccountFields()}
                                        <div className="text-center mt-4">
                                            <Button
                                                variant="success"
                                                size="lg"
                                                onClick={handleFinalApprove}
                                            >
                                                Final Approve
                                            </Button>
                                        </div>
                                    </>
                                )}
                            </Card.Body>
                        </Card>

                        {/* Admin Comments */}
                        <Card className="mb-4">
                            <Card.Header as="h4" className="bg-secondary text-white">Admin Comments</Card.Header>
                            <Card.Body>
                                {requestDetails.adminComments.length > 0 ? (
                                    <ListGroup>
                                        {requestDetails.adminComments.map((comment, index) => (
                                            <ListGroup.Item
                                                key={index}
                                                style={{
                                                    backgroundColor: comment.username === currentUsername ? "#e8f5e9" : "white",
                                                    border: comment.username === currentUsername ? "1px solid #a5d6a7" : "1px solid #dee2e6",
                                                    textAlign: comment.username === currentUsername ? "right" : "left",
                                                }}
                                            >
                                                {comment.username === currentUsername ? (
                                                    <>
                                                        <div><em>{new Date(comment.timestamp).toLocaleString()}</em></div>
                                                        <div><strong>{comment.username}</strong></div>
                                                    </>
                                                ) : (
                                                    <>
                                                        <div><strong>{comment.username}</strong></div>
                                                        <div><em>{new Date(comment.timestamp).toLocaleString()}</em></div>
                                                    </>
                                                )}
                                                <div>{comment.comment}</div>
                                            </ListGroup.Item>
                                        ))}
                                    </ListGroup>
                                ) : (
                                    <Alert variant="info" className="text-center">No comments yet.</Alert>
                                )}
                            </Card.Body>
                        </Card>

                        {/* Add Comment */}
                        {isPending && (
                            <Card>
                                <Card.Header as="h4" className="bg-light">Add Comment</Card.Header>
                                <Card.Body>
                                    {commentError && <Alert severity="danger">{commentError}</Alert>}
                                    <Form onSubmit={handleCommentSubmit}>
                                        <Form.Group className="mb-3">
                                            <Form.Control
                                                as="textarea"
                                                rows={3}
                                                value={newComment}
                                                onChange={(e) => setNewComment(e.target.value)}
                                                placeholder="Write your comment here..."
                                                required
                                            />
                                        </Form.Group>
                                        <Button
                                            type="submit"
                                            variant="primary"
                                            disabled={submitting || newComment.trim() === ""}
                                        >
                                            {submitting ? "Submitting" : "Submit Comment"}
                                        </Button>
                                    </Form>
                                </Card.Body>
                            </Card>
                        )}
                    </Col>
                </Row>
            ) : (
                <Alert variant="warning" className="text-center">No details available for this request.</Alert>
            )}
        </div>
    );
};

export default AccountCreationRequestDetails;