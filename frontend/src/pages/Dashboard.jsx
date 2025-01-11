import AccountList from "../components/AccountList";
import {useNavigate} from "react-router-dom";
import {Button} from "react-bootstrap";
import authService from "../services/authService";

const Dashboard = () => {
    const navigate = useNavigate();

    const handleLogout = () => {
        authService.logout(navigate);
    }

    return <div>
        <h1>Dashboard page.</h1>
        <AccountList />
        <div className="mt-3 d-flex gap-3">
            <Button
                variant="primary"
                onClick={() => navigate("/create-account")}
            >
                Create Account
            </Button>
            <Button
            variant="primary"
            onClick={handleLogout}
            >
                Logout
            </Button>
        </div>
    </div>
}

export default Dashboard;