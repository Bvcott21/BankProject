import AccountList from "../components/AccountList";
import {Link} from "react-router-dom";

const Dashboard = () => {
    return <div>
        <h1>Dashboard page.</h1>
        <AccountList />
        <Link to="/create-account" className="btn btn-primary">
            Create Account
        </Link>
    </div>
}

export default Dashboard;