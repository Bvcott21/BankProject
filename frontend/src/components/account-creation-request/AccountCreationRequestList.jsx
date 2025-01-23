import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {fetchAccountCreationRequests} from "../../services/accountService";
import Spinner from "react-bootstrap/Spinner";
import {Alert} from "react-bootstrap";
import Table from "react-bootstrap/Table";

const AccountCreationRequestList = () => {
    const [accountCreationRequests, setAccountCreationRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const loadAccountCreationRequests = async () => {
            try {
                const accountCreationRequestsData = await fetchAccountCreationRequests();
                setAccountCreationRequests(accountCreationRequestsData);
            } catch (error) {
                setError(error);
            } finally {
                setLoading(false);
            }
        };
        loadAccountCreationRequests();
    }, []);

    const columns = [
        "Request ID",
        "Account Type",
        "Requested By",
        "Status",
        "Requested At",
        "Reviewed By",
        "Reviewed At"
    ];

    const getAttributeValue = (accountCreationRequest, attribute) => {
        switch(attribute) {
            case "Request ID":
                return accountCreationRequest.requestId;
            case "Account Type":
                return accountCreationRequest.accountType.toUpperCase();
            case "Requested By":
                return accountCreationRequest.requestedByUsername;
            case "Status":
                return accountCreationRequest.status;
            case "Requested At":
                return new Date(accountCreationRequest.createdAt).toLocaleString();
            case "Reviewed By":
                return accountCreationRequest.reviewedByUsername || "Not yet reviewed";
            case "Reviewed At":
                return accountCreationRequest.reviewedAt
                    ? new Date(accountCreationRequest.reviewedAt).toLocaleString()
                    : "Not yet reviewed";
            default:
                return "N/A";
        }
    };

    if (loading) {
        return (
            <div style={{ textAlign: "center", margin: "20px" }}>
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
            </div>
        );
    }

    if(error) {
        return <Alert variant="danger">Error: {error.message || "Unknown Error"}</Alert>;
    }

    return (
        <div>
            <h2>Account Creation Requests</h2>
            <Table striped bordered hover responsive>
                <thead>
                <tr>
                    {columns.map((column) => (
                        <th key={column}>{column}</th>
                    ))}
                </tr>
                </thead>
                <tbody>
                {accountCreationRequests.map((accountCreationRequest) => (
                    <tr
                        key={accountCreationRequest.requestId}
                        onClick={() => navigate(`/admin/account-requests/${accountCreationRequest.requestId}`)}
                        style={{ cursor: "pointer" }}>
                        {columns.map((column) => (
                            <td key={column}>
                                {getAttributeValue(accountCreationRequest, column)}
                            </td>
                        ))}
                    </tr>
                ))}
                </tbody>
            </Table>
        </div>
    )
}

export default AccountCreationRequestList;