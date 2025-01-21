import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import Home from "./pages/Home";
import Login from "./components/auth/Login";
import Register from "./components/auth/Register";
import ProtectedRoute from "./components/auth/ProtectedRoute";
import Dashboard from "./pages/Dashboard";
import CreateAccountPage from "./pages/CreateAccountPage";
import AccountList from "./components/account/AccountList";
import AccountDetails from "./components/account/AccountDetails";
import CreateTransactionForm from "./components/txn/CreateTransactionForm";
import AdminDashboard from "./pages/AdminDashboard";
import Unauthorized from "./pages/Unauthorized";

function App() {
  return (
    <Router>
      <div className="container mt-4">
        <Routes>
            {/*Public routes*/}
          <Route path="/" element={<Home />}/>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/unauthorized" element={<Unauthorized />} />
            {/*Authenticated customer routes*/}
          <Route
            path="/dashboard"
            element={
                <ProtectedRoute requiredRole="ROLE_CUSTOMER" >
                    <Dashboard />
                </ProtectedRoute>
            }
          />
          <Route
            path="/apply-to-account"
            element={
              <ProtectedRoute>
                  <CreateAccountPage requiredRole="ROLE_CUSTOMER"/>
              </ProtectedRoute>
            }
          />
          <Route
            path="/accounts"
            element={
              <ProtectedRoute>
                  <AccountList requiredRole="ROLE_CUSTOMER"/>
              </ProtectedRoute>
            }/>
          <Route
            path="/accounts/:accountNumber"
            element={
              <ProtectedRoute>
                  <AccountDetails requiredRole="ROLE_CUSTOMER"/>
              </ProtectedRoute>
            }
          />
            <Route
                path="/create-transaction"
                element={
                <ProtectedRoute requiredRole="ROLE_CUSTOMER">
                    <CreateTransactionForm />
                </ProtectedRoute>
                }
            />
            {/*Authenticated admin routes*/}
            <Route
                path="/admin/dashboard"
                element={
                <ProtectedRoute requiredRole="ROLE_ADMIN">
                    <AdminDashboard />
                </ProtectedRoute>
                }
            />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
